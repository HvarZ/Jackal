package eleron.jackal;

public interface IDetectable {
    void faceDetectAndCut(String pathRead, String pathWrite) throws DetectException;

    void magicWand(String pathRead, String pathWrite, int[] kernelSettings, int[] preparingSettings) throws DetectException;

    void backgroundBlur(String pathRead, String pathWrite) throws DetectException;

    byte[] backgroundBlur(String pathRead) throws DetectException;

    byte[] backgroundBlur(byte[] imageBytes) throws DetectException;


}
