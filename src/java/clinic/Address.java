package clinic;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Address extends ActiveRecord {

    public String street;
    public String home;
    public int sector_number;

    public Address(String street, String home, int area_number) {
        this.street = street;
        this.home = home;
        this.sector_number = area_number;
    }

    public Address(Object[] row) {
//        this.street = (String) row[0];
//        this.home = home;
//        this.sector_number = area_number;
    }

    public Address() {
    }

    @Override
    public void complete() {
        try {
            String select_query = "SELECT \"Номер_участка\" FROM \"Адреса\" WHERE \"Улица\"='" + street + "' AND \"Дом\"='" + home + "'";
            ResultSet resultSet = DBHandler.getResultSet(select_query);
            resultSet.next();
            sector_number = resultSet.getInt("Номер_участка");
        } catch (SQLException ex) {
            //TO-DO
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"Адреса\" VALUES ('" + street + "','" + home + "','" + sector_number + "')";
    }

    @Override
    public String updateQuery() {
        return "UPDATE \"Адреса\" SET \"Номер_участка\"='" + sector_number + "' WHERE \"Улица\"='" + street + "' AND \"Дом\"='" + home + "'";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"Адреса\" WHERE \"Улица\"='" + street + "' AND \"Дом\"='" + home + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{street, home, sector_number + ""};
    }

    @Override
    public String getTableName() {
        return "Адреса";
    }

}
