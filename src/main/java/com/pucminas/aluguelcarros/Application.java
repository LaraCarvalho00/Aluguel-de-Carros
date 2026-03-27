package com.pucminas.aluguelcarros;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.inject.Singleton;
import java.awt.Desktop;
import java.net.URI;

@OpenAPIDefinition(
        info = @Info(
                title = "Aluguel de Carros API",
                version = "0.1.0",
                description = "API REST para o sistema de gestão de aluguel de automóveis - PUC Minas",
                contact = @Contact(name = "Equipe", url = "https://github.com/seu-usuario/aluguel-de-carros")
        )
)
@Singleton
public class Application {

    private final EmbeddedServer server;

    public Application(EmbeddedServer server) {
        this.server = server;
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }

    @EventListener
    public void onStartup(StartupEvent event) {
        String url = server.getURL() + "/swagger-ui/index.html";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ignored) {
        }
    }
}
