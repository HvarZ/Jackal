import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;
import eleron.jackal.JackalTypes;

public class FaceDetectTest {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector();
            detector.faceDetect(
            "src/test/resources/main/TestFaceDetectImage.png",
            "src/test/resources/detectResults/TestFaceDetectResultImage.png",
                    JackalTypes.FRONTAL_FACE
                    );
            detector.faceDetect(
            "src/test/resources/main/TestFaceDetectImageOnePerson.png",
            "src/test/resources/detectResults/TestFaceDetectResultOnePerson.png",
                    JackalTypes.FRONTAL_FACE
            );

            IDetectable detector_2 = new Detector();
            detector_2.faceDetect(
            "src/test/resources/main/TestFaceDetectImage.png",
            "src/test/resources/detectResults/TestFaceDetectResultImage_2.png",
                    JackalTypes.FRONTAL_FACE_2
            );

            detector_2.faceDetect(
            "src/test/resources/main/TestFaceDetectImageOnePerson.png",
            "src/test/resources/detectResults/TestFaceDetectResultOnePerson_2.png",
                    JackalTypes.FRONTAL_FACE_2
            );


        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
