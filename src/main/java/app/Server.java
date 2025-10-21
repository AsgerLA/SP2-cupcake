package app;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.sql.SQLException;

public class Server {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/cupcake?currentSchema=public";
    public static Database db;

    public static void main(String[] args)
    {
        Javalin app;
        try {
            db = new Database(USERNAME, PASSWORD, URL);
        } catch (SQLException e) {
            System.err.println("Failed to connect to db");
            System.exit(1);
        }
        System.out.println("Starting server");

        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);

        app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.http.asyncTimeout = 10_000L;
            config.fileRenderer(new JavalinThymeleaf(templateEngine));
            config.staticFiles.add("/public");
        });

        UserController.addRoutes(app);
        OrderController.addRoutes(app);

        app.start(8000);
    }
}
