package ShortPay_Rebill;
import java.util.ArrayList;


public class clsAPICreateJob
   {
       private ArrayList<clsAPIFile> FileArrayList;
       public ArrayList<clsAPIFile> getFileArrayList() {
           return FileArrayList;
       }

       public void setFileArrayList(ArrayList<clsAPIFile> FileArrayList) {
           this.FileArrayList = FileArrayList;
       }


       private boolean IsFileTemplate;
       public boolean getIsFileTemplate() {
           return IsFileTemplate;
       }

       public void setIsFileTemplate(boolean IsFileTemplate) {
           this.IsFileTemplate = IsFileTemplate;
       }


       private boolean IsColorFile;
       public boolean getIsColorFile() {
           return IsColorFile;
       }

       public void setIsColorFile(boolean IsColorFile) {
           this.IsColorFile = IsColorFile;
       }

       private boolean StoreToContacts;
       public boolean getStoreToContacts() {
           return StoreToContacts;
       }

       public void setStoreToContacts(boolean storeToContacts) {
           this.StoreToContacts = StoreToContacts;
       }

       private ArrayList<clsRecipient> Recipients;
       public ArrayList<clsRecipient> getRecipients() {
           return Recipients;
       }

       public void setRecipients(ArrayList<clsRecipient> recipients) {
           this.Recipients = Recipients;
       }

       private String CarrierID;
       public String getCarrierID() {
           return CarrierID;
       }

       public void setCarrierID(String carrierID) {
           this.CarrierID = CarrierID;
       }

       private int PageTypeID;
       public int getPageTypeID() {
           return PageTypeID;
       }

       public void setPageTypeID(int pageTypeID) {
           this.PageTypeID = PageTypeID;
       }

       private String JobName;
       public String getJobName() {
           return JobName;
       }

       public void setJobName(String jobName) {
           this.JobName = JobName;
       }

       private clsReturnAddress ReturnAddress;
       public clsReturnAddress getReturnAddress() {
           return ReturnAddress;
       }

       public void setReturnAddress(clsReturnAddress returnAddress) {
           this.ReturnAddress = ReturnAddress;
       }

       private clsReturnAddress FromAddress;
       public clsReturnAddress getFromAddress() {
           return FromAddress;
       }

       public void setFromAddress(clsReturnAddress fromAddress) {
           FromAddress = FromAddress;
       }

       private long FromAddressID;
       public long getFromAddressID() {
           return FromAddressID;
       }

       public void setFromAddressID(long FromAddressID) {
           FromAddressID = FromAddressID;
       }

       private long ReturnAddressID;
       public long getReturnAddressID() {
           return ReturnAddressID;
       }

       public void setReturnAddressID(long returnAddressID) {
           this.ReturnAddressID = ReturnAddressID;
       }

       private boolean IncludeReturnEnvelope;
       public boolean getIncludeReturnEnvelope() {
           return IncludeReturnEnvelope;
       }

       public void setIncludeReturnEnvelope(boolean IncludeReturnEnvelope) {
           IncludeReturnEnvelope = IncludeReturnEnvelope;
       }

       private boolean IncludePaidReturnEnvelope;
       public boolean IsIncludePaidReturnEnvelope() {
           return IncludePaidReturnEnvelope;
       }

       public void setIncludePaidReturnEnvelope(boolean IncludePaidReturnEnvelope) {
           this.IncludePaidReturnEnvelope = IncludePaidReturnEnvelope;
       }

       private boolean IsDuplexPrint;
       public boolean getIsDuplexPrint() {
           return IsDuplexPrint;
       }

       public void setIsDuplexPrint(boolean IsDuplexPrint) {
           IsDuplexPrint = IsDuplexPrint;
       }

       private String WindowNotice;
       public String getWindowNotice() {
           return WindowNotice;
       }

       public void setWindowNotice(String WindowNotice) {
           WindowNotice = WindowNotice;
       }

       private String BatchGroupName;
       public String getBatchGroupName() {
           return BatchGroupName;
       }

       public void setBatchGroupName(String BatchGroupName) {
           this.BatchGroupName = BatchGroupName;
       }

       private boolean IncludeReferralCode;
       public boolean isIncludeReferralCode() {
           return IncludeReferralCode;
       }

       public void setIncludeReferralCode(boolean IncludeReferralCode) {
           this.IncludeReferralCode = IncludeReferralCode;
       }

       private int IncludeCoverSheetInTemplate;
       public int getIncludeCoverSheetInTemplate() {
           return IncludeCoverSheetInTemplate;
       }

       public void setIncludeCoverSheetInTemplate(int IncludeCoverSheetInTemplate) {
           this.IncludeCoverSheetInTemplate = IncludeCoverSheetInTemplate;
       }

       private boolean IsOneBusinessDay;
       public boolean getIsOneBusinessDay() {
           return IsOneBusinessDay;
       }

       public void setIsOneBusinessDay(boolean IsoneBusinessDay) {
           IsOneBusinessDay = IsoneBusinessDay;
       }

       private double TotalCost;
       public double getTotalCost() {
           return TotalCost;
       }

       public void setTotalCost(double TotalCost) {
           this.TotalCost = TotalCost;
       }

       private String Source;
       public String getSource() {
           return Source;
       }

       public void setSource(String Source) {
           Source = Source;
       }

       private boolean IsEcoFriendlyReturnEnvelope;
       public boolean getIsEcoFriendlyReturnEnvelope() {
           return IsEcoFriendlyReturnEnvelope;
       }

       public void setIsEcoFriendlyReturnEnvelope(boolean IsEcoFriendlyReturnEnvelope) {
           IsEcoFriendlyReturnEnvelope = IsEcoFriendlyReturnEnvelope;
       }

       private boolean IsNCOAEnable;
       public boolean getIsNCOAEnable() {
           return IsNCOAEnable;
       }

       public void setIsNCOAEnable(boolean NCOAEnable) {
           IsNCOAEnable = IsNCOAEnable;
       }

       private boolean IsCOAForwarded;
       public boolean getIsCOAForwarded() {
           return IsCOAForwarded;
       }

       public void setIsCOAForwarded(boolean IsCOAForwarded) {
           IsCOAForwarded = IsCOAForwarded;
       }


       public boolean getIsIncludeReturnEnvelope() {
           return IncludeReturnEnvelope;
       }
       public void setIsIncludeReturnEnvelope(boolean IncludeReturnEnvelope) {
           this.IncludeReturnEnvelope = IncludeReturnEnvelope;
       }
       private boolean IsIncludeReturnEnvelope;
       private boolean NeedReview;
       public boolean getNeedReview() {
           return NeedReview;
       }

       public void setNeedReview(boolean needReview) {
           this.NeedReview = NeedReview;
       }
    }











































