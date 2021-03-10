package ShortPay_Rebill;

public class clsAPICreateJobResponse
{
    private clsResponseStatus ObjResponseStatus;
    public clsResponseStatus getObjResponseStatus() {
        return ObjResponseStatus;
    }

    public void setObjResponseStatus(clsResponseStatus ObjResponseStatus) {
        this.ObjResponseStatus = ObjResponseStatus;
    }

    private long BatchID;
    public long getBatchID() {
        return BatchID;
    }

    public void setBatchID(long batchID) {
        this.BatchID = BatchID;
    }
    private int Status;
    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }


    private double Balance;
    public double getBalance() {
        return Balance;
    }

    public void setBalance(double balance) {
        this.Balance = balance;
    }

    private double BatchPrice;
    public double getBatchPrice() {
        return BatchPrice;
    }

    public void setBatchPrice(double batchPrice) {
        this.BatchPrice = BatchPrice;
    }

    private clsJobPrice ObjPrice;
    public clsJobPrice getObjPrice() {
        return ObjPrice;
    }

    public void setObjPrice(clsJobPrice objPrice) {
        ObjPrice = ObjPrice;
    }
}
