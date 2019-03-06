package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import javax.naming.NamingException;

public class TableMapping {

    private final String[] pk_names;
    private final HashMap<String, String> colums;
    private final HashMap<String[], HashMap<String, String>> data;
    private final String insert_query;
    private final String update_query;
    private final String delete_query;
    private final MessageFormat DATA_FORMAT;
    private final MessageFormat INPUT_FORMAT;

    public TableMapping(String table_name, String[] pk_names, HashMap<String, String> columns, HashMap<String[], HashMap<String, String>> data) {
        this.pk_names = pk_names;
        this.colums = columns;
        this.data = data;
        this.insert_query = insertQuery(table_name, columns);
        this.update_query = updateQuery(table_name, columns);
        this.delete_query = deleteQuery(table_name, pk_names);
        this.DATA_FORMAT = DATA_FORMAT(columns);
        this.INPUT_FORMAT = INPUT_FORMAT(columns, DATA_FORMAT);
    }

    public String insert(HashMap<String, String[]> parameters) throws NamingException, SQLException {
        PreparedStatement stmt = DBHandler.getPreparedStatement(insert_query);
        String[] pk_values = new String[0];
        for (String pk_name : pk_names) {
            pk_values = Arrays.copyOf(pk_values, pk_values.length + 1);
            pk_values[pk_values.length - 1] = parameters.get(pk_name)[0];
        }
        HashMap<String, String> row = new HashMap<>();
        int i = 1;
        for (String name : colums.keySet()) {
            String value = parameters.get(name)[0];
            if (colums.get(name).equals("serial") && value.equals("")) {
                ResultSet nextVal = DBHandler.getConnection().createStatement().executeQuery(String.format("SELECT nextVal('%s')", name));
                nextVal.next();
                value = nextVal.getString("nextVal");
            }
            setIntoStmt(stmt, i, colums.get(name), value);
            row.put(name, value);
            i++;
        }
        stmt.executeUpdate();
        data.put(pk_values, row);
        return buildHTMLData();
    }

    public String buildHTMLData() {
        String HTMLData = DATA_FORMAT.format(colums.keySet().toArray());
        for (HashMap<String, String> row : data.values()) {
            HTMLData += DATA_FORMAT.format(row.values().toArray());
        }
        return HTMLData;
    }

    public String buildHTMLInput(int i) {
        String HTMLInput = DATA_FORMAT.format(colums.keySet().toArray());
        if (i != -1) {
            HTMLInput += INPUT_FORMAT.format(colums.keySet().toArray());
        } else {
            HTMLInput += INPUT_FORMAT.format(colums.keySet().toArray());
        }
        return HTMLInput;
    }

    private static String insertQuery(String table_name, HashMap<String, String> column) {
        String insertQuery = "INSERT INTO " + table_name + " (";
        String column_pattern = "{0}";
        for (int i = 1; i < column.size(); i++) {
            column_pattern += String.format(", {%d}", i);
        }
        insertQuery += new MessageFormat(column_pattern).format(column.keySet().toArray());
        insertQuery += ") VALUES (";
        for (int i = 0; i < column.size() - 1; i++) {
            insertQuery += "?, ";
        }
        insertQuery += "?)";
        return insertQuery;
    }

    private static String updateQuery(String table_name, HashMap<String, String> column) {
        for (int i = 0; i < column.size() - 1; i++) {

        }
        return "";
    }

    private String deleteQuery(String table_name, String[] pk_names) {
        for (int i = 1; i < pk_names.length; i++) {

        }
        return "";
    }

    private static MessageFormat DATA_FORMAT(HashMap<String, String> columns) {
        String DATA_FORMAT = "\t\t<tr>\n";
        int i = 0;
        for (String name : columns.keySet()) {
            DATA_FORMAT += String.format("\t\t\t<td class=\"%s\">{%d}</td>\n", name, i);
            i++;
        }
        DATA_FORMAT += "\t\t</tr>\n";
        return new MessageFormat(DATA_FORMAT);
    }

    private static MessageFormat INPUT_FORMAT(HashMap<String, String> columns, MessageFormat DATA_FORMAT) {
        String[] INPUT_FORMAT = new String[0];
        int i = 0;
        for (String name : columns.keySet()) {
            INPUT_FORMAT = Arrays.copyOf(INPUT_FORMAT, INPUT_FORMAT.length + 1);
            INPUT_FORMAT[INPUT_FORMAT.length - 1] = String.format(INPUT, name, TYPE_MAPPING.get(columns.get(name)), "{" + i + "}", columns.get(name).equals("serial") ? "" : "required");
            i++;
        }
        return new MessageFormat(DATA_FORMAT.format(INPUT_FORMAT));
    }

    public static void setIntoStmt(PreparedStatement stmt, int index, String type, String value) throws SQLException {
        switch (type) {
            case "serial":
            case "int4":
            case "int8":
                stmt.setInt(index, Integer.parseInt(value));
                break;
            case "text":
                stmt.setString(index, value);
                break;
            case "date":
                stmt.setDate(index, Date.valueOf(value));
        }
    }

    private final static String INPUT = "<input name=\"%s\" type=\"%s\" value=\"%s\" %s>";
    public final static HashMap<String, String> TYPE_MAPPING = new HashMap<>();

    static {
        TYPE_MAPPING.put("serial", "number");
        TYPE_MAPPING.put("int4", "number");
        TYPE_MAPPING.put("int8", "number");
        TYPE_MAPPING.put("text", "text");
        TYPE_MAPPING.put("date", "date");
    }

}
