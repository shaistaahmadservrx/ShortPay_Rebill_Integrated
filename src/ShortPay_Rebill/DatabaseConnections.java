/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ShortPay_Rebill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ShortPay_Rebill.Config.*;


/**
 *
 * @author Tiberiu
 */
public class DatabaseConnections {

    public static Connection connectToFirebird() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try
        {
            //Tries to create the database connection
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            conn = DriverManager.getConnection(fbDatabaseProd.get("connectionString").toString(), fbDatabaseProd.get("login").toString(), fbDatabaseProd.get("password").toString());
            //conn = DriverManager.getConnection(fbDatabaseWarehouse.get("connectionString").toString(), fbDatabaseWarehouse.get("login").toString(), fbDatabaseWarehouse.get("password").toString());
        }
        catch(Exception e)
        {
            Utilities.addExceptionToLog(e);
            throw e;
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
    public static Connection connectToMSSql()
    {
        Connection conn = null;
        try{
            //Tries to create the database connection
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(sqlServerDatabaseProd.get("connectionString").toString());
        }
        catch(Exception e)
        {
            Utilities.addExceptionToLog(e);
        }
        //Returns the connection if it occurs without error
        return conn;
    }
}
