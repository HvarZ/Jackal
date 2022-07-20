import eleron.jackal.DetectException;
import eleron.jackal.Detector;

public class FaceDetectTest {
    public static void main(String[] args) {
        try {
            Detector detector = new Detector();
            detector.backgroundBlur(
            "src/test/resources/main/TestFaceDetectImage.png",
            "src/test/resources/detectResults/TestFaceDetectResultImage.png"
                    );
            detector.backgroundBlur(
            "src/test/resources/main/TestFaceDetectImageOnePerson.png",
            "src/test/resources/detectResults/TestFaceDetectResultOnePerson.png"
            );

            Detector detector_2 = new Detector();
            detector_2.backgroundBlur(
            "src/test/resources/main/TestFaceDetectImage.png",
            "src/test/resources/detectResults/TestFaceDetectResultImage_2.png"
            );

            detector_2.backgroundBlur(
            "src/test/resources/main/TestFaceDetectImageOnePerson.png",
            "src/test/resources/detectResults/TestFaceDetectResultOnePerson_2.png"
            );


        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
