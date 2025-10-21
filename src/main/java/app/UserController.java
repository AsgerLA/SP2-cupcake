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
        app.post(Path.Web.LOGIN, UserController::handleLoginPost);
        app.post(Path.Web.LOGOUT, UserController::handleLogoutPost);
        app.post(Path.Web.REGISTER, UserController::handleRegisterPost);
    }

    public static void serveIndexPage(Context ctx)
    {
        // FIXME: for debugging
        Connection conn = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            conn = Server.db.connect();
            String sql = "SELECT Toppings.name FROM Toppings";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (Exception e) {
            System.err.println(e);
            return;
        } finally {
            if (conn != null)
                Server.db.close(conn);
        }

        ctx.render(Path.Template.INDEX);
    }

    public static void serveLoginPage(Context ctx)
    {
        ctx.render(Path.Template.LOGIN);
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
        user = UserMapper.login(email, password);
        if (user == null) {
            ctx.attribute("errmsg", "* Invalid email or password");
            ctx.redirect(Path.Template.LOGIN);
            return;
        }
        ctx.sessionAttribute("user", user);
        ctx.redirect(Path.Template.INDEX);
    }

    public static void handleLogoutPost(Context ctx)
    {
        ctx.sessionAttribute("user", null);
        ctx.redirect(Path.Template.INDEX);
    }

    public static void handleRegisterPost(Context ctx)
    {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        if (email == null || password == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }
        if (!UserMapper.register(email, password)) {
            ctx.attribute("errmsg", "* Failed to register");
            return;
        }
        ctx.redirect(Path.Template.LOGIN);
    }
}
