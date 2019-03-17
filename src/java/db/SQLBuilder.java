package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import javax.naming.NamingException;

public class SQLBuilder {

    private final LinkedHashMap<String, Column> columns;
    private final String insertQuery;
    private final String updateQuery;
    private final String deleteQuery;

    public SQLBuilder(String table_name, LinkedHashMap<String, Column> columns) throws NamingException, SQLException {
        this.columns = columns;
        insertQuery = "INSERT INTO \"" + table_name + "\" " + buildInsertQuery(columns.keySet().toArray());
        updateQuery = "UPDATE \"" + table_name + "\" SET " + buildUpdateQuery(columns.keySet().toArray()) + " " + buildDeleteQuery(columns);
        deleteQuery = "DELETE FROM \"" + table_name + "\" " + buildDeleteQuery(columns);

    }

    public void insert(LinkedHashMap<String, String> values) throws SQLException, NamingException {
        try (Connection connection = DBHandler.getConnection(); PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            setObjects(1, insertStatement, columns, values);
            insertStatement.executeUpdate();
        }
    }

    public void update(LinkedHashMap<String, String> values, LinkedHashMap<String, String> pk_values) throws SQLException, NamingException {
        try (Connection connection = DBHandler.getConnection(); PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            setObjects(1, updateStatement, columns, values);
            setObjects(values.size() + 1, updateStatement, columns, pk_values);
            updateStatement.executeUpdate();
        }
    }

    public void delete(LinkedHashMap<String, String> pk_values) throws SQLException, NamingException {
        try (Connection connection = DBHandler.getConnection(); PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            setObjects(1, deleteStatement, columns, pk_values);
            deleteStatement.executeUpdate();
        }
    }

    private static String buildInsertQuery(Object[] column_names) {
        String insertQuery = "(";
        String format = "{0}";
        for (int i = 1; i < column_names.length; i++) {
            format += ", {" + i + "}";
        }
        insertQuery += new MessageFormat(format).format(column_names);
        insertQuery += ") VALUES (?";
        for (int i = 1; i < column_names.length; i++) {
            insertQuery += ", ?";
        }
        insertQuery += ")";
        return insertQuery;
    }

    private static String buildUpdateQuery(Object[] column_names) {
        String updateQuery = "";
        String format = "{0}=?";
        for (int i = 1; i < column_names.length; i++) {
            format += ", {" + i + "}=?";
        }
        updateQuery += new MessageFormat(format).format(column_names);
        return updateQuery;
    }

    private static String buildDeleteQuery(LinkedHashMap<String, Column> columns) {
        LinkedHashSet<String> pk = new LinkedHashSet<>();
        for (String name : columns.keySet()) {
            if (columns.get(name).isPK) {
                pk.add(name);
            }
        }
        String deleteQuery = "WHERE (";
        String format = "{0}=?";
        for (int i = 1; i < pk.size(); i++) {
            format += " AND {" + i + "}=?";
        }
        deleteQuery += new MessageFormat(format).format(pk.toArray());
        deleteQuery += ")";
        return deleteQuery;
    }

    private static void setObjects(int i, PreparedStatement stmt, LinkedHashMap<String, Column> columns, LinkedHashMap<String, String> values) throws SQLException {
        for (String column_names : values.keySet()) {
            stmt.setObject(i, values.get(column_names), columns.get(column_names).SQLType);
            i++;
        }
    }

}
