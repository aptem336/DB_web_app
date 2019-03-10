package db;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class HTMLBuilder {

    private final String[] NULL;
    private final String HEADER;
    private final MessageFormat ROW_FORMAT;
    private final LinkedHashMap<String, String> fk;
    public String HTML_TABLE;

    public HTMLBuilder(LinkedHashMap<String, String> columns, LinkedHashMap<String, String> fk) {
        this.fk = fk;
        this.NULL = new String[columns.keySet().size() - fk.keySet().size()];
        Arrays.fill(this.NULL, "");
        this.HEADER = DATA_HEADER(columns, fk);
        this.ROW_FORMAT = DATA_ROW_FORMAT(columns, fk);
    }

    //...
    public String buildOptions(Collection<LinkedHashMap<String, String>> data, LinkedHashSet<String> pk, String showingColumn, String value) {
        String OPTIONS = "";
        String key = pk.toArray()[0].toString();
        showingColumn = showingColumn != null ? showingColumn : key;
        for (LinkedHashMap<String, String> row : data) {
            OPTIONS += String.format("\t\t\t\t\t<option value=\"%s\" %s>%s\n", row.get(key), row.get(key).equals(value) ? "selected" : "", row.get(showingColumn));
        }
        return OPTIONS;
    }

    public void updateDataTable(Collection<LinkedHashMap<String, String>> data) {
        this.HTML_TABLE = HEADER + buildDATA_TABLE(data, ROW_FORMAT, NULL, fk);
    }

    private static String buildDATA_TABLE(Collection<LinkedHashMap<String, String>> data, MessageFormat ROW_FORMAT, String[] NULL, LinkedHashMap<String, String> fk) {
        String HTMLData = "";
        int i = 0;
        for (LinkedHashMap<String, String> row : data) {
            HTMLData += "\t\t<tr class=\"data_row\">\n" + ROW_FORMAT.format(row.values().toArray());
            for (String name : fk.keySet()) {
                HTMLData += String.format("\t\t\t<td>\n\t\t\t\t<select name=\"%s\" required disabled>\n%s", name, DBHandler.TABLE_HANDLERS.get(fk.get(name)).getHTMLOptions(row.get(name)));
            }
            HTMLData += String.format(INPUT_FORMAT, "type", "×", "submit", "", "off", "", "enabled", "formnovalidate", "");
            HTMLData += String.format(INPUT_FORMAT, "index", i, "hidden", "", "off", "", "disabled", "");
            i++;
        }
        HTMLData += "\t\t<tr class=\"data_row\">\n" + ROW_FORMAT.format(NULL);
        for (String name : fk.keySet()) {
            HTMLData += String.format("\t\t\t<td>\n\t\t\t\t<select name=\"%s\" required disabled>\n%s", name, DBHandler.TABLE_HANDLERS.get(fk.get(name)).getHTMLOptions(""));
        }
        HTMLData += String.format(INPUT_FORMAT, "index", i, "hidden", "", "off", "", "disabled", "");
        return HTMLData;
    }

    private static String DATA_HEADER(LinkedHashMap<String, String> columns, LinkedHashMap<String, String> fk) {
        String DATA_HEADER = "\t\t<tr class=\"data_header\">\n";
        for (String name : columns.keySet()) {
            if (!fk.containsKey(name)) {
                DATA_HEADER += "\t\t\t<th>" + name + "\n";
            }
        }
        for (String name : fk.keySet()) {
            DATA_HEADER += "\t\t\t<th>" + fk.get(name) + "\n";
        }
        return DATA_HEADER;
    }

    private final static String INPUT_FORMAT = "\t\t\t<td><input name=\"%1$s\" value=\"%2$s\" type=\"%3$s\" placeholder=\"%1$s\" title=\"%4$s\" autocomplete=\"%5$s\" %6$s %7$s %8$s>\n";

    private static MessageFormat DATA_ROW_FORMAT(LinkedHashMap<String, String> columns, LinkedHashMap<String, String> fk) {
        String DATA_ROW_FORMAT = "";
        int i = 0;
        for (String name : columns.keySet()) {
            if (!fk.containsKey(name)) {
                DATA_ROW_FORMAT += String.format(INPUT_FORMAT, name, "{" + i + "}", HTML_TYPE_MAPPING.get(columns.get(name)), "Дважды кликните, чтобы изменить", "off", columns.get(name).equals("serial") ? "" : "required", "disabled", "");
            }
            i++;
        }
        return new MessageFormat(DATA_ROW_FORMAT);
    }

    private final static HashMap<String, String> HTML_TYPE_MAPPING = new HashMap<>();

    static {
        HTML_TYPE_MAPPING.put("serial", "number");
        HTML_TYPE_MAPPING.put("int4", "number");
        HTML_TYPE_MAPPING.put("int8", "number");
        HTML_TYPE_MAPPING.put("text", "text");
        HTML_TYPE_MAPPING.put("date", "date");
    }
}
