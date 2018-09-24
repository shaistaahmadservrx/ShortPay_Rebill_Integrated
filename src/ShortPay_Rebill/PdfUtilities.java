/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

import java.awt.Rectangle;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author toprisiu
 */
public class PdfUtilities {

    public static boolean copyPDFs(String sourceDirectory, String destDirectory) {
        int x = 0;

        //Makes sure the the listFiles function only grabs .pdfs
        FilenameFilter pdfFileNameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {

                // match path name extension
                if (name.contains(".pdf")) {
                    return true;
                }
                return false;
            }
        };

        //Attempts to Delete the temp folder in case the previous attempt failed to do so
        Utilities.deleteFolder(destDirectory);

        try {
            File dir = new File(sourceDirectory);
            File[] directoryListing = dir.listFiles(pdfFileNameFilter);
            //System.out.println(directoryListing);
            for (int i = 0; i < directoryListing.length; i++) {
                if (directoryListing[i].isFile()) {
                    //System.out.println(directoryListing[i].getName());
                    String[] compare = directoryListing[i].getName().split(" ");
                    //System.out.println(directoryListing[i]);
                    PDDocument doc = PDDocument.load(directoryListing[i]);

                    PDDocument docOriginal = PDDocument.load(directoryListing[i]);
                    File destinationFolder = new File(destDirectory + docOriginal.getNumberOfPages());
                    File destinationFile = new File(destDirectory + docOriginal.getNumberOfPages() + "/" + x);
                    if (!destinationFolder.exists()) {
                        destinationFolder.mkdirs();
                    }
                    Utilities.copyFiles(directoryListing[i], destinationFile);
                    docOriginal.close();
                    doc.close();
                    x++;

                    Utilities.addToLog("PDF Processing Count is " + x);
                } else {
                    Utilities.addToLog("File " + x + " was skipped due to it not bring a pdf file");
                }
            }
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return true;
    }

    public static boolean mergePdfsInFolder(String sourceDirectory, String destDirectory) {
        File sourceDir = new File(sourceDirectory);
        File destDir = new File(destDirectory);

        //If either the source or destination directory do not exists, return false
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        //Get the list of folders in the provided directory
        File[] directoryListing = sourceDir.listFiles();

        //If the directory listing is empty then it returns false so the program can be stopped
        if (directoryListing == null) {
            Utilities.addToLog("There are no new pdfs");
            return false;
        }

        //Itterate through the files in the directory
        for (int dirCount = 0; dirCount < directoryListing.length; dirCount++) {
            try {
                Utilities.addToLog("Folder Count is " + dirCount);
                Utilities.addToLog(directoryListing[dirCount].toString());

                //List all of the files in the subdirectory
                File[] pdfListing = directoryListing[dirCount].listFiles();

                //Get the intended file name
                String[] compare = directoryListing[dirCount].getName().split("/");

                //Set the destination file name
                File pdfDest = new File(destDirectory + "/" + compare[compare.length - 1] + ".pdf");

                //Finds the first file of the group
                int firstFile = 0;
                for (int x = 1; x < pdfListing.length; x++) {
                    //Get the full file name
                    String[] firstCompareFullFilename = pdfListing[firstFile].getName().split("/");
                    String[] secondCompareFullFilename = pdfListing[x].getName().split("/");

                    //Remove the .PDF of it
                    String[] firstCompare = firstCompareFullFilename[firstCompareFullFilename.length - 1].split("\\.");
                    String[] secondCompare = secondCompareFullFilename[secondCompareFullFilename.length - 1].split("\\.");

                    //Compares it all the way through to find the smallerst of the list
                    if (Integer.parseInt(firstCompare[0]) > Integer.parseInt(secondCompare[0])) {
                        firstFile = x;

                    }
                }

                //If the file does not exist, then create it
                if (!pdfDest.exists()) {
                    Utilities.copyFiles(pdfListing[firstFile], pdfDest);
                }

                //Load the destination pdfDocument
                //PDDocument pdfDocumentDest = PDDocument.load(pdfDest);
                //Now itterate through the remainder of the files.
                for (int pdfCount = 0; pdfCount < pdfListing.length; pdfCount++) {
                    if (pdfCount != firstFile) {
                        PDDocument pdfDocumentDest = PDDocument.load(pdfDest);
                        Utilities.addToLog("PDF Count is " + pdfCount + " and PDF File is" + pdfListing[pdfCount].getName());

                        //Load original PDF document
                        PDDocument sourcePDF = PDDocument.load(pdfListing[pdfCount]);

                        //Merge the original with the destination PDF
                        PDFMergerUtility mergePdf = new PDFMergerUtility();
                        //mergePdf.appendDocument(pdfDocumentDest, sourcePDF);
                        mergePdf.appendDocument(pdfDocumentDest, sourcePDF);

                        //Close the source PDF
                        pdfDocumentDest.save(pdfDest);
                        pdfDocumentDest.close();
                        sourcePDF.close();
                    }

                }
                //Save the dest PDF and close it
                //pdfDocumentDest.save(pdfDest);
                //pdfDocumentDest.close();
            } catch (Exception e) {
                Utilities.addExceptionToLog(e);
            }
        }
        return true;
    }

    //Counts the Laker Groupsnums in the provided directory, saves that information to a Map and returns it
    public static Map countFiles(String directory) {
        //Create the map that will hold the information
        Map<String, Integer> groupNumPageCount = new HashMap<String, Integer>();

        try {

            //Gets a listing of all the files found in the provided directory String
            File dir = new File(directory);
            File[] directoryListing = dir.listFiles();

            for (int i = 0; i < directoryListing.length; i++) {

                if (directoryListing[i].isDirectory()) {
                    //Create Temporary Map to hold the recurssive findings
                    Map<String, Integer> groupNumPageCountTemp = new HashMap<String, Integer>();

                    //Saves the Temporary Map for Processing
                    groupNumPageCountTemp = countFiles(directoryListing[i].getName());

                    for (Entry<String, Integer> entry : groupNumPageCountTemp.entrySet()) {
                        Utilities.addToLog(entry.getKey() + "/" + entry.getValue());

                        //Checks to see if the GroupNum has already been counted
                        if (groupNumPageCount.get(entry.getKey()) == null) {
                            //If the GroupNum has not been counted before, it adds the new count
                            groupNumPageCount.put(entry.getKey(), entry.getValue());
                        } else {
                            //If it's already been counted then it adds it to what's currently there
                            groupNumPageCount.put(entry.getKey(), groupNumPageCount.get(entry.getKey()) + entry.getValue());
                        }
                    }
                } else {
                    System.out.println(directoryListing[i].getName());
                    String[] compare = directoryListing[i].getName().split(" ");
                    //Checks and sees if the file it's currently looking at is in the list
                    //Loads the pdf document and it either adds the page number to what's there already 
                    //minus the front page, or it creates a new entry.;
                    PDDocument doc = PDDocument.load(directoryListing[i]);
                    if (groupNumPageCount.get(compare[1]) == null) {
                        groupNumPageCount.put(compare[1], doc.getNumberOfPages() - 1);
                    } else {
                        groupNumPageCount.put(compare[1], groupNumPageCount.get(compare[1]) + doc.getNumberOfPages() - 1);
                    }
                    doc.close();

                }
            }
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return groupNumPageCount;
    }

    public static void displayFiles(File[] files) {
        for (File file : files) {
            System.out.printf("File: %-20s Last Modified:" + new Date(file.lastModified()) + "\n", file.getName());
        }
    }

    public static String getPatientID(String pdfLocation) {
        try {
            File pdfFile = new File(pdfLocation);
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            Rectangle rect = new Rectangle(500, 110, 70, 12);
            stripper.addRegion("regionName", rect);
            PDPage firstPage = (PDPage) document.getPage(0);
            stripper.extractRegions(firstPage);
            String text = stripper.getTextForRegion("regionName");

            //Close all open items
            document.close();
            return text;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return "error";
    }

    public static String getReceivedDate(String pdfLocation) {
        try {
            File pdfFile = new File(pdfLocation);
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            Rectangle rect = new Rectangle(500, 145, 70, 12);
            stripper.addRegion("regionName", rect);
            PDPage firstPage = (PDPage) document.getPage(0);
            stripper.extractRegions(firstPage);
            String text = stripper.getTextForRegion("regionName");

            //Close all open items
            document.close();
            return text;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return "error";
    }

    public static String getCollectedDate(String pdfLocation) {
        try {
            File pdfFile = new File(pdfLocation);
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            Rectangle rect = new Rectangle(500, 135, 70, 12);
            stripper.addRegion("regionName", rect);
            PDPage firstPage = (PDPage) document.getPage(0);
            stripper.extractRegions(firstPage);
            String text = stripper.getTextForRegion("regionName");

            //Close all open items
            document.close();
            return text;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return "error";
    }

    public static String getText(String pdfLocation, int x, int y, int width, int height, int page) {
        try {
            page--;
            //Rectangle Dimensions and location
            Rectangle rect = new Rectangle(x, y, width, height);

            File pdfFile = new File(pdfLocation);
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.addRegion("regionName", rect);
            PDPage firstPage = (PDPage) document.getPage(page);
            //String text = stripper.getText(document);
            // document.
            stripper.extractRegions(firstPage);
            String text = stripper.getTextForRegion("regionName");

            //Close all open items
            document.close();
            return text;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return "error";
    }

    public static String getName(String pdfLocation) {
        try {
            //Rectangle Dimensions and location
            Rectangle rect = new Rectangle(30, 125, 200, 12);

            File pdfFile = new File(pdfLocation);
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.addRegion("regionName", rect);
            PDPage firstPage = (PDPage) document.getPage(0);
            stripper.extractRegions(firstPage);
            String text = stripper.getTextForRegion("regionName");

            //Close all open items
            document.close();
            return text;

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
        return "error";
    }

    public static void combineMailMergeWithInvoices(String invoices, String mailMerge, String finishedFolder) {
        //Get Files
        File invoiceFile = new File(invoices);
        File mailMergeFile = new File(mailMerge);

        //List Files
        File[] invoiceFileList = invoiceFile.listFiles();
        File[] mailMergeFileList = mailMergeFile.listFiles();

        for (int i = 0; i < invoiceFileList.length; i++) {
            String invoiceFileName = invoiceFileList[i].getName();
            String[] invoiceFileNameSplit = invoiceFileName.split("_");
            int mailMergeFileListLocation = 0;
            for (int x = 0; x < mailMergeFileList.length; x++) {
                String comp = invoiceFileNameSplit[0] + ".pdf";

                if (mailMergeFileList[x].getName().equalsIgnoreCase(comp)) {
                    mailMergeFileListLocation = x;
                }
            }
            //System.out.println("mailMergeFileListLocation is "+ mailMergeFileListLocation);
            try {
                String finishedFile = finishedFolder + invoiceFileList[i].getName();
                File finishedFileLocation = new File(finishedFile);
                PDDocument docInvoice = PDDocument.load(invoiceFileList[i]);
                PDFMergerUtility mergePdf = new PDFMergerUtility();
                PDDocument docDest = new PDDocument();

                //Adds cover sheet
                docDest.importPage(docInvoice.getPage(0));

                //Add Mail Merge pdf
                PDDocument mailMergePDF = PDDocument.load(mailMergeFileList[mailMergeFileListLocation]);
                docDest.importPage(mailMergePDF.getPage(0));

                //Add in rest of invoice
                docInvoice.removePage(0);
                docInvoice.save(invoiceFileList[i]);
                //docInvoice.close();
                //PDDocument docInvoice2 = PDDocument.load(invoiceFileList[i]);
                mergePdf.appendDocument(docDest, docInvoice);

                //Save PDF
                docDest.save(finishedFileLocation);

                //Close PDF
                docDest.close();
                docInvoice.close();
                mailMergePDF.close();
            } catch (Exception e) {
                Utilities.addExceptionToLog(e);
            }
        }

    }

    public static void writeToPDF(String pdf, String saveLocation, int x, int y, String text, int page) {
        try {
            if (text != null) {
                PDFont font = PDType1Font.TIMES_BOLD;
                PDDocument originalDoc = PDDocument.load(new File(pdf));
                page--;
                PDPage page1 = originalDoc.getPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(originalDoc, page1, true, true, true);
                contentStream.setFont(font, 11);
                contentStream.setNonStrokingColor(0);
                contentStream.beginText();

                contentStream.moveTextPositionByAmount(x, y);
                contentStream.drawString(text);  // deprecated. Use showText(String text)
                contentStream.endText();
                contentStream.close();
                originalDoc.save(saveLocation);
                originalDoc.close();
            } else {
                Utilities.addToLog("There is no text to write");
            }
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static boolean processFlRepackPDF(String pdf, String tempDir, String errorDir, String completedDir) {
        try {
            boolean result = true;
            Utilities.addToLog("Working on file - " + pdf);

            //Copies file to temp dir
            File pdfFile = new File(pdf);
            String temp = tempDir + pdfFile.getName();
            File tempFile = new File(temp);
            Utilities.copyFiles(pdfFile, tempFile);

            PDDocument originalDoc = PDDocument.load(tempFile);
            originalDoc.getNumberOfPages();
            originalDoc.close();

            //Go through all of the pages on the PDF that are larger than 1
            //For any of the identified pages look at all the possible line items and figure out their associated NDC if there is one
            for (int i = 2; i <= originalDoc.getNumberOfPages(); i++) {

                //Get the NPI
                String npi = PdfUtilities.getText(temp, 230, 420, 80, 10, i); //NPI
                npi = npi.trim();

                //Get the 1st Row of Meds
                String ndc1 = PdfUtilities.getText(temp, 230, 520, 60, 10, i); //NDC1 1
                String rx1 = PdfUtilities.getText(temp, 320, 520, 50, 10, i); //Rx1
                ndc1 = ndc1.trim();
                rx1 = rx1.trim();

                //If an NDC was located then find the associated Repack NDC
                if (ndc1.length() > 1) {
                    String foundRepackNDC = Utilities.findNdcNumber(ndc1, npi, rx1);
                    //If there is an associated Repack NDC then, write it to the PDF
                    if (foundRepackNDC.length() > 1) {
                        String repackNDC = "REPACK NDC" + foundRepackNDC;
                        PdfUtilities.writeToPDF(temp, temp, 55, 231, repackNDC, i);
                    } else {
                        result = false;
                    }
                }

                //Get the 2nd Row of Meds                
                String ndc2 = PdfUtilities.getText(temp, 230, 542, 60, 10, i); //NDC 2
                String rx2 = PdfUtilities.getText(temp, 320, 542, 50, 10, i); //Rx2
                ndc2 = ndc2.trim();
                rx2 = rx2.trim();

                if (ndc2.length() > 1) {
                    String foundRepackNDC = Utilities.findNdcNumber(ndc2, npi, rx2);
                    if (foundRepackNDC.length() > 1) {
                        String repackNDC = "REPACK NDC" + foundRepackNDC;
                        PdfUtilities.writeToPDF(temp, temp, 55, 205, repackNDC, i);
                    } else {
                        result = false;
                    }
                }

                //Get the 3rd Row of Meds
                String ndc3 = PdfUtilities.getText(temp, 230, 565, 60, 10, i); //ndc3
                String rx3 = PdfUtilities.getText(temp, 320, 565, 50, 10, i);
                ndc3 = ndc3.trim();
                rx3 = rx3.trim();

                if (ndc3.length() > 1) {
                    String foundRepackNDC = Utilities.findNdcNumber(ndc3, npi, rx3);
                    if (foundRepackNDC.length() > 1) {
                        String repackNDC = "REPACK NDC" + foundRepackNDC;
                        PdfUtilities.writeToPDF(temp, temp, 55, 185, repackNDC, i);
                    } else {
                        result = false;
                    }
                }

                //Get the 4th Row of Meds
                String ndc4 = PdfUtilities.getText(temp, 230, 585, 60, 10, 2); //NDC 4
                String rx4 = PdfUtilities.getText(temp, 320, 585, 50, 10, 2); //RX 4
                ndc4 = ndc4.trim();
                rx4 = rx4.trim();

                if (ndc4.length() > 1) {
                    String foundRepackNDC = Utilities.findNdcNumber(ndc4, npi, rx4);
                    if (foundRepackNDC.length() > 1) {
                        String repackNDC = "REPACK NDC" + foundRepackNDC;
                        PdfUtilities.writeToPDF(temp, temp, 55, 165, repackNDC, i);
                    } else {
                        result = false;
                    }
                }

                //Get the 5th Row of Meds
                String ndc5 = PdfUtilities.getText(temp, 230, 608, 60, 10, 2); //NDC 5
                String rx5 = PdfUtilities.getText(temp, 320, 608, 50, 10, 2); //RX 5
                ndc5 = ndc5.trim();
                rx5 = rx5.trim();

                if (ndc5.length() > 1) {
                    String foundRepackNDC = Utilities.findNdcNumber(ndc5, npi, rx5);
                    if (foundRepackNDC.length() > 1) {
                        String repackNDC = "REPACK NDC" + foundRepackNDC;
                        PdfUtilities.writeToPDF(temp, temp, 55, 142, repackNDC, i);
                    } else {
                        result = false;
                    }
                }

                //Get the 6th Row of Meds
                String ndc6 = PdfUtilities.getText(temp, 230, 630, 60, 10, 2); //NDC 6
                String rx6 = PdfUtilities.getText(temp, 320, 630, 50, 10, 2); //RX 6
                ndc6 = ndc6.trim();
                rx6 = rx6.trim();
                if (ndc6.length() > 1) {
                    String foundRepackNDC = Utilities.findNdcNumber(ndc6, npi, rx6);
                    if (foundRepackNDC.length() > 1) {
                        String repackNDC = "REPACK NDC" + foundRepackNDC;
                        PdfUtilities.writeToPDF(temp, temp, 55, 120, repackNDC, i);
                    } else {
                        result = false;
                    }
                }

            }

            if (result == false) {
                Utilities.addToLog("Error with file - " + pdf);
                String[] pdfFileNameSplit = pdfFile.getName().split("\\.");
                String origPdfDest = errorDir + pdfFileNameSplit[0] + "_Original." + pdfFileNameSplit[1];

                //Copy the original file to the Error Dir
                File origPdfDestFile = new File(origPdfDest);
                Utilities.copyFiles(pdfFile, origPdfDestFile);

                //Copy the temp file to the Error Dir
                String tempFileDest = errorDir + tempFile.getName();
                File tempFileDestFile = new File(tempFileDest);
                Utilities.copyFiles(tempFile, tempFileDestFile);
            } else {
                Utilities.addToLog("Finished file - " + pdf);
                String completedDest = completedDir + tempFile.getName();
                Utilities.copyFiles(tempFile, new File(completedDest));
            }
            Utilities.cleanDirectory(new File(tempDir));
            return result;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return false;
        }
    }

    public static int getPages(String pdfLoc) {
        try {
            PDDocument pdfDoc = PDDocument.load(new File(pdfLoc));
            return pdfDoc.getNumberOfPages();
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return 0;
        }
    }

    public static void addMergeLetter(String pdfLetterOrig, String workingDir, ArrayList invoicesToRebill, String destDir) {
        Utilities.addToLog("Adding Merge Letter");
        File workingDirFile = new File(workingDir);
        File[] invoicesToProcess = workingDirFile.listFiles();
        int currentInv = 1;
        int filesToProcess = invoicesToProcess.length;
        for (File invoiceToProcess : invoicesToProcess) {
            try {
                Utilities.addToLog("Merging " + currentInv + "/" + filesToProcess);
                Utilities.addToLog("Processing file: " + invoiceToProcess.getName());

                String destDirFile = destDir + invoiceToProcess.getName();
                File completedPdf = new File(destDirFile);

                if (!completedPdf.exists()) {
                    String invoiceNumber = "";
                    if (invoiceToProcess.getName().contains(" ")) {
                        //Figure out the GroupNum and save it
                        String fileName[] = invoiceToProcess.getName().split(" ");

                        invoiceNumber = fileName[0];
                    } else {
                        //Figure out the GroupNum and save it
                        String fileName[] = invoiceToProcess.getName().split("_");

                        invoiceNumber = fileName[0];
                    }
                    //String pdfLetter = "C:\\Users\\toprisiu.SERVRXCOM\\Documents\\Work\\Invoices\\10-10-2016 Short Pay Rebill\\Letter.pdf";
                    String pdfLetterDest = "Letter2.pdf";

                    Utilities.addToLog("Creating Merge Letter");
                    String[] carrierInfo = Database_Queries.findLetterMergeData(invoiceNumber);

                    //Adding Carrier Info
                    PdfUtilities.writeToPDF(pdfLetterOrig, pdfLetterDest, 135, 565, carrierInfo[0], 1);
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 135, 555, carrierInfo[1], 1);
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 135, 545, carrierInfo[2], 1);

                    //Adding Patient Name
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 150, 525, carrierInfo[3], 1);

                    //Adding Cleim #
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 115, 511, carrierInfo[4], 1);

                    //Adding DOS
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 102, 498, carrierInfo[5], 1);

                    //Adding Invoice Num
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 128, 484, carrierInfo[6], 1);

                    //Adding Outstanding Amount
                    PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 150, 470, carrierInfo[7], 1);

                    PDDocument origPdf = PDDocument.load(invoiceToProcess);
                    PDDocument mergeLetter = PDDocument.load(new File(pdfLetterDest));
                    //FileUtils.copyFile(invoiceToProcess, completedPdf);
                    PDDocument destPdf = new PDDocument();

                    destPdf.importPage(origPdf.getPage(0));
                    destPdf.importPage(mergeLetter.getPage(0));
                    for (int i = 1; i < origPdf.getNumberOfPages(); i++) {
                        destPdf.importPage(origPdf.getPage(i));
                        //destPdf.save(completedPdf);
                    }
                    destPdf.save(completedPdf);
                    origPdf.close();
                    mergeLetter.close();
                    destPdf.close();
                    new File(pdfLetterDest).delete();
                } else {
                    Utilities.addToLog("Skipping PDF since it exists");
                }
            } catch (Exception e) {

                Utilities.addExceptionToLog(e);
            }
            currentInv++;
        }

    }

    public static void addMergeLetterSingleInvoice(String pdfLetterOrig, String invoiceLoc, String workingDir) {
        Utilities.addToLog("Adding Merge Letter");
        
        Calendar cal = Calendar.getInstance();

        //Gets the month and then increases it by 1 so Jan = 1
        int month = cal.get(Calendar.MONTH) + 1;
        //Create the filename dependant on the current date.
        String currentDate = "" + month + '/' + cal.get(Calendar.DATE) + '/' + cal.get(Calendar.YEAR);

        try {

            File completedPdf = new File(invoiceLoc);
            File invoiceToProcess = new File(invoiceLoc);

            String invoiceNumber = "";
            if (invoiceToProcess.getName().contains(" ")) {
                //Figure out the GroupNum and save it
                String fileName[] = invoiceToProcess.getName().split(" ");

                invoiceNumber = fileName[0];
            } else {
                //Figure out the GroupNum and save it
                String fileName[] = invoiceToProcess.getName().split("_");

                invoiceNumber = fileName[0];
            }
            //String pdfLetter = "C:\\Users\\toprisiu.SERVRXCOM\\Documents\\Work\\Invoices\\10-10-2016 Short Pay Rebill\\Letter.pdf";
            String pdfLetterDest = workingDir + "Letter2.pdf";

            String[] carrierInfo = Database_Queries.findLetterMergeData(invoiceNumber);

            //Adding Carrier Info
            PdfUtilities.writeToPDF(pdfLetterOrig, pdfLetterDest, 72, 600, currentDate, 1);
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 135, 565, carrierInfo[0], 1);
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 135, 555, carrierInfo[1], 1);
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 135, 545, carrierInfo[2], 1);

            //Adding Patient Name
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 150, 525, carrierInfo[3], 1);

            //Adding Cleim #
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 115, 511, carrierInfo[4], 1);

            //Adding DOS
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 102, 498, carrierInfo[5], 1);

            //Adding Invoice Num
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 128, 484, carrierInfo[6], 1);

            //Adding Outstanding Amount
            PdfUtilities.writeToPDF(pdfLetterDest, pdfLetterDest, 150, 470, carrierInfo[7], 1);

            PDDocument origPdf = PDDocument.load(invoiceToProcess);
            PDDocument mergeLetter = PDDocument.load(new File(pdfLetterDest));
            //FileUtils.copyFile(invoiceToProcess, completedPdf);
            PDDocument destPdf = new PDDocument();

            destPdf.importPage(origPdf.getPage(0));
            destPdf.importPage(mergeLetter.getPage(0));
            for (int i = 1; i < origPdf.getNumberOfPages(); i++) {
                destPdf.importPage(origPdf.getPage(i));
                //destPdf.save(completedPdf);
            }
            destPdf.save(completedPdf);
            origPdf.close();
            mergeLetter.close();
            destPdf.close();
            new File(pdfLetterDest).delete();

        } catch (Exception e) {

            Utilities.addExceptionToLog(e);
        }

    }

    public static boolean checkPdfIntegrity(String file) {
        try {
            PDDocument origPdf = PDDocument.load(new File(file));
            origPdf.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
