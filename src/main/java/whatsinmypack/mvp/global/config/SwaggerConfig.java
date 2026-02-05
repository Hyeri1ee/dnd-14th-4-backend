package whatsinmypack.mvp.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "https://whatsinmypack.duckdns.org", description = "배포 서버"),
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        }
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";
        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSchemas("LocalTime", new StringSchema().example("14:30:30"))
                .addSecuritySchemes(jwtSchemeName,
                        new SecurityScheme()
                                .name(jwtSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("What's in my pack REST API Specifications")
                .description("Specification")
                .version("1.0.0");
    }
}
