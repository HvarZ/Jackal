package eleron.jackal;

public interface IDetectable {
    void faceDetect(String pathRead, String pathWrite) throws DetectException;

    void faceDetectAndCut(String pathRead, String pathWrite) throws DetectException;
}
