/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static ShortPay_Rebill.Config.ninjaAutoUser;
import static ShortPay_Rebill.Config.ninjaDownloadUser;

/**
 *
 * @author toprisiu
 */
public class httpUtilities {

    public static int retries = 0;

    public static void downloadFileWithLogin(List invoicesToProcess, String downloadDir) {
        try {

            final WebClient webClient = new WebClient();
            webClient.setCookieManager(getCookie());

            int i = 1;
            int numOfInvoices = invoicesToProcess.size();
            for (Object inv : invoicesToProcess) {
                Utilities.addToLog("Downloading " + i + "/" + numOfInvoices);
                String temp[] = inv.toString().split("\\*");
                UnexpectedPage pdfFile = null;
                if (!webClient.getPage("http://ninja.servrx.com/index.php/edi_files/download/" + temp[0]).isHtmlPage()) {
                    pdfFile = webClient.getPage("http://ninja.servrx.com/index.php/edi_files/download/" + temp[0]);
                    webClient.waitForBackgroundJavaScript(60000);
                    InputStream is = pdfFile.getInputStream();
                    final Path destination = Paths.get(downloadDir + temp[2]);
                    destination.toFile();
                    Files.copy(is, destination);
                } else {
                    Utilities.addToLog("Error with Invoice: " + temp[0] + " - " + temp[1] + " - " + temp[2]);
                }

                i++;
            }
            Utilities.addToLog("Finished downloading");
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static void addNinjaNoteAndStatus(List claimsForStatusChange, CookieManager cookies) {
        try {

            String login = ninjaAutoUser.get("login").toString();
            String password = ninjaAutoUser.get("password").toString();

            final WebClient webClient = new WebClient();
            //webClient.setCookieManager(getCookie());
            webClient.setCookieManager(cookies);
            //Shuts off all of the warnings
            //LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
            //java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
            //java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

            //Go to main Page and log in
            //Utilities.addToLog("Signing into Ninja");
            /*
            final HtmlPage page1 = webClient.getPage("http://ninja.servrx.com");
            final HtmlForm form = page1.getFormByName("f_logon");
            webClient.getOptions().setJavaScriptEnabled(true);
            final HtmlSubmitInput button = form.getInputByName("submit");
            final HtmlTextInput loginTextField = form.getInputByName("username");
            loginTextField.setValueAttribute(login);
            final HtmlPasswordInput passwordTextField = form.getInputByName("password");
            passwordTextField.setValueAttribute(password);
            button.click();*/
            int claimNumber = 1;
            int totalClaimNumber = claimsForStatusChange.size();
            for (Object claim : claimsForStatusChange) {
                Utilities.addToLog("Working on claim: " + claim + " - " + claimNumber + "/" + totalClaimNumber);
                try {

                    //Getting Claim Page
                    HtmlPage claimPage = webClient.getPage("http://ninja.servrx.com/claims/show/" + claim);
                    webClient.waitForBackgroundJavaScript(60000);

                    //Finding the Staus Change Button and clicking it
                    HtmlAnchor changeStatusButton = claimPage.getAnchorByText("Change Status");
                    changeStatusButton.click();
                    webClient.waitForBackgroundJavaScript(60000);

                    //Filling out the form
                    //Checking the box for This Claim
                    DomElement checkThisClaim = claimPage.getElementById("this_claim");
                    checkThisClaim.click();

                    //Setting the Status
                    HtmlSelect statusDropDown = claimPage.getHtmlElementById("claim_status");
                    HtmlOption option = statusDropDown.getOptionByText("Shortpay/Rebill");
                    statusDropDown.setSelectedAttribute(option, true);

                    //Filling out the text box
                    HtmlTextArea noteInput = claimPage.getHtmlElementById("reason_for_change");
                    noteInput.setTextContent("​ADDITIONAL PAYMENT RECON SENT");

                    //Finding all of the input buttons
                    DomNodeList<DomElement> pageElements = claimPage.getElementsByTagName("INPUT");

                    //Goigng through the input buttons and finding the one that saves the status change
                    for (DomElement pageElement : pageElements) {
                        String compare = pageElement.toString();

                        if (compare.equalsIgnoreCase("HtmlButtonInput[<input type=\"button\" style=\"padding: 2px 8px;\" onclick=\"save_claim_status();\" value=\"Save\">]")) {
                            Utilities.addToLog("Click");
                            pageElement.click();

                            webClient.waitForBackgroundJavaScript(999999999);
                            boolean done = false;
                            int i = 0;
                            while (!done) {

                                Utilities.addToLog("Waiting...");
                                if (claimPage.asText().contains("Shortpay/Rebill")) {
                                    done = true;
                                }
                                webClient.waitForBackgroundJavaScript(999999999);
                                i++;
                                if (i > 20) {
                                    ArrayList invoiceNumber = new ArrayList();
                                    invoiceNumber.add(claim);
                                    httpUtilities.addNinjaNoteAndStatus(invoiceNumber, cookies);
                                    done = true;
                                }
                            }
                            Utilities.addToLog("Finished claim: " + claim + " - " + claimNumber + "/" + totalClaimNumber);

                        }

                    }
                    claimNumber++;
                } catch (Exception e) {
                    Utilities.addExceptionToLog(e);
                    addNinjaNoteAndStatus(claimsForStatusChange, cookies);
                }
            }
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static String getAccessToken() throws IOException {
        String accessToken = null;
        String login = ninjaAutoUser.get("login").toString();
        String password = ninjaAutoUser.get("password").toString();

        try {
            final WebClient webClient = new WebClient();
            URL url = new URL("http://ninja.servrx.com/api/login");
            WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);

            //Request Body
            requestSettings.setRequestBody("username="+ login + "&password="+ password);

            //Go to the page
            Page redirectPage = webClient.getPage(requestSettings);
            accessToken = redirectPage.getWebResponse().getStatusMessage();
            WebResponse response = redirectPage.getWebResponse();

            //If the response is json then get what it is
            if (response.getContentType().equals("application/json")) {
                accessToken = response.getContentAsString();
                //System.out.println(pagesource);
            }

            //Instantiate the parser then get the access_token
            JsonObject jsonObject = new JsonParser().parse(accessToken).getAsJsonObject();
            accessToken = jsonObject.get("SessionID").getAsString();

            //System.out.println(jsonObject.get("name").getAsString()); //John
            return accessToken;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
            //return accessToken;
        }
    }

    public static void addNinjaNoteAndStatusAPI(String claim, String claimStatusID, String note, String accessToken) throws InterruptedException, IOException {

        try {
            Utilities.addToLog("Changing Status for ClaimID: " + claim);
            final WebClient webClient = new WebClient();
            URL url = new URL("http://ninja.servrx.com/api/change_claim_status");
            WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);

            //Request Headers
            requestSettings.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            String noteConverted = "";
            char[] test2 = note.toCharArray();
            for (int i = 1; i < test2.length; i++) {
                noteConverted = noteConverted + test2[i];
            }

            params.add(new NameValuePair("SessionID", accessToken));
            params.add(new NameValuePair("ClaimID", claim));
            params.add(new NameValuePair("ClaimStatusID", claimStatusID));
            params.add(new NameValuePair("Notes", noteConverted));

            requestSettings.setRequestParameters(params);

            //Go to the page
            Page redirectPage = webClient.getPage(requestSettings);
            accessToken = redirectPage.getWebResponse().getStatusMessage();
            WebResponse response = redirectPage.getWebResponse();

        } catch (Exception e) {
            retries = retries + 1;
            Utilities.addExceptionToLog(e);
            Utilities.addToLog("Pausing 15 seconds");
            TimeUnit.SECONDS.sleep(15);
            if(retries <= 3)
                httpUtilities.addNinjaNoteAndStatusAPI(claim, claimStatusID, note, accessToken);
            else
                throw e;
        }
    }

    public static void addNinjaNoteAndStatusAPI2(String claim, String claimStatusID, String note, String accessToken) {

        try {

            Utilities.addToLog("Changing Status for ClaimID: " + claim);
            final WebClient webClient = new WebClient();
            URL url = new URL("http://ninja.servrx.com/api/change_claim_status");
            WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);

            //Request Headers
            requestSettings.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");

            //Request Body
            /*requestSettings.setRequestBody("SessionID=" + accessToken
                    + "&ClaimID=" + claim
                    + "&ClaimStatusID=" + claimStatusID
                    + "&Notes=" + note);*/
            String request = "SessionID=15420&ClaimID=91199&ClaimStatusID=8&Notes=" + note;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair("SessionID", accessToken));
            //char noteTest = ;
            params.add(new NameValuePair("ClaimID", "91199"));
            params.add(new NameValuePair("ClaimStatusID", "8"));
            params.add(new NameValuePair("Notes", note));
            //System.out.println(request);
            //System.exit(0);
            //requestSettings.setRequestBody(request);
            requestSettings.setRequestParameters(params);

            //System.out.println(note.charAt(0));
            //System.exit(0);
            //Go to the page
            Page redirectPage = webClient.getPage(requestSettings);
            accessToken = redirectPage.getWebResponse().getStatusMessage();
            WebResponse response = redirectPage.getWebResponse();

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static CookieManager getCookie() throws IOException {
        CookieManager cookies = new CookieManager();

        try {
            String login = ninjaDownloadUser.get("login").toString();
            String password = ninjaDownloadUser.get("password").toString();
            
            final WebClient webClient = new WebClient();

            //Shuts off all of the warnings
            LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
            java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
            java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

            //Go to main Page and log in
            Utilities.addToLog("Signing into Ninja");

            final HtmlPage page1 = webClient.getPage("http://ninja.servrx.com");
            final HtmlForm form = page1.getFormByName("f_logon");
            webClient.getOptions().setJavaScriptEnabled(true);
            final HtmlSubmitInput button = form.getInputByName("submit");
            final HtmlTextInput loginTextField = form.getInputByName("username");
            loginTextField.setValueAttribute(login);
            final HtmlPasswordInput passwordTextField = form.getInputByName("password");
            passwordTextField.setValueAttribute(password);
            button.click();
            webClient.setJavaScriptTimeout(999999999);
            cookies = webClient.getCookieManager();

            return cookies;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
            //return cookies;
        }
    }

    public static String downloadFileAndCheckIntegrity(String invoiceNumber, String downloadLoc, CookieManager cookies) throws SQLException, IOException, ClassNotFoundException {
        final WebClient webClient = new WebClient();
        //webClient.setCookieManager(getCookie());
        webClient.setCookieManager(cookies);
        boolean downloaded = false;
        Path destination = null;
        try {

            ArrayList invoiceDownloadInfo = Database_Queries.getProcessFileIDs(invoiceNumber);

            for (int i = 0; i < invoiceDownloadInfo.size(); i++) {
                try {
                    if (!downloaded) {

                        String temp[] = invoiceDownloadInfo.get(i).toString().split("\\*");
                        UnexpectedPage pdfFile = null;
                        if (!webClient.getPage("http://ninja.servrx.com/index.php/edi_files/download/" + temp[0]).isHtmlPage()) {
                            pdfFile = webClient.getPage("http://ninja.servrx.com/index.php/edi_files/download/" + temp[0]);
                            webClient.waitForBackgroundJavaScript(60000);
                            InputStream is = pdfFile.getInputStream();
                            destination = Paths.get(downloadLoc + temp[1]);
                            destination.toFile();

                            Files.copy(is, destination);
                            downloaded = PdfUtilities.checkPdfIntegrity(destination.toString());
                       if (!downloaded) {
                            File downloadFile = new File(destination.toString());
                            downloadFile.delete();
                        }
                        }

                        else {
                            Utilities.addToLog("Error with Invoice: " + temp[0] + " - " + temp[1]);
                        }
                    }
                }

                catch(Exception ex)
                {
                   throw ex;
                }
            }
            Utilities.addToLog("Finished downloading");
            return destination.toString();
            }
        catch (Exception e) {
            Utilities.addExceptionToLog(e);
            //return destination.toString();
            throw e;
        }
    }



}
