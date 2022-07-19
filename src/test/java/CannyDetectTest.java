import eleron.jackal.DetectException;
import eleron.jackal.Detector;

public class CannyDetectTest {
    public static void main(String[] args) {
        try {
            Detector detector = new Detector();
            detector.magicWand(
                    "src/test/resources/main/TestCannyDetectImage.png",
                    "src/test/resources/cannyDetectResults/TestCannyDetectResult.png",
                    new int[]{50, 200, 3},
                    new int[]{3, 3, 235}
            );

        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
