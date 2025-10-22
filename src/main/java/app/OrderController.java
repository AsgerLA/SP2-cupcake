package app;

import app.entities.User;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

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
        ctx.status(HttpStatus.NOT_IMPLEMENTED);
    }

    public static void handleRemoveOrderPost(Context ctx)
    {
        ctx.status(HttpStatus.NOT_IMPLEMENTED);
    }
}
