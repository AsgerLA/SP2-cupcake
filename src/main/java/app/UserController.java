package app;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.INDEX, UserController::serveIndexPage);
    }

    public static void serveIndexPage(Context ctx)
    {
        ctx.render(Path.Template.INDEX);
    }
}
