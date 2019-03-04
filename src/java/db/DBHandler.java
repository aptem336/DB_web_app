package db;

import entities.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {

    public final static HashMap<String, ActiveRecord> UNITS = new HashMap<>();
    public final static HashMap<String, ArrayList<ActiveRecord>> DATA = new HashMap<>();

    static {
        UNITS.put(new Address().getTableName(), new Address());
        UNITS.put(new Sector().getTableName(), new Sector());
        UNITS.put(new Status().getTableName(), new Status());
        UNITS.put(new Purpose().getTableName(), new Purpose());
        UNITS.put(new Doctor().getTableName(), new Doctor());
        UNITS.put(new Diagnosis().getTableName(), new Diagnosis());
        UNITS.put(new Patient().getTableName(), new Patient());
        UNITS.put(new Visit().getTableName(), new Visit());
        UNITS.values().forEach((record) -> {
            readData(record);
        });
    }

    private static void readData(ActiveRecord record) {
        try {
            ArrayList<ActiveRecord> records = new ArrayList<>();
            ResultSet data = getResultSet("SELECT * FROM \"" + record.getTableName() + "\"");
            while (data.next()) {
                Object[] row = new Object[data.getMetaData().getColumnCount()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = data.getObject(i + 1);
                }
                records.add(record.cast(row));
            }
            DATA.put(record.getTableName(), records);
        } catch (SQLException ex) {
            //TO-DO
            System.out.println(ex.getMessage());
        }
    }

    public static ResultSet getResultSet(String select_query) {
        try {
            return getConnection().createStatement().executeQuery(select_query);
        } catch (NamingException | SQLException ex) {
            //TO-DO
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static void executeUpdate(String update_query) throws NamingException, SQLException {
        getConnection().createStatement().executeUpdate(update_query);
    }

    private static Connection getConnection() throws NamingException, SQLException {
        InitialContext initContext = new InitialContext();
        DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/sql");
        return dataSource.getConnection();
    }
}
