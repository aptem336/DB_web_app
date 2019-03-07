package db;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class HTMLBuilder {

    private final String[] NULL;
    private final MessageFormat DATA_ROW_FORMAT;

    public HTMLBuilder(HashMap<String, String> columns) {
        this.NULL = new String[columns.keySet().size()];
        Arrays.fill(this.NULL, "");
        this.DATA_ROW_FORMAT = DATA_ROW_FORMAT(columns);
    }

    public String buildDATA_TABLE(Collection<HashMap<String, String>> data) {
        return HTMLBuilder.buildDATA_TABLE(data, DATA_ROW_FORMAT, NULL);
    }

    public static String buildDATA_TABLE(Collection<HashMap<String, String>> data, MessageFormat DATA_FORMAT, String[] NULL) {
        String HTMLData = "";
        int i = 0;
        for (HashMap<String, String> row : data) {
            HTMLData += "\t\t<tr>\n" + DATA_FORMAT.format(row.values().toArray());
            HTMLData += String.format(DATA_COLUMN_FORMAT, "index", i, "hidden", "", "", "disabled", "");
            i++;
        }
        HTMLData += "\t\t<tr>\n" + DATA_FORMAT.format(NULL);
        return HTMLData;
    }
    private final static String DATA_COLUMN_FORMAT = "\t\t\t<td><input name=\"%1$s\" value=\"%2$s\" type=\"%3$s\" placeholder=\"%1$s\" title=\"%4$s\" %5$s %6$s %7$s>\n";

    private static MessageFormat DATA_ROW_FORMAT(HashMap<String, String> columns) {
        String DATA_ROW_FORMAT = "";
        int i = 0;
        for (String name : columns.keySet()) {
            DATA_ROW_FORMAT += String.format(DATA_COLUMN_FORMAT, name, "{" + i + "}", HTML_TYPE_MAPPING.get(columns.get(name)), "Дважды кликните, чтобы изменить", columns.get(name).equals("serial") ? "" : "required", "disabled", "");
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
