package eleron.jackal;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

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

    @Override
    public void faceDetectAndCut(String pathRead, String pathWrite) throws DetectException {
        DetectorService.checkValidity(pathRead, null, false);
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_1.xml");
            isLoaded = true;
        }
        Mat imageRead = Imgcodecs.imread(pathRead);
        Mat imageCopy = imageRead.clone();

        Rect faceRect = faceDetect(imageCopy);

        Imgcodecs.imwrite(pathWrite, new Mat(imageRead, faceRect));
        imageRead.release();
    }

    @Override
    public void backgroundBlur(String pathRead, String pathWrite) throws DetectException {
        Mat blurImage = getBlurBackground(pathRead);
        Imgcodecs.imwrite(pathWrite, blurImage);

        blurImage.release();
    }

    @Override
    public byte[] backgroundBlur(String pathRead) throws DetectException {
        Mat blurImage = getBlurBackground(pathRead);
        return Converter.saveImageBinary(blurImage);
    }

    @Override
    public byte[] backgroundBlur(byte[] imageBytes) throws DetectException {
        Mat image = Converter.loadMatBinary(imageBytes);
        Mat blurImage = getBlurBackground(image);

        return Converter.saveImageBinary(blurImage);
    }


    @Override
    public void magicWand(String pathRead, String pathWrite, int[] kernelSettings, int[] preparingSettings) throws DetectException {
        DetectorService.checkValidity(pathRead, kernelSettings, true);
        Mat image = Imgcodecs.imread(pathRead);
        Mat greySrc = DetectorService.getGrayMat(pathRead, pathWrite);
        Mat preparingImage = new Mat();
        Mat detectedEdges = new Mat();

        DetectorService.prepareImage(greySrc, preparingImage, new Size(preparingSettings[0], preparingSettings[1]), preparingSettings[2]);

        Imgproc.Canny(preparingImage,
                detectedEdges,
                kernelSettings[0], kernelSettings[1], kernelSettings[2],
                false);

        Mat hierarchy = new Mat();

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(detectedEdges,
                contours,
                hierarchy,
                Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_NONE);

        DetectorService.fillNestedContour(preparingImage, contours, hierarchy);

        DetectorService.setBlackBackground(image, preparingImage);
        Imgcodecs.imwrite(pathWrite, image);

        image.release();
        greySrc.release();
        detectedEdges.release();
        hierarchy.release();
        preparingImage.release();
    }

    private Rect faceDetect(Mat imageRead) throws DetectException {
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_1.xml");
            isLoaded = true;
        }

        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        Rect result = Arrays.stream(
                        faceDetections.toArray()).max(Comparator.comparingInt(x -> x.height * x.width))
                .orElseThrow(() -> new DetectException("Not detected"));

        imageRead.release();
        return result;
    }

    private Mat getBlurBackground(String pathRead) throws DetectException {
        DetectorService.checkValidity(pathRead, null, false);
        Mat image = Imgcodecs.imread(pathRead);

        return getBlurBackground(image);
    }

    private Mat getBlurBackground(Mat image) throws DetectException {
        if (image.empty()) {
            throw new DetectException("getBlurBackground: image is empty");
        }

        Mat imageCopy = image.clone();

        Rect faceRect = faceDetect(imageCopy);
        DetectorService.scaleRect(faceRect, 2.5, 3);
        DetectorService.correctFaceRect(faceRect, image);

        Mat blurImage = image.clone();
        Mat face = new Mat(image, faceRect);
        Mat roi = blurImage.submat(new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height));

        Imgproc.GaussianBlur(image, blurImage, new Size(41, 41), 140);
        face.copyTo(roi);

        return blurImage;
    }
}