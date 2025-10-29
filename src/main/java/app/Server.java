package app;

import app.controllers.AdminController;
import app.controllers.OrderController;
import app.controllers.UserController;
import app.entities.Bottom;
import app.entities.Topping;
import app.persistence.Database;
import app.persistence.OrderMapper;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;
import java.util.Scanner;

public class Server {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/cupcake?currentSchema=public";
    public static Database db;
    private static Javalin app;

    public static class AppData {
        public static List<Topping> toppings = null;
        public static List<Bottom> bottoms = null;
    }

    public static void start(String ip, int port)
    {
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
        AdminController.addRoutes(app);

        AppData.toppings = OrderMapper.getToppings();
        AppData.bottoms = OrderMapper.getBottoms();

        app.start(ip, port);
    }

    public static void stop()
    {
        System.out.println("Stopping server");
        app.stop();
    }

    public static void main(String[] args)
    {
        try {
           Server.db = new Database(USERNAME, PASSWORD, URL);
        } catch (Exception e) {
            System.err.println("Failed to connect to db");
            System.exit(1);
        }

        Server.start("127.0.0.1", 8000);

        Scanner scan = new Scanner(System.in);
        while (true) {
            String line = scan.nextLine();
            if (line.equals("stop")) {
                break;
            }
        }

        Server.stop();
    }
}
