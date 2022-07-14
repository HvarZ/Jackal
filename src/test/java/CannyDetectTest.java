import eleron.jackal.DetectException;
import eleron.jackal.Detector;

public class CannyDetectTest {
    public static void main(String[] args) {
        try {
            Detector detector = new Detector();
            detector.edgeDetect(
                    "src/test/resources/main/TestCannyDetectImage.png",
                    "src/test/resources/cannyDetectResults/TestCannyDetectResult.png",
                    new int[]{100, 100, 3}
            );
        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
