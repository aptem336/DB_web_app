package entities;

import db.ActiveRecord;

public class Sector extends ActiveRecord {

    public int sector_number;

    public Sector() {
    }

    public Sector(Object[] row) {
        this.sector_number = (Integer) row[0];
    }

    @Override
    public void complete() {
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES ('" + sector_number + "')";
    }

    @Override
    public String updateQuery() {
        return "UPDATE \"" + getTableName() + "\" SET \"Номер_участка\"='" + sector_number + "'";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Номер_участка\"='" + sector_number + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Номер_участка"};
    }

    @Override
    public String[] getData() {
        return new String[]{sector_number + ""};
    }

    @Override
    public String getTableName() {
        return "Участки";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Sector(row);
    }

}
