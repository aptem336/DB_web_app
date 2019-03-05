package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class TypeMapping {

    public final static HashMap<String, TypeMapping> MAPPING = new HashMap<>();

    static {
        MAPPING.put("serial", new TypeMapping("hidden", null));
        MAPPING.put("int4", new TypeMapping("number", Integer.class));
        MAPPING.put("int8", new TypeMapping("number", Integer.class));
        MAPPING.put("text", new TypeMapping("text", String.class));
        MAPPING.put("date", new TypeMapping("date", Date.class));
    }

    public final String HTML_TYPE;
    public final Class JAVA_TYPE;

    private TypeMapping(String HTML_TYPE, Class JAVA_TYPE) {
        this.HTML_TYPE = HTML_TYPE;
        this.JAVA_TYPE = JAVA_TYPE;
    }

    public static void set(PreparedStatement stmt, int index, String type, String value) throws SQLException {
        switch (type) {
            case "serial":
                stmt.setString(index, value);
                break;
            case "int4":
            case "int8":
                stmt.setInt(index, Integer.parseInt(value));
                break;
            case "text":
                stmt.setString(index, value);
                break;
            case "date":
                stmt.setDate(index, Date.valueOf(value));
        }
    }

}
