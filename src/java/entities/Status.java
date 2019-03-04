package entities;

import db.ActiveRecord;

public class Status extends ActiveRecord {

    public String status;
    public String new_status;

    public Status() {
    }

    public Status(Object[] row) {
        this.status = (String) row[0];
    }

    @Override
    public void complete() {
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES ('" + status + "')";
    }

    @Override
    public String updateQuery() {
        String update_query = "UPDATE \"" + getTableName() + "\" SET \"Название_диагноза\"='" + new_status + "' WHERE \"Название_диагноза\"='" + status + "'";
        status = new_status;
        return update_query;
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Статус\"='" + status + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Статус"};
    }

    @Override
    public String[] getData() {
        return new String[]{status};
    }

    @Override
    public String getTableName() {
        return "Статусы";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Status(row);
    }
}
