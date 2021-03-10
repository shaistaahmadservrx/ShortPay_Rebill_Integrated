package ShortPay_Rebill;

public class clsGetQueueJobStatusResponse
{
    public String getBatchID() {
        return BatchID;
    }

    public void setBatchID(String BatchID) {
        this.BatchID = BatchID;
    }

    private String BatchID;

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int StatusCode) {
        this.StatusCode = StatusCode;
    }

    private int StatusCode;

    public String getStatusDescription() {
        return StatusDescription;
    }

    public void setStatusDescription(String StatusDescription) {
        this.StatusDescription = StatusDescription;
    }

    private String StatusDescription;

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double Price) {
        this.Price = Price;
    }

    private Double Price;

    public String getBatchpostaldate() {
        return Batchpostaldate;
    }

    public void setBatchpostaldate(String Batchpostaldate) {
        this.Batchpostaldate = Batchpostaldate;
    }

    private String Batchpostaldate;

    public clsResponseStatus getObjResponseStatus() {
        return ObjResponseStatus;
    }

    public void setObjResponseStatus(clsResponseStatus ObjResponseStatus) {
        this.ObjResponseStatus = ObjResponseStatus;
    }

    private clsResponseStatus ObjResponseStatus;
}