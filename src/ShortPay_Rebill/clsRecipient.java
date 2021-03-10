package ShortPay_Rebill;

import java.util.ArrayList;

public class clsRecipient
{
    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    private String FirstName;

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    private String LastName;

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String MiddleName) {
        this.MiddleName = MiddleName;
    }

    private String MiddleName;

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }

    private String Prefix;

    public String getSuffix() {
        return Suffix;
    }

    public void setSuffix(String Suffix) {
        this.Suffix = Suffix;
    }

    private String Suffix;

    public String getBusinessName() {
        return BusinessName;
    }

    public void setBusinessName(String businessName) {
        this.BusinessName = BusinessName;
    }

    private String BusinessName;

    public String getJobTitle() {
        return JobTitle;
    }

    public void setJobTitle(String JobTitle) {
        this.JobTitle = JobTitle;
    }

    private String JobTitle ;

    public String getAddressLabel() {
        return AddressLabel;
    }

    public void setAddressLabel(String AddressLabel) {
        this.AddressLabel = AddressLabel;
    }

    private String AddressLabel ;

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        this.Address1 = Address1;
    }

    private String Address1 ;

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        this.Address2 = Address2;
    }

    private String Address2 ;

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        this.City = City;
    }

    private String City ;

    public String getState() {
        return State;
    }

    public void setState(String state) {
        this.State = State;
    }

    private String State;

    public String getZipcode() {
        return Zipcode;
    }

    public void setZipcode(String zipcode) {
        this.Zipcode = Zipcode;
    }

    private String Zipcode ;

    public ArrayList<clsPhoneNumbers> getObjPhoneNumbersList() {
        return ObjPhoneNumbersList;
    }

    public void setObjPhoneNumbersList(ArrayList<clsPhoneNumbers> ObjPhoneNumbersList) {
        this.ObjPhoneNumbersList = ObjPhoneNumbersList;
    }

    private ArrayList<clsPhoneNumbers> ObjPhoneNumbersList;

    public ArrayList<clsEmailAddresses> getObjEmailAddressList() {
        return ObjEmailAddressList;
    }

    public void setObjEmailAddressList(ArrayList<clsEmailAddresses> ObjEmailAddressList) {
        this.ObjEmailAddressList = ObjEmailAddressList;
    }

    private ArrayList<clsEmailAddresses> ObjEmailAddressList;

    public ArrayList<clsDate> getObjDatesList() {
        return ObjDatesList;
    }

    public void setObjDatesList(ArrayList<clsDate> objDatesList) {
        this.ObjDatesList = ObjDatesList;
    }

    private ArrayList<clsDate> ObjDatesList;

    public ArrayList<clsUrls> getObjUrlList() {
        return ObjUrlList;
    }

    public void setObjUrlList(ArrayList<clsUrls> objUrlList) {
        this.ObjUrlList = ObjUrlList;
    }

    private ArrayList<clsUrls> ObjUrlList;

    public ArrayList<clsCustomFields> getObjCustomFieldsList() {
        return ObjCustomFieldsList;
    }

    public void setObjCustomFieldsList(ArrayList<clsCustomFields> ObjCustomFieldsList) {
        this.ObjCustomFieldsList = ObjCustomFieldsList;
    }

    private ArrayList<clsCustomFields> ObjCustomFieldsList;
}



