package eleron.jackal;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.*;
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

    public static byte[] saveImageBinary(Mat image) throws DetectException {
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

        byte[] imageParameters = getBytesImageParameters(image.rows(), image.cols(), image.channels());
        byte[] buffer = new byte[image.channels() * image.cols() * image.rows()];

        image.get(0, 0, buffer);

        return mergeBytes(imageParameters, buffer);
    }

    public static Mat loadMatBinary(byte[] imageBinary) throws DetectException {
        if (imageBinary.length == 0) {
            throw new DetectException("loadMatBinary: imageBinary is empty");
        }

        int rows = getParameterFromBytes(imageBinary, 0);
        if (rows < 1) {
            throw new DetectException("loadMatBinary: invalid rows number");
        }

        int cols = getParameterFromBytes(imageBinary, 2);
        if (cols < 1) {
            throw new DetectException("loadMatBinary: invalid cols number");
        }

        int channels = getParameterFromBytes(imageBinary, 4);
        int type;
        if (channels == 1) {
            type = CvType.CV_8UC1;
        } else if (channels == 3) {
            type = CvType.CV_8UC1;
        } else if (channels == 4) {
            type = CvType.CV_8UC4;
        } else {
            return new Mat();
        }
        Mat image = new Mat(rows, cols, type);

        removeParameters(imageBinary);
        image.put(0, 0, imageBinary);

        return image;
    }

    public static byte[] convertIntToByte(int number) throws DetectException {
        if (number > 32_767) {
            throw new DetectException("convertIntToByte: Number is too large");
        } else if (number < 0) {
            throw new DetectException("convertIntToByte: Number is negative");
        }
        byte[] result = new byte[JackalTypes.INT_IMP_BYTES];
        result[0] = (byte) (number & 0xFF);
        result[1] = (byte) ((number >> 8) & 0xFF);

        return result;
    }

    public static byte[] getBytesImageParameters(int rows, int cols, int channels) throws DetectException {
        byte[] rowsBytes = convertIntToByte(rows);
        byte[] colsBytes = convertIntToByte(cols);
        byte[] channelsBytes = convertIntToByte(channels);
        byte[] result = mergeBytes(rowsBytes, colsBytes);

        return mergeBytes(result, channelsBytes);
    }

    public static int getParameterFromBytes(byte[] imageBytes, int indexStart) {
        byte[] numberByte = new byte[2];
        numberByte[0] = imageBytes[indexStart];
        numberByte[1] = imageBytes[indexStart + 1];

        return convertByteToInt(numberByte);
    }

    public static int convertByteToInt(byte[] number) {
        int high = number[1] >= 0 ? number[1] : 256 + number[1];
        int low = number[0] >= 0 ? number[0] : 256 + number[0];

        return low | (high << 8);
    }

    public static byte[] mergeBytes(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static void removeParameters(byte[] imageBytes) {
        System.arraycopy(imageBytes, 6, imageBytes, 0, imageBytes.length - 6);
    }
}
