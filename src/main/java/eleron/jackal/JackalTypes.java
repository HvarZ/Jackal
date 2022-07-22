package eleron.jackal;

import org.opencv.core.Size;

/*
    Класс, полезных для разработки констант
 */
public final class JackalTypes {
    public final static double X_SCALE_COEFFICIENT = 1.5;
    public final static double Y_SCALE_COEFFICIENT = 2.25;

    public static Size BLUR_SQUARE = new Size(41, 41);
    public static int BLUR_SIGMA = 140;

    public static String SAVE_TYPE = ".jpg";
}
