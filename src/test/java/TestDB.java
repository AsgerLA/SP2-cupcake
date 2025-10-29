import app.Server;
import app.persistence.Database;
import app.persistence.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class TestDB {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/cupcake?currentSchema=test";

    @BeforeAll
    public static void beforeAll()
    {
        try {
            Server.db = new Database(USERNAME, PASSWORD, URL);
            try (Connection testConnection = Server.db.getConnection()) {
                try (Statement stmt = testConnection.createStatement()) {
                    // The test schema is already created, so we only need to delete/create test tables
                    stmt.execute("DROP TABLE IF EXISTS test.orders");
                    stmt.execute("DROP TABLE IF EXISTS test.toppings");
                    stmt.execute("DROP TABLE IF EXISTS test.bottoms");
                    stmt.execute("DROP TABLE IF EXISTS test.users");

                    stmt.execute("DROP SEQUENCE IF EXISTS test.users_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.orders_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.toppings_id_seq CASCADE;");
                    stmt.execute("DROP SEQUENCE IF EXISTS test.bottoms_id_seq CASCADE;");

                    // Create tables as copy of original public schema structure
                    stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.toppings AS (SELECT * from public.toppings) WITH NO DATA");
                    stmt.execute("CREATE TABLE test.bottoms AS (SELECT * from public.bottoms) WITH NO DATA");

                    // Create sequences for auto generating id's
                    stmt.execute("CREATE SEQUENCE test.users_id_seq");
                    stmt.execute("ALTER TABLE test.users ALTER COLUMN id SET DEFAULT nextval('test.users_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.orders_id_seq");
                    stmt.execute("ALTER TABLE test.orders ALTER COLUMN id SET DEFAULT nextval('test.orders_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.toppings_id_seq");
                    stmt.execute("ALTER TABLE test.toppings ALTER COLUMN id SET DEFAULT nextval('test.toppings_id_seq')");
                    stmt.execute("CREATE SEQUENCE test.bottoms_id_seq");
                    stmt.execute("ALTER TABLE test.bottoms ALTER COLUMN id SET DEFAULT nextval('test.bottoms_id_seq')");
                }
            }
        } catch (Exception e) {
            fail("Failed to connect to db");
        }
    }

    public static void setUp()
    {
        try (Connection testConnection = Server.db.getConnection()) {
            try (Statement stmt = testConnection.createStatement() ) {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");
                stmt.execute("DELETE FROM test.toppings");
                stmt.execute("DELETE FROM test.bottoms");

                // Reset the sequence number
                stmt.execute("SELECT setval('test.users_id_seq', 1)");
                stmt.execute("SELECT setval('test.orders_id_seq', 1)");
                stmt.execute("SELECT setval('test.toppings_id_seq', 1)");
                stmt.execute("SELECT setval('test.bottoms_id_seq', 1)");

                // Insert rows
                stmt.execute("INSERT INTO test.bottoms (name, price) VALUES " +
                        "('Chocolate', 5.00), " +
                        "('Vanilla', 5.00), " +
                        "('Nutmeg', 5.00), " +
                        "('Pistacio', 6.00), " +
                        "('Almond', 7.00)");

                stmt.execute("INSERT INTO test.toppings (name, price) VALUES " +
                        "('Chocolate', 5.00)," +
                        "('Blueberry', 5.00), " +
                        "('Rasberry', 5.00), " +
                        "('Crispy', 6.00), " +
                        "('Strawberry', 6.00), " +
                        "('Rum/Raisin', 7.00), " +
                        "('Orange', 8.00), " +
                        "('Lemon', 8.00), " +
                        "('Blue cheese', 9.00)");
            }
        } catch (Exception e) {
            fail("Database setup failed");
        }
    }

    @BeforeEach
    public void beforeEach()
    {
        setUp();
    }

    @Test
    public void testConnection()
            throws Exception
    {
        Connection conn = Server.db.getConnection();
        assertNotNull(conn);
        conn.close();
    }




    @Test
    public void testLogin()
    {
        String email = "user@mail";
        String password = "password";
        assertNull(UserMapper.login(email, password));
        assertTrue(UserMapper.register(email, password));
        assertFalse(UserMapper.register(email, password));
        assertNotNull(UserMapper.login(email, password));
    }
}
