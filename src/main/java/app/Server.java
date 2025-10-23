package app;

import app.entities.Bottom;
import app.entities.Topping;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.sql.SQLException;
import java.util.List;

public class Server {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/cupcake?currentSchema=public";
    public static Database db;

    public static class AppData {
        public static List<Topping> toppings = null;
        public static List<Bottom> bottoms = null;
    }

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

        app.error(404, UserController::serveErrorPage);
        app.exception(java.io.FileNotFoundException.class, (e, ctx) -> {
            ctx.status(404);
        });
        app.exception(org.thymeleaf.exceptions.TemplateInputException.class, (e, ctx) -> {
            ctx.status(404);
        });
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
        });

        UserController.addRoutes(app);
        OrderController.addRoutes(app);

        AppData.toppings = OrderMapper.getToppings();
        AppData.bottoms = OrderMapper.getBottoms();

        app.start(8000);
    }
}
