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
        String sql = "SELECT orders.id, orders.price, orders.count, toppings.name, bottoms.name FROM orders JOIN toppings ON orders.top_id=toppings.id JOIN bottoms ON orders.bot_id=bottoms.id WHERE orders.user_id=?";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt(1),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(3),
                        rs.getDouble(2)));
            }

        } catch (Exception e) {
            System.err.println(e);
        }

        return orders;
    }

    public static List<Order> getAllUserOrders()
    {
        List<Order> allOrders = new ArrayList<>();
        ResultSet rs;
        String sql = "SELECT orders.id, orders.price, orders.count, toppings.name, bottoms.name FROM orders JOIN toppings ON orders.top_id=toppings.id JOIN bottoms ON orders.bot_id=bottoms.id";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            //ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                allOrders.add(new Order(
                        rs.getInt(1),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(3),
                        rs.getDouble(2)
                        ));
            }

        } catch (Exception e) {
            System.err.println(e);
        }

        return allOrders;
    }

    public static boolean addUserOrders(int userId, List<Order> orders)
    {
        String sql = "INSERT INTO orders (user_id, top_id, bot_id, count, price) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Order o : orders) {
                ps.setInt(1, userId);
                ps.setInt(2, o.topId);
                ps.setInt(3, o.botId);
                ps.setInt(4, o.getCount());
                ps.setDouble(5, o.getPrice());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }

    public static boolean delOrder(int orderId)
    {
        String sql = "DELETE FROM orders WHERE orders.id=?";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
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

    public static boolean addTopping(String name, double price)
    {
        String sql = "INSERT INTO toppings (name, price) VALUES(?, ?)";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }

    public static boolean addBottom(String name, double price)
    {
        String sql = "INSERT INTO bottoms (name, price) VALUES(?, ?)";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }

    public static boolean delTopping(int id)
    {
        String sql = "DELETE FROM toppings WHERE toppings.id=?";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }

    public static boolean delBottom(int id)
    {
        String sql = "DELETE FROM bottoms WHERE bottoms.id=?";

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }
}
