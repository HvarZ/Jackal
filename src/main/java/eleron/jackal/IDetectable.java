package eleron.jackal;

import java.io.File;

public interface IDetectable {
    void detect(File input, File output, Modes mode) throws DetectException;

    byte[] detect(File pathRead, Modes mode) throws DetectException;

    byte[] detect(byte[] imageBytes, Modes mode) throws DetectException;
}
