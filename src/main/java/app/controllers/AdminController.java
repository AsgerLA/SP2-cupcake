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
        app.before(Path.Web.ADMIN_BEFORE, AdminController::ensureAdmin);
        app.get(Path.Web.ADMIN, AdminController::serveAdminPage);
        app.get(Path.Web.ADMIN_ORDERS, AdminController::serveAdminOrdersPage);
        app.get(Path.Web.ADMIN_SPEC_ORDERS, AdminController::serveAdminSpecOrdersPage);
        app.post(Path.Web.ADMIN_REMOVE_ORDER, AdminController::handleRemovePost);
        app.post(Path.Web.ADMIN_REMOVE_USER, AdminController::handleUserDeletePost);

    }

    public static void ensureAdmin(Context ctx)
    {
        User user;

        user = ctx.sessionAttribute("user");
        if (user == null || !user.isAdmin()) {
            ctx.sessionAttribute("errmsg", "unauthorized");
            ctx.sessionAttribute("loginredirect", Path.Web.ADMIN);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }

        ctx.attribute("user", user);
    }

    public static void serveAdminPage(Context ctx)
    {
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
        User admin = ctx.sessionAttribute("user");

        if (admin == null) {
            ctx.sessionAttribute("loginredirect", Path.Web.ADMIN_SPEC_ORDERS);
            ctx.redirect(Path.Web.LOGIN);
            return;
        }

        //Får id'et fra url'en og tager det med videre
        int userId = Integer.parseInt(ctx.pathParam("id"));

        User selectedUser = UserMapper.getUserById(userId);

        ctx.attribute("user", selectedUser);
        ctx.attribute("orders", OrderMapper.getUserOrders(userId));


        ctx.render(Path.Template.ADMIN_SPEC_ORDERS);
    }

    public static void handleRemovePost(Context ctx)
    {
        int orderId = Integer.parseInt(ctx.pathParam("id"));
        OrderMapper.delOrder(orderId);
        ctx.redirect(Path.Web.ADMIN_ORDERS);
    }

    public static void handleUserDeletePost(Context ctx){
        int userId = Integer.parseInt(ctx.pathParam("id"));
        UserMapper.delUser(userId);
        ctx.redirect(Path.Web.ADMIN);

    }


}
