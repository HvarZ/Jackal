import eleron.jackal.Converter;
import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;

public class ConvertTest {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector();
            byte[] imageBytes = detector.backgroundBlur(
                "src/test/resources/main/TestFaceDetectImageOnePerson.png"
            );

            Converter.convertBytesToImage(
                    imageBytes,
                    "src/test/resources/convertingResults/converting.png"
            );
        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
