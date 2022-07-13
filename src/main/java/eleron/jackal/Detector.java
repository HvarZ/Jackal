package eleron.jackal;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class Detector implements IDetectable {
    static {
        OpenCV.loadLocally();
    }
    private final CascadeClassifier classifier;

    public Detector(int cascadeVariant) throws DetectException {
        if (cascadeVariant < 0 || cascadeVariant > 2) {
            throw new DetectException("Invalid cascade number: cascade number valid only (> 0 && < 3)");
        }
        classifier = new CascadeClassifier();
        classifier.load("src/main/resources/haarcascade_" + cascadeVariant + ".xml");
    }

    @Override
    public void faceDetect(String pathRead, String pathWrite) throws DetectException {
        if (!(new File(pathRead).exists())) {
            throw new DetectException("File (" + pathRead + ") not found");
        }
        Mat imageRead = Imgcodecs.imread(pathRead);
        MatOfRect faceDetections = detect(imageRead);

        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(
                    imageRead,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 0, 255),
                    3
            );
        }
        Imgcodecs.imwrite(pathWrite, imageRead);
    }

    @Override
    public void faceDetectAndCut(String pathRead, String pathWrite) throws DetectException {
        if (!(new File(pathRead).exists())) {
            throw new DetectException("File (" + pathRead + ") not found");
        }
        Mat imageRead = Imgcodecs.imread(pathRead);
        MatOfRect faceDetections = detect(imageRead);
        Optional<Rect> optionalRect = Arrays.stream(faceDetections.toArray()).max(Comparator.comparingInt(x -> x.height * x.width));
        Imgcodecs.imwrite(pathWrite, new Mat(imageRead, optionalRect.orElseThrow(() -> new DetectException("Not detected"))));
    }

    private MatOfRect detect(Mat imageRead) {
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        return faceDetections;
    }
}
