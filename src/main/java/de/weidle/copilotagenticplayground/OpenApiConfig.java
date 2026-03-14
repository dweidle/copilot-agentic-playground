package de.weidle.copilotagenticplayground;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Copilot Agentic Playground API",
                        version = "1.0",
                        description = "REST API for the greeting service"))
public class OpenApiConfig {}
