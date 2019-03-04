package entities;

import db.ActiveRecord;

public class Purpose extends ActiveRecord{

    public String name_purpose;
    public String new_name_purpose;

    public Purpose() {
    }

    public Purpose(Object[] row) {
        this.name_purpose = (String)row[0];
    }
    
    @Override
    public void complete() {
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES ('" + name_purpose + "')";
    }

    @Override
    public String updateQuery() {
        String update_query = "UPDATE \"" + getTableName() + "\" SET \"Название_диагноза\"='" + new_name_purpose + "' WHERE \"Название_диагноза\"='" + name_purpose + "'";
        name_purpose = new_name_purpose;
        return update_query;
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Название_цели\"='" + name_purpose + "'"; 
    }

    @Override
    public String[] getFields() {
        return new String[]{"Название_цели"};
    }

    @Override
    public String[] getData() {
        return new String[]{name_purpose};
    }

    @Override
    public String getTableName() {
        return "Цели посещения";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Purpose(row);
    }

    
}
