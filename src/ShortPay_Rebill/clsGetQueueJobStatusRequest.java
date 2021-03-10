package ShortPay_Rebill;

public class clsGetQueueJobStatusRequest
{
    public String getBatchID() {
        return BatchID;
    }

    public void setBatchID(String BatchID) {
        this.BatchID = BatchID;
    }

    private String BatchID;
}