import eleron.jackal.DetectException;
import eleron.jackal.Detector;

public class BackgroundBlurTest {
    public static void main(String[] args) {
        try {
            Detector detector = new Detector();
            detector.backgroundBlur(
                    "src/test/resources/main/TestFaceDetectImageOnePerson.png",
                    "src/test/resources/backgroundBlurResults/backgroundBlurResult.png");

            detector.backgroundBlur(
                    "src/test/resources/main/TestBlurbackground.png",
                    "src/test/resources/backgroundBlurResults/backgroundBlurResult2.png"
            );
        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
