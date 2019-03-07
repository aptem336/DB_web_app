package db;

import java.util.Collection;
import java.util.HashMap;

public class TableHandler {

    private final HTMLBuilder HTMLBuilder;
    private final SQLBuilder SQLBuilder;
    private final Collection<HashMap<String, String>> DATA;

    public TableHandler(HTMLBuilder HTMLBuilder, SQLBuilder SQLBuilder, Collection<HashMap<String, String>> DATA) {
        this.HTMLBuilder = HTMLBuilder;
        this.SQLBuilder = SQLBuilder;
        this.DATA = DATA;
    }

    public String buildHTML() {
        return HTMLBuilder.buildDATA_TABLE(DATA);
    }
}
