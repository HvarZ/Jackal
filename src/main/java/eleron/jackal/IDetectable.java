package eleron.jackal;

public interface IDetectable {
    void detect(String pathRead, String pathWrite, Modes mode) throws DetectException;

    byte[] detect(String pathRead, Modes mode) throws DetectException;

    byte[] detect(byte[] imageBytes, Modes mode) throws DetectException;
}
