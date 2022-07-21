package eleron.jackal;

import org.opencv.core.Scalar;

public final class JackalTypes {
    public final static int IMAGE_GREY_LEVEL = 0;

    public final static int HIERARCHY_NOT_FOUND = -1;
    public final static int HIERARCHY_NEXT = 0;
    public final static int HIERARCHY_PREV = 1;
    public final static int HIERARCHY_PARENT = 2;
    public final static int HIERARCHY_CHILD = 3;

    public final static Scalar WHITE = new Scalar(255, 255, 255);
    public final static Scalar RED = new Scalar(0, 0, 255);
    public final static Scalar GREEN = new Scalar(0, 255, 0);
    public final static Scalar BLUE = new Scalar(255, 0, 0);

    public final static int MAX_BRIGHTNESS = 255;

    public final static int KERNEL_SIZE = 3;
    public final static byte INT_IMP_BYTES = 2;
}
