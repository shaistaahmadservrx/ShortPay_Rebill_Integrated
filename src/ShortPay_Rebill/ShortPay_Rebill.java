/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

//import com.gargoylesoftware.htmlunit.javascript.host.URL;

import com.gargoylesoftware.htmlunit.CookieManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


/**
 *
 * @author toprisiu
 */
public class ShortPay_Rebill {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Need to get the current date for the finished folder
        Calendar cal = Calendar.getInstance();

        //Gets the month and then increases it by 1 so Jan = 1
        int month = cal.get(Calendar.MONTH) + 1;
        //Create the filename dependant on the current date.
        String currentDate = "" + month + '-' + cal.get(Calendar.DATE) + '-' + cal.get(Calendar.YEAR);
        String currentDateTime = currentDate + " " + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND);
        String sunday = Utilities.getSundayDate();

        String workingDirInvoice = "Working - Invoices\\";
        String workingDirMergeLetter = "Working - Merge Letter\\";
        String completedDir = "Completed - Zip\\";
        String zipFile = "ShortPay Rebill " + currentDate + ".zip";

        String networkFolder = "\\\\fs-01\\toprisiu\\Invoices\\" + sunday + " Weekly Billing\\Zip Files To Process\\";
        //String networkFolder = "C:\\Invoices\\" + sunday + " Weekly Billing\\Zip Files To Process\\"; // for testing
        String pdfLetter = "Letter.pdf";
        String invoicesLocation;


        try {
            Config.loadConfigFile("C:\\PW\\pw.txt");

            Utilities.addToLog("Finding invoices to process");
            ArrayList invoicesToProcess = Database_Queries.findShortPayRebillWeeklyInvoices(); //comment for testing
            CookieManager cookies = httpUtilities.getCookie();
            String accessToken = httpUtilities.getAccessToken();

            //Create working folders
            Utilities.createFolderIfMissing(workingDirInvoice);
            Utilities.createFolderIfMissing(workingDirMergeLetter);
            Utilities.createFolderIfMissing(completedDir);

            //Clean working folders if they were already there
            Utilities.cleanDirectory(new File(workingDirInvoice));
            Utilities.cleanDirectory(new File(workingDirMergeLetter));

            //testing
            //ArrayList invoicesToProcess = new ArrayList();
            //invoicesToProcess.add(320554);
            int numberOfInvoices = invoicesToProcess.size();

            int i = 1;
            for (Object invoice : invoicesToProcess) {

                try {
                    Utilities.addToLog("Starting on Invoice # " + invoice.toString() + " " + i + "/" + numberOfInvoices);
                    invoicesLocation = httpUtilities.downloadFileAndCheckIntegrity(invoice.toString(), workingDirInvoice, cookies);
                } catch (Exception ex) {
                    Utilities.addExceptionToLog(ex);
                    Utilities.LogError(invoice.toString(), "Download Failed", ex, "ShortPay Rebills");
                    continue;
                }


                try {
                    PdfUtilities.addMergeLetterSingleInvoice(pdfLetter, invoicesLocation, workingDirMergeLetter);
                } catch (Exception ex) {
                    Utilities.LogError(invoice.toString(), "Merge Failed", ex, "ShortPay Rebills");
                    continue;
                }


                try {
                    ArrayList claimsToChangeStatus = Database_Queries.getAssociatedClaimIDs(invoice.toString());

                    for (Object claim : claimsToChangeStatus) {
                        try {
                            httpUtilities.addNinjaNoteAndStatusAPI(claim.toString(), "42", "​ADDITIONAL PAYMENT RECON SENT", accessToken);
                        } catch (Exception ex) {
                            Utilities.LogError(invoice.toString(), "Add Ninja Note Failed for claim " + claim.toString(), ex, "ShortPay Rebills");
                            continue;
                        }
                    }
                }
                catch (Exception ex) {
                    Utilities.LogError(invoice.toString(), "Get Associated Claim Ids Failed", ex, "ShortPay Rebills");
                    Utilities.addExceptionToLog(ex);
                    continue;
                }

                try{

                    PdfUtilities.GetInvoicenumber(invoicesLocation, workingDirInvoice);
                }
                catch(Exception ex)
                {
                    Utilities.LogError(invoice.toString(), "Failed to send invoice to letterhub", ex, "ShortPay Rebills");
                    continue;
                }

                i++;
            }
            //Zip files
            Utilities.addToLog("Zipping Pdfs");
            Utilities.zipFolder(workingDirInvoice, completedDir + zipFile);

            //Create network folder if missing
            Utilities.createFolderIfMissing(networkFolder);

            //Move zip file to network folder
            Utilities.addToLog("Copying Zip file to network drive");
            Utilities.copyFolderNew(new File(completedDir + zipFile), new File(networkFolder + zipFile));
        }
        catch (Exception e)
        {
            Utilities.addExceptionToLog(e);
            Utilities.LogException(e);
        }
    }
}
