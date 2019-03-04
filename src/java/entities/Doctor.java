package entities;

import db.ActiveRecord;
import db.DBHandler;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor extends ActiveRecord {

    public int personnel_number;
    public String FIO;
    public String specialty;
    public String category;
    public int salary;
    public Sector sector;

    public Doctor() {
    }

    public Doctor(Object[] row) {
        this.personnel_number = (Integer) row[0];
        this.FIO = (String) row[1];
        this.specialty = (String) row[2];
        this.category = (String) row[3];
        this.salary = (Integer) row[4];
        Sector newSector = new Sector();
        newSector.sector_number = (Integer) row[5];
        this.sector = newSector;
    }

    @Override
    public void complete() {
        try {
            String select_query = "SELECT * FROM \"" + getTableName() + "\" WHERE \"Табельный_номер\"='" + personnel_number + "'";
            ResultSet resultSet = DBHandler.getResultSet(select_query);
            resultSet.next();
            FIO = resultSet.getString("ФИО");
            specialty = resultSet.getString("Cпециальность");
            category = resultSet.getString("Категория");
            salary = resultSet.getInt("Ставка");
            Sector newSector = new Sector();
            newSector.sector_number = resultSet.getInt("Номер_участка");
            sector = newSector;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + "\" VALUES (nextval('Табельный_номер'), '" + FIO + "','" + specialty + "','" + category + "','" + salary + "','" + sector.sector_number + "')";
    }

    @Override
    public String updateQuery() {
        return "UPDATE \"" + getTableName() + "\" SET \"Номер_участка\"='" + sector.sector_number + "',\"ФИО\"='"
                + FIO + "',\"Специальность\"='" + specialty + "',\"Категория\"='" + category + "',\"Ставка\"='"
                + salary + "' WHERE \"Табельный_номер\"='" + personnel_number + "'";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Табельный_номер\"='" + personnel_number + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Табельный_номер", "ФИО", "Специальность", "Категория", "Ставка", "Номер_участка"};
    }

    @Override
    public String[] getData() {
        return new String[]{personnel_number + "", FIO, specialty, category, salary + "", sector.sector_number + ""};
    }

    @Override
    public String getTableName() {
        return "Врачи";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Doctor(row);
    }

}
