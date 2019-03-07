package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {

    public final static HashMap<String, TableHandler> TABLE_HANDLERS = new HashMap<>();

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
                ArrayList<HashMap<String, String>> data_table = new ArrayList<>();
                ResultSet pkRS = database_MD.getPrimaryKeys(null, null, table_name);
                Set<String> pk_names = new HashSet<>();
                while (pkRS.next()) {
                    pk_names.add(pkRS.getString("COLUMN_NAME"));
                }
                while (dataRS.next()) {
                    HashMap<String, String> data_row = new HashMap<>();
                    for (String name : columns.keySet()) {
                        data_row.put(name, dataRS.getString(name));
                    }
                    data_table.add(data_row);
                }
                TABLE_HANDLERS.put(table_name, new TableHandler(new HTMLBuilder(columns), new SQLBuilder(), data_table));
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

    static void apply(HashMap<String, String[]> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
