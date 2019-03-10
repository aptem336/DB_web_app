package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.naming.NamingException;

public class TableHandler {

    private final LinkedHashSet<String> pk;
    private final HashMap<String, String> columns;
    private final ArrayList<LinkedHashMap<String, String>> data;
    private final HTMLBuilder HTMLBuilder;
    private final SQLBuilder SQLBuilder;

    public TableHandler(String table_name, LinkedHashMap<String, String> columns, LinkedHashSet<String> pk, LinkedHashMap<String, String> fk, ArrayList<LinkedHashMap<String, String>> data) throws NamingException, SQLException {
        this.pk = pk;
        this.columns = columns;
        this.data = data;
        this.HTMLBuilder = new HTMLBuilder(columns, fk);
        this.SQLBuilder = new SQLBuilder(table_name, pk, columns);
    }

    //TO-DO:
    //объединить с delete?
    public void apply(HashMap<String, String[]> parameters) throws SQLException, NamingException {
        int i = 0;
        for (String index_string : parameters.get("index")) {
            int index = Integer.parseInt(index_string);
            LinkedHashMap<String, String> row = new LinkedHashMap<>();
            for (String name : columns.keySet()) {
                String value = parameters.get(name)[i];
                if (columns.get(name).equals("serial") && value.equals("")) {
                    try (ResultSet rs = DBHandler.getConnection().createStatement().executeQuery(String.format("SELECT nextVal('%s')", name))) {
                        rs.next();
                        value = rs.getString(1);
                    }
                }
                row.put(name, value);
            }
            if (index == data.size()) {
                SQLBuilder.insert(row);
                data.add(row);
            } else {
                SQLBuilder.update(row, buildPK_values(index));
                data.set(index, row);
            }
            i++;
        }
        HTMLBuilder.updateDataTable(data);
    }

    public void delete(HashMap<String, String[]> parameters) throws SQLException, NamingException {
        int index = Integer.parseInt(parameters.get("index")[0]);
        SQLBuilder.delete(buildPK_values(index));
        data.remove(index);
        HTMLBuilder.updateDataTable(data);
    }

    private LinkedHashMap<String, String> buildPK_values(int index) {
        LinkedHashMap<String, String> pk_values = new LinkedHashMap<>();
        pk.forEach((key) -> {
            pk_values.put(key, data.get(index).get(key));
        });
        return pk_values;
    }

    public String showing_column;

    public String getHTMLOptions(String value) {
        return HTMLBuilder.buildOptions(data, pk, showing_column, value);
    }

    public String getHTMLTable() {
        HTMLBuilder.updateDataTable(data);
        return HTMLBuilder.HTML_TABLE;
    }
}
