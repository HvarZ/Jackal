import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.Modes;

import java.io.File;

public class BackgroundBlurTest {
    public static void main(String[] args) {
        try {
            Detector detector = new Detector();
            detector.detect(
                    new File("src/test/resources/main/TestFaceDetectImageOnePerson.png"),
                    new File("src/test/resources/backgroundBlurResults/backgroundBlurResult.png"),
                    Modes.Blurring
            );

           detector.detect(
                   new File("src/test/resources/main/TestFaceDetectImageOnePerson.png"),
                   new File("src/test/resources/backgroundBlurResults/backgroundBlurResult2.png"),
                   Modes.Cutting
            );


        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
