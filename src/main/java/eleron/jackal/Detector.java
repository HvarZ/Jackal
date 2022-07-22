package eleron.jackal;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.*;
public final class Detector implements IDetectable {
    static {
        OpenCV.loadLocally();
    }
    private final CascadeClassifier classifier;
    private boolean isLoaded;

    public Detector() throws DetectException {
        classifier = new CascadeClassifier();
        isLoaded = false;
    }

    private Mat faceDetectAndCut(Mat image) throws DetectException {
        Mat imageCopy = image.clone();

        Rect faceRect = faceDetect(imageCopy);
        DetectorService.scaleRect(faceRect, JackalTypes.X_SCALE_COEFFICIENT, JackalTypes.Y_SCALE_COEFFICIENT);
        DetectorService.correctFaceRect(faceRect, imageCopy);

        return new Mat(image, faceRect);
    }

    @Override
    public void detect(String pathRead, String pathWrite, Modes mode) throws DetectException {
        if (!(new File(pathRead).exists())) {
            throw new DetectException("File (" + pathRead + ") not found");
        }
        Mat image = Imgcodecs.imread(pathRead);
        switch (mode) {
            case Cutting -> {
                Mat face = faceDetectAndCut(image);
                Imgcodecs.imwrite(pathWrite, face);
            }
            case Blurring -> {
                Mat blurImage = getBlurBackground(image);
                Imgcodecs.imwrite(pathWrite, blurImage);
            }
            default -> throw new DetectException("Unknown work mode");
        }
        image.release();
    }

    @Override
    public byte[] detect(String pathRead, Modes mode) throws DetectException {
        if (!(new File(pathRead).exists())) {
            throw new DetectException("File (" + pathRead + ") not found");
        }
        Mat image = Imgcodecs.imread(pathRead);
        return detect(image, mode);
    }

    @Override
    public byte[] detect(byte[] imageBytes, Modes mode) throws DetectException {
        Mat image = Converter.loadMatBinary(imageBytes);
        return detect(image, mode);
    }

    private byte[] detect(Mat image, Modes mode) throws DetectException {
        switch (mode) {
            case Cutting -> {
                Mat face = faceDetectAndCut(image);
                return Converter.saveImageBinary(face);
            }
            case Blurring -> {
                Mat blurImage = getBlurBackground(image);
                return Converter.saveImageBinary(blurImage);
            }
            default -> throw new DetectException("Unknown work mode");
        }
    }

    private Rect faceDetect(Mat imageRead) throws DetectException {
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_1.xml");
            isLoaded = true;
        }

        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        return Arrays.stream(faceDetections.toArray()).max(Comparator.comparingInt(x -> x.height * x.width))
                .orElseThrow(() -> new DetectException("faceDetect: not detected"));
    }

    private Mat getBlurBackground(Mat image) throws DetectException {
        if (image.empty()) {
            throw new DetectException("getBlurBackground: image is empty");
        }

        Mat imageCopy = image.clone();

        Rect faceRect = faceDetect(imageCopy);
        DetectorService.scaleRect(
                faceRect,
                JackalTypes.X_SCALE_COEFFICIENT,
                JackalTypes.Y_SCALE_COEFFICIENT
        );
        DetectorService.correctFaceRect(faceRect, image);

        Mat blurImage = image.clone();
        Mat face = new Mat(image, faceRect);
        Mat roi = blurImage.submat(new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height));

        Imgproc.GaussianBlur(image, blurImage, JackalTypes.BLUR_SQUARE, JackalTypes.BLUR_SIGMA);
        face.copyTo(roi);

        return blurImage;
    }
}