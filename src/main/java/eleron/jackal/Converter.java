package eleron.jackal;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Converter {
    public static void convertBytesToImage(final byte[] imagesBytes, String pathWrite) throws DetectException {
        Mat image = loadMatBinary(imagesBytes);
        Imgcodecs.imwrite(pathWrite, image);
    }

    public static byte[] convertImageToBytes(String pathRead) throws DetectException {
        Mat image = Imgcodecs.imread(pathRead);
        return saveImageBinary(image);
    }

    protected static byte[] saveImageBinary(Mat image) throws DetectException {
        if (image == null || image.empty()) {
            throw new DetectException("saveImageBinary: image is empty");
        }

        if (image.depth() == CvType.CV_8U) {
        } else if (image.depth() == CvType.CV_16U) {
            Mat m_16 = new Mat();
            image.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
            image = m_16;
        } else if (image.depth() == CvType.CV_32F) {
            Mat m_32 = new Mat();
            image.convertTo(m_32, CvType.CV_8U, 255);
            image = m_32;
        } else {
            throw new DetectException("saveImageBinary: unknown image type");
        }

        if (image.channels() == 2 || image.channels() > 4) {
            throw new DetectException("saveImageBinary: invalid numbers of image channels");
        }

        byte[] imageParameters = DetectorService.getBytesImageParameters(image.rows(), image.cols(), image.channels());
        byte[] buffer = new byte[image.channels() * image.cols() * image.rows()];

        image.get(0, 0, buffer);

        return DetectorService.mergeBytes(imageParameters, buffer);
    }

    protected static Mat loadMatBinary(final byte[] imageBinary) throws DetectException {
        if (imageBinary.length == 0) {
            throw new DetectException("loadMatBinary: imageBinary is empty");
        }

        int rows = DetectorService.getParameterFromBytes(imageBinary, 0);
        if (rows < 1) {
            throw new DetectException("loadMatBinary: invalid rows number");
        }

        int cols = DetectorService.getParameterFromBytes(imageBinary, 2);
        if (cols < 1) {
            throw new DetectException("loadMatBinary: invalid cols number");
        }

        int channels = DetectorService.getParameterFromBytes(imageBinary, 4);
        int type;
        if (channels == 1) {
            type = CvType.CV_8UC1;
        } else if (channels == 3) {
            type = CvType.CV_8UC3;
        } else if (channels == 4) {
            type = CvType.CV_8UC4;
        } else {
            return new Mat();
        }
        Mat image = new Mat(rows, cols, type);

        byte[] imageWithoutParameters = imageBinary.clone();
        DetectorService.removeParameters(imageWithoutParameters);
        image.put(0, 0, imageWithoutParameters);

        return image;
    }
}
