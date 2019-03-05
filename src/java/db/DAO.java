package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.NamingException;

public class DAO {

    private final String table_name;

    public final HashMap<String, String> column;
    private final String insert_query;
    private final String update_query;
    private final String delete_query;

    public DAO(String table_name, HashMap<String, String> column) {
        this.table_name = table_name;
        this.column = column;
        this.insert_query = insertQueryString(table_name, column);
        this.update_query = updateQueryString(table_name, column);
        this.delete_query = "";
    }

    public void insert(HashMap<String, String[]> HTML_values) throws NamingException, SQLException {
        PreparedStatement stmt = DBHandler.getPreparedStatement(insert_query);
        int i = 1;
        for (String name : column.keySet()) {
            System.out.println("!!!" + name);
            TypeMapping.set(stmt, i, column.get(name), HTML_values.get(name)[0]);
            i++;
        }
        stmt.executeUpdate();
    }

    public String getTableName() {
        return table_name;
    }

    private static String insertQueryString(String table_name, HashMap<String, String> column) {
        String insertQuery = "INSERT INTO \"" + table_name + "\" VALUES (";
        for (int i = 0; i < column.size() - 1; i++) {
            insertQuery += "?, ";
        }
        return insertQuery + "?)";
    }

    private static String updateQueryString(String table_name, HashMap<String, String> column) {
        String update = "INSERT INTO \"" + table_name + "\" VALUES (";
        for (int i = 0; i < column.size() - 1; i++) {
            update += "?, ";
        }
        return update + "?)";
    }

}
