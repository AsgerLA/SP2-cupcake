package app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.sql.Connection;

public class Database  {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public Database(String username, String password, String url)
            throws SQLException
    {
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public Connection getConnection()
            throws Exception
    {
        return ds.getConnection();
    }

}
