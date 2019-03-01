package clinic;

public abstract class ActiveRecord {

    public abstract void complete();

    public abstract String insertQuery();

    public abstract String updateQuery();

    public abstract String deleteQuery();

    public void insert() {
        DBHandler.executeUpdate(insertQuery());
    }

    public void update() {
        DBHandler.executeUpdate(updateQuery());
    }

    public void delete() {
        DBHandler.executeUpdate(deleteQuery());
    }

    public abstract String[] getFields();

    public abstract String getTableName();

}
