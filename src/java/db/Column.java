package db;

import java.util.HashMap;

public class Column {

    public final int SQLType;
    public final String HTMLType;
    public final boolean isAutoIncrement;
    public final boolean isNullable;

    public Column(int SQLType, boolean isAutoIncrement, boolean isNullable) {
        this.SQLType = SQLType;
        this.HTMLType = HTML_TYPE_MAPPING.get(SQLType);
        this.isAutoIncrement = isAutoIncrement;
        this.isNullable = isNullable;
    }

    private final static HashMap<Integer, String> HTML_TYPE_MAPPING = new HashMap<>();

    static {
        HTML_TYPE_MAPPING.put(4, "number");
        HTML_TYPE_MAPPING.put(12, "text");
        HTML_TYPE_MAPPING.put(91, "date");
    }

}
