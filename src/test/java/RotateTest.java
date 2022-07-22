import eleron.jackal.DetectException;
import eleron.jackal.Detector;
import eleron.jackal.IDetectable;
import eleron.jackal.Modes;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;


public class RotateTest {
    public static void rotate(Mat img, double angle) {
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2BGRA);
        Mat M = Imgproc.getRotationMatrix2D(
                new Point((double)img.width() / 2, (double)img.height() / 2), angle, 1);

        Rect rect = new RotatedRect(
                new Point((double)img.width() / 2, (double)img.height() / 2),
                new Size(img.width(), img.height()), angle).boundingRect();
        double[] arrX = M.get(0, 2);
        double[] arrY = M.get(1, 2);
        arrX[0] -= rect.x;
        arrY[0] -= rect.y;
        M.put(0, 2, arrX);
        M.put(1, 2, arrY);
        Imgproc.warpAffine(img, img, M, rect.size(),
                Imgproc.INTER_LINEAR, Core.BORDER_CONSTANT,
                new Scalar(255, 255, 255, 255));
    }

    public static void main(String[] args) throws DetectException {
        int angle = 0;
        IDetectable detector = new Detector();

        while (true) {
            try {
                Mat image = Imgcodecs.imread("src/test/resources/main/1.jpg");
                rotate(image, angle);
                Imgcodecs.imwrite("src/test/resources/rotate/1_" + angle + ".jpg", image);
                detector.detect(
                        new File("src/test/resources/rotate/1_" + angle + ".jpg"),
                        Modes.Blurring);

                angle--;
            } catch (DetectException e) {
                break;
            }
        }

        System.out.println(angle);
    }
}
