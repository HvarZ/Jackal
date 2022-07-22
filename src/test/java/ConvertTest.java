import eleron.jackal.*;

import java.io.File;
import java.io.IOException;

public class ConvertTest {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector();
            byte[] imageBytes = detector.detect(
                new File("src/test/resources/main/TestFaceDetectImageOnePerson.png"),
                    Modes.Blurring
            );

            Converter.convertBytesToImage(
                    imageBytes,
                    new File("src/test/resources/convertingResults/convertingBlur.png")
            );

            byte[] imageNatural = Converter.convertImageToBytes(
                    new File("src/test/resources/main/1.jpg")
            );

            byte[] blurImageBytes = detector.detect(imageNatural, Modes.Blurring);

            Converter.convertBytesToImage(
                    blurImageBytes,
                    new File("src/test/resources/convertingResults/convertingBlur.png")
            );


        } catch (DetectException | IOException e) {
            e.printStackTrace();
        }
    }
}
