package whatsinmypack.mvp.performance;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackDetailResponse;
import whatsinmypack.mvp.adapter.out.persistence.relation.ItemWishListJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.PackWishListJpaRepository;
import whatsinmypack.mvp.application.pack.getlist.GetPacksUseCase;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.entity.value.ItemTag;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.ItemWishList;
import whatsinmypack.mvp.domain.relation.entity.PackItem;
import whatsinmypack.mvp.domain.relation.entity.PackWishList;
import whatsinmypack.mvp.domain.user.entity.AuthProvider;
import whatsinmypack.mvp.domain.user.entity.User;

@SpringBootTest
@ActiveProfiles("test")
class PackDetailQueryBenchmarkTest {
    private static final Logger log = LoggerFactory.getLogger(PackDetailQueryBenchmarkTest.class);
    private static final Path CSV_PATH = Path.of("build/benchmark/pack-detail-benchmark.csv");
    private static final Path PIVOT_PATH = Path.of("build/benchmark/pack-detail-benchmark-pivot.md");
    private static final int BATCH_SIZE = 1000;
    private static final int IN_CLAUSE_CHUNK_SIZE = 1000;
    private static final List<String> STRATEGIES = List.of(
            "N_PLUS_ONE_BASELINE",
            "FETCH_JOIN_ITEM",
            "FETCH_JOIN_ITEM_IMAGE",
            "FETCH_JOIN_ITEM_TAG"
    );

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private GetPacksUseCase getPacksUseCase;

    @Autowired
    private PackWishListJpaRepository packWishListJpaRepository;

    @Autowired
    private ItemWishListJpaRepository itemWishListJpaRepository;

    @BeforeAll
    static void initReportFile() throws IOException {
        Files.createDirectories(CSV_PATH.getParent());
        Files.writeString(
                CSV_PATH,
                "itemCount,strategy,totalQueries,dbRoundTrips,responseMs,usedMemoryBytes,javaHibernateMs,entityLoadCount,collectionFetchCount"
                        + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
        Files.writeString(
                PIVOT_PATH,
                "# Pack Detail Benchmark Pivot" + System.lineSeparator() + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    @AfterAll
    static void buildPivotReport() throws IOException {
        if (!Files.exists(CSV_PATH)) {
            return;
        }

        List<String> lines = Files.readAllLines(CSV_PATH);
        if (lines.size() <= 1) {
            return;
        }

        Map<Integer, Map<String, CsvMetricRow>> matrix = new LinkedHashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length != 9) {
                continue;
            }
            CsvMetricRow row = new CsvMetricRow(
                    Integer.parseInt(parts[0]),
                    parts[1],
                    Long.parseLong(parts[2]),
                    Long.parseLong(parts[3]),
                    Long.parseLong(parts[4]),
                    Long.parseLong(parts[5]),
                    Long.parseLong(parts[6]),
                    Long.parseLong(parts[7]),
                    Long.parseLong(parts[8])
            );
            matrix.computeIfAbsent(row.itemCount(), ignored -> new LinkedHashMap<>())
                    .put(row.strategy(), row);
        }

        StringBuilder md = new StringBuilder();
        md.append("# Pack Detail Benchmark Pivot").append(System.lineSeparator()).append(System.lineSeparator());
        md.append("Rows: itemCount, Columns: strategy").append(System.lineSeparator()).append(System.lineSeparator());

        appendPivotTable(md, "totalQueries", matrix, CsvMetricRow::totalQueries);
        appendPivotTable(md, "dbRoundTrips", matrix, CsvMetricRow::dbRoundTrips);
        appendPivotTable(md, "responseMs", matrix, CsvMetricRow::responseMs);
        appendPivotTable(md, "usedMemoryBytes", matrix, CsvMetricRow::usedMemoryBytes);
        appendPivotTable(md, "javaHibernateMs", matrix, CsvMetricRow::javaHibernateMs);
        appendImprovementTable(md, matrix);

        Files.writeString(
                PIVOT_PATH,
                md.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    @BeforeEach
    void cleanDatabase() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createQuery("delete from ItemWishList").executeUpdate();
            entityManager.createQuery("delete from PackWishList").executeUpdate();
            entityManager.createQuery("delete from PackItem").executeUpdate();
            entityManager.createQuery("delete from Pack").executeUpdate();
            entityManager.createQuery("delete from ItemImage").executeUpdate();
            entityManager.createQuery("delete from ItemTag").executeUpdate();
            entityManager.createQuery("delete from Item").executeUpdate();
            entityManager.createQuery("delete from ContextCategory").executeUpdate();
            entityManager.createQuery("delete from User").executeUpdate();
            entityManager.flush();
            entityManager.clear();
        });
    }

    @DisplayName("팩 상세 조회: N+1 vs Fetch Join 범위별 성능 비교")
    @ParameterizedTest(name = "itemCount={0}")
    @MethodSource("itemCounts")
    void compareNPlusOneVsFetchJoinScopes(int itemCount) throws IOException {
        Fixture fixture = seedFixture(itemCount, 1, 1);
        List<ScenarioResult> results = new ArrayList<>();

        BenchmarkResult nPlusOne = runMeasured(
                "N_PLUS_ONE_BASELINE",
                () -> runCurrentControllerFlow(fixture.userId(), fixture.packId())
        );
        BenchmarkResult fetchItem = runMeasured(
                "FETCH_JOIN_ITEM",
                () -> runFetchJoinItemScopeFlow(fixture.userId(), fixture.packId())
        );
        BenchmarkResult fetchImage = runMeasured(
                "FETCH_JOIN_ITEM_IMAGE",
                () -> runFetchJoinItemImageScopeFlow(fixture.userId(), fixture.packId())
        );
        BenchmarkResult fetchTag = runMeasured(
                "FETCH_JOIN_ITEM_TAG",
                () -> runFetchJoinItemTagScopeFlow(fixture.userId(), fixture.packId())
        );

        results.add(new ScenarioResult(itemCount, nPlusOne));
        results.add(new ScenarioResult(itemCount, fetchItem));
        results.add(new ScenarioResult(itemCount, fetchImage));
        results.add(new ScenarioResult(itemCount, fetchTag));

        printMarkdownTable(itemCount, results);
        appendCsv(results);

        assertThat(nPlusOne.response()).isNotNull();
        assertThat(fetchItem.response()).isNotNull();
        assertThat(fetchImage.response()).isNotNull();
        assertThat(fetchTag.response()).isNotNull();
    }

    private static Stream<Integer> itemCounts() {
        String override = System.getenv("BENCHMARK_ITEM_COUNTS");
        if (override == null || override.isBlank()) {
            return Stream.of(100, 1000, 10000, 100000);
        }
        return Stream.of(override.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt);
    }

    private BenchmarkResult runMeasured(String strategy, Flow flow) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.clear();

        forceGc();
        long memoryBefore = usedMemoryBytes();
        long startedAt = System.nanoTime();

        FlowResult flowResult = transactionTemplate.execute(status -> {
            entityManager.clear();
            return flow.run();
        });

        long finishedAt = System.nanoTime();
        long memoryAfter = usedMemoryBytes();

        long responseTimeMs = nanosToMillis(finishedAt - startedAt);
        long usedMemoryBytes = Math.max(0L, memoryAfter - memoryBefore);

        return new BenchmarkResult(
                strategy,
                flowResult.response(),
                statistics.getPrepareStatementCount(),
                statistics.getPrepareStatementCount(),
                responseTimeMs,
                usedMemoryBytes,
                nanosToMillis(flowResult.dtoMappingNanos()),
                statistics.getEntityLoadCount(),
                statistics.getCollectionFetchCount()
        );
    }

    private FlowResult runCurrentControllerFlow(Long userId, Long packId) {
        long mapStartedAt = 0L;
        Pack pack = getPacksUseCase.findById(packId);

        boolean isPackInWishList = !packWishListJpaRepository
                .findWishlistedPackIdsByUserIdAndPackIds(userId, List.of(pack.getId()))
                .isEmpty();

        Set<Long> itemIds = pack.getPackItems().stream()
                .map(PackItem::getItem)
                .map(Item::getId)
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> wishlistedItemIds = itemIds.isEmpty()
                ? Set.of()
                : findWishlistedItemIds(userId, itemIds);

        mapStartedAt = System.nanoTime();
        PackDetailResponse response = PackDetailResponse.from(pack, isPackInWishList, wishlistedItemIds);
        long mapFinishedAt = System.nanoTime();
        return new FlowResult(response, mapFinishedAt - mapStartedAt);
    }

    private FlowResult runFetchJoinItemScopeFlow(Long userId, Long packId) {
        Pack pack = entityManager.createQuery("""
                        select distinct p
                        from Pack p
                        left join fetch p.user
                        left join fetch p.contextCategory
                        left join fetch p.packItems pi
                        left join fetch pi.item i
                        where p.id = :packId
                        """, Pack.class)
                .setParameter("packId", packId)
                .getSingleResult();

        boolean isPackInWishList = !packWishListJpaRepository
                .findWishlistedPackIdsByUserIdAndPackIds(userId, List.of(pack.getId()))
                .isEmpty();

        Set<Long> itemIds = pack.getPackItems().stream()
                .map(PackItem::getItem)
                .map(Item::getId)
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> wishlistedItemIds = itemIds.isEmpty()
                ? Set.of()
                : findWishlistedItemIds(userId, itemIds);

        long mapStartedAt = System.nanoTime();
        PackDetailResponse response = PackDetailResponse.from(pack, isPackInWishList, wishlistedItemIds);
        long mapFinishedAt = System.nanoTime();
        return new FlowResult(response, mapFinishedAt - mapStartedAt);
    }

    private FlowResult runFetchJoinItemImageScopeFlow(Long userId, Long packId) {
        Pack pack = fetchPackWithItemScope(packId);
        Set<Long> itemIds = extractItemIds(pack);
        preloadItemImages(itemIds);
        return mapWithWishlist(userId, pack);
    }

    private FlowResult runFetchJoinItemTagScopeFlow(Long userId, Long packId) {
        Pack pack = fetchPackWithItemScope(packId);
        Set<Long> itemIds = extractItemIds(pack);
        preloadItemTags(itemIds);
        return mapWithWishlist(userId, pack);
    }

    private FlowResult mapWithWishlist(Long userId, Pack pack) {
        boolean isPackInWishList = !packWishListJpaRepository
                .findWishlistedPackIdsByUserIdAndPackIds(userId, List.of(pack.getId()))
                .isEmpty();

        Set<Long> itemIds = extractItemIds(pack);

        Set<Long> wishlistedItemIds = itemIds.isEmpty()
                ? Set.of()
                : findWishlistedItemIds(userId, itemIds);

        long mapStartedAt = System.nanoTime();
        PackDetailResponse response = PackDetailResponse.from(pack, isPackInWishList, wishlistedItemIds);
        long mapFinishedAt = System.nanoTime();
        return new FlowResult(response, mapFinishedAt - mapStartedAt);
    }

    private Fixture seedFixture(int itemCount, int imagesPerItem, int tagsPerItem) {
        Fixture baseFixture = transactionTemplate.execute(status -> {
            User user = User.builder()
                    .email("benchmark-user@example.com")
                    .nickname("benchmark-user")
                    .authProvider(AuthProvider.KAKAO)
                    .profileImage("https://cdn.example.com/u.png")
                    .build();
            entityManager.persist(user);

            ContextCategory contextCategory = ContextCategory.builder()
                    .name("travel")
                    .detail("travel-detail")
                    .build();
            entityManager.persist(contextCategory);

            Pack pack = Pack.builder()
                    .title("benchmark-pack")
                    .introduction("benchmark-introduction")
                    .user(user)
                    .contextCategory(contextCategory)
                    .build();
            entityManager.persist(pack);
            entityManager.flush();
            Long userId = user.getId();
            Long packId = pack.getId();
            entityManager.clear();
            return new Fixture(userId, packId);
        });

        for (int from = 0; from < itemCount; from += BATCH_SIZE) {
            int to = Math.min(from + BATCH_SIZE, itemCount);
            persistItemBatch(
                    baseFixture.userId(),
                    baseFixture.packId(),
                    from,
                    to,
                    imagesPerItem,
                    tagsPerItem
            );
        }

        transactionTemplate.executeWithoutResult(status -> {
            PackWishList packWishList = PackWishList.builder()
                    .pack(entityManager.getReference(Pack.class, baseFixture.packId()))
                    .user(entityManager.getReference(User.class, baseFixture.userId()))
                    .isWishlist(true)
                    .build();
            entityManager.persist(packWishList);
            entityManager.flush();
            entityManager.clear();
        });

        return baseFixture;
    }

    private void persistItemBatch(
            Long userId,
            Long packId,
            int fromInclusive,
            int toExclusive,
            int imagesPerItem,
            int tagsPerItem
    ) {
        transactionTemplate.executeWithoutResult(status -> {
            User userRef = entityManager.getReference(User.class, userId);
            Pack packRef = entityManager.getReference(Pack.class, packId);

            for (int i = fromInclusive; i < toExclusive; i++) {
                Item item = Item.builder()
                        .title("item-" + i)
                        .brand("brand-" + i)
                        .review("review-" + i)
                        .satisfaction(Satisfaction.GOOD)
                        .usePeriod(UsePeriod.BELOW_ONE_YEAR)
                        .purchase("purchase-" + i)
                        .user(userRef)
                        .build();
                entityManager.persist(item);

                for (int img = 0; img < imagesPerItem; img++) {
                    ItemImage image = ItemImage.fromPath("https://cdn.example.com/item-" + i + "-img-" + img + ".jpg");
                    image.setItem(item);
                    entityManager.persist(image);
                }

                for (int tag = 0; tag < tagsPerItem; tag++) {
                    ItemTag itemTag = ItemTag.builder().tag("tag-" + tag).build();
                    itemTag.setItem(item);
                    entityManager.persist(itemTag);
                }

                PackItem packItem = new PackItem(packRef, item);
                entityManager.persist(packItem);

                if (i % 2 == 0) {
                    ItemWishList itemWishList = ItemWishList.builder()
                            .item(item)
                            .user(userRef)
                            .isWishlist(true)
                            .build();
                    entityManager.persist(itemWishList);
                }
            }

            entityManager.flush();
            entityManager.clear();
        });
    }

    private void printMarkdownTable(int itemCount, List<ScenarioResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator())
                .append("### itemCount=").append(itemCount).append(System.lineSeparator())
                .append("| strategy | totalQueries | dbRoundTrips(IO) | responseMs | usedMemoryBytes | javaHibernateMs | entityLoadCount | collectionFetchCount |")
                .append(System.lineSeparator())
                .append("|---|---:|---:|---:|---:|---:|---:|---:|")
                .append(System.lineSeparator());

        for (ScenarioResult result : results) {
            BenchmarkResult m = result.metrics();
            sb.append("| ")
                    .append(m.strategy()).append(" | ")
                    .append(m.totalQueryCount()).append(" | ")
                    .append(m.dbRoundTrips()).append(" | ")
                    .append(m.responseTimeMs()).append(" | ")
                    .append(m.usedMemoryBytes()).append(" | ")
                    .append(m.javaHibernateProcessingMs()).append(" | ")
                    .append(m.entityLoadCount()).append(" | ")
                    .append(m.collectionFetchCount()).append(" |")
                    .append(System.lineSeparator());
        }
        log.info(sb.toString());
    }

    private void appendCsv(List<ScenarioResult> results) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ScenarioResult result : results) {
            BenchmarkResult m = result.metrics();
            sb.append(result.itemCount()).append(",")
                    .append(m.strategy()).append(",")
                    .append(m.totalQueryCount()).append(",")
                    .append(m.dbRoundTrips()).append(",")
                    .append(m.responseTimeMs()).append(",")
                    .append(m.usedMemoryBytes()).append(",")
                    .append(m.javaHibernateProcessingMs()).append(",")
                    .append(m.entityLoadCount()).append(",")
                    .append(m.collectionFetchCount()).append(System.lineSeparator());
        }
        Files.writeString(CSV_PATH, sb.toString(), StandardOpenOption.APPEND);
    }

    private static void appendPivotTable(
            StringBuilder sb,
            String metricName,
            Map<Integer, Map<String, CsvMetricRow>> matrix,
            MetricSelector selector
    ) {
        sb.append("## ").append(metricName).append(System.lineSeparator());
        sb.append("| itemCount | ").append(String.join(" | ", STRATEGIES)).append(" |").append(System.lineSeparator());
        sb.append("|---:|---:|---:|---:|---:|").append(System.lineSeparator());

        matrix.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .forEach(itemCount -> {
                    sb.append("| ").append(itemCount).append(" | ");
                    for (int idx = 0; idx < STRATEGIES.size(); idx++) {
                        String strategy = STRATEGIES.get(idx);
                        CsvMetricRow row = matrix.get(itemCount).get(strategy);
                        sb.append(row == null ? "-" : selector.select(row));
                        sb.append(" | ");
                    }
                    sb.append(System.lineSeparator());
                });
        sb.append(System.lineSeparator());
    }

    private static void appendImprovementTable(StringBuilder sb, Map<Integer, Map<String, CsvMetricRow>> matrix) {
        sb.append("## improvementVsNPlusOne(%)").append(System.lineSeparator());
        sb.append("| itemCount | strategy | totalQueries | dbRoundTrips | responseMs | usedMemoryBytes | javaHibernateMs |")
                .append(System.lineSeparator());
        sb.append("|---:|---|---:|---:|---:|---:|---:|").append(System.lineSeparator());

        matrix.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .forEach(itemCount -> {
                    Map<String, CsvMetricRow> strategyRows = matrix.get(itemCount);
                    CsvMetricRow baseline = strategyRows.get("N_PLUS_ONE_BASELINE");
                    if (baseline == null) {
                        return;
                    }
                    for (String strategy : STRATEGIES) {
                        if ("N_PLUS_ONE_BASELINE".equals(strategy)) {
                            continue;
                        }
                        CsvMetricRow target = strategyRows.get(strategy);
                        if (target == null) {
                            continue;
                        }
                        sb.append("| ").append(itemCount).append(" | ")
                                .append(strategy).append(" | ")
                                .append(formatImprovement(baseline.totalQueries(), target.totalQueries())).append(" | ")
                                .append(formatImprovement(baseline.dbRoundTrips(), target.dbRoundTrips())).append(" | ")
                                .append(formatImprovement(baseline.responseMs(), target.responseMs())).append(" | ")
                                .append(formatImprovement(baseline.usedMemoryBytes(), target.usedMemoryBytes())).append(" | ")
                                .append(formatImprovement(baseline.javaHibernateMs(), target.javaHibernateMs())).append(" |")
                                .append(System.lineSeparator());
                    }
                });

        sb.append(System.lineSeparator());
    }

    private static String formatImprovement(long baseline, long current) {
        if (baseline <= 0) {
            return "-";
        }
        double pct = ((double) (baseline - current) / baseline) * 100.0;
        return String.format("%.2f%%", pct);
    }

    private Pack fetchPackWithItemScope(Long packId) {
        return entityManager.createQuery("""
                        select distinct p
                        from Pack p
                        left join fetch p.user
                        left join fetch p.contextCategory
                        left join fetch p.packItems pi
                        left join fetch pi.item i
                        where p.id = :packId
                        """, Pack.class)
                .setParameter("packId", packId)
                .getSingleResult();
    }

    private Set<Long> extractItemIds(Pack pack) {
        return pack.getPackItems().stream()
                .map(PackItem::getItem)
                .map(Item::getId)
                .collect(java.util.stream.Collectors.toSet());
    }

    private Set<Long> findWishlistedItemIds(Long userId, Set<Long> itemIds) {
        Set<Long> result = new HashSet<>();
        List<Long> ids = List.copyOf(itemIds);
        for (int from = 0; from < ids.size(); from += IN_CLAUSE_CHUNK_SIZE) {
            int to = Math.min(from + IN_CLAUSE_CHUNK_SIZE, ids.size());
            List<Long> chunk = ids.subList(from, to);
            result.addAll(itemWishListJpaRepository.findWishlistedItemIdsByUserIdAndItemIds(userId, chunk));
        }
        return result;
    }

    private void preloadItemImages(Set<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return;
        }
        List<Long> ids = List.copyOf(itemIds);
        for (int from = 0; from < ids.size(); from += IN_CLAUSE_CHUNK_SIZE) {
            int to = Math.min(from + IN_CLAUSE_CHUNK_SIZE, ids.size());
            List<Long> chunk = ids.subList(from, to);
            entityManager.createQuery("""
                            select distinct i
                            from Item i
                            left join fetch i.images
                            where i.id in :itemIds
                            """, Item.class)
                    .setParameter("itemIds", chunk)
                    .getResultList();
        }
    }

    private void preloadItemTags(Set<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return;
        }
        List<Long> ids = List.copyOf(itemIds);
        for (int from = 0; from < ids.size(); from += IN_CLAUSE_CHUNK_SIZE) {
            int to = Math.min(from + IN_CLAUSE_CHUNK_SIZE, ids.size());
            List<Long> chunk = ids.subList(from, to);
            entityManager.createQuery("""
                            select distinct i
                            from Item i
                            left join fetch i.tags
                            where i.id in :itemIds
                            """, Item.class)
                    .setParameter("itemIds", chunk)
                    .getResultList();
        }
    }

    private static long usedMemoryBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void forceGc() {
        System.gc();
    }

    private static long nanosToMillis(long nanos) {
        return nanos / 1_000_000L;
    }

    private record Fixture(Long userId, Long packId) {
    }

    private record FlowResult(PackDetailResponse response, long dtoMappingNanos) {
    }

    private record BenchmarkResult(
            String strategy,
            PackDetailResponse response,
            long totalQueryCount,
            long dbRoundTrips,
            long responseTimeMs,
            long usedMemoryBytes,
            long javaHibernateProcessingMs,
            long entityLoadCount,
            long collectionFetchCount
    ) {
    }

    private record ScenarioResult(int itemCount, BenchmarkResult metrics) {
    }

    private record CsvMetricRow(
            int itemCount,
            String strategy,
            long totalQueries,
            long dbRoundTrips,
            long responseMs,
            long usedMemoryBytes,
            long javaHibernateMs,
            long entityLoadCount,
            long collectionFetchCount
    ) {
    }

    @FunctionalInterface
    private interface MetricSelector {
        long select(CsvMetricRow row);
    }

    @FunctionalInterface
    private interface Flow {
        FlowResult run();
    }
}
