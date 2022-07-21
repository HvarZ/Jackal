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
                    "src/test/resources/convertingResults/convertingBlur.png"
            );

            byte[] imageBytes2 =  Converter.convertImageToBytes(
                    "src/test/resources/main/TestFaceDetectImageOnePerson.png"
            );

            Converter.convertBytesToImage(
                    imageBytes2,
                    "src/test/resources/convertingResults/convertingNormal.png"
            );

            byte[] imageBlurBytes = detector.backgroundBlur(imageBytes2);
            Converter.convertBytesToImage(
                    imageBlurBytes,
                    "src/test/resources/convertingResults/convertingBlur2.png"
            );
        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
