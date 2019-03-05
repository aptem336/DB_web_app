package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {

    public final static HashMap<String, DAO> TABLES = new HashMap<>();

    static {
        try (Connection connection = getConnection(); ResultSet table_names = connection.getMetaData().getTables("postgres", "public", "%", new String[]{"TABLE"})) {
            while (table_names.next()) {
                String table_name = table_names.getString("TABLE_NAME");
                ResultSetMetaData rsmd = connection.createStatement().executeQuery("SELECT * FROM \"" + table_name + "\"").getMetaData();
                HashMap<String, String> column = new HashMap<>();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    column.put(rsmd.getColumnName(i + 1), rsmd.getColumnTypeName(i + 1));
                }
                TABLES.put(table_name, new DAO(table_name, column));
            }
        } catch (NamingException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static PreparedStatement getPreparedStatement(String query) throws NamingException, SQLException {
        return getConnection().prepareStatement(query);
    }

    public static Connection getConnection() throws NamingException, SQLException {
        InitialContext initContext = new InitialContext();
        DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/sql");
        return dataSource.getConnection();
    }
}
