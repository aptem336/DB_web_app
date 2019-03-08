package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import javax.naming.NamingException;

public class SQLBuilder {

    private final LinkedHashMap<String, String> columns;
    private final String insertQuery;
    private final String updateQuery;
    private final String deleteQuery;

    //разобраться с PStmt, открывать при переходе на таблицу?
    public SQLBuilder(String table_name, LinkedHashSet<String> pk, LinkedHashMap<String, String> columns) throws NamingException, SQLException {
        this.columns = columns;
        deleteQuery = "DELETE FROM \"" + table_name + "\" " + buildDeleteQuery(pk);
        updateQuery = "UPDATE \"" + table_name + "\" SET " + buildUpdateQuery(pk, columns) + " " + buildDeleteQuery(pk);
        insertQuery = "INSERT INTO \"" + table_name + "\" " + buildInsertQuery(columns);
    }

    public void insert(LinkedHashMap<String, String> row) throws SQLException, NamingException {
        PreparedStatement insertStatement = getPreparedStatement(insertQuery);
        JAVA_TYPE_MAPPING(insertStatement, columns, row);
        insertStatement.executeUpdate();
    }

    //не получается вставить в PStmt из-за уникальности ключей
    public void update(LinkedHashMap<String, String> row, LinkedHashMap<String, String> pk) throws SQLException, NamingException {
        PreparedStatement updateStatement = getPreparedStatement(updateQuery);
        JAVA_TYPE_MAPPING(updateStatement, columns, row);
        JAVA_TYPE_MAPPING(updateStatement, columns, pk);
        updateStatement.executeUpdate();
    }

    public void delete(LinkedHashMap<String, String> pk) throws SQLException, NamingException {
        PreparedStatement deleteStatement = getPreparedStatement(deleteQuery);
        JAVA_TYPE_MAPPING(deleteStatement, columns, pk);
        deleteStatement.executeUpdate();
    }

    public static PreparedStatement getPreparedStatement(String query) throws NamingException, SQLException {
        return DBHandler.getConnection().prepareStatement(query);
    }

    private static String buildInsertQuery(LinkedHashMap<String, String> columns) {
        String insertQuery = "(";
        String format = "{0}";
        for (int i = 1; i < columns.keySet().size(); i++) {
            format += ", {" + i + "}";
        }
        insertQuery += new MessageFormat(format).format(columns.keySet().toArray());
        insertQuery += ") VALUES (?";
        for (int i = 1; i < columns.keySet().size(); i++) {
            insertQuery += ", ?";
        }
        insertQuery += ")";
        return insertQuery;
    }

    private static String buildUpdateQuery(LinkedHashSet<String> pk, LinkedHashMap<String, String> columns) {
        String updateQuery = "";
        String format = "{0}=?";
        for (int i = 1; i < columns.keySet().size(); i++) {
            format += ", {" + i + "}=?";
        }
        updateQuery += new MessageFormat(format).format(columns.keySet().toArray());
        return updateQuery;
    }

    private static String buildDeleteQuery(LinkedHashSet<String> pk) {
        String deleteQuery = "WHERE (";
        String format = "{0}=?";
        for (int i = 1; i < pk.size(); i++) {
            format += " AND {" + i + "}=?";
        }
        deleteQuery += new MessageFormat(format).format(pk.toArray());
        deleteQuery += ")";
        return deleteQuery;
    }

    private static void JAVA_TYPE_MAPPING(PreparedStatement stmt, LinkedHashMap<String, String> columns, LinkedHashMap<String, String> values) throws SQLException {
        int i = 1;
        for (String name : values.keySet()) {
            switch (columns.get(name)) {
                case "serial":
                case "int4":
                case "int8":
                    stmt.setInt(i, Integer.parseInt(values.get(name)));
                    break;
                case "text":
                    stmt.setString(i, values.get(name));
                    break;
                case "date":
                    stmt.setDate(i, Date.valueOf(values.get(name)));
                    break;
            }
            i++;
        }
    }

}
