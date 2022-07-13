package eleron.jackal;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

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
        MatOfRect faceDetections = new MatOfRect();

        classifier.detectMultiScale(imageRead, faceDetections);

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
}
