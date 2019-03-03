package db;

import java.sql.SQLException;
import javax.naming.NamingException;

public abstract class ActiveRecord {

    public ActiveRecord() {
    }

    public abstract void complete();

    public abstract String insertQuery();

    public abstract String updateQuery();

    public abstract String deleteQuery();

    public void insert() throws NamingException, SQLException {
        DBHandler.executeUpdate(insertQuery());
        DBHandler.DATA.get(getTableName()).add(this);
    }

    public void update() throws NamingException, SQLException {
        DBHandler.executeUpdate(updateQuery());
    }

    public void delete() throws NamingException, SQLException {
        DBHandler.executeUpdate(deleteQuery());
        DBHandler.DATA.get(getTableName()).remove(this);
    }

    public abstract String[] getFields();

    public abstract String[] getData();

    public abstract String getTableName();

    public abstract ActiveRecord cast(Object[] row);

}
