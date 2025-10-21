package app;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class OrderController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.ORDERS, OrderController::serveOrdersPage);
        app.get(Path.Web.ORDER, OrderController::handleOrderPost);
        app.get(Path.Web.REMOVE_ORDER, OrderController::handleRemoveOrderPost);
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
