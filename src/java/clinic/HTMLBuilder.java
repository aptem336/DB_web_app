package clinic;

import java.util.ArrayList;
import java.util.HashMap;

public class HTMLBuilder {

    public final static HashMap<String, String> HTML_TABLES = new HashMap<>();

    static {
        for (String table_name : DBHandler.DATA.keySet()) {
            buildHTMLTable(table_name);
        }
    }

    public static void buildHTMLTable(String table_name) {
        ArrayList<ActiveRecord> RECORDS = DBHandler.DATA.get(table_name);
        String HTML_table = "<table border=1>\n";
        for (ActiveRecord record : RECORDS) {
            HTML_table += "\t<tr>\n";
            for (String field : record.getFields()) {
                HTML_table += "\t\t<td>\n";
                HTML_table += "\t\t" + field + "\n";
                HTML_table += "\t\t</td>\n";
            }
            HTML_table += "\t</tr>\n";
        }
        HTML_table += "</table>\n";
        HTML_TABLES.put(table_name, HTML_table);
    }
}
