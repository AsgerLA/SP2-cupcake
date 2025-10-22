package app.persistence;

import app.Server;
import app.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class OrderMapper {

    public static List<Order> getUserOrders(int userId)
    {
        List<Order> orders = new ArrayList<>();
        ResultSet rs;
        String sql = "SELECT orders.id, orders.price, orders.count, toppings.name, bottoms.name FROM orders JOIN toppings ON orders.top_id=toppings.top_id JOIN bottoms ON orders.bot_id=bottoms.bot_id WHERE orders.user_id=?";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("orders.id"),
                        rs.getString("toppings.name"),
                        rs.getString("bottoms.name"),
                        rs.getInt("orders.count"),
                        rs.getDouble("orders.price")));
            }

        } catch (Exception e) {
            System.err.println(e);
        }

        return orders;
    }

    public static boolean addUserOrder(int userId, int topId, int botId, int count, double price)
    {
        String sql = "INSERT INTO orders (user_id, top_id, bot_id, count, price) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, topId);
            ps.setInt(3, botId);
            ps.setInt(4, count);
            ps.setDouble(5, price);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }

    public static List<Topping> getToppings()
    {
        List<Topping> toppings = new ArrayList<>();
        ResultSet rs;
        String sql = "SELECT toppings.id, toppings.name, toppings.price FROM toppings";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            rs = ps.executeQuery();
            while (rs.next()) {
                toppings.add(new Topping(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
            }

        } catch (Exception e) {
            System.err.println(e);
        }

        return toppings;
    }

    public static List<Bottom> getBottoms()
    {
        List<Bottom> bottoms = new ArrayList<>();
        ResultSet rs;
        String sql = "SELECT bottoms.id, bottoms.name, bottoms.price FROM bottoms";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            rs = ps.executeQuery();
            while (rs.next()) {
                bottoms.add(new Bottom(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return bottoms;
    }
}
