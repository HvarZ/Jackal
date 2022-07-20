package eleron.jackal;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DetectorService {
    public static MatOfPoint findBiggestContour(List<MatOfPoint> contours) throws DetectException {
        return contours.stream().max(
                Comparator.comparingDouble(
                        x -> Imgproc.arcLength(new MatOfPoint2f(x.toArray()), false)
                )).orElseThrow(() -> new DetectException("Not detected"));
    }
    public static void setBlackBackground(Mat image, Mat mask) {
        for (int i = 0; i < mask.rows(); ++i) {
            for (int j = 0; j < mask.cols(); ++j) {
                if (mask.get(i, j)[0] == 0) {
                    image.put(i, j, new byte[image.channels()]);
                }
            }
        }
    }

    public static void fillNestedContour(Mat img, List<MatOfPoint> contours, Mat hierarchy) {
        List<MatOfPoint> fillingContours = new ArrayList<>();
        for (int i = 0; i < hierarchy.rows(); ++i) {
            for (int j = 0; j < hierarchy.cols(); ++j) {
                if (hierarchy.get(i, j)[2] != -1) {
                    fillingContours.add(contours.get(j));
                }
            }
        }
        Imgproc.fillPoly(img, fillingContours, new Scalar(255, 255, 255));
    }


    public static void prepareImage(Mat srcImage, Mat resultImage, Size kernelSize, int thresholdBinary) {
        Mat blurImage = new Mat();
        Mat binaryImage = new Mat();
        Imgproc.blur(srcImage,
                blurImage,
                kernelSize);

        Imgproc.threshold(blurImage,
                binaryImage,
                thresholdBinary, 255,
                Imgproc.THRESH_BINARY_INV);

        Imgproc.morphologyEx(binaryImage,
                resultImage,
                Imgproc.MORPH_CLOSE,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kernelSize));
    }

    public static void checkValidity(String path, int[] kernel, boolean isNeededKernel, int cascadeVariant) throws DetectException {
        if (!(new File(path).exists())) {
            throw new DetectException("File (" + path + ") not found");
        }
        if (cascadeVariant < 0 || cascadeVariant > 2) {
            throw new DetectException("Invalid cascade number: cascade number valid only (> 0 && < 3)");
        }
        if (isNeededKernel && kernel != null && kernel.length != 3) {
            throw new DetectException("Invalid size of kernelSettings: != 3");
        }
    }

    public static void scaleRect(Rect rect, double coefficient) {
        scaleRect(rect, coefficient, coefficient);
    }

    public static void scaleRect(Rect rect, double xCoefficient, double yCoefficient) {
        Point centrePoint = getCentre(rect);
        double newWidth = rect.width * Math.sqrt(xCoefficient);
        double newHeight = rect.height * Math.sqrt(yCoefficient);

        rect.set(new double[]{
                centrePoint.x - newWidth / 2,
                centrePoint.y - newHeight / 2,
                newWidth,
                newHeight
        });
    }

    public static Point getCentre(Rect rect) {
        return new Point(rect.x + (double)rect.width / 2, rect.y + (double)rect.height / 2);
    }


    public static Mat getGrayMat(String pathRead, String pathWrite) throws DetectException {
        checkValidity(pathRead, null, false, JackalTypes.DEFAULT);
        Mat imageRead = Imgcodecs.imread(pathRead);
        Mat greyImage = new Mat(imageRead.size(), imageRead.type());
        Imgproc.cvtColor(imageRead, greyImage, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(pathWrite, greyImage);
        imageRead.release();

        return greyImage;
    }
}
