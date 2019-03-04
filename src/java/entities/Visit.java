package entities;

import db.ActiveRecord;
import db.DBHandler;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Visit extends ActiveRecord {

    public int token_number;
    public Doctor doctor;
    public Patient patient;
    public Purpose purpose;
    public Status status;
    public Diagnosis diagnosis;
    public Date visit_date;

    public Visit() {
    }

    public Visit(Object[] row) {
        this.token_number = (Integer) row[0];
        Doctor newDoctor = new Doctor();
        Patient newPatient = new Patient();
        Purpose newPurpose = new Purpose();
        Status newStatus = new Status();
        Diagnosis newDiagnosis = new Diagnosis();

        newDoctor.personnel_number = (Integer) row[1];
        newDoctor.complete();
        newPatient.med_card_number = (Integer) row[2];
        newPatient.complete();
        newPurpose.name_purpose = (String) row[3];
        newPurpose.complete();
        newStatus.status = (String) row[4];
        newStatus.complete();
        newDiagnosis.name_diagnosis = (String) row[5];
        newDiagnosis.complete();

        this.doctor = newDoctor;
        this.patient = newPatient;
        this.purpose = newPurpose;
        this.status = newStatus;
        this.diagnosis = newDiagnosis;
        this.visit_date = (Date) row[5];
    }

    @Override
    public void complete() {
        try {
            String select_query = "SELECT * FROM \"" + getTableName() + "\" WHERE \"Номер_талона\"='" + token_number;
            ResultSet resultSet = DBHandler.getResultSet(select_query);
            resultSet.next();
            Doctor newDoctor = new Doctor();
            Patient newPatient = new Patient();
            Purpose newPurpose = new Purpose();
            Status newStatus = new Status();
            Diagnosis newDiagnosis = new Diagnosis();

            newDoctor.personnel_number = resultSet.getInt("Табельный_номер");
            newDoctor.complete();
            newPatient.med_card_number = resultSet.getInt("Номер_мед_карты");
            newPatient.complete();
            newPurpose.name_purpose = resultSet.getString("Название_цели");
            newPurpose.complete();
            newStatus.status = resultSet.getString("Название_цели");
            newStatus.complete();
            newDiagnosis.name_diagnosis = resultSet.getString("Название_диагноза");
            newDiagnosis.complete();

            doctor = newDoctor;
            patient = newPatient;
            purpose = newPurpose;
            status = newStatus;
            diagnosis = newDiagnosis;
            visit_date = resultSet.getDate("Дата_визита");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String insertQuery() {
        return "INSERT INTO \"" + getTableName() + " \" VALUES (nextval('Номер_талона'), '" + doctor.personnel_number + "','"
                + patient.med_card_number + "','" + purpose.name_purpose + "','" + status.status + "','" + diagnosis.name_diagnosis + "')";
    }

    @Override
    public String updateQuery() {
        return "UPDATE \"" + getTableName() + "\" SET \"Табельный_номер\"='" + doctor.personnel_number + "',\"Номер_мед_карты\"='"
                + patient.med_card_number + "',\"Название_цели\"='" + purpose.name_purpose + "',\"Статус\"='" + status.status + "',\"Название_диагноза\"='"
                + diagnosis.name_diagnosis + "',\"Дата_визита\"='" + visit_date + "' WHERE \"Номер_талона\"='" + token_number + "'";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM \"" + getTableName() + "\" WHERE \"Номер_талона\"='" + token_number + "'";
    }

    @Override
    public String[] getFields() {
        return new String[]{"Номер_талона", "Врач", "Пациент", "Цель", "Статус", "Название_диагноза", "Дата_визита"};
    }

    @Override
    public String[] getData() {
        return new String[]{token_number + "", doctor.FIO + "", patient.FIO + "", purpose.name_purpose, status.status, diagnosis.name_diagnosis, visit_date.toString() + ""};
    }

    @Override
    public String getTableName() {
        return "Посещения";
    }

    @Override
    public ActiveRecord cast(Object[] row) {
        return new Visit(row);
    }

}
