package clinic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBHandler {

    public final static ArrayList<ActiveRecord> UNITS = new ArrayList<>();
    public final static HashMap<String, ArrayList<ActiveRecord>> DATA = new HashMap<>();
    public final static ArrayList<String> TABLE_NAMES = new ArrayList<>();

    //..
    static {
        UNITS.add(new Address());
//        UNITS.put("Aдреса", new Address());
//        UNITS.put("Aдреса", new Address());
//        UNITS.put("Aдреса", new Address());
//        UNITS.put("Aдреса", new Address());
//        UNITS.put("Aдреса", new Address());
//        UNITS.put("Aдреса", new Address());
//        UNITS.put("Aдреса", new Address());
        UNITS.forEach((record) -> {
            readTable(record);
        });
    }

    //..
    public static void readTable(ActiveRecord record) {
        try {
            ArrayList<ActiveRecord> records = new ArrayList<>();
            ResultSet data = getResultSet("SELECT * FROM \"" + record.getTableName() + "\"");
            while (data.next()) {
                Object[] row = new Object[data.getMetaData().getColumnCount()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = data.getObject(i);
                }
                records.add(record);
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

    public static void executeUpdate(String update_query) {
        try {
            getConnection().createStatement().executeUpdate(update_query);
        } catch (NamingException | SQLException ex) {
            //TO-DO
            System.out.println(ex.getMessage());
        }
    }

    public static Connection getConnection() throws NamingException, SQLException {
        InitialContext initContext = new InitialContext();
        DataSource dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/sql");
        return dataSource.getConnection();
    }

}
