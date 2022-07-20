package eleron.jackal;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class DetectorService {
    public static MatOfPoint findBiggestContour(List<MatOfPoint> contours) throws DetectException {
        return contours.stream().max(
                        Comparator.comparingDouble(
                                x -> Imgproc.arcLength(new MatOfPoint2f(x.toArray()), false)
                        ))
                .orElseThrow(() -> new DetectException("findBiggestContour: Not detected"));
    }

    public static void setBlackBackground(Mat image, Mat mask) throws DetectException {
        if (image.height() != mask.height() || image.width() != mask.width()) {
            throw new DetectException("SetBlackBackground: Size of image is not equal to size of mask");
        }
        for (int i = 0; i < mask.rows(); ++i) {
            for (int j = 0; j < mask.cols(); ++j) {
                if (mask.get(i, j)[JackalTypes.IMAGE_GREY_LEVEL] == 0) {
                    image.put(i, j, new byte[image.channels()]);
                }
            }
        }
    }

    public static void fillNestedContour(Mat img, List<MatOfPoint> contours, Mat hierarchy) throws DetectException {
        if (contours.size() != hierarchy.cols()) {
            throw new DetectException("fillNestedContour: Length of contours is not equal to hierarchy size");
        }

        List<MatOfPoint> fillingContours = new ArrayList<>();
        for (int i = 0; i < hierarchy.rows(); ++i) {
            for (int j = 0; j < hierarchy.cols(); ++j) {
                if (hierarchy.get(i, j)[JackalTypes.HIERARCHY_PARENT] != JackalTypes.HIERARCHY_NOT_FOUND) {
                    fillingContours.add(contours.get(j));
                }
            }
        }

        Imgproc.fillPoly(img, fillingContours, JackalTypes.WHITE);
    }


    public static void prepareImage(Mat srcImage, Mat resultImage, Size kernelSize, int thresholdBinary) {
        Mat blurImage = new Mat();
        Mat binaryImage = new Mat();

        Imgproc.blur(srcImage,
                blurImage,
                kernelSize);

        Imgproc.threshold(blurImage,
                binaryImage,
                thresholdBinary,
                JackalTypes.MAX_BRIGHTNESS,
                Imgproc.THRESH_BINARY_INV);

        Imgproc.morphologyEx(binaryImage,
                resultImage,
                Imgproc.MORPH_CLOSE,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kernelSize));
    }

    public static void checkValidity(String path, int[] kernel, boolean isNeededKernel) throws DetectException {
        if (!(new File(path).exists())) {
            throw new DetectException("File (" + path + ") not found");
        }

        if (isNeededKernel && kernel != null && kernel.length != JackalTypes.KERNEL_SIZE) {
            throw new DetectException("Invalid size of kernelSettings: != " + JackalTypes.KERNEL_SIZE);
        }
    }

    public static void scaleRect(Rect rect, double coefficient) throws DetectException {
        scaleRect(rect, coefficient, coefficient);
    }

    public static void scaleRect(Rect rect, double xCoefficient, double yCoefficient) throws DetectException {
        if (xCoefficient <= 0) {
            throw new DetectException("scaleRect: invalid argument (xCoefficient must be > 0)");
        }
        if (yCoefficient <= 0) {
            throw new DetectException("scaleRect: invalid argument (yCoefficient must be > 0)");
        }
        if (rect.empty()) {
            throw new DetectException("scaleRect: rect is empty");
        }

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

    public static Point getCentre(Rect rect) throws DetectException {
        if (rect.empty()) {
            throw new DetectException("getCentre: rect is empty");
        }
        return new Point(rect.x + (double) rect.width / 2, rect.y + (double) rect.height / 2);
    }


    public static Mat getGrayMat(String pathRead, String pathWrite) throws DetectException {
        checkValidity(pathRead, null, false);

        Mat imageRead = Imgcodecs.imread(pathRead);
        Mat greyImage = new Mat(imageRead.size(), imageRead.type());

        Imgproc.cvtColor(imageRead, greyImage, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(pathWrite, greyImage);

        imageRead.release();

        return greyImage;
    }
}
