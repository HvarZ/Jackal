package eleron.jackal;

public interface IDetectable {
    void faceDetect(String pathRead, String pathWrite, int cascadeVariant) throws DetectException;

    void faceDetectAndCut(String pathRead, String pathWrite, int cascadeVariant) throws DetectException;

    void magicWand(String pathRead, String pathWrite, int[] kernelSettings, int[] preparingSettings) throws DetectException;

    void findAndBlur(String pathRead, String pathWrite) throws DetectException;
}
