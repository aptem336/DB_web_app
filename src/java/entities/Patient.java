package entities;

import db.ActiveRecord;
import db.DBHandler;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends ActiveRecord {

    public int med_card_number;
    public int med_policy_number;
    public String FIO;
    public String gender;
    public Date birthday;
    public String street;
    public String home;

    public Patient() {
    }

    public Patient(Object[] row) {
        this.med_card_number = (Integer) row[0];
        this.med_policy_number = (Integer) row[1];
        this.FIO = (String) row[2];
        this.gender = (String) row[3];
        this.birthday = (Date) row[4];
        this.street = (String) row[5];
        this.home = (String) row[6];
    }

    @Override
    public void complete() {
        try {
            String select_query = "SELECT * FROM \"" + getTableName() + "\" WHERE \"Номер_мед_карты\"='" + med_card_number;
            ResultSet resultSet = DBHandler.getResultSet(select_query);
            resultSet.next();
            med_policy_number = resultSet.getInt("Номер_медполиса");
            FIO = resultSet.getString("ФИО");
            gender = resultSet.getString("Пол");
            birthday = resultSet.getDate("Дата_рождения");
            street = resultSet.getString("Улица");
            home = resultSet.getString("Дом");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES (nextval('Номер_мед_карты'), '" + med_policy_number + "','"
                + FIO + "','" + gender + "','" + birthday + "','" + street + "','" + home + "')";
    }

    @Override
    public String updateQuery() {
        return "UPDATE \"" + getTableName() + "\" SET \"Номер_мед_полиса\"='" + med_policy_number + "',\"ФИО\"='"
                + FIO + "',\"Пол\"='" + gender + "',\"Дата_рождения\"='" + birthday + "',\"Улица\"='"
                + street + "',\"Дом\"='" + home + "' WHERE \"Номер_мед_карты\"='" + med_card_number + "'";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Номер_мед_карты\"='" + med_card_number + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Номер_мед_карты", "Номер_мед_полиса", "ФИО", "Пол", "Дата_рождения", "Улица", "Дом"};
    }

    @Override
    public String[] getData() {
        return new String[]{med_card_number + "", med_policy_number + "", FIO, gender, birthday + "", street, home};
    }

    @Override
    public String getTableName() {
        return "Пациенты";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Patient(row);
    }

}
