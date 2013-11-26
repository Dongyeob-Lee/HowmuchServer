package database;



import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

//
// Here are the dbcp-specific classes.
// Note that they are only used in the setupDataSource
// method. In normal use, your classes interact
// only with the standard JDBC API
//
import org.apache.commons.dbcp.BasicDataSource;
 
//
// Here's a simple example of how to use the BasicDataSource.
//

//
// Note that this example is very similiar to the PoolingDriver
// example.

//
// To compile this example, you'll want:
//  * commons-pool-1.5.6.jar
//  * commons-dbcp-1.3.jar (JDK 1.4-1.5) or commons-dbcp-1.4 (JDK 1.6+)
//  * j2ee.jar (for the javax.sql classes)
// in your classpath.
//
// To run this example, you'll want:
//  * commons-pool-1.5.6.jar
//  * commons-dbcp-1.3.jar (JDK 1.4-1.5) or commons-dbcp-1.4 (JDK 1.6+)
//  * j2ee.jar (for the javax.sql classes)
//  * the classes for your (underlying) JDBC driver
// in your classpath.
//
// Invoke the class using two arguments:
//  * the connect string for your underlying JDBC driver
//  * the query you'd like to execute
// You'll also want to ensure your underlying JDBC driver
// is registered.  You can use the "jdbc.drivers"
// property to do this.
//
// For example:
//  java -Djdbc.drivers=oracle.jdbc.driver.OracleDriver \
//       -classpath commons-pool-1.5.6.jar:commons-dbcp-1.4.jar:j2ee.jar:oracle-jdbc.jar:. \
//       PoolingDataSourceExample
//       "jdbc:oracle:thin:scott/tiger@myhost:1521:mysid"
//       "SELECT * FROM DUAL"
//
public class DBCPUtils {

    public static void main(String[] args) {
        // First we set up the BasicDataSource.
        // Normally this would be handled auto-magically by
        // an external configuration, but in this example we'll
        // do it manually.
        //
        System.out.println("Setting up data source.");
        DataSource dataSource = setupDataSource(args[0]);
        System.out.println("Done.");

        //
        // Now, we can use JDBC DataSource as we normally would.
        //
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
            System.out.println("Creating connection.");
            conn = dataSource.getConnection();
            System.out.println("Creating statement.");
            stmt = conn.createStatement();
            System.out.println("Executing statement.");
            rset = stmt.executeQuery(args[1]);
            System.out.println("Results:");
            int numcols = rset.getMetaData().getColumnCount();
            while(rset.next()) {
                for(int i=1;i<=numcols;i++) {
                    System.out.print("\t" + rset.getString(i));
                }
                System.out.println("");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
    }

    public static DataSource setupDataSource(String connectURI) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername("ua32645");
        ds.setPassword("uaua32645");
        ds.setUrl(connectURI);
        return ds;
    }

    public static void printDataSourceStats(DataSource ds) {
        BasicDataSource bds = (BasicDataSource) ds;
        System.out.println("NumActive: " + bds.getNumActive());
        System.out.println("NumIdle: " + bds.getNumIdle());
    }

    public static void shutdownDataSource(DataSource ds) throws SQLException {
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();
    }
    
    private static DBCPUtils instance = new DBCPUtils();
    
    public static DBCPUtils getInstance() {
     
     return instance;
    }
    
    String connectURI = "jdbc:mysql://localhost/ua32645";
    
    DataSource dataSource;
    
    private DBCPUtils() {
     dataSource = setupDataSource(connectURI);
    }
    
    public Connection getConnection() throws SQLException {
    	System.out.println("Connection 1");
     return dataSource.getConnection();
    }
}