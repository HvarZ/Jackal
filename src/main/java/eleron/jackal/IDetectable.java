package eleron.jackal;

public interface IDetectable {
    void faceDetect(String pathRead, String pathWrite, int cascadeVariant) throws DetectException;

    void faceDetectAndCut(String pathRead, String pathWrite, int cascadeVariant) throws DetectException;
}
