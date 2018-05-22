package DB;

import java.sql.*;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnector {

    private static String current_table_name;
    private static String current_table_pk;
    private static final String[] ALL_TABLES_NAMES = {
        "Специальности", "Группы", "Студенты", "Предметы", "Аттестации", "Оценки"
    };
    private static final String[] CONFINES = {
        "Семестр:1:8", "Балл:1:5", "Часы:0:300", "Начало периода:1:7", "Конец периода:2:8", "НЗК:10000000:99999999"
    };

    public static String change_table(int table_index) throws NamingException {
        try {
            current_table_name = ALL_TABLES_NAMES[table_index];
            current_table_pk = getPrimaryKey(current_table_name);
            return build_data_table();
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "</div>";
        }
    }

    private static String getPrimaryKey(String table_name) throws NamingException, SQLException {
        try (Connection connection = getConnection(); ResultSet pk = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), connection.getSchema(), table_name)) {
            pk.next();
            return pk.getString("COLUMN_NAME");
        }
    }

    public static String build_data_table(String title, String query) throws NamingException {
        try {
            return build_table(title, buildRSData(query), null);
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "<br>" + query + "</div>";
        }
    }

    private static String build_data_table() throws NamingException {
        try {
            return build_table(current_table_name.toUpperCase(), buildRSData("SELECT * FROM " + current_table_name + " ORDER BY " + current_table_pk), buildRSData("SELECT " + current_table_pk + " FROM " + current_table_name + " ORDER BY " + current_table_pk));
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "</div>";
        }
    }

    private static String build_table(String title, String[][] table_data, String[][] table_id) {
        String table_string = "<span class=\"title\">" + title + "</span>";
        table_string += ("<table>\n");
        table_string += "<tr>\n";
        for (String item : table_data[0]) {
            table_string += "<th>";
            table_string += item;
            table_string += "</th>\n";
        }
        table_string += "</tr>\n";
        for (int i = 1; i < table_data.length; i++) {
            table_string += "<tr";
            if (table_id != null) {
                table_string += " id=\"" + table_id[i][0] + "\" onclick=\"mark_row('" + table_id[i][0] + "')\"";
            }
            table_string += ">\n";
            for (String item : table_data[i]) {
                table_string += "<td>";
                table_string += item;
                table_string += "</td>\n";
            }
            table_string += "</tr>\n";
        }
        table_string += "</table>";
        return table_string;
    }

    private static String[][] buildRSData(String query) throws NamingException, SQLException {
        try (Connection connection = getConnection(); ResultSet rs = connection.createStatement().executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colum_count = rsmd.getColumnCount();
            String[][] data = new String[1][colum_count];
            for (int j = 0; j < colum_count; j++) {
                data[0][j] = rsmd.getColumnName(j + 1);
            }
            while (rs.next()) {
                String[][] memory = data.clone();
                data = new String[data.length + 1][colum_count];
                for (int j = 0; j < colum_count; j++) {
                    data[data.length - 1][j] = rs.getString(j + 1);
                }
                System.arraycopy(memory, 0, data, 0, memory.length);
            }
            return data;
        }
    }

    private static void executePreparedQuery(String query, Object[] data) throws NamingException, SQLException {
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            for (int i = 0; i < data.length; i++) {
                ps.setObject(i + 1, data[i]);
            }
            ps.executeUpdate();
        }
    }

    public static String delete(int id) throws NamingException {
        try {
            try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement("DELETE FROM " + current_table_name + " WHERE " + current_table_pk + "=" + id)) {
                ps.executeUpdate();
            }
            return build_data_table();
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "</div>";
        }
    }

    public static String getRequiredInput() throws NamingException {
        return getRequiredInput("SELECT * FROM " + current_table_name, false);
    }

    public static String getRequiredInput(String id) throws NamingException {
        return getRequiredInput("SELECT * FROM " + current_table_name + " WHERE " + current_table_pk + "=" + id, true);
    }

    public static String getRequiredReportInput(String query) throws NamingException {
        return getRequiredInput(query, false);
    }

    //<editor-fold defaultstate="collapsed" desc="need refactoring!">
    private static String getRequiredInput(String query, boolean isEdit) throws NamingException {
        try (Connection connection = getConnection(); ResultSet rs = connection.createStatement().executeQuery(query); ResultSet fk = connection.getMetaData().getImportedKeys(connection.getCatalog(), connection.getSchema(), current_table_name)) {
            String input = "<span class=\"title\">" + ((isEdit) ? "Внесите изменения:" : "Заполните необходимые поля:") + "</span>\n<br>\n";
            rs.next();
            ArrayList<String[]> fk_array = new ArrayList();
            while (fk.next()) {
                fk_array.add(new String[]{fk.getString("FKCOLUMN_NAME"), fk.getString("PKCOLUMN_NAME"), fk.getString("PKTABLE_NAME")});
            }
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                String column_type = rsmd.getColumnTypeName(i + 1);
                String column_name = rsmd.getColumnName(i + 1);
                String column_data = rs.getString(i + 1);
                input += "<span>" + column_name + "</span>\n";
                String select = (isEdit) ? checkForeignKeys(fk_array, column_name, column_data) : checkForeignKeys(fk_array, column_name, "");
                if (select != null) {
                    input += select;
                } else {
                    input += "<input class=\"data_field\" onkeyup=\"check_valid()\" required ";
                    if (isEdit) {
                        input += "value=" + column_data + " ";
                    }
                    input += "type=";
                    switch (column_type) {
                        case "text":
                            input += "\"text\">\n<span class=\"data_error\">Заполните поле!</span>\n";
                            break;
                        default:
                            input += "\"number\"" + checkCON(column_name);
                            break;
                    }
                }
            }
            return input;
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "</div>";
        }
    }

    private static String checkCON(String column_name) {
        if (column_name.equals(current_table_pk)) {
            return ">\n<span class=\"data_error\">Заполните поле!<br>Ключ '" + column_name + "' должен быть уникальным!</span>\n";
        }
        for (String CON : CONFINES) {
            String[] con_data = CON.split(":");
            if (con_data[0].equals(column_name)) {
                int min = Integer.parseInt(con_data[1]);
                int max = Integer.parseInt(con_data[2]);
                return " min=\"" + min + "\" max=\"" + max + "\">\n<span class=\"data_error\">Значение должно находиться в пределах от " + min + " до " + max + "!</span>\n";
            }
        }
        return "><br>\n<span class=\"data_error\">Заполните поле!</span>\n";
    }

    private static String checkForeignKeys(ArrayList<String[]> fk_array, String column_name, String column_data) throws NamingException, SQLException {
        for (String[] fkey_data : fk_array) {
            if (fkey_data[0].equals(column_name)) {
                String[][] possible_options = buildRSData("SELECT " + fkey_data[1] + " FROM " + fkey_data[2] + " ORDER BY " + getPrimaryKey(fkey_data[2]));
                String[][] foreight_table_rows = buildRSData("SELECT * FROM " + fkey_data[2] + " ORDER BY " + getPrimaryKey(fkey_data[2]));
                String select = "<select class= \"data_field\" size=\"" + (foreight_table_rows.length - 1) + "\" onchange=\"check_valid()\" required>\n";
                for (int j = 1; j < possible_options.length; j++) {
                    select += "<option value=\"" + possible_options[j][0] + "\" ";
                    if (possible_options[j][0].equals(column_data)) {
                        select += "selected";
                    }
                    select += ">";
                    for (String item : foreight_table_rows[j]) {
                        select += "<" + item + ">";
                    }
                    select += "</option>\n";
                }
                select += "</select>\n<span class=\"data_error\">Выберите один из пунктов!</span>\n";
                return select;
            }
        }
        return null;
    }
    //</editor-fold>

    public static String insert(String data) throws NamingException {
        try {
            try (Connection connection = getConnection(); ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + current_table_name)) {
                String query = "INSERT INTO " + current_table_name + "(";
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 0; i < rsmd.getColumnCount() - 1; i++) {
                    query += rsmd.getColumnName(i + 1) + ", ";
                }
                query += rsmd.getColumnName(rsmd.getColumnCount()) + ") VALUES(";
                for (int i = 0; i < rsmd.getColumnCount() - 1; i++) {
                    query += "?, ";

                }
                query += "?)";
                executePreparedQuery(query, getPreparedObjects(rsmd, data.split(":")));
            }
            return build_data_table();
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "</div>";
        }
    }

    public static String update(int id, String data) throws NamingException {
        try {
            try (Connection connection = getConnection(); ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + current_table_name)) {
                ResultSetMetaData rsmd = rs.getMetaData();
                String query = "UPDATE " + current_table_name + " SET ";
                for (int i = 0; i < rsmd.getColumnCount() - 1; i++) {
                    query += rsmd.getColumnName(i + 1) + " = ?, ";
                }
                query += rsmd.getColumnName(rsmd.getColumnCount()) + " = ? WHERE " + current_table_pk + "=" + id;
                executePreparedQuery(query, getPreparedObjects(rsmd, data.split(":")));
            }
            return build_data_table();
        } catch (SQLException ex) {
            return "<div class=\"SQL_error\">" + ex.getMessage() + "</div>";
        }
    }

    private static Object[] getPreparedObjects(ResultSetMetaData rsmd, String[] data_array) throws SQLException {
        Object[] objects = new Object[rsmd.getColumnCount()];
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            switch (rsmd.getColumnTypeName(i + 1)) {
                case "text":
                    objects[i] = data_array[i];
                    break;
                default:
                    objects[i] = Integer.parseInt(data_array[i]);
                    break;
            }
        }
        return objects;
    }

    private static Connection getConnection() throws NamingException, SQLException {
        InitialContext initContext = new InitialContext();
        DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/sql");
        return dataSource.getConnection();
    }
}
