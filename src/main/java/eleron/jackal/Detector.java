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
    private boolean isLoaded;

    public Detector() throws DetectException {
        classifier = new CascadeClassifier();
        isLoaded = false;
    }

    @Override
    public void faceDetect(String pathRead, String pathWrite, int cascadeVariant) throws DetectException {
        checkValidity(pathRead, null, false, cascadeVariant);
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_" + cascadeVariant + ".xml");
            isLoaded = true;
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
    public void faceDetectAndCut(String pathRead, String pathWrite, int cascadeVariant) throws DetectException {
        checkValidity(pathRead, null, false, cascadeVariant);
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_" + cascadeVariant + ".xml");
            isLoaded = true;
        }
        Mat imageRead = Imgcodecs.imread(pathRead);
        MatOfRect faceDetections = detect(imageRead);
        Optional<Rect> optionalRect = Arrays.stream(
                faceDetections.toArray()).max(Comparator.comparingInt(x -> x.height * x.width)
        );
        Imgcodecs.imwrite(pathWrite, new Mat(
                imageRead, optionalRect.orElseThrow(() -> new DetectException("Not detected"))
        ));
    }

    public void edgeDetect(String pathRead, String pathWrite, int[] kernelSettings) throws DetectException {
        checkValidity(pathRead, kernelSettings, true, JackalTypes.DEFAULT);
        Mat greySrc = getGrayMat(pathRead, pathWrite);
        Mat detectedEdges = new Mat();
        Imgproc.blur(greySrc, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, kernelSettings[0], kernelSettings[1], kernelSettings[2]);
        Imgcodecs.imwrite(pathWrite, detectedEdges);
    }

    private void checkValidity(String path, int[] kernel, boolean isNeededKernel, int cascadeVariant) throws DetectException {
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


    private Mat getGrayMat(String pathRead, String pathWrite) throws DetectException {
        checkValidity(pathRead, null, false, JackalTypes.DEFAULT);
        Mat imageRead = Imgcodecs.imread(pathRead);
        Mat greyImage = new Mat(imageRead.size(), imageRead.type());
        Imgproc.cvtColor(imageRead, greyImage, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(pathWrite, greyImage);

        return greyImage;
    }


    private MatOfRect detect(Mat imageRead) {
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        return faceDetections;
    }
}
