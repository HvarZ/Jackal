package eleron.jackal;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.*;
import java.util.*;

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

    private Rect faceDetect(String pathRead) throws DetectException {
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_1.xml");
            isLoaded = true;
        }
        Mat imageRead = Imgcodecs.imread(pathRead);
        MatOfRect faceDetections = detect(imageRead);
        Rect result = Arrays.stream(
                faceDetections.toArray()).max(Comparator.comparingInt(x -> x.height * x.width)).orElseThrow(() ->
                new DetectException("Not detected"));

        imageRead.release();
        return result;
    }

    @Override
    public void faceDetectAndCut(String pathRead, String pathWrite) throws DetectException {
        DetectorService.checkValidity(pathRead, null, false);
        if (!isLoaded) {
            classifier.load("src/main/resources/haarcascade_1.xml");
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
        imageRead.release();
    }

    @Override
    public void backgroundBlur(String pathRead, String pathWrite) throws DetectException {
        DetectorService.checkValidity(pathRead, null, false);
        Mat image = Imgcodecs.imread(pathRead);

        Rect faceRect = faceDetect(pathRead);
        DetectorService.scaleRect(faceRect, 2.5, 3);

        Mat blurImage = image.clone();
        Mat face = new Mat(image, faceRect);
        Mat roi = blurImage.submat(new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height));

        Imgproc.GaussianBlur(image, blurImage, new Size(41, 41), 140);
        face.copyTo(roi);

        Imgcodecs.imwrite(pathWrite, blurImage);

        face.release();
        image.release();
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

    public static boolean saveImageBinary(Mat image, String path) {
        if (image == null || image.empty()) {
            return false;
        }
        if (path == null || path.length() < 5 || !path.endsWith(".mat"))
            return false;
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
            return false;
        }

        if (image.channels() == 2 || image.channels() > 4) {
            return false;
        }

        byte[] buffer = new byte[image.channels() * image.cols() * image.rows()];
        image.get(0, 0, buffer);

        try (OutputStream out = new FileOutputStream(path);
             BufferedOutputStream bout = new BufferedOutputStream(out);
             DataOutputStream dout = new DataOutputStream(bout))
        {
            dout.writeInt(image.rows());
            dout.writeInt(image.cols());
            dout.writeInt(image.channels());
            dout.write(buffer);
            dout.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Mat loadMatBinary(String path) {
        if (path == null || path.length() < 5 || !path.endsWith(".mat")) {
            return new Mat();
        }

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return new Mat();
        }

        try (
                InputStream in = new FileInputStream(path);
                BufferedInputStream bin = new BufferedInputStream(in);
                DataInputStream din = new DataInputStream(bin)
        )
        {
            int rows = din.readInt();
            if (rows < 1) {
                return new Mat();
            }
            int cols = din.readInt();
            if (cols < 1) {
                return new Mat();
            }
            int ch = din.readInt();
            int type;
            if (ch == 1) {
                type = CvType.CV_8UC1;
            } else if (ch == 3) {
                type = CvType.CV_8UC1;
            } else if (ch == 4) {
                type = CvType.CV_8UC4;
            } else {
                return new Mat();
            }

            int size = ch * cols * rows;
            byte[] buf = new byte[size];
            int resize = din.read(buf);
            if (size != resize) {
                return new Mat();
            }

            Mat image = new Mat(rows, cols, type);
            image.put(0, 0, buf);
            return image;
        } catch (IOException e) {
            return new Mat();
        }
    }
    private MatOfRect detect(Mat imageRead) {
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        return faceDetections;
    }
}