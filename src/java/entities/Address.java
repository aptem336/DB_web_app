package entities;

import db.ActiveRecord;
import db.DBHandler;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Address extends ActiveRecord {

    public String street;
    public String home;
    public Sector sector;

    public Address() {
    }

    public Address(Object[] row) {
        this.street = (String) row[0];
        this.home = (String) row[1];
        Sector newSector = new Sector();
        newSector.sector_number = (Integer) row[2];
        newSector.complete();
        this.sector = newSector;
    }

    @Override
    public void complete() {
        try {
            String select_query = "SELECT * FROM \"Адреса\" WHERE \"Улица\"='" + street + "' AND \"Дом\"='" + home + "'";
            ResultSet resultSet = DBHandler.getResultSet(select_query);
            resultSet.next();
            Sector newSector = new Sector();
            newSector.sector_number = resultSet.getInt("Номер_участка");
            newSector.complete();
            sector = newSector;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES ('" + street + "','" + home + "','" + sector.sector_number + "')";
    }

    @Override
    public String updateQuery() {
        return "UPDATE \"" + getTableName() + "\" SET \"Номер_участка\"='" + sector.sector_number + "' WHERE \"Улица\"='" + street + "' AND \"Дом\"='" + home + "'";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Улица\"='" + street + "' AND \"Дом\"='" + home + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Улица", "Дом", "Номер_участка"};
    }

    @Override
    public String[] getData() {
        return new String[]{street, home, sector.sector_number + ""};
    }

    @Override
    public String getTableName() {
        return "Адреса";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Address(row);
    }


}
