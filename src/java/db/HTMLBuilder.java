package db;

public class HTMLBuilder {

    public static String buildHTMLTable(String table_name) {
        String HTML_table = "<table>\n";
        HTML_table += "\t<tr>\n";
        for (String title : DBHandler.UNITS.get(table_name).getFields()) {
            HTML_table += "\t\t<th>" + title + "</th>\n";
        }
        HTML_table += "\t</tr>\n";
        for (ActiveRecord record : DBHandler.DATA.get(table_name)) {
            HTML_table += "\t<tr>\n";
            for (String field : record.getData()) {
                HTML_table += "\t\t<td>" + field + "</td>\n";
            }
            HTML_table += "\t</tr>\n";
        }
        return HTML_table;
    }

    public static String buildInputForm(ActiveRecord record) {
        return "";
    }
}
