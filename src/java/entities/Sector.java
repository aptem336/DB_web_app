package entities;

import db.ActiveRecord;

public class Sector extends ActiveRecord {

    public int sector_number;
    public int new_sector_number;

    public Sector() {
    }

    public Sector(Object[] row) {
        this.sector_number = (Integer) row[0];
        this.new_sector_number = (Integer) row[0];
    }

    @Override
    public void complete() {
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES (nextval('Номер_участка'))";
    }

    @Override
    public String updateQuery() {
        String update_query = "UPDATE \"" + getTableName() + "\" SET \"Номер_участка\"='" + new_sector_number + "' WHERE \"Номер_участка\"='" + sector_number + "'";
        sector_number = new_sector_number;
        return update_query;
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
