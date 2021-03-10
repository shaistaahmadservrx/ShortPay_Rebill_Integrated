/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author toprisiu
 */
public class Config {

    public static Map fbDatabaseProd;
    public static Map fbDatabaseWarehouse;
    public static Map salesforceTodd;
    public static Map mySqlDatabaseProd;
    public static Map mySqlDatabaseWarehouse;
    public static Map oracleDatabaseProd;
    public static Map oracleDatabaseWarehouse;
    public static Map ninjaAutoUser;
    public static Map ninjaDownloadUser;
    public static Map ninjaOverpaymentNotifier;
    public static Map emailIncomingReports;
    public static Map sftpIncomingNotes;
    public static Map ringCentralExtension;
    public static Map lakerAutoUser;
    public static Map sqlServerDatabaseProd;
    public static Map letterhubApiProd;
    public static boolean loadConfigFile(String fileLoc) {
        Utilities.addToLog("Loading Config File");
        Map configFile = readConfigFile(fileLoc);

        if (configFile.size() > 0) {
            if (setGlobalVariables(configFile)) {
                Utilities.addToLog("Config File Loaded");
                return true;
            } else {
                Utilities.addToLog("Error loading config File");
                System.exit(0);
                return false;
            }
        } else {
            Utilities.addToLog("Error loading config File");
            System.exit(0);
            return false;
        }

    }

    public static boolean setGlobalVariables(Map configFile) {
        try {
            Map fbDatabase = (Map) configFile.get("fbDatabase");
            fbDatabaseProd = (Map) fbDatabase.get("Production");
            fbDatabaseWarehouse = (Map) fbDatabase.get("Warehouse");

            Map salesforce = (Map) configFile.get("salesforce");
            salesforceTodd = (Map) salesforce.get("todd.delano@gmail.com");
            
            Map mySqlDatabase = (Map) configFile.get("mySqlDatabase");
            mySqlDatabaseProd = (Map) mySqlDatabase.get("Production");
            mySqlDatabaseWarehouse = (Map) mySqlDatabase.get("Localhost");
            
            Map oracleDatabase = (Map) configFile.get("oracleDatabase");
            oracleDatabaseProd = (Map) oracleDatabase.get("Production");
            oracleDatabaseWarehouse = (Map) oracleDatabase.get("UAT");
            
            Map ninja = (Map) configFile.get("ninja");
            ninjaAutoUser = (Map) ninja.get("automated_user");
            ninjaDownloadUser = (Map) ninja.get("download_user");
            ninjaOverpaymentNotifier = (Map) ninja.get("overpayment_notifer");
            
            Map email = (Map) configFile.get("email");
            emailIncomingReports = (Map) email.get("incomingreports@servrx.com");
            
            Map sftp = (Map) configFile.get("sftp");
            sftpIncomingNotes = (Map) sftp.get("DynaMD_ServRx-servrx.brickftp.com");
            
            Map ringCentral = (Map) configFile.get("ringCentral");
            ringCentralExtension = (Map) ringCentral.get("715");
            
            Map lakerAPI = (Map) configFile.get("lakerAPI");
            lakerAutoUser = (Map) lakerAPI.get("autouser");

            Map sqlServerDatabase = (Map) configFile.get("sqlServerDatabase");
            sqlServerDatabaseProd = (Map) sqlServerDatabase.get("Production");

            Map letterhubApi = (Map) configFile.get("letterhubApi");
            letterhubApiProd = (Map) letterhubApi.get("Production");
            
            return true;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return false;
        }
    }

    public static Map readConfigFile(String fileLoc) {
        Map<String, Map> configFileMap = new HashMap<String, Map>();
        Map<String, Map> fbDatabase = new HashMap<String, Map>();
        Map<String, Map> mySqlDatabase = new HashMap<String, Map>();
        Map<String, Map> oracleDatabase = new HashMap<String, Map>();
        Map<String, Map> ninja = new HashMap<String, Map>();
        Map<String, Map> salesforce = new HashMap<String, Map>();
        Map<String, Map> email = new HashMap<String, Map>();
        Map<String, Map> sftp = new HashMap<String, Map>();
        Map<String, Map> ringCentral = new HashMap<String, Map>();
        Map<String, Map> lakerAPI = new HashMap<String, Map>();
        Map<String, Map> sqlServerDatabase = new HashMap<String, Map>();
        Map<String, Map> letterhubApi = new HashMap<String, Map>();

        try {
            String configFile = Config.readFile(fileLoc);
            String[] configFileSplit = configFile.split("[\\r\\n]+");

            String type = "";
            for (String line : configFileSplit) {
                //System.out.println(line);
                if (!line.contains("~")) {
                    type = line;
                } else {
                    //Firebird Database Config
                    if (type.equalsIgnoreCase("FB_Database")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("connectionString", tempLine[1]);
                        connectionInfo.put("login", tempLine[2]);
                        connectionInfo.put("password", tempLine[3]);
                        fbDatabase.put(tempLine[0], connectionInfo);
                    } //MySQL Database Config
                    else if (type.equalsIgnoreCase("MySQL_Database")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("connectionString", tempLine[1]);
                        connectionInfo.put("loginString", tempLine[2]);
                        mySqlDatabase.put(tempLine[0], connectionInfo);
                    } //Oracle Database Config
                    else if (type.equalsIgnoreCase("Oracle_Database")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("connectionString", tempLine[1]);
                        connectionInfo.put("login", tempLine[2]);
                        connectionInfo.put("password", tempLine[3]);
                        oracleDatabase.put(tempLine[0], connectionInfo);
                    } //Ninja Login Info
                    else if (type.equalsIgnoreCase("Ninja")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("login", tempLine[0]);
                        connectionInfo.put("password", tempLine[1]);
                        ninja.put(tempLine[0], connectionInfo);
                    } //Salesforce Login Info
                    else if (type.equalsIgnoreCase("Salesforce")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("login", tempLine[0]);
                        connectionInfo.put("password", tempLine[1]);
                        connectionInfo.put("clientID", tempLine[2]);
                        connectionInfo.put("clientSecret", tempLine[3]);
                        salesforce.put(tempLine[0], connectionInfo);
                    } //Email Login Info
                    else if (type.equalsIgnoreCase("E-Mail")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("email", tempLine[0]);
                        connectionInfo.put("password", tempLine[1]);
                        email.put(tempLine[0], connectionInfo);
                    }//SFTP info
                    else if (type.equalsIgnoreCase("SFTP")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("host", tempLine[1]);
                        connectionInfo.put("port", tempLine[2]);
                        connectionInfo.put("login", tempLine[3]);
                        connectionInfo.put("password", tempLine[4]);
                        connectionInfo.put("remotePath", tempLine[5]);
                        sftp.put(tempLine[0], connectionInfo);
                    }//RingCenteal
                    else if (type.equalsIgnoreCase("RingCentral")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("extension", tempLine[0]);
                        connectionInfo.put("authorizationHeader", tempLine[1]);
                        connectionInfo.put("requestBody", tempLine[2]);
                        ringCentral.put(tempLine[0], connectionInfo);
                    }//Laker API
                    else if (type.equalsIgnoreCase("LakerAPI")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("authorization", tempLine[1]);
                        lakerAPI.put(tempLine[0], connectionInfo);
                    }
                    //Sql Server
                    else if (type.equalsIgnoreCase("SqlServer_Database")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("connectionString", tempLine[1]);
                        sqlServerDatabase.put(tempLine[0], connectionInfo);
                    }
                    //Letterhub Api
                    else if (type.equalsIgnoreCase("LetterhubApi")) {
                        String[] tempLine = line.split("~");
                        Map<String, String> connectionInfo = new HashMap<String, String>();
                        connectionInfo.put("ApiRootURI", tempLine[1]);
                        connectionInfo.put("CreateBatch", tempLine[2]);
                        connectionInfo.put("GetBatchDetails", tempLine[3]);
                        connectionInfo.put("CreateBatchURL", tempLine[4]);
                        connectionInfo.put("UserAccessKey", tempLine[5]);
                        connectionInfo.put("UserAPIKeyValue", tempLine[6]);
                        connectionInfo.put("ApplicationName", tempLine[7]);
                        letterhubApi.put(tempLine[0], connectionInfo);
                    }

                }
            }
            configFileMap.put("fbDatabase", fbDatabase);
            configFileMap.put("mySqlDatabase", mySqlDatabase);
            configFileMap.put("oracleDatabase", oracleDatabase);
            configFileMap.put("ninja", ninja);
            configFileMap.put("salesforce", salesforce);
            configFileMap.put("email", email);
            configFileMap.put("sftp", sftp);
            configFileMap.put("ringCentral", ringCentral);
            configFileMap.put("lakerAPI", lakerAPI);
            configFileMap.put("sqlServerDatabase", sqlServerDatabase);
            configFileMap.put("letterhubApi", letterhubApi);
            
            return configFileMap;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }

        return configFileMap;
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();

            }
            //System.out.println(sb.toString());
            return sb.toString();
        } finally {
            br.close();
        }
    }
}
