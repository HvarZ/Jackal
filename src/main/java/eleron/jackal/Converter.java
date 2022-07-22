package eleron.jackal;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Converter {
    public static void convertBytesToImage(final byte[] imageBytes, File output) throws DetectException {
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);
        Imgcodecs.imwrite(output.getPath(), image);
    }

    public static byte[] convertImageToBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }


    protected static byte[] saveImageBinary(Mat image) throws DetectException {
        if (image == null || image.empty()) {
            throw new DetectException("saveImageBinary: image is empty");
        }
        MatOfByte imageArray = new MatOfByte();
        Imgcodecs.imencode(JackalTypes.SAVE_TYPE, image, imageArray);

        return imageArray.toArray();
    }
}
