import eleron.jackal.*;

public class ConvertTest {
    public static void main(String[] args) {
        try {
            IDetectable detector = new Detector();
            byte[] imageBytes = detector.detect(
                "src/test/resources/main/1.jpg",
                    Modes.Blurring
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

            byte[] imageBlurBytes = detector.detect(imageBytes2, Modes.Blurring);
            Converter.convertBytesToImage(
                    imageBlurBytes,
                    "src/test/resources/convertingResults/convertingBlur2.png"
            );

            byte[] imageBytes3 = detector.detect(
                    "src/test/resources/main/1.jpg",
                    Modes.Cutting
            );

            Converter.convertBytesToImage(
                    imageBytes3,
                    "src/test/resources/convertingResults/convertingBlur3.png"
            );

            byte[] imageBytes4 =  Converter.convertImageToBytes(
                    "src/test/resources/main/TestFaceDetectImageOnePerson.png"
            );

            Converter.convertBytesToImage(
                    imageBytes4,
                    "src/test/resources/convertingResults/convertingNormal4.png"
            );

            byte[] imageBlurBytes5 = detector.detect(imageBytes2, Modes.Cutting);
            Converter.convertBytesToImage(
                    imageBlurBytes5,
                    "src/test/resources/convertingResults/convertingBlur5.png"
            );
        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
}
