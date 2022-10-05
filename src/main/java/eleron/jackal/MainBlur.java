package eleron.jackal;

import java.io.File;

public class MainBlur {
    private static void start(String... args) {
        try {
            Detector detector = new Detector();
            if (args[2].equals("-blur")) {
                detector.detect(new File(args[0]), new File(args[1]), Modes.Blurring);
            } else if (args[2].equals("-cut")) {
                detector.detect(new File(args[0]), new File(args[1]), Modes.Cutting);
            } else {
                System.out.println("Unknown mode flag (-cut or -blur)");
            }
        } catch (DetectException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        if (args.length == 2) {
            start(args[0], args[1], "-blur");
        } else if (args.length == 3) {
            start(args);
        } else {
            System.out.println("Support only 2 or 3 arguments");
        }
    }
}
