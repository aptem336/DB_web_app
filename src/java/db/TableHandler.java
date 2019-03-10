package db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.naming.NamingException;

public class TableHandler {

    private final LinkedHashSet<String> pk;
    private final HashMap<String, String> columns;
    private final ArrayList<LinkedHashMap<String, String>> table;
    private final HTMLBuilder HTMLBuilder;
    private final SQLBuilder SQLBuilder;

    public TableHandler(String table_name, LinkedHashMap<String, String> columns, LinkedHashSet<String> pk, LinkedHashMap<String, String> fk, ArrayList<LinkedHashMap<String, String>> table) throws NamingException, SQLException {
        this.pk = pk;
        this.columns = columns;
        this.table = table;
        this.HTMLBuilder = new HTMLBuilder(columns, fk);
        HTMLBuilder.updateDataTable(table);
        this.SQLBuilder = new SQLBuilder(table_name, pk, columns);
    }

    //TO-DO:
    //сократить, многое повторяется
    public void apply(HashMap<String, String[]> parameters) throws SQLException, NamingException {
        int i = 0;
        for (String index_string : parameters.get("index")) {
            int index = Integer.parseInt(index_string);
            LinkedHashMap<String, String> row = new LinkedHashMap<>();
            for (String name : columns.keySet()) {
                row.put(name, parameters.get(name)[i]);
            }
            if (index == table.size()) {
                SQLBuilder.insert(row);
                table.add(row);
            } else {
                LinkedHashMap<String, String> pk_values = new LinkedHashMap<>();
                pk.forEach((key) -> {
                    pk_values.put(key, table.get(index).get(key));
                });
                SQLBuilder.update(row, pk_values);
                table.set(index, row);
            }
            i++;
        }
        HTMLBuilder.updateDataTable(table);
    }

    //TO-DO:
    //сократить, многое повторяется
    public void delete(HashMap<String, String[]> parameters) throws SQLException, NamingException {
        int index = Integer.parseInt(parameters.get("index")[0]);
        LinkedHashMap<String, String> pk_values = new LinkedHashMap<>();
        pk.forEach((key) -> {
            pk_values.put(key, table.get(index).get(key));
        });
        SQLBuilder.delete(pk_values);
        table.remove(index);
        HTMLBuilder.updateDataTable(table);
    }

    public String getHTMLTable() {
        return HTMLBuilder.HTML_TABLE;
    }
}
