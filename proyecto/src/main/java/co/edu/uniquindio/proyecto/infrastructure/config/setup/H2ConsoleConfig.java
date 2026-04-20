package co.edu.uniquindio.proyecto.infrastructure.config.setup;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuración de la consola web de H2 para entornos de desarrollo.
 *
 * <p>Registra el servlet de H2 en la ruta {@code /h2-console/*} permitiendo
 * inspeccionar la base de datos en memoria directamente desde el navegador.
 * Esta configuración está excluida del perfil {@code prod} mediante
 * {@code @Profile("!prod")} para evitar exposición en producción.</p>
 */
@Configuration
@Profile("!prod")
public class H2ConsoleConfig {

    /**
     * Registra el servlet de la consola H2 en el contexto de Spring.
     *
     * @return Bean de registro del servlet de H2.
     */
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> bean =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        bean.addInitParameter("-webAllowOthers", "false");
        bean.setLoadOnStartup(1);
        return bean;
    }
}