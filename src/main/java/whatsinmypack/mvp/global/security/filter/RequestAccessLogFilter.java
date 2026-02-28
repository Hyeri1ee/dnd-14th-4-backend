package whatsinmypack.mvp.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import whatsinmypack.mvp.adapter.out.persistence.accesslog.RequestAccessLogJpaRepository;
import whatsinmypack.mvp.domain.accesslog.entity.RequestAccessLog;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Component
@RequiredArgsConstructor
public class RequestAccessLogFilter extends OncePerRequestFilter {

    private final RequestAccessLogJpaRepository requestAccessLogJpaRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            requestAccessLogJpaRepository.save(
                    RequestAccessLog.builder()
                            .requestUrl(request.getRequestURI())
                            .userId(resolveUserId(request))
                            .statusCode(response.getStatus())
                            .build()
            );
        }
    }

    private Long resolveUserId(HttpServletRequest request) {
        Object authenticatedUserId = request.getAttribute("authenticatedUserId");
        if (authenticatedUserId instanceof Long userIdFromRequest) {
            return userIdFromRequest;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }
}
