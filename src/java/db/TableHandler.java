package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.naming.NamingException;

public class TableHandler {

    private final LinkedHashMap<String, Column> columns;
    private final ArrayList<LinkedHashMap<String, String>> data;
    private final HTMLBuilder HTMLBuilder;
    private final SQLBuilder SQLBuilder;

    public TableHandler(String table_name, LinkedHashMap<String, Column> columns, ArrayList<LinkedHashMap<String, String>> data) throws NamingException, SQLException {
        this.columns = columns;
        this.data = data;
        this.HTMLBuilder = new HTMLBuilder(columns);
        this.SQLBuilder = new SQLBuilder(table_name, columns);
        for (String column_name : columns.keySet()) {
            if (columns.get(column_name).isPK) {
                pk_column_name = column_name;
            }
        }
    }

    public void apply(HashMap<String, String[]> parameters) throws SQLException, NamingException {
        int i = 0;
        for (String index_string : parameters.get("index")) {
            LinkedHashMap<String, String> values = new LinkedHashMap<>();
            for (String column_name : columns.keySet()) {
                Column column = columns.get(column_name);
                if (parameters.get(column_name)[i].equals("")) {
                    if (column.isAutoIncrement) {
                        try (ResultSet rs = DBHandler.getConnection().createStatement().executeQuery(String.format("SELECT nextVal('%s')", column_name))) {
                            rs.next();
                            values.put(column_name, rs.getString(1));
                        }
                    } else if (column.isNullable) {
                        values.put(column_name, null);
                    }
                } else {
                    values.put(column_name, parameters.get(column_name)[i]);
                }
            }
            int index = Integer.parseInt(index_string);
            if (index == data.size()) {
                SQLBuilder.insert(values);
                data.add(values);
            } else {
                SQLBuilder.update(values, getPKValues(index));
                data.set(index, values);
            }
            i++;
        }
    }

    public void delete(HashMap<String, String[]> parameters) throws SQLException, NamingException {
        int index = Integer.parseInt(parameters.get("index")[0]);
        SQLBuilder.delete(getPKValues(index));
        data.remove(index);
    }

    private LinkedHashMap<String, String> getPKValues(int index) {
        LinkedHashMap<String, String> pk_values = new LinkedHashMap<>();
        for (String column_name : columns.keySet()) {
            if (columns.get(column_name).isPK) {
                pk_values.put(column_name, data.get(index).get(column_name));
            }
        }
        return pk_values;
    }

    public String getHTMLTable() {
        return HTMLBuilder.buildHTMLTable(data);
    }

    private String pk_column_name;
    private String showing_column_name;

    public String getAvailableOptions(String value) {
        String options = "";
        for (LinkedHashMap<String, String> row : data) {
            options += String.format("<option value=%s %s>%s", row.get(pk_column_name), row.get(pk_column_name).equals(value) ? "selected" : "", row.get(showing_column_name));
        }
        return options;
    }

    public void setShowing_column_name(String showing_column_name) {
        this.showing_column_name = showing_column_name;
    }
}
