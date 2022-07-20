import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;

public class FaceDetectingAndCuttingTest {
    public static void main(String[] args) {
        try {
            Detector detector = new Detector();
            detector.faceDetectAndCut(
                "src/test/resources/main/TestFaceDetectImage.png",
                "src/test/resources/cuttingResults/TestFaceDetectImageCuttingResult.png"
            );

            detector.faceDetectAndCut(
                "src/test/resources/main/TestFaceDetectImageOnePerson.png",
                "src/test/resources/cuttingResults/TestFaceDetectImageCuttingResult2.png"
            );

        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
