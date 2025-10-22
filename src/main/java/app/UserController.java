package app;

import app.entities.User;
import app.persistence.UserMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.INDEX, UserController::serveIndexPage);
        app.get(Path.Web.LOGIN, UserController::serveLoginPage);
        app.get(Path.Web.BASKET, UserController::serveBasketPage);
        app.post(Path.Web.LOGIN, UserController::handleLoginPost);
        app.post(Path.Web.LOGOUT, UserController::handleLogoutPost);
        app.post(Path.Web.REGISTER, UserController::handleRegisterPost);

    }

    public static void serveIndexPage(Context ctx)
    {
        // FIXME: for debugging
        String sql = "SELECT Toppings.name FROM Toppings";
        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ctx.status(500);
            return;
        }

        ctx.attribute("user", ctx.sessionAttribute("user"));
        ctx.render(Path.Template.INDEX);
    }

    public static void serveLoginPage(Context ctx)
    {
        ctx.attribute("errmsg", ctx.sessionAttribute("errmsg"));
        ctx.render(Path.Template.LOGIN);
        ctx.sessionAttribute("errmsg", null);
    }

    public static void serveBasketPage(Context ctx){
        ctx.attribute("errmsg", ctx.sessionAttribute("errmsg"));
        ctx.render(Path.Template.BASKET);
        ctx.sessionAttribute("errmsg", null);
    }

    public static void handleLoginPost(Context ctx)
    {
        User user;
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        if (email == null || password == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }
        if (email.isEmpty() || password.isEmpty()) {
            ctx.sessionAttribute("errmsg", "* Invalid email or password");
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        user = UserMapper.login(email, password);
        if (user == null) {
            ctx.sessionAttribute("errmsg", "* Invalid email or password");
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        ctx.sessionAttribute("user", user);
        ctx.redirect(Path.Web.INDEX);
    }

    public static void handleLogoutPost(Context ctx)
    {
        ctx.sessionAttribute("user", null);
        ctx.redirect(Path.Web.INDEX);
    }

    public static void handleRegisterPost(Context ctx)
    {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        if (email == null || password == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }
        if (email.isEmpty() || password.isEmpty() ||
                !UserMapper.register(email, password)) {
            ctx.sessionAttribute("errmsg", "* Failed to register");
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        ctx.redirect(Path.Web.LOGIN);
    }
}
