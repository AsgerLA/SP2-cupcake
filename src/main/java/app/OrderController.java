package app;

import app.entities.Order;
import app.entities.User;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;

public class OrderController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.ORDERS, OrderController::serveOrdersPage);
        app.post(Path.Web.ORDER, OrderController::handleOrderPost);
        app.post(Path.Web.REMOVE_ORDER, OrderController::handleRemoveOrderPost);
    }

    public static void serveOrdersPage(Context ctx)
    {
        User user;

        user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.sessionAttribute("loginredirect", Path.Web.ORDERS);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        ctx.attribute("user", user);
        ctx.attribute("orders", OrderMapper.getUserOrders(user.getId()));

        ctx.render(Path.Template.ORDERS);
    }

    public static void handleOrderPost(Context ctx)
    {
        User user;
        List<Order> basket;
        double subtotal;

        try {
            user = ctx.sessionAttribute("user");
            if (user == null) {
                ctx.sessionAttribute("loginredirect", Path.Web.BASKET);
                ctx.redirect(Path.Web.LOGIN);
                return;
            }
            subtotal = ctx.sessionAttribute("subtotal");
            if (subtotal > user.getBalance()) {
                ctx.sessionAttribute("errmsg", "* insufficient funds");
                ctx.redirect(Path.Web.BASKET);
                return;
            }
            basket = ctx.sessionAttribute("basket");
            assert basket != null;
            OrderMapper.addUserOrders(user.getId(), basket);
            basket.clear();
            ctx.redirect(Path.Web.BASKET);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }

    public static void handleRemoveOrderPost(Context ctx)
    {
        ctx.status(HttpStatus.NOT_IMPLEMENTED);
    }
}
