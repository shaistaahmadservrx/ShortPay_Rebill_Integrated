/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

import java.sql.Connection;
import java.sql.Statement;

/**
 *
 * @author toprisiu
 */
public class Invoice_Reader {

    public static String[] readFL(String loc, String invoiceNumber) {
        Connection connMySQL = DatabaseConnections.connectToMySQL();
        int drugNumber = 1;
        String items[] = null;
        System.out.println(loc);
        for (int i = 2; i <= PdfUtilities.getPages(loc); i++) {
            
            //First Drug
            String ndc1 = PdfUtilities.getText(loc, 40, 268, 150, 10, i); ///NDC 1
            String rx1 = PdfUtilities.getText(loc, 40, 283, 50, 10, i); //RX 1

            //Remove all empty characters
            ndc1 = ndc1.replaceAll("[^A-Za-z0-9]", "");
            rx1 = rx1.replaceAll("[^A-Za-z0-9]", "");

            if (!ndc1.isEmpty()) {
                try {
                    Statement stmtMySQL = connMySQL.createStatement();
                    String insertStatement = "INSERT INTO invoice_medication_order.order_listing (LAKER_INVOICE, PAGE, DRUG_NUMBER, NDC, RX_NUMBER) "
                            + "Values (" + invoiceNumber + ", "+ i + ", " + 1 + ", '" + ndc1 + "', '" + rx1 + "');";
                    stmtMySQL.executeUpdate(insertStatement);
                    drugNumber++;
                } catch (Exception e) {
                    Utilities.addExceptionToLog(e);
                }
            }
            
            //Second Drug
            String ndc2 = PdfUtilities.getText(loc, 40, 323, 150, 10, i);
            String rx2 = PdfUtilities.getText(loc, 40, 338, 50, 10, i);
            
            //Remove all empty characters
            ndc2 = ndc2.replaceAll("[^A-Za-z0-9]", "");
            rx2 = rx2.replaceAll("[^A-Za-z0-9]", "");
            
            if (ndc2.length() > 4) {
                try {
                    Statement stmtMySQL = connMySQL.createStatement();
                    String insertStatement = "INSERT INTO invoice_medication_order.order_listing (LAKER_INVOICE, PAGE, DRUG_NUMBER, NDC, RX_NUMBER) "
                            + "Values (" + invoiceNumber + ", " + i + ", " + 2 + ", '" + ndc2 + "', '" + rx2 + "');";
                    stmtMySQL.executeUpdate(insertStatement);
                    drugNumber++;
                } catch (Exception e) {
                    Utilities.addExceptionToLog(e);
                }
            }
            
            //Third Drug
            String ndc3 = PdfUtilities.getText(loc, 40, 383, 150, 10, i);
            String rx3 = PdfUtilities.getText(loc, 40, 398, 50, 10, i);
            
            //Remove all empty characters
            ndc3 = ndc3.replaceAll("[^A-Za-z0-9]", "");
            rx3 = rx3.replaceAll("[^A-Za-z0-9]", "");
            
            if (!ndc3.isEmpty()) {
                try {
                    Statement stmtMySQL = connMySQL.createStatement();
                    String insertStatement = "INSERT INTO invoice_medication_order.order_listing (LAKER_INVOICE, PAGE, DRUG_NUMBER, NDC, RX_NUMBER) "
                            + "Values (" + invoiceNumber + ", " + i + ", " + 3 + ", '" + ndc3 + "', '" + rx3 + "');";
                    stmtMySQL.executeUpdate(insertStatement);
                    drugNumber++;
                } catch (Exception e) {
                    Utilities.addExceptionToLog(e);
                }
            }

        }
        return items;

    }
}
