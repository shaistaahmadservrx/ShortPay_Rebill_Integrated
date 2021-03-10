/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author toprisiu
 */
public class Database_Queries {

    public static ArrayList findUnprocessedInvoices() throws SQLException, ClassNotFoundException {
        ArrayList<String> invoicesToProcess = null;
        try
        {
            Connection connFireBird = DatabaseConnections.connectToFirebird();
            invoicesToProcess = new ArrayList<String>();

            Statement stmtFB = connFireBird.createStatement();
            String firebirdQuery = "SELECT PF_ID, ORIG_FILENAME\n"
                    + "FROM PROCESS_FILE a\n"
                    + "WHERE ORIG_FILENAME LIKE '%EOB_FL_%'";
            ResultSet rs = stmtFB.executeQuery(firebirdQuery);
            while (rs.next()) {
                invoicesToProcess.add(rs.getString(1) + "*" + rs.getString(2));
            }
            connFireBird.close();
            return invoicesToProcess;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
            //return invoicesToProcess;
        }
    }

    public static ArrayList findNotReceivedInvoices() {

        ArrayList<String> invoicesToProcess = null;
        try
        {
            Connection connFireBird = DatabaseConnections.connectToFirebird();
            invoicesToProcess = new ArrayList<String>();

            Statement stmtFB = connFireBird.createStatement();
            String firebirdQuery = "SELECT DISTINCT(CLAIM.LAKER_INVOICE_NO)\n"
                    + "FROM CLAIM\n"
                    + "WHERE (CLAIM.FK_CLAIM_STATUS_ID = 2\n"
                    + "OR CLAIM.FK_CLAIM_STATUS_ID = 31) \n"
                    + "AND CLAIM.INVOICE_DATE >= '2016-06-01' \n"
                    + "AND CLAIM.INVOICE_DATE <= '2016-08-27'";
            ResultSet rs = stmtFB.executeQuery(firebirdQuery);
            while (rs.next()) {
                invoicesToProcess.add(rs.getString(1));
            }
            connFireBird.close();
            return invoicesToProcess;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return invoicesToProcess;
        }
    }

    public static ArrayList getNotReceivedInvoicesAssociatedClaims() {
        ArrayList<String> invoicesToProcess = null;
        try
        {
            Connection connFireBird = DatabaseConnections.connectToFirebird();
            invoicesToProcess = new ArrayList<String>();

            Statement stmtFB = connFireBird.createStatement();
            String firebirdQuery = "SELECT DISTINCT(CLAIM.CLAIM_ID)\n"
                    + "FROM CLAIM\n"
                    + "WHERE (CLAIM.FK_CLAIM_STATUS_ID = 2\n"
                    + "OR CLAIM.FK_CLAIM_STATUS_ID = 31) \n"
                    + "AND CLAIM.INVOICE_DATE >= '2016-06-01' \n"
                    + "AND CLAIM.INVOICE_DATE <= '2016-08-27' \n";
            ResultSet rs = stmtFB.executeQuery(firebirdQuery);
            while (rs.next()) {
                invoicesToProcess.add(rs.getString(1));
            }
            connFireBird.close();
            return invoicesToProcess;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return invoicesToProcess;
        }
    }

    public static String[] findLetterMergeData(String invoiceNumber) {
        Connection connMySql = DatabaseConnections.connectToMySQL();
        String[] carrierInfo = new String[8];
        try {
            Statement stmtMySql = connMySql.createStatement();
            String mySqlQuery = "SELECT \n"
                    + "    TRIM(IFNULL(portal_data.ar_account.ACCOUNT_NAME, '')) AS AccountName,\n"
                    + "    CONCAT(IFNULL(portal_data.ar_account.ACCT_ADDRESS1, ''),\n"
                    + "            IFNULL(portal_data.ar_account.ACCT_ADDRESS2, '')) AS Address,\n"
                    + "    CONCAT(IFNULL(portal_data.ar_account.ACCT_CITY, ''),\n"
                    + "            ', ',\n"
                    + "            IFNULL(portal_data.ar_account.ACCT_STATE, ''),\n"
                    + "            portal_data.ar_account.ACCT_ZIPCODE) AS ThirdLine,\n"
                    + "    CONCAT(portal_data.claim.L_CLAIMANT_LNAME_FULL,\n"
                    + "            ', ',\n"
                    + "            portal_data.claim.L_CLAIMANT_FNAME_FULL) AS Full_Name,\n"
                    + "    portal_data.claim.LAKER_CLAIM_ID,\n"
                    + "    portal_data.claim.RX_DATE,\n"
                    + "    portal_data.claim.LAKER_INVOICE_NO,\n"
                    + "    SUM(IF(portal_data.claim.TOTALRECEIPT > portal_data.claim.TOTALBILLED,\n"
                    + "        0,\n"
                    + "        portal_data.claim.TOTALBILLED - portal_data.claim.TOTALRECEIPT)) AS Outstanding\n"
                    + "FROM\n"
                    + "    portal_data.ar_account\n"
                    + "        LEFT JOIN\n"
                    + "    portal_data.claim ON portal_data.ar_account.AR_ID = portal_data.claim.FK_AR_ID\n"
                    + "WHERE\n"
                    + "    portal_data.claim.LAKER_INVOICE_NO = " + invoiceNumber + "\n"
                    + "GROUP BY LAKER_INVOICE_NO\n"
                    + "LIMIT 1";
            ResultSet rs = stmtMySql.executeQuery(mySqlQuery);
            while (rs.next()) {
                for (int i = 0; i < 8; i++) {
                    carrierInfo[i] = rs.getString(i + 1);
                }

            }
            connMySql.close();
            return carrierInfo;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return carrierInfo;
        }
    }

    public static String[] GetInvoiceAddress(String invoiceNumber) {
        Connection connMySql = DatabaseConnections.connectToMySQL();
        String[] carrierInfo = new String[8];
        try {
            Statement stmtMySql = connMySql.createStatement();
            String mySqlQuery = "select b.LAKER_INVOICE_NO, b.INVOICE_DATE, a.ACCOUNT_NAME, " +
                                "a.ACCT_ADDRESS1,a.ACCT_ADDRESS2,a.ACCT_CITY,a.ACCT_STATE,a.ACCT_ZIPCODE " +
                                "from ar_account a, claim b where a.AR_ID = b.FK_AR_ID and " +
                                "b.LAKER_INVOICE_NO = " + invoiceNumber +
                                " LIMIT 1";
            ResultSet rs = stmtMySql.executeQuery(mySqlQuery);
            while (rs.next()) {
                for (int i = 0; i < 8; i++) {
                    carrierInfo[i] = rs.getString(i + 1);
                }

            }
            connMySql.close();
            return carrierInfo;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return carrierInfo;
        }
    }

    public static ArrayList findInvoicesToRebill(String sqlQuerLoc) {
        Connection connMySql = DatabaseConnections.connectToMySQL();
        ArrayList<String> invoicesToRebill = new ArrayList<String>();
        Utilities.addToLog("Finding Invoices");
        try {

            Statement stmtMySql = connMySql.createStatement();
            String mySqlQuery = Utilities.readFile(sqlQuerLoc);

            ResultSet rs = stmtMySql.executeQuery(mySqlQuery);
            //System.out.println(mySqlQuery);
            while (rs.next()) {
                invoicesToRebill.add(rs.getString(1) + "*" + rs.getString(2) + "*" + rs.getString(3));
            }
            connMySql.close();
            return invoicesToRebill;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return invoicesToRebill;
        }
    }

    public static ArrayList testFB(ArrayList invoices) {
        ArrayList<String> invoicesChangeStatus = null;
        try
        {
            Connection connFirebird = DatabaseConnections.connectToFirebird();
            invoicesChangeStatus = new ArrayList<String>();
            Utilities.addToLog("Finding Invoices");

            String invoicesToQuery = "";
            for (int i = 0; i < invoices.size(); i++) {
                String temp[] = invoices.get(i).toString().split("\\*");
                invoicesToQuery = invoicesToQuery + temp[1];
                //System.out.println("i: "+i);
                if (i % 1000 == 0) {

                    String firebirdQuery = "SELECT CLAIM_ID\n"
                            + "FROM CLAIM LEFT JOIN AR_ACCOUNT ON CLAIM.FK_AR_ID = AR_ACCOUNT.AR_ID\n"
                            + "WHERE AR_ACCOUNT.ACCOUNT_NAME NOT LIKE '%PIP%' AND L_CLAIMANT_LNAME NOT LIKE '%PIP%'\n"
                            + "AND CLAIM.COMPOUND_CODE !=2\n"
                            + "AND AR_ACCOUNT.AR_ID != 4886\n"
                            + "AND CLAIM.TOTALBILLED != 0\n"
                            + "AND (((CLAIM.TOTALRECEIPT/CLAIM.TOTALBILLED) < .98) OR ((CLAIM.TOTALBILLED - CLAIM.TOTALRECEIPT) > 11))\n"
                            + "AND CLAIM.FK_CLAIM_STATUS_ID != 42\n"
                            + "AND CLAIM.LAKER_INVOICE_NO IN (" + invoicesToQuery + ")";
                    Statement stmtFirebird = connFirebird.createStatement();

                    ResultSet rs = stmtFirebird.executeQuery(firebirdQuery);
                    while (rs.next()) {
                        invoicesChangeStatus.add(rs.getString(1));
                    }
                    invoicesToQuery = "";
                } else {
                    invoicesToQuery = invoicesToQuery + ",";
                }
            }
            if (!invoicesToQuery.isEmpty()) {
                invoicesToQuery = invoicesToQuery.substring(0, invoicesToQuery.length() - 1);
                String firebirdQuery = "SELECT CLAIM_ID\n"
                        + "FROM CLAIM LEFT JOIN AR_ACCOUNT ON CLAIM.FK_AR_ID = AR_ACCOUNT.AR_ID\n"
                        + "WHERE AR_ACCOUNT.ACCOUNT_NAME NOT LIKE '%PIP%' AND L_CLAIMANT_LNAME NOT LIKE '%PIP%'\n"
                        + "AND CLAIM.COMPOUND_CODE !=2\n"
                        + "AND AR_ACCOUNT.AR_ID != 4886\n"
                        + "AND CLAIM.TOTALBILLED != 0\n"
                        + "AND (((CLAIM.TOTALRECEIPT/CLAIM.TOTALBILLED) < .98) OR ((CLAIM.TOTALBILLED - CLAIM.TOTALRECEIPT) > 11))\n"
                        + "AND CLAIM.FK_CLAIM_STATUS_ID != 42\n"
                        + "AND CLAIM.LAKER_INVOICE_NO IN (" + invoicesToQuery + ")";
                Statement stmtFirebird = connFirebird.createStatement();

                ResultSet rs = stmtFirebird.executeQuery(firebirdQuery);
                while (rs.next()) {
                    invoicesChangeStatus.add(rs.getString(1));
                }
            }
            //System.out.println(firebirdQuery);

            connFirebird.close();
            return invoicesChangeStatus;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return invoicesChangeStatus;
        }
    }

    public static ArrayList findShortPayRebillWeeklyInvoices() throws SQLException, ClassNotFoundException {

        ArrayList invoicesToProcess = null;
        try
        {
            Connection connFirebird = DatabaseConnections.connectToFirebird();
            invoicesToProcess = new ArrayList();
            String[] dates = Utilities.getDatesForLastWeek();


            String firebirdQuery = "SELECT DISTINCT\n"
                    + "                        (LAKER_INVOICE_NO)\n"
                    + "                    FROM\n"
                    + "                        CLAIM\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_ACCOUNT ON CLAIM.FK_AR_ID = AR_ACCOUNT.AR_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        CLAIM_ACTIVITY ON CLAIM.CLAIM_ID = CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        AR_RECEIPT_ALLOC ON CLAIM.CLAIM_ID = AR_RECEIPT_ALLOC.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_RECEIPT ON AR_RECEIPT_ALLOC.FK_ARRECEIPT_ID = AR_RECEIPT.ARR_ID\n"
                    + "                    WHERE\n"
                    + "                        AR_ACCOUNT.ACCOUNT_NAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_ACCOUNT.AR_ID NOT IN (4886, 11278)\n"
                    + "                            AND CLAIM.VENUE_STATE NOT IN ('ME', 'MO', 'NE', 'NH', 'NJ', 'SD', 'UT', 'VA', 'MD', 'DC', 'IA', 'IL', 'IN', 'WV',\n"
                    + "                                 'CA', 'AR', 'KY', 'MT', 'NC', 'NV', 'OH', 'OR', 'RI', 'NY', 'CT', 'MO')\n"
                    + "                            AND claim.L_CLAIMANT_LNAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_RECEIPT_ALLOC.ALLOC_AMOUNT != 0\n"
                    + "                            AND CLAIM.CLAIM_ID NOT IN (SELECT CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                                FROM CLAIM_ACTIVITY\n"
                    + "                                WHERE CLAIM_ACTIVITY.ACTIVITY_NOTE LIKE '%Negotiated- Check Mailed%')\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE >= '" + dates[0] + "'\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE < '" + dates[1] + "'\n"
                    + "                            AND (CLAIM.COMPOUND_CODE != 2\n"
                    + "                            or CLAIM.COMPOUND_CODE IS NULL)\n"
                    + "                            AND (CLAIM.FK_AP_ID != 2\n"
                    + "                            AND CLAIM.LAKER_CLAIM_ID NOT LIKE '007548%')\n"
                    + "                            AND CLAIM.FK_CLAIM_STATUS_ID = 3\n"
                    + "                            AND AR_ACCOUNT.AR_ID != 4886\n"
                    + "                            AND CLAIM.TOTALBILLED != 0\n"
                    + "                            AND CLAIM.TOTALRECEIPT > 0\n"
                    + "                            AND (((CLAIM.TOTALRECEIPT / CLAIM.TOTALBILLED) < .98)\n"
                    + "                            AND ((CLAIM.TOTALBILLED - CLAIM.TOTALRECEIPT) > 11))\n"
                    + "UNION\n"
                    + "SELECT DISTINCT\n"
                    + "                        (LAKER_INVOICE_NO)\n"
                    + "                    FROM\n"
                    + "                        CLAIM\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_ACCOUNT ON CLAIM.FK_AR_ID = AR_ACCOUNT.AR_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        CLAIM_ACTIVITY ON CLAIM.CLAIM_ID = CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        AR_RECEIPT_ALLOC ON CLAIM.CLAIM_ID = AR_RECEIPT_ALLOC.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_RECEIPT ON AR_RECEIPT_ALLOC.FK_ARRECEIPT_ID = AR_RECEIPT.ARR_ID\n"
                    + "                            LEFT JOIN"
                    + "                        AP_ACCOUNT ON CLAIM.FK_AP_ID = AP_ACCOUNT.AP_ID"
                    + "                    WHERE\n"
                    + "                        AP_ACCOUNT.AP_ID = 210\n"
                    + "                            AND AR_ACCOUNT.ACCOUNT_NAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_ACCOUNT.AR_ID NOT IN (4886, 11278)\n"
                    + "                            AND CLAIM.VENUE_STATE NOT IN ('ME', 'MO', 'NE', 'NH', 'NJ', 'SD', 'UT', 'VA', 'MD', 'DC', 'IA', 'IL', 'IN', 'WV',\n"
                    + "                                 'CA', 'AR', 'KY', 'MT', 'NC', 'NV', 'OH', 'OR', 'RI', 'NY', 'CT', 'MO')\n"
                    + "                            AND claim.L_CLAIMANT_LNAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_RECEIPT_ALLOC.ALLOC_AMOUNT != 0\n"
                    + "                            AND CLAIM.CLAIM_ID NOT IN (SELECT CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                                FROM CLAIM_ACTIVITY\n"
                    + "                                WHERE CLAIM_ACTIVITY.ACTIVITY_NOTE LIKE '%Negotiated- Check Mailed%')\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE >= '" + dates[0] + "'\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE < '" + dates[1] + "'\n"
                    + "                            AND (CLAIM.COMPOUND_CODE = 2)\n"
                    + "                            AND (CLAIM.FK_AP_ID != 2\n"
                    + "                            AND CLAIM.LAKER_CLAIM_ID NOT LIKE '007548%')\n"
                    + "                            AND CLAIM.FK_CLAIM_STATUS_ID = 3\n"
                    + "                            AND AR_ACCOUNT.AR_ID != 4886\n"
                    + "                            AND CLAIM.TOTALBILLED != 0\n"
                    + "                            AND CLAIM.TOTALRECEIPT > 0\n"
                    + "                            AND (((CLAIM.TOTALRECEIPT / CLAIM.TOTALBILLED) < .98)\n"
                    + "                            AND ((CLAIM.TOTALBILLED - CLAIM.TOTALRECEIPT) > 11))\n";
            System.out.println(firebirdQuery);
            Statement stmtFirebird = connFirebird.createStatement();

            ResultSet rs = stmtFirebird.executeQuery(firebirdQuery);

            while (rs.next()) {
                invoicesToProcess.add(rs.getString(1));
            }
            return invoicesToProcess;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
            //return invoicesToProcess;
        }
    }

    public static ArrayList getProcessFileIDs(String invoiceNumber) throws SQLException, ClassNotFoundException {
        ArrayList invoiceDownloadInfo = null;
        try
        {
            Connection connFirebird = DatabaseConnections.connectToFirebird();
            invoiceDownloadInfo = new ArrayList();
            String[] dates = Utilities.getDatesForLastWeek();
            String firebirdQuery = "SELECT PF_ID, ORIG_FILENAME FROM PROCESS_FILE WHERE LAKER_INV_NO = " + invoiceNumber + " ORDER BY PF_ID DESC";
            Statement stmtFirebird = connFirebird.createStatement();
            //System.out.println(firebirdQuery);
            ResultSet rs = stmtFirebird.executeQuery(firebirdQuery);

            while (rs.next()) {
                invoiceDownloadInfo.add(rs.getString(1) + "*" + rs.getString(2));
            }
            return invoiceDownloadInfo;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            //return invoiceDownloadInfo;
            throw e;
        }
    }

    public static ArrayList getAssociatedClaimIDs(String invoiceNumber) throws SQLException, ClassNotFoundException {
        ArrayList invoicesToProcess = null;
        try
        {
            Connection connFirebird = DatabaseConnections.connectToFirebird();
            invoicesToProcess = new ArrayList();
            String[] dates = Utilities.getDatesForLastWeek();
            String firebirdQuery = "SELECT DISTINCT\n"
                    + "                        (CLAIM_ID)\n"
                    + "                    FROM\n"
                    + "                        CLAIM\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_ACCOUNT ON CLAIM.FK_AR_ID = AR_ACCOUNT.AR_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        CLAIM_ACTIVITY ON CLAIM.CLAIM_ID = CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        AR_RECEIPT_ALLOC ON CLAIM.CLAIM_ID = AR_RECEIPT_ALLOC.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_RECEIPT ON AR_RECEIPT_ALLOC.FK_ARRECEIPT_ID = AR_RECEIPT.ARR_ID\n"
                    + "                    WHERE\n"
                    + "                        AR_ACCOUNT.ACCOUNT_NAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_ACCOUNT.AR_ID NOT IN (4886, 11278)\n"
                    + "                            AND CLAIM.VENUE_STATE NOT IN ('ME', 'MO', 'NE', 'NH', 'NJ', 'SD', 'UT', 'VA', 'MD', 'DC', 'IA', 'IL', 'IN', 'WV')\n"
                    + "                            AND claim.L_CLAIMANT_LNAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_RECEIPT_ALLOC.ALLOC_AMOUNT != 0\n"
                    + "                            AND CLAIM.CLAIM_ID NOT IN (SELECT CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                                FROM CLAIM_ACTIVITY\n"
                    + "                                WHERE CLAIM_ACTIVITY.ACTIVITY_NOTE LIKE '%Negotiated- Check Mailed%')\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE >= '" + dates[0] + "'\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE < '" + dates[1] + "'\n"
                    + "                            AND (CLAIM.COMPOUND_CODE != 2\n"
                    + "                            or CLAIM.COMPOUND_CODE IS NULL)\n"
                    + "                            AND (CLAIM.FK_AP_ID != 2\n"
                    + "                            AND CLAIM.LAKER_CLAIM_ID NOT LIKE '007548%')\n"
                    + "                            AND CLAIM.FK_CLAIM_STATUS_ID = 3\n"
                    + "                            AND CLAIM.LAKER_INVOICE_NO = " + invoiceNumber + "\n"
                    + "                            AND AR_ACCOUNT.AR_ID != 4886\n"
                    + "                            AND CLAIM.TOTALBILLED != 0\n"
                    + "                            AND CLAIM.TOTALRECEIPT > 0\n"
                    + "                            AND (((CLAIM.TOTALRECEIPT / CLAIM.TOTALBILLED) < .98)\n"
                    + "                            AND ((CLAIM.TOTALBILLED - CLAIM.TOTALRECEIPT) > 11))\n"
                    + " UNION\n"
                    + "SELECT DISTINCT\n"
                    + "                        (CLAIM_ID)\n"
                    + "                    FROM\n"
                    + "                        CLAIM\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_ACCOUNT ON CLAIM.FK_AR_ID = AR_ACCOUNT.AR_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        CLAIM_ACTIVITY ON CLAIM.CLAIM_ID = CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN \n"
                    + "                        AR_RECEIPT_ALLOC ON CLAIM.CLAIM_ID = AR_RECEIPT_ALLOC.FK_CLAIM_ID\n"
                    + "                            LEFT JOIN\n"
                    + "                        AR_RECEIPT ON AR_RECEIPT_ALLOC.FK_ARRECEIPT_ID = AR_RECEIPT.ARR_ID\n"
                    + "                    WHERE\n"
                    + "                         CLAIM.FK_AP_ID = 210\n"
                    + "                            AND AR_ACCOUNT.ACCOUNT_NAME NOT LIKE '%PIP%'\n"
                    + "                            AND claim.L_CLAIMANT_LNAME NOT LIKE '%PIP%'\n"
                    + "                            AND AR_ACCOUNT.AR_ID NOT IN (4886, 11278)\n"
                    + "                            AND CLAIM.VENUE_STATE NOT IN ('ME', 'MO', 'NE', 'NH', 'NJ', 'SD', 'UT', 'VA', 'MD', 'DC', 'IA', 'IL', 'IN', 'WV')\n"
                    + "                            AND AR_RECEIPT_ALLOC.ALLOC_AMOUNT != 0\n"
                    + "                            AND CLAIM.CLAIM_ID NOT IN (SELECT CLAIM_ACTIVITY.FK_CLAIM_ID\n"
                    + "                                FROM CLAIM_ACTIVITY\n"
                    + "                                WHERE CLAIM_ACTIVITY.ACTIVITY_NOTE LIKE '%Negotiated- Check Mailed%')\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE >= '" + dates[0] + "'\n"
                    + "                            AND AR_RECEIPT.BANK_DEPOSIT_DATE < '" + dates[1] + "'\n"
                    + "                            AND (CLAIM.COMPOUND_CODE = 2)\n"
                    + "                            AND (CLAIM.FK_AP_ID != 2\n"
                    + "                            AND CLAIM.LAKER_CLAIM_ID NOT LIKE '007548%')\n"
                    + "                            AND CLAIM.FK_CLAIM_STATUS_ID = 3\n"
                    + "                            AND CLAIM.LAKER_INVOICE_NO = " + invoiceNumber + "\n"
                    + "                            AND AR_ACCOUNT.AR_ID != 4886\n"
                    + "                            AND CLAIM.TOTALBILLED != 0\n"
                    + "                            AND CLAIM.TOTALRECEIPT > 0\n"
                    + "                            AND (((CLAIM.TOTALRECEIPT / CLAIM.TOTALBILLED) < .98)\n"
                    + "                            AND ((CLAIM.TOTALBILLED - CLAIM.TOTALRECEIPT) > 11))";
            Statement stmtFirebird = connFirebird.createStatement();
            //System.out.println(firebirdQuery);
            //System.out.println(firebirdQuery);
            ResultSet rs = stmtFirebird.executeQuery(firebirdQuery);

            while (rs.next()) {
                invoicesToProcess.add(rs.getString(1));
            }
            return invoicesToProcess;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
            //return invoicesToProcess;
        }
    }

    public static String checkIfJobScheduled(String jobID) {
        Connection connMySql = DatabaseConnections.connectToMySQL();
        String scheduleID = "";
        try {
            Statement stmtMySql = connMySql.createStatement();
            String mySqlQuery = "SELECT \n"
                    + "    SCHEDULE_ID\n"
                    + "FROM\n"
                    + "    cron_jobs.schedule\n"
                    + "WHERE\n"
                    + "    FK_JOB_ID = " + jobID + " AND FK_STATUS_ID = 1";
            ResultSet rs = stmtMySql.executeQuery(mySqlQuery);
            if (!rs.next()) {
                return scheduleID;
            }

            scheduleID = rs.getString(1);
            connMySql.close();
            return scheduleID;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return scheduleID;
        }
    }

    public static void setJobAsStarted(String scheduleID) {
        Connection connMySql = DatabaseConnections.connectToMySQL();

        try {
            Statement stmtMySql = connMySql.createStatement();
            String mySqlQuery = "UPDATE cron_jobs.schedule\n"
                    + "SET FK_STATUS_ID = 2, COMPLETED_TIMESTAMP = NOW()\n"
                    + "WHERE SCHEDULE_ID = " + scheduleID;
            stmtMySql.executeUpdate(mySqlQuery);

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);

        }
    }

    public static void setJobAsCompleted(String scheduleID) {
        Connection connMySql = DatabaseConnections.connectToMySQL();

        try {
            Statement stmtMySql = connMySql.createStatement();
            String mySqlQuery = "UPDATE cron_jobs.schedule\n"
                    + "SET FK_STATUS_ID = 3, COMPLETED_TIMESTAMP = NOW()\n"
                    + "WHERE SCHEDULE_ID = " + scheduleID;
            stmtMySql.executeUpdate(mySqlQuery);

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);

        }
    }
}
