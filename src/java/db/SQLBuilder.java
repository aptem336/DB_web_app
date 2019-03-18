package db;

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

    public SQLBuilder(String table_name, LinkedHashMap<String, Column> columns, LinkedHashSet<String> pk_columns) throws NamingException, SQLException {
        this.columns = columns;
        insertQuery = "INSERT INTO \"" + table_name + "\" " + buildInsertQuery(columns.keySet().toArray());
        String where = where(pk_columns);
        updateQuery = "UPDATE \"" + table_name + "\" SET " + buildUpdateQuery(columns.keySet().toArray()) + " " + where;
        deleteQuery = "DELETE FROM \"" + table_name + "\" " + where;
    }

    //формирование запросов как строк???
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

    private static String where(LinkedHashSet<String> pk_columns) {
        String deleteQuery = "WHERE (";
        String format = "{0}=?";
        for (int i = 1; i < pk_columns.size(); i++) {
            format += " AND {" + i + "}=?";
        }
        deleteQuery += new MessageFormat(format).format(pk_columns.toArray());
        deleteQuery += ")";
        return deleteQuery;
    }

    private static PreparedStatement insertStatement;
    private static PreparedStatement updateStatement;
    private static PreparedStatement deleteStatement;

    public void prepareStatements() throws SQLException, NamingException {
        if (insertStatement != null) {
            insertStatement.close();
        }
        if (updateStatement != null) {
            updateStatement.close();
        }
        if (deleteStatement != null) {
            deleteStatement.close();
        }
        insertStatement = DBHandler.getConnection().prepareStatement(insertQuery);
        updateStatement = DBHandler.getConnection().prepareStatement(updateQuery);
        deleteStatement = DBHandler.getConnection().prepareStatement(deleteQuery);
    }

    public void insert(LinkedHashMap<String, String> values) throws SQLException, NamingException {
        setObjects(1, insertStatement, columns, values);
        insertStatement.executeUpdate();
    }

    public void update(LinkedHashMap<String, String> values, LinkedHashMap<String, String> pk_values) throws SQLException, NamingException {
        setObjects(1, updateStatement, columns, values);
        setObjects(values.size() + 1, updateStatement, columns, pk_values);
        updateStatement.executeUpdate();
    }

    public void delete(LinkedHashMap<String, String> pk_values) throws SQLException, NamingException {
        setObjects(1, deleteStatement, columns, pk_values);
        deleteStatement.executeUpdate();
    }

    private static void setObjects(int i, PreparedStatement stmt, LinkedHashMap<String, Column> columns, LinkedHashMap<String, String> values) throws SQLException {
        for (String column_names : values.keySet()) {
            stmt.setObject(i, values.get(column_names), columns.get(column_names).SQLType);
            i++;
        }
    }

}
