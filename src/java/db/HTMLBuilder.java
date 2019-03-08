package db;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HTMLBuilder {

    private final String[] NULL;
    private final String HEADER;
    private final MessageFormat ROW_FORMAT;

    public HTMLBuilder(LinkedHashMap<String, String> columns) {
        this.NULL = new String[columns.keySet().size()];
        Arrays.fill(this.NULL, "");
        this.HEADER = DATA_HEADER(columns);
        this.ROW_FORMAT = DATA_ROW_FORMAT(columns);
    }

    public String HTML_TABLE;

    public void updateDataTable(Collection<LinkedHashMap<String, String>> data) {
        this.HTML_TABLE = HEADER + buildDATA_TABLE(data, ROW_FORMAT, NULL);
    }

    private static String buildDATA_TABLE(Collection<LinkedHashMap<String, String>> data, MessageFormat ROW_FORMAT, String[] NULL) {
        String HTMLData = "";
        int i = 0;
        for (LinkedHashMap<String, String> row : data) {
            HTMLData += "\t\t<tr class=\"data_row\">\n" + ROW_FORMAT.format(row.values().toArray());
            HTMLData += String.format(DATA_COLUMN_FORMAT, "index", i, "hidden", "", "off", "", "disabled", "", "");
            i++;
        }
        HTMLData += "\t\t<tr class=\"data_row\">\n" + ROW_FORMAT.format(NULL);
        HTMLData += String.format(DATA_COLUMN_FORMAT, "index", i, "hidden", "", "off", "", "disabled", "", "");
        return HTMLData;
    }

    private static String DATA_HEADER(LinkedHashMap<String, String> columns) {
        String DATA_HEADER = "\t\t<tr class=\"data_header\">\n";
        for (String name : columns.keySet()) {
            DATA_HEADER += "\t\t\t<th>" + name + "\n";
        }
        return DATA_HEADER;
    }

    private final static String DATA_COLUMN_FORMAT = "\t\t\t<td><input name=\"%1$s\" value=\"%2$s\" type=\"%3$s\" placeholder=\"%1$s\" title=\"%4$s\" autocomplete=\"%5$s\" %6$s %7$s %8$s list=\"%9$s\">\n";

    private static MessageFormat DATA_ROW_FORMAT(LinkedHashMap<String, String> columns) {
        String DATA_ROW_FORMAT = "";
        int i = 0;
        for (String name : columns.keySet()) {
            DATA_ROW_FORMAT += String.format(DATA_COLUMN_FORMAT, name, "{" + i + "}", HTML_TYPE_MAPPING.get(columns.get(name)), "Дважды кликните, чтобы изменить", "off", columns.get(name).equals("serial") ? "" : "required", "disabled", "", "");
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
