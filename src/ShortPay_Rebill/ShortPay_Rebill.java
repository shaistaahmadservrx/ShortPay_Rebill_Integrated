/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

//import com.gargoylesoftware.htmlunit.javascript.host.URL;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.File;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author toprisiu
 */
public class ShortPay_Rebill {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //int threadCount = Integer.parseInt(4);
        //int threadCount = 4;
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

        String pdfLetter = "Letter.pdf";


        try {
            Config.loadConfigFile("C:\\PW\\pw.txt");

            /*
            String jobID = "4";

            String scheduleID = Database_Queries.checkIfJobScheduled(jobID);

            if (scheduleID.equalsIgnoreCase("")) {
                System.exit(0);
            }
            Database_Queries.setJobAsStarted(scheduleID);*/
            
            Utilities.addToLog("Finding invoices to process");
            ArrayList invoicesToProcess = Database_Queries.findShortPayRebillWeeklyInvoices();
            //ArrayList claimsForStatusChange = Database_Queries.findShortPayRebillWeeklyInvoices();
            CookieManager cookies = httpUtilities.getCookie();
            String accessToken = httpUtilities.getAccessToken();

            //Create working folders
            Utilities.createFolderIfMissing(workingDirInvoice);
            Utilities.createFolderIfMissing(workingDirMergeLetter);
            Utilities.createFolderIfMissing(completedDir);

            //Clean working folders if they were already there
            Utilities.cleanDirectory(new File(workingDirInvoice));
            Utilities.cleanDirectory(new File(workingDirMergeLetter));

            int numberOfInvoices = invoicesToProcess.size();
            //System.out.println(numberOfInvoices);
            //System.exit(0);
            int i = 1;
            for (Object invoice : invoicesToProcess) {
                //if (i % 50 == 0)
                //{
                //accessToken = httpUtilities.getAccessToken();
                //cookies = httpUtilities.getCookie();
                //}

                Utilities.addToLog("Starting on Invoice # " + invoice.toString() + " " + i + "/" + numberOfInvoices);
                String invoicesLocation = httpUtilities.downloadFileAndCheckIntegrity(invoice.toString(), workingDirInvoice, cookies);
                PdfUtilities.addMergeLetterSingleInvoice(pdfLetter, invoicesLocation, workingDirMergeLetter);
                ArrayList claimsToChangeStatus = Database_Queries.getAssociatedClaimIDs(invoice.toString());

                for (Object claim : claimsToChangeStatus) {
                    httpUtilities.addNinjaNoteAndStatusAPI(claim.toString(), "42", "​ADDITIONAL PAYMENT RECON SENT", accessToken);
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

            
            //Database_Queries.setJobAsCompleted(scheduleID);
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static void copyFileUsingJava7Files(File source, File dest)
            throws Exception {
        Files.copy(source.toPath(), dest.toPath());

    }

}
