/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShortPay_Rebill;

import com.opencsv.CSVReader;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static ShortPay_Rebill.Config.emailIncomingReports;

/**
 *
 * @author toprisiu
 */
public class Utilities {

    public static Date date;
    public static SimpleDateFormat formatter;

    public static  StringWriter sw;

   public Utilities()
   {
       date = new Date();
       formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
   }



    public static void copyFiles(File source, File dest)
            throws Exception {
        Files.copy(source.toPath(), dest.toPath());

    }

    
    public static void copyAllFiles(String source, String dest) {
        try {
            File sourceDirFile = new File(source);
            File destDestFolder = new File(dest);

            File[] dirListing = sourceDirFile.listFiles();

            for (int i = 0; i < dirListing.length; i++) {
                if (dirListing[i].isFile()) {
                    File destinationFile = new File(dest + dirListing[i].getName());
                    //FileChannel channel = new RandomAccessFile(dirListing[i], "rw").getChannel();
                    //FileLock lock = channel.lock();

                    InputStream in = new FileInputStream(dirListing[i]);
                    OutputStream out = new FileOutputStream(destinationFile);

                    byte[] buffer = new byte[1024];

                    int length;
                    //copy the file content in bytes 
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    in.close();
                    out.close();
                    //System.out.println("File copied from " + src + " to " + dest);

                    //FileUtils.copyFile(dirListing[i], destinationFile);
                    //lock.release();
                } else {
                    FileUtils.copyDirectory(sourceDirFile, destDestFolder);
                }
            }

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static void copyFolderNew(File src, File dest) throws IOException {
        try {

            if (src.isDirectory()) {

                //if directory not exists, create it
                if (!dest.exists()) {
                    dest.mkdir();
                    System.out.println("Directory copied from "
                            + src + "  to " + dest);
                }

                //list all the directory contents
                String files[] = src.list();

                for (String file : files) {
                    //construct the src and dest file structure
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    //recursive copy
                    copyFolderNew(srcFile, destFile);
                }

            } else {
                //if file, then copy it
                //Use bytes stream to support all file types
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes 
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
                Utilities.addToLog("File copied from " + src + " to " + dest);
            }
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
        }
    }

    public static void copyFolders(String source, String dest) {
        try {
            //    throws Exception {
            FileFilter folderFileFilter = new FileFilter() {

                @Override
                public boolean accept(File pathname) {

                    // match path name extension
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    return false;
                }
            };

            File sourceDirFile = new File(source);
            File destDestFile = new File(dest);
            File[] tempFolders = sourceDirFile.listFiles(folderFileFilter);
            for (int i = 0; i < tempFolders.length; i++) {

                //Creates the new folder location
                //File tempFinalFolder = new File(dest + "\\"+ tempFolders[i].getName());
                //Copies the folder to the new location with all contents
                FileUtils.copyDirectoryToDirectory(tempFolders[i], destDestFile);
            }
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }

    }

    public static void recreateSpeicalFolders(String dir) {
        try {
            //Create the Broadspire folder
            File broadspireSpecialFolder = new File(dir + "Broadspire\\");
            broadspireSpecialFolder.mkdirs();

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }

    }

    public static void deleteFolder(String folderDir) {
        try {
            File dir = new File(folderDir);
            FileUtils.deleteDirectory(dir);

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static void archiveFolder(String folderDir, String archiveDir) {
        try {
            File soureDir = new File(folderDir);
            File destDir = new File(archiveDir);

            //If the destination folder does not exist, it creates it
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            Utilities.copyFolderNew(soureDir, destDir);
            FileUtils.cleanDirectory(soureDir);

        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }

    public static void cleanDirectory(File folderDir) throws IOException {
        try {
            FileUtils.cleanDirectory(folderDir);
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            throw e;
        }
    }

    public static void addExceptionToLog(Exception e) {
        try {
            java.util.Date date = new java.util.Date();
            System.out.println("Here");
            //Gets the year and adds 1900 to it to get it in the right format
            Integer year = date.getYear() + 1900;
            Integer month = date.getMonth() + 1;

            //Create the filename dependant on the current date.
            String fileName = "Logs\\TransmissionLog - " + month + '-' + date.getDate() + '-' + year + ".txt";

            //This takes the exception and prints the stack trace into a string so it can be added to the logfile
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            System.out.println(errors.toString());

            //Create/opens the file and adds the error to it. Afterwards it closes and saves it
            FileWriter fw = new FileWriter(fileName, true);
            fw.write("\n" + date.toString() + " - " + errors.toString() + "\n");
            fw.write(" ");
            fw.close();

            //Since there was an error, the log file is automatically emailed and the program is closed.
            //sendAttachmentEmail("Error with Rebill Downloader and Organizer", "There was an error encountered", fileName);
            //System.exit(0);

        } catch (Exception ex) {
            System.out.println("Could not write to logfile - " + ex);
        }
    }
    
    public static void addExceptionToLogAndExit(Exception e) {
        try {
            java.util.Date date = new java.util.Date();
            System.out.println("Here");
            //Gets the year and adds 1900 to it to get it in the right format
            Integer year = date.getYear() + 1900;
            Integer month = date.getMonth() + 1;

            //Create the filename dependant on the current date.
            String fileName = "Logs\\TransmissionLog - " + month + '-' + date.getDate() + '-' + year + ".txt";

            //This takes the exception and prints the stack trace into a string so it can be added to the logfile
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            System.out.println(errors.toString());

            //Create/opens the file and adds the error to it. Afterwards it closes and saves it
            FileWriter fw = new FileWriter(fileName, true);
            fw.write("\n" + date.toString() + " - " + errors.toString() + "\n");
            fw.write(" ");
            fw.close();

            //Since there was an error, the log file is automatically emailed and the program is closed.
            sendAttachmentEmail("Error with FL Repack Processor", "There was an error encountered", fileName);
            System.exit(0);

        } catch (Exception ex) {
            System.out.println("Could not write to logfile - " + ex);
        }
    }

    public static void emailLog() {
        try {
            java.util.Date date = new java.util.Date();
            //Gets the year and adds 1900 to it to get it in the right format
            Integer year = date.getYear() + 1900;
            Integer month = date.getMonth() + 1;

            //Create the filename dependant on the current date.
            String fileName = "Logs\\TransmissionLog - " + month + '-' + date.getDate() + '-' + year + ".txt";

            //The program completed succesfully so the email indicating so is sent out with the log file
            sendAttachmentEmail("Gemino Open AR Export Completed Successfully", "Attached you can find the log file.", fileName);
            System.exit(0);

        } catch (Exception ex) {
            System.out.println("Could not write to logfile - " + ex);
        }
    }

    public static void addToLog(String log) {
        try {
            java.util.Date date = new java.util.Date();

            //Gets the year and adds 1900 to it to get it in the right format
            Integer year = date.getYear() + 1900;
            Integer month = date.getMonth() + 1;
            System.out.println(log);

            //Create the filename dependant on the current date.
            String fileName = "Logs\\TransmissionLog - " + month + '-' + date.getDate() + '-' + year + ".txt";

            File folder = new File("Logs");
            //Create/opens the file and adds the error to it. Afterwards it closes and saves it
            if (!folder.exists()) {
                folder.mkdirs();
            }
            FileWriter fw = new FileWriter(fileName, true);
            fw.write('\n' + date.toString() + " - " + log);
            fw.write(" ");
            fw.close();
        } catch (Exception ex) {
            System.out.println("Could not write to logfile - " + ex);
        }
    }

    public static void sendAttachmentEmail(String subject, String body, String fileName) {
        final String fromEmail = emailIncomingReports.get("email").toString(); //requires valid gmail id
        final String password = emailIncomingReports.get("password").toString(); // correct password for gmail id
        final String toEmail = "toprisiu@servrx.com"; // can be any email id 

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("incomingreports@servrx.com", "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse("incomingreports@servrx.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Second part is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(fileName);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with attachment!!");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void SendNotificationEmail(Map<String, Exception> invoicesFailedToDownload, Map<String, Exception> invoicesFailedToMerge, Map<String, Exception> invoicesFailedToAddNote,
                                             Map<String, Exception> claimsFailedToAddNote, Map<String, Exception> invoicesFailedToSend)
    {
        final String fromEmail = emailIncomingReports.get("email").toString(); //requires valid gmail id
        final String password = emailIncomingReports.get("password").toString(); // correct password for gmail id
        final String toEmail = "sahmad@servrx.com"; // can be any email id

        String body =  "<table width='100%' border='1' align='center'>"
                + "<tr align='center'>"
                + "<td><b>Invoice Number <b></td>"
                + "<td><b>Exception<b></td>"
                + "</tr>";
        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("incomingreports@servrx.com", "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse("incomingreports@servrx.com", false));


            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            if(invoicesFailedToDownload.size() > 0) {
                msg.setSubject("Download Failed", "UTF-8");
                for (Map.Entry<String, Exception> me :invoicesFailedToDownload.entrySet()) {
                    body += "<tr align='center'>"+"<td>" + me.getKey() + "</td>"
                            + "<td>" + me.getValue() + "</td>"+"</tr>";
                }

            }

            if(invoicesFailedToMerge.size() > 0) {
                msg.setSubject("Merge Failed", "UTF-8");
                for (Map.Entry<String, Exception> me :invoicesFailedToMerge.entrySet()) {
                    body += "<tr align='center'>"+"<td>" + me.getKey() + "</td>"
                            + "<td>" + me.getValue() + "</td>"+"</tr>";
                }
            }

            if(invoicesFailedToAddNote.size() > 0) {
                msg.setSubject("Invoice Add Note Failed", "UTF-8");
                for (Map.Entry<String, Exception> me :invoicesFailedToAddNote.entrySet()) {
                    body += "<tr align='center'>"+"<td>" + me.getKey() + "</td>"
                            + "<td>" + me.getValue() + "</td>"+"</tr>";
                }
            }

            if(claimsFailedToAddNote.size() > 0) {
                msg.setSubject("Claim Add Note Failed", "UTF-8");
                for (Map.Entry<String, Exception> me :claimsFailedToAddNote.entrySet()) {
                    body += "<tr align='center'>"+"<td>" + me.getKey() + "</td>"
                            + "<td>" + me.getValue() + "</td>"+"</tr>";
                }
            }

            if(invoicesFailedToSend.size() > 0) {
                msg.setSubject("Invoice Failed to Sent To Letterhub", "UTF-8");
                for (Map.Entry<String, Exception> me :invoicesFailedToSend.entrySet()) {
                    body += "<tr align='center'>"+"<td>" + me.getKey() + "</td>"
                            + "<td>" + me.getValue() + "</td>"+"</tr>";
                }
            }


            messageBodyPart.setContent(body, "text/HTML; charset=UTF-8");
            //messageBodyPart.setText(body);

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            msg.setContent(multipart);

            // Send message
            Transport.send(msg);
            System.out.println("EMail Sent Successfully with attachment!!");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public static String createExpressScriptsImportQueryCSV(String csvFile) {
        String importQuery = "INSERT INTO express_scripts.fl_repack_data\n"
                + "(`RX_NUMBER`, `NDC_1`,`NDC_2`,`NPI`)\n"
                + "VALUES";

        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String[] nextLine;
            nextLine = reader.readNext();
            int x = 0;
            while ((nextLine = reader.readNext()) != null) {
                x++;
                System.out.println("X: "+x);
                importQuery = importQuery.replace(";", ",");
                // nextLine[] is an array of values from the line
                String ndc1 = nextLine[44].replace("-", "");
                String ndc2 = nextLine[59].replace("-", "");
                //System.out.println(nextLine[1] + " - " + nextLine[44] + " - " + nextLine[59] + " - " + nextLine[92]);
                importQuery = importQuery + "(\"" + nextLine[1] + "\",\"" + ndc1 + "\",\"" + ndc2 + "\",\"" + nextLine[92] + "\");\n";
            }
            reader.close();
        } catch (Exception e) {
            Utilities.addExceptionToLogAndExit(e);
        }
        
        return importQuery;
    }



    public static void LogError(String invoiceNumber, String message, Exception ex, String app) {
        sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        date = new Date();
        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String user = System.getProperty("user.name");
        InetAddress localhost = null;
        try
        {
            localhost = InetAddress.getLocalHost();
        }

        catch(Exception e)
        {
            Utilities.addExceptionToLog(e);
        }

        try
        {
            Connection connToSqlServer = DatabaseConnections.connectToMSSql();
            String query = "INSERT INTO log ("
                                                + " Date,"
                                                + " InvoiceNumber,"
                                                + " Message,"
                                                + " Exception,"
                                                + " UserName,"
                                                + " IPAddress,"
                                                + " StackTrace,"
                                                + " Application) VALUES ("
                                                + "?, ?, ?, ?, ?, ?, ?, ?)";

            // set all the preparedstatement parameters
            java.sql.PreparedStatement st = connToSqlServer.prepareStatement(query);
            st.setString(1, formatter.format(date));
            st.setString(2, invoiceNumber);
            st.setString(3, message);
            st.setString(4, ex.toString());
            st.setString(5, user);
            st.setString(6, String.valueOf(localhost));
            st.setString(7, exceptionAsString);
            st.setString(8, app);

            // execute the preparedstatement insert
            st.executeUpdate();
            st.close();
        }
        catch(Exception e)
        {
            LogException((e));
        }
    }

    public static void LogException(Exception ex)  {
        sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        date = new Date();
        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try
        {
            Connection connToSqlServer = DatabaseConnections.connectToMSSql();
            String query = "INSERT INTO log ("
                    + " Date,"
                    + " Message,"
                    + " Exception,"
                    + " UserName,"
                    + " StackTrace,"
                    + " Application) VALUES ("
                    + "?, ?, ?, ?, ?, ?)";

            // set all the preparedstatement parameters
            java.sql.PreparedStatement st = connToSqlServer.prepareStatement(query);
            st.setString(1, formatter.format(date));
            st.setString(2, ex.getMessage());
            st.setString(3, ex.toString());
            st.setString(4, System.getProperty("user.name"));
            st.setString(5, exceptionAsString);
            st.setString(6, "ShortPayRebills");

            // execute the preparedstatement insert
            st.executeUpdate();
            st.close();
        }
        catch(Exception e)
        {
            addExceptionToLog(e);
        }
    }

    public static void LogTransmissionDetails(String invoiceNumber, String batchId, Integer statusCode, Integer noOfPages, String invoiceDate, String statusDescription)
    {
        date = new Date();
        formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try
        {
            Connection connToSqlServer = DatabaseConnections.connectToMSSql();
            String query = "INSERT INTO RebillsLetterhubInvoiceTransmissionLog ("
                    + " InvoiceNumber,"
                    + " TransmissionDateTime,"
                    + " BatchId,"
                    + " StatusCode,"
                    + " NoOfPages,"
                    + " InvoiceDate,"
                    + " StatusDescription) VALUES ("
                    + "?, ?, ?, ?, ?, ?, ?)";

            // set all the preparedstatement parameters
            java.sql.PreparedStatement st = connToSqlServer.prepareStatement(query);

            st.setString(1, invoiceNumber);
            st.setString(2, formatter.format(date));
            st.setString(3, batchId);
            st.setString(4, statusCode.toString());
            st.setString(5, noOfPages.toString());
            st.setString(6, invoiceDate);
            st.setString(7, statusDescription);

            // execute the preparedstatement insert
            st.executeUpdate();
            st.close();
        }
        catch(Exception e)
        {
            LogException((e));
        }

    }

    public static String findNdcNumber(String ndc2, String npi, String rxNumber) {
        String ndc = "";

        try {
            Connection connMySQL = DatabaseConnections.connectToMySQL();
            Statement stmtMySQL = connMySQL.createStatement();

            String mySqlStatement = "SELECT \n"
                    + "    NDC_1\n"
                    + "FROM\n"
                    + "    express_scripts.fl_repack_data\n"
                    + "WHERE\n"
                    + "    NDC_2 = " + ndc2 + " AND NPI = '" + npi + "'\n"
                    + "        AND RX_NUMBER = " + rxNumber;
            //Utilities.addToLog(mySqlStatement);
            ResultSet rs = stmtMySQL.executeQuery(mySqlStatement);
            rs.next();
            ndc = rs.getString(1);
            //if(rs.next())
            //{
            //    Utilities.addToLog("Here");
            //}
            return ndc;
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
            return "";
        }
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
            return sb.toString();
        } finally {
            br.close();
        }
    }
    
    public static String[] getDatesForLastWeek()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = df.format(cal.getTime());   
        cal.add(Calendar.DATE,-7);
        String beginDate = df.format(cal.getTime());
        
        String[] dates = {beginDate, endDate};
        
        return dates;
    }
    
    public static void createFolderIfMissing(String folder)
    {
        try {
            File file = new File(folder);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        catch(Exception ex)
        {
            throw ex;
        }
    }
    
    public static void zipFolder(String sourceDir, String zipLoc) {
        try {
            ZipFile zipFile = new ZipFile(zipLoc);
            ZipParameters parameters = new ZipParameters();
            parameters.setIncludeRootFolder(false);
            zipFile.createZipFileFromFolder(sourceDir, parameters, false, 0);
            
            
        } catch (Exception e) {
            Utilities.addExceptionToLog(e);
        }
    }
    
    public static String getSundayDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        return df.format(cal.getTime());  
    }
}
