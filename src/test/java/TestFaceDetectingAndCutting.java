import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;
import eleron.jackal.JackalTypes;

public class TestFaceDetectingAndCutting {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector(JackalTypes.FRONTAL_FACE);
            detector.faceDetectAndCut(
                "src/test/resources/TestFaceDetectImage.png",
                "src/test/resources/cuttingResults/TestFaceDetectImageCuttingResult.png"
            );

            IDetectable detector2 = new Detector(JackalTypes.FRONTAL_FACE_2);
            detector.faceDetectAndCut(
                    "src/test/resources/TestFaceDetectImageOnePerson.png",
                    "src/test/resources/cuttingResults/TestFaceDetectImageCuttingResult2.png"
            );

        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
