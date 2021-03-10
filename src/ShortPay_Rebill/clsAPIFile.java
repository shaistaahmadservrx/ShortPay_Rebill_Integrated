package ShortPay_Rebill;

public class clsAPIFile
{
    public byte[] getFileByteArray() {
        return FileByteArray;
    }

    public void setFileByteArray(byte[] FileByteArray) {
        this.FileByteArray = FileByteArray;
    }

    private String FileType;
    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        this.FileType = FileType;
    }

    private byte[] FileByteArray;

    private int FileSequenceNo;
    public int getFileSequenceNo() {
        return FileSequenceNo;
    }

    public void setFileSequenceNo(int FileSequenceNo) {
        this.FileSequenceNo = FileSequenceNo;
    }
}