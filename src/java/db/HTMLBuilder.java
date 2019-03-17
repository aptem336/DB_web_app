package db;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashMap;

public class HTMLBuilder {

    private final LinkedHashMap<String, String> NULL;
    private final String HTML_TABLE_HEADER;
    private final MessageFormat HTML_TABLE_ROW_FORMAT;
    private final LinkedHashMap<String, String> FK_table_names;

    public HTMLBuilder(LinkedHashMap<String, Column> columns) {
        this.NULL = new LinkedHashMap<>();
        columns.keySet().forEach((column_name) -> {
            NULL.put(column_name, "");
        });
        this.HTML_TABLE_HEADER = buildHTML_TABLE_HEADER(columns);
        this.HTML_TABLE_ROW_FORMAT = buildHTML_TABLE_ROW_FORMAT(columns);
        this.FK_table_names = new LinkedHashMap<>();
        columns.keySet().forEach((column_name) -> {
            Column column = columns.get(column_name);
            if (column.isFK) {
                FK_table_names.put(column_name, column.FK_table_name);
            }
        });
    }

    public String buildHTMLTable(Collection<LinkedHashMap<String, String>> data) {
        return HTML_TABLE_HEADER + buildHTML_TABLE(data, HTML_TABLE_ROW_FORMAT, FK_table_names, NULL);
    }

    private static String buildHTML_TABLE(Collection<LinkedHashMap<String, String>> data, MessageFormat ROW_FORMAT, LinkedHashMap<String, String> FK_table_names, LinkedHashMap<String, String> NULL) {
        String HTMLData = "";
        int i = 0;
        for (LinkedHashMap<String, String> row : data) {
            LinkedHashMap<String, String> HTML_row = new LinkedHashMap<>(row);
            FK_table_names.keySet().forEach((column_name) -> {
                HTML_row.put(column_name, DBHandler.TABLE_HANDLERS.get(FK_table_names.get(column_name)).getAvailableOptions(row.get(column_name)));
            });
            HTMLData += "\t\t<tr class=\"data_row\">\n" + ROW_FORMAT.format(HTML_row.values().toArray());
            HTMLData += String.format("\t\t\t<td><button type=\"submit\" name=\"type\" value=\"delete\" formnovalidate>×</button>\n");
            HTMLData += String.format(INPUT_FORMAT, "index", i, "hidden", "");
            i++;
        }
        FK_table_names.keySet().forEach((column_name) -> {
            NULL.put(column_name, DBHandler.TABLE_HANDLERS.get(FK_table_names.get(column_name)).getAvailableOptions(""));
        });
        HTMLData += "\t\t<tr class=\"data_row\">\n" + ROW_FORMAT.format(NULL.values().toArray());
        HTMLData += String.format(INPUT_FORMAT, "index", i, "hidden", "", "off", "", "disabled", "");
        return HTMLData;
    }

    private static String buildHTML_TABLE_HEADER(LinkedHashMap<String, Column> columns) {
        String DATA_HEADER = "\t\t<tr id=\"data_header\">\n";
        for (String name : columns.keySet()) {
            Column column = columns.get(name);
            if (!column.isFK) {
                DATA_HEADER += "\t\t\t<th>" + name + "\n";
            } else {
                DATA_HEADER += "\t\t\t<th>" + column.FK_table_name + "\n";
            }
        }
        return DATA_HEADER;
    }

    private final static String INPUT_FORMAT = "\t\t\t<td><input name=\"%1$s\" value=\"%2$s\" type=\"%3$s\" placeholder=\"%1$s\" autocomplete=\"off\" disabled %4$s>\n";

    private static MessageFormat buildHTML_TABLE_ROW_FORMAT(LinkedHashMap<String, Column> columns) {
        String DATA_ROW_FORMAT = "";
        int i = 0;
        for (String column_name : columns.keySet()) {
            Column column = columns.get(column_name);
            if (!column.isFK) {
                DATA_ROW_FORMAT += String.format(INPUT_FORMAT, column_name, "{" + i + "}", column.HTMLType, column.isAutoIncrement | column.isNullable ? "" : "required");
            } else {
                DATA_ROW_FORMAT += String.format("\t\t\t<td><select name=\"" + column_name + "\" disabled>{" + i + "}\n");
            }
            i++;
        }
        return new MessageFormat(DATA_ROW_FORMAT);
    }
}
