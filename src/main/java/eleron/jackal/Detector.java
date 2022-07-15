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
        imageRead.release();
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
        imageRead.release();
    }

    public void edgeDetect(String pathRead, String pathWrite, int[] kernelSettings) throws DetectException {
        checkValidity(pathRead, kernelSettings, true, JackalTypes.DEFAULT);
        Mat image = Imgcodecs.imread(pathRead);
        Mat greySrc = getGrayMat(pathRead, pathWrite);
        Mat detectedEdges = new Mat();

        Imgproc.blur(greySrc,
                     detectedEdges,
                     new Size(3, 3));

        Imgproc.Canny(detectedEdges,
                      detectedEdges,
                      kernelSettings[0], kernelSettings[1], kernelSettings[2],
            false);

        Mat edgesCopy = detectedEdges.clone();
        Mat hierarchy = new Mat();
        Mat mask = new Mat();

        List<MatOfPoint> counters = new ArrayList<>();
        Imgproc.findContours(edgesCopy,
                             counters,
                             hierarchy,
                             Imgproc.RETR_LIST,
                             Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint biggestCounter = counters.stream().max(
                Comparator.comparingDouble(
                    x -> Imgproc.arcLength(new MatOfPoint2f(x.toArray()), false)
        )).orElseThrow(() -> new DetectException("Not detected"));

        Imgproc.grabCut(image,
                        mask,
                        Imgproc.boundingRect(biggestCounter),
                        new Mat(),
                        new Mat(),
                        1,
                        Imgproc.GC_INIT_WITH_RECT);

        Mat maskPR_FGD = new Mat();

        Core.compare(mask, new Scalar(Imgproc.GC_PR_FGD), maskPR_FGD,
                Core.CMP_EQ);

        Mat resultPR_FGD = new Mat(image.rows(), image.cols(), CvType.CV_8UC3,
                new Scalar(0, 0, 0));

        image.copyTo(resultPR_FGD, maskPR_FGD);
        Imgcodecs.imwrite(pathWrite, resultPR_FGD);

        image.release();
        greySrc.release();
        detectedEdges.release();
        edgesCopy.release();
        hierarchy.release();
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
        imageRead.release();

        return greyImage;
    }


    private MatOfRect detect(Mat imageRead) {
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        return faceDetections;
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

    private static Mat loadMatBinary(String path) {
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
}
