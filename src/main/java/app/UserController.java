package app;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserController {
    public static void addRoutes(Javalin app)
    {
        app.get(Path.Web.INDEX, UserController::serveIndexPage);
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
}
