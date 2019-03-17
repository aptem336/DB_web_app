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

    static {
        try (Connection connection = getConnection()) {
            DatabaseMetaData databaseMD = connection.getMetaData();
            ResultSet table_names = databaseMD.getTables(null, null, "%", new String[]{"TABLE"});
            while (table_names.next()) {
                String table_name = table_names.getString("TABLE_NAME");
                ResultSet dataRS = connection.createStatement().executeQuery("SELECT * FROM \"" + table_name + "\"");
                ResultSetMetaData dataMD = dataRS.getMetaData();
                LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
                for (int i = 0; i < dataMD.getColumnCount(); i++) {
                    columns.put(dataMD.getColumnName(i + 1), new Column(dataMD.getColumnType(i + 1), dataMD.isAutoIncrement(i + 1), dataMD.isNullable(i + 1) == 1));
                }
                ResultSet pkRS = databaseMD.getPrimaryKeys(null, null, table_name);
                LinkedHashSet<String> pk_columns = new LinkedHashSet<>();
                while (pkRS.next()) {
                    pk_columns.add(pkRS.getString("COLUMN_NAME"));
                }
                ResultSet fkRS = databaseMD.getImportedKeys(null, null, table_name);
                LinkedHashMap<String, String> fk_columns = new LinkedHashMap<>();
                while (fkRS.next()) {
                    fk_columns.put(fkRS.getString("FKCOLUMN_NAME"), fkRS.getString("PKTABLE_NAME"));
                }
                ArrayList<LinkedHashMap<String, String>> data = new ArrayList<>();
                while (dataRS.next()) {
                    LinkedHashMap<String, String> row = new LinkedHashMap<>();
                    for (String name : columns.keySet()) {
                        row.put(name, dataRS.getString(name));
                    }
                    data.add(row);
                }
                TABLE_HANDLERS.put(table_name, new TableHandler(table_name, columns, pk_columns, fk_columns, data));
            }
            TABLE_HANDLERS.get("Специальность").setShowing_column_name("Название_специальности");
            TABLE_HANDLERS.get("Дисциплина").setShowing_column_name("Название_дисциплины");
            TABLE_HANDLERS.get("Книга").setShowing_column_name("Название");
            TABLE_HANDLERS.get("Цикл").setShowing_column_name("Наименование_цикла");
            TABLE_HANDLERS.get("Читатель").setShowing_column_name("ФИО");
        } catch (NamingException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Connection getConnection() throws NamingException, SQLException {
        InitialContext initContext = new InitialContext();
        DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/sql");
        return dataSource.getConnection();
    }
}
