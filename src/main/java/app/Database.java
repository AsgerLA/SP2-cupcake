package app;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Database  {

    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private static final int POOL_SIZE = 10;


    public Database(String username, String password, String url)
            throws SQLException
    {
        int i;

        connectionPool = new ArrayList<>(POOL_SIZE);
        for (i = 0; i < POOL_SIZE; ++i) {
            connectionPool.add(DriverManager.getConnection(url, username, password));
        }
    }

    public Connection connect()
            throws Exception
    {
        int loopcnt = 0;
        while (connectionPool.isEmpty()) {
            Thread.sleep(1);
            ++loopcnt;
            if (loopcnt > 100)
                throw new Exception("connection pool timeout");
            System.out.println("Database.connect(): loop");
        }

        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean close(Connection connection)
    {
        assert connection != null;
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

}
