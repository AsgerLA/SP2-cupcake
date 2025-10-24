package app.controllers;

import app.Path;
import app.Server;
import app.entities.User;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.ADMIN, AdminController::serveAdminPage);
        app.get(Path.Web.ADMIN_ORDERS, AdminController::serveAdminOrdersPage);
        app.get(Path.Web.ADMIN_SPEC_ORDERS, AdminController::serveAdminSpecOrdersPage);

    }
    public static void serveAdminPage(Context ctx)
    {
        User user;

        user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.sessionAttribute("loginredirect", Path.Web.ADMIN);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }

        ctx.attribute("user", user);
        ctx.attribute("toppings", Server.AppData.toppings);
        ctx.attribute("bottoms", Server.AppData.bottoms);

        ctx.attribute("users", UserMapper.getUsers());

        ctx.attribute("errmsg", ctx.sessionAttribute("errmsg"));
        ctx.render(Path.Template.ADMIN);
        ctx.sessionAttribute("errmsg", null);
    }
    public static void serveAdminOrdersPage(Context ctx)
    {
        User user;

        user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.sessionAttribute("loginredirect", Path.Web.ADMIN_ORDERS);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        //ctx.attribute("user", user);
        ctx.attribute("allOrders", OrderMapper.getAllUserOrders());

        ctx.render(Path.Template.ADMIN_ORDERS);
    }

    public static void serveAdminSpecOrdersPage(Context ctx)
    {
        User user;

        user = ctx.sessionAttribute("user");
        if (user == null) {
            ctx.sessionAttribute("loginredirect", Path.Web.ADMIN_SPEC_ORDERS);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }
        ctx.attribute("user", user);
        ctx.attribute("orders", OrderMapper.getUserOrders(user.getId()));

        ctx.render(Path.Template.ADMIN_SPEC_ORDERS);
    }

}
