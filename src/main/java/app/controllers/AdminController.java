package app.controllers;

import app.Path;
import app.Server;
import app.entities.User;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class AdminController {
    public static void addRoutes(Javalin app)
    {
        app.before(Path.Web.ADMIN_BEFORE, AdminController::ensureAdmin);
        app.get(Path.Web.ADMIN, AdminController::serveAdminPage);
        app.get(Path.Web.ADMIN_ORDERS, AdminController::serveAdminOrdersPage);
        app.get(Path.Web.ADMIN_SPEC_ORDERS, AdminController::serveAdminSpecOrdersPage);
        app.post(Path.Web.ADMIN_REMOVE_ORDER, AdminController::handleRemovePost);
        app.post(Path.Web.ADMIN_REMOVE_USER, AdminController::handleUserDeletePost);
        app.post(Path.Web.ADMIN_ADD_CUPCAKE, AdminController::handleAddCupcakePost);
        app.post(Path.Web.ADMIN_DEL_TOPPING, AdminController::handleDelToppingPost);
        app.post(Path.Web.ADMIN_DEL_BOTTOM, AdminController::handleDelBottomPost);

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

    public static void handleAddCupcakePost(Context ctx)
    {
        String name;
        double price;
        String type;
        boolean res = false;

        try {
            name = ctx.formParam("name");
            price = Double.parseDouble(ctx.formParam("price"));
            type = ctx.formParam("type");
            if (type.equals("topping")) {
                res = OrderMapper.addTopping(name, price);
                Server.AppData.toppings = OrderMapper.getToppings();
            } else if (type.equals("bottom")) {
                res = OrderMapper.addBottom(name, price);
                Server.AppData.bottoms = OrderMapper.getBottoms();
            }
            if (!res)
                ctx.sessionAttribute("errmsg", "Could not add cupcake!");
            ctx.redirect(Path.Web.ADMIN);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }

    public static void handleDelToppingPost(Context ctx)
    {
        int id;

        try {
            id = Integer.parseInt(ctx.pathParam("id"));
            if (id <= 0)
                throw new Exception("bad id");
            if (!OrderMapper.delTopping(id))
                ctx.sessionAttribute("errmsg", "Could not delete topping");
            else
                Server.AppData.toppings = OrderMapper.getToppings();
            ctx.redirect(Path.Web.ADMIN);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }

    public static void handleDelBottomPost(Context ctx)
    {
        int id;

        try {
            id = Integer.parseInt(ctx.pathParam("id"));
            if (id <= 0)
                throw new Exception("bad id");
            if (!OrderMapper.delBottom(id))
                ctx.sessionAttribute("errmsg", "Could not delete bottom");
            else
                Server.AppData.bottoms = OrderMapper.getBottoms();
            ctx.redirect(Path.Web.ADMIN);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }

}
