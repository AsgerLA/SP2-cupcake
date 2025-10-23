package app.persistence;

import app.Server;
import app.entities.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserMapper {

    private static byte[] genSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(String password, byte[] salt)
            throws Exception
    {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    public static User login(String email, String password)
    {
        User user = null;

        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT users.id, users.email, users.password, users.salt, users.admin, users.balance FROM users WHERE users.email=?";

        try (Connection conn = Server.db.getConnection()){
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                byte[] inHash = rs.getBytes("password");
                byte[] inSalt = rs.getBytes("salt");
                byte[] hash = hashPassword(password, inSalt);
                if (Arrays.equals(hash, inHash)) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getDouble("balance"),
                            rs.getBoolean("admin") );
                }
            }
            assert !rs.next();

        } catch (Exception e) {
            System.err.println(e);
        }

        return user;
    }

    public static boolean register(String email, String password)
    {
        PreparedStatement ps;
        ResultSet rs;
        String sqlQuery = "SELECT users.id FROM users WHERE users.email=?";
        String sqlUpdate = "INSERT INTO users (email, password, salt, balance, admin) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Server.db.getConnection()){
            ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (!rs.next()) {
                byte[] salt = genSalt();
                byte[] hash = hashPassword(password, salt);
                ps = conn.prepareStatement(sqlUpdate);
                ps.setString(1, email);
                ps.setBytes(2, hash);
                ps.setBytes(3, salt);
                ps.setDouble(4, 0.0);
                ps.setBoolean(5, false);
                return (ps.executeUpdate() > 0);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return false;
    }

    public static List<User> getUsers()
    {
        ResultSet rs;
        String sql = "SELECT users.id, users.email, users.balance, users.admin FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = Server.db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getDouble("balance"),
                        rs.getBoolean("admin")));
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return users;
    }
}
