package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import javax.naming.NamingException;

public class DBHandler {

    public final static LinkedHashMap<String, TableHandler> TABLE_HANDLERS = new LinkedHashMap<>();

    public static void init() {
        try {
            Connection connection = getConnection();
            DatabaseMetaData databaseMD = connection.getMetaData();
            try (ResultSet table_names = databaseMD.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (table_names.next()) {
                    String table_name = table_names.getString("TABLE_NAME");
                    LinkedHashMap<String, Column> columns;
                    LinkedHashSet<String> pk_columns;
                    LinkedHashMap<String, String> fk_columns;
                    ArrayList<LinkedHashMap<String, String>> data;
                    try (ResultSet dataRS = connection.createStatement().executeQuery("SELECT * FROM \"" + table_name + "\"")) {
                        ResultSetMetaData dataMD = dataRS.getMetaData();
                        columns = new LinkedHashMap<>();
                        for (int i = 0; i < dataMD.getColumnCount(); i++) {
                            columns.put(dataMD.getColumnName(i + 1), new Column(dataMD.getColumnType(i + 1), dataMD.isAutoIncrement(i + 1), dataMD.isNullable(i + 1) == 1));
                        }
                        try (ResultSet pkRS = databaseMD.getPrimaryKeys(null, null, table_name)) {
                            pk_columns = new LinkedHashSet<>();
                            while (pkRS.next()) {
                                pk_columns.add(pkRS.getString("COLUMN_NAME"));
                            }
                        }
                        try (ResultSet fkRS = databaseMD.getImportedKeys(null, null, table_name)) {
                            fk_columns = new LinkedHashMap<>();
                            while (fkRS.next()) {
                                fk_columns.put(fkRS.getString("FKCOLUMN_NAME"), fkRS.getString("PKTABLE_NAME"));
                            }
                        }
                        data = new ArrayList<>();
                        while (dataRS.next()) {
                            LinkedHashMap<String, String> row = new LinkedHashMap<>();
                            for (String name : columns.keySet()) {
                                row.put(name, dataRS.getString(name));
                            }
                            data.add(row);
                        }
                    }
                    TABLE_HANDLERS.put(table_name, new TableHandler(table_name, columns, pk_columns, fk_columns, data));
                }
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

    private static Connection CONNECTION;

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            if (CONNECTION == null) {
                CONNECTION = DriverManager.getConnection("jdbc:postgresql://localhost:5432/library", "postgres", "39547710");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return CONNECTION;
    }

    public static void destroy() {
        try {
            CONNECTION.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
