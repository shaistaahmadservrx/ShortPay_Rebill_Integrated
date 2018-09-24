/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ShortPay_Rebill;

import static ShortPay_Rebill.Config.fbDatabaseProd;
import static ShortPay_Rebill.Config.mySqlDatabaseProd;
import static ShortPay_Rebill.Config.oracleDatabaseProd;
import java.sql.Connection;
import java.sql.DriverManager;


/**
 *
 * @author Tiberiu
 */
public class DatabaseConnections {

    public static Connection connectToFirebird()  
    {
        Connection conn = null;
        try{
            //Tries to create the database connection
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            conn = DriverManager.getConnection(fbDatabaseProd.get("connectionString").toString(), fbDatabaseProd.get("login").toString(), fbDatabaseProd.get("password").toString());
            //conn = DriverManager.getConnection(fbDatabaseWarehouse.get("connectionString").toString(), fbDatabaseWarehouse.get("login").toString(), fbDatabaseWarehouse.get("password").toString());
        }
        catch(Exception e)
        {
            Utilities.addExceptionToLog(e);
        }
        //Returns the connection if it occurs without error
        return conn;
    }
    
    public static Connection connectToMySQL()  
    {
        Connection conn = null;
        try{
            //Tries to create the database connection
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(mySqlDatabaseProd.get("connectionString").toString() + mySqlDatabaseProd.get("loginString").toString());     
            //conn = DriverManager.getConnection(mySqlDatabaseWarehouse.get("connectionString").toString() + mySqlDatabaseWarehouse.get("loginString").toString());    
        }
        catch(Exception e)
        {
            Utilities.addExceptionToLog(e);
        }
        //Returns the connection if it occurs without error
        return conn;
    }
    
    public static Connection connectToOracle()  
    {
        Connection conn = null;
        try{
            //Tries to create the database connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(oracleDatabaseProd.get("connectionString").toString(), oracleDatabaseProd.get("login").toString(), oracleDatabaseProd.get("password").toString());
            //conn = DriverManager.getConnection(oracleDatabaseWarehouse.get("connectionString").toString(), oracleDatabaseWarehouse.get("login").toString(), oracleDatabaseWarehouse.get("password").toString());  
        }
        catch(Exception e)
        {
            Utilities.addExceptionToLog(e);
        }
        //Returns the connection if it occurs without error
        return conn;
    }
}
