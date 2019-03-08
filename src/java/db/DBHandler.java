package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {

    public final static LinkedHashMap<String, TableHandler> TABLE_HANDLERS = new LinkedHashMap<>();

    //TO-DO:
    //Соединение - синглтон?
    static {
        try (Connection connection = getConnection()) {
            DatabaseMetaData database_MD = connection.getMetaData();
            ResultSet table_names = database_MD.getTables(null, null, "%", new String[]{"TABLE"});
            while (table_names.next()) {
                String table_name = table_names.getString("TABLE_NAME");
                ResultSet dataRS = connection.createStatement().executeQuery("SELECT * FROM \"" + table_name + "\"");
                ResultSetMetaData dataMD = dataRS.getMetaData();
                LinkedHashMap<String, String> columns = new LinkedHashMap<>();
                for (int i = 0; i < dataMD.getColumnCount(); i++) {
                    columns.put(dataMD.getColumnName(i + 1), dataMD.getColumnTypeName(i + 1));
                }
                ArrayList<LinkedHashMap<String, String>> table = new ArrayList<>();
                ResultSet pkRS = database_MD.getPrimaryKeys(null, null, table_name);
                LinkedHashSet<String> pk = new LinkedHashSet<>();
                while (pkRS.next()) {
                    pk.add(pkRS.getString("COLUMN_NAME"));
                }
                while (dataRS.next()) {
                    LinkedHashMap<String, String> row = new LinkedHashMap<>();
                    for (String name : columns.keySet()) {
                        row.put(name, dataRS.getString(name));
                    }
                    table.add(row);
                }
                TABLE_HANDLERS.put(table_name, new TableHandler(table_name, pk, columns, table));
            }
        } catch (NamingException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static Connection getConnection() throws NamingException, SQLException {
        InitialContext initContext = new InitialContext();
        DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/sql");
        return dataSource.getConnection();
    }
}
