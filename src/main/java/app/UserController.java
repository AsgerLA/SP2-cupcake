package app;

import app.entities.Order;
import app.entities.Topping;
import app.entities.Bottom;
import app.entities.User;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.INDEX, UserController::serveIndexPage);
        app.get(Path.Web.LOGIN, UserController::serveLoginPage);
        app.get(Path.Web.BASKET, UserController::serveBasketPage);
        app.post(Path.Web.LOGIN, UserController::handleLoginPost);
        app.post(Path.Web.LOGOUT, UserController::handleLogoutPost);
        app.post(Path.Web.REGISTER, UserController::handleRegisterPost);
        app.post(Path.Web.BASKET, UserController::handleBasketPost);
        app.post(Path.Web.REMOVE_BASKET, UserController::handleRemoveBasketPost);

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

        ctx.attribute("toppings", OrderMapper.getToppings());
        ctx.attribute("bottoms", OrderMapper.getBottoms());

        ctx.attribute("user", ctx.sessionAttribute("user"));

        ctx.attribute("errmsg", ctx.sessionAttribute("errmsg"));
        ctx.render(Path.Template.INDEX);
        ctx.sessionAttribute("errmsg", null);
    }

    public static void serveLoginPage(Context ctx)
    {
        ctx.attribute("errmsg", ctx.sessionAttribute("errmsg"));
        ctx.render(Path.Template.LOGIN);
        ctx.sessionAttribute("errmsg", null);
    }

    public static void serveBasketPage(Context ctx){
        if (ctx.sessionAttribute("user") == null) {
            ctx.sessionAttribute("loginredirect", Path.Web.BASKET);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        ctx.attribute("basket", ctx.sessionAttribute("basket"));
        ctx.attribute("subtotal", ctx.sessionAttribute("subtotal"));
        ctx.render(Path.Template.BASKET);
    }

    public static void handleLoginPost(Context ctx)
    {
        User user;
        String redirect = ctx.sessionAttribute("loginredirect");
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
        ctx.sessionAttribute("basket", new ArrayList<>());
        ctx.sessionAttribute("subtotal", 0.0);
        if (redirect != null) {
            ctx.sessionAttribute("loginredirect", null);
            ctx.redirect(redirect);
            return;
        }
        ctx.redirect(Path.Web.INDEX);
    }

    public static void handleLogoutPost(Context ctx)
    {
        ctx.sessionAttribute("user", null);
        ctx.sessionAttribute("basket", null);
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

    public static void handleBasketPost(Context ctx)
    {
        User user;
        int count;
        int topId;
        int botId;

        try {
            user = ctx.sessionAttribute("user");
            if (user == null) {
                ctx.sessionAttribute("loginredirect", Path.Web.INDEX);
                ctx.redirect(Path.Web.LOGIN);
                return;
            }
            count = Integer.decode(Objects.requireNonNull(ctx.formParam("count")));
            if (count == 0)
                throw new Exception();
            topId = Integer.decode(ctx.formParam("topping"));
            botId = Integer.decode(ctx.formParam("bottom"));
            if (topId == 0 || botId == 0)
                throw new Exception();
            List<Order> basket = ctx.sessionAttribute("basket");
            assert basket != null;
            List<Topping> tops = OrderMapper.getToppings();
            double price = tops.get(topId-1).getPrice();
            List<Bottom> bots = OrderMapper.getBottoms();
            price += tops.get(topId-1).getPrice();
            Order o = new Order(0, tops.get(topId-1).getName(), bots.get(botId-1).getName(), count, price);
            o.topId = topId;
            o.botId = botId;
            basket.add(o);
            double subtotal = ctx.sessionAttribute("subtotal");
            ctx.sessionAttribute("subtotal", subtotal+price*count);
            ctx.redirect(Path.Web.INDEX);
        } catch (Exception e) {
            ctx.sessionAttribute("errmsg", "* Invalid order");
            e.printStackTrace();
            ctx.redirect(Path.Web.INDEX);
        }
    }

    public static void handleRemoveBasketPost(Context ctx)
    {
        User user;
        int id;

        try {
            user = ctx.sessionAttribute("user");
            if (user == null) {
                ctx.sessionAttribute("loginredirect", Path.Web.ORDERS);
                ctx.redirect(Path.Web.LOGIN);
                return;
            }
            id = Integer.decode(ctx.pathParam("id"));
            List<Order> basket = ctx.sessionAttribute("basket");
            assert basket != null;
            double subtotal = ctx.sessionAttribute("subtotal");
            Order o = basket.get(id);
            subtotal -= o.getPrice()*o.getCount();
            basket.remove(id);
            ctx.sessionAttribute("subtotal", subtotal);
            ctx.redirect(Path.Web.BASKET);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }
}
