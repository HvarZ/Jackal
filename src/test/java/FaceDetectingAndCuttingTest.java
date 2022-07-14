import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;
import eleron.jackal.JackalTypes;

public class FaceDetectingAndCuttingTest {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector();
            detector.faceDetectAndCut(
                "src/test/resources/main/TestFaceDetectImage.png",
                "src/test/resources/cuttingResults/TestFaceDetectImageCuttingResult.png",
                    JackalTypes.FRONTAL_FACE
            );

            IDetectable detector2 = new Detector();
            detector.faceDetectAndCut(
                    "src/test/resources/main/TestFaceDetectImageOnePerson.png",
                    "src/test/resources/cuttingResults/TestFaceDetectImageCuttingResult2.png",
                    JackalTypes.FRONTAL_FACE_2
            );

        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
