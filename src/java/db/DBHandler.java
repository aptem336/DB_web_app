package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {

    public final static HashMap<String, TableMapping> TABLES = new HashMap<>();

    static {
        try (Connection connection = getConnection()) {
            DatabaseMetaData database_MD = connection.getMetaData();
            ResultSet table_names = database_MD.getTables(null, null, "%", new String[]{"TABLE"});
            while (table_names.next()) {
                String table_name = table_names.getString("TABLE_NAME");
                ResultSet dataRS = connection.createStatement().executeQuery("SELECT * FROM \"" + table_name + "\"");
                ResultSetMetaData dataMD = dataRS.getMetaData();
                HashMap<String, String> columns = new HashMap<>();
                for (int i = 0; i < dataMD.getColumnCount(); i++) {
                    columns.put(dataMD.getColumnName(i + 1), dataMD.getColumnTypeName(i + 1));
                }
                HashMap<String[], HashMap<String, String>> data = new HashMap<>();
                ResultSet pk = database_MD.getPrimaryKeys(null, null, table_name);
                String[] pk_names = new String[0];
                while (pk.next()) {
                    pk_names = Arrays.copyOf(pk_names, pk_names.length + 1);
                    pk_names[pk_names.length - 1] = pk.getString("COLUMN_NAME");
                }
                while (dataRS.next()) {
                    HashMap<String, String> row = new HashMap<>();
                    String[] pk_values = new String[0];
                    for (String pk_name : pk_names) {
                        pk_values = Arrays.copyOf(pk_values, pk_values.length + 1);
                        pk_values[pk_values.length - 1] = dataRS.getString(pk_name);
                    }
                    for (String name : columns.keySet()) {
                        row.put(name, dataRS.getString(name));
                    }
                    data.put(pk_values, row);
                }
                TABLES.put(table_name, new TableMapping(table_name, pk_names, columns, data));
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
