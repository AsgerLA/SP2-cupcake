package app;

import app.entities.Order;
import app.entities.User;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Objects;

public class OrderController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.ORDERS, OrderController::serveOrdersPage);
        app.post(Path.Web.ORDER, OrderController::handleOrderPost);
        app.post(Path.Web.REMOVE_ORDER, OrderController::handleRemoveOrderPost);
    }

    public static void serveOrdersPage(Context ctx)
    {
        ctx.status(HttpStatus.NOT_IMPLEMENTED);
    }

    public static void handleOrderPost(Context ctx)
    {
        User user;
        List<Order> basket;

        try {
            user = ctx.sessionAttribute("user");
            if (user == null) {
                ctx.redirect(Path.Web.LOGIN);
                return;
            }
            basket = ctx.sessionAttribute("basket");
            assert basket != null;
            System.out.println("TODO: put basket into DB orders table");
            basket.clear();
            ctx.redirect(Path.Web.INDEX);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
        ctx.status(HttpStatus.NOT_IMPLEMENTED);
    }

    public static void handleRemoveOrderPost(Context ctx)
    {
        ctx.status(HttpStatus.NOT_IMPLEMENTED);
    }
}
