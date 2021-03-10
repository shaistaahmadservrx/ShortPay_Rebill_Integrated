package ShortPay_Rebill;

public class clsQueueJobResponse
{
    public String getBatchID() {
        return BatchID;
    }

    public void setBatchID(String batchID) {
        this.BatchID = BatchID;
    }

    private String BatchID;

    public String getJobName() {
        return JobName;
    }

    public void setJobName(String jobName) {
        this.JobName = JobName;
    }

    private String JobName;

    public String getNote() {
        return Note;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }

    private String Note;

    public clsResponseStatus getObjResponseStatus() {
        return ObjResponseStatus;
    }

    public void setObjResponseStatus(clsResponseStatus ObjResponseStatus) {
        this.ObjResponseStatus = ObjResponseStatus;
    }

    private clsResponseStatus ObjResponseStatus;
}




