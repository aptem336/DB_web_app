package entities;

import db.ActiveRecord;
import db.DBHandler;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Diagnosis extends ActiveRecord {

    public String name_diagnosis;
    public String new_name_diagnosis;

    public Diagnosis() {
    }

    public Diagnosis(Object[] row) {
        this.name_diagnosis = (String) row[0];
        this.new_name_diagnosis = (String) row[0];
    }

    @Override
    public void complete() {
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES ('" + name_diagnosis + "')";
    }

    @Override
    public String updateQuery() {
        String update_query = "UPDATE \"" + getTableName() + "\" SET \"Название_диагноза\"='" + new_name_diagnosis + "' WHERE \"Название_диагноза\"='" + name_diagnosis + "'";
        name_diagnosis = new_name_diagnosis;
        return update_query;
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Название_диагноза\"='" + name_diagnosis + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Название_диагноза"};
    }

    @Override
    public String[] getData() {
        return new String[]{name_diagnosis};
    }

    @Override
    public String getTableName() {
        return "Диагнозы";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Diagnosis(row);
    }

}
