package eleron.jackal;

public interface IDetectable {
    void faceDetectAndCut(String pathRead, String pathWrite) throws DetectException;

    void magicWand(String pathRead, String pathWrite, int[] kernelSettings, int[] preparingSettings) throws DetectException;

    void backgroundBlur(String pathRead, String pathWrite) throws DetectException;
}
