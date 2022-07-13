import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;
import eleron.jackal.JackalTypes;

public class FaceDetectTest {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector(JackalTypes.FRONTAL_FACE);
            detector.faceDetect(
            "src/test/resources/TestFaceDetectImage.png",
            "src/test/resources/detectResults/TestFaceDetectResultImage.png");
            detector.faceDetect(
            "src/test/resources/TestFaceDetectImageOnePerson.png",
            "src/test/resources/detectResults/TestFaceDetectResultOnePerson.png"
            );

            IDetectable detector_2 = new Detector(JackalTypes.FRONTAL_FACE_2);
            detector_2.faceDetect(
            "src/test/resources/TestFaceDetectImage.png",
            "src/test/resources/detectResults/TestFaceDetectResultImage_2.png");

            detector_2.faceDetect(
            "src/test/resources/TestFaceDetectImageOnePerson.png",
            "src/test/resources/detectResults/TestFaceDetectResultOnePerson_2.png"
            );


        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
