package eleron.jackal;

import org.opencv.core.*;


final class DetectorService {
    public static void scaleRect(Rect rect, double xCoefficient, double yCoefficient) throws DetectException {
        if (xCoefficient <= 0) {
            throw new DetectException("scaleRect: invalid argument (xCoefficient must be > 0)");
        }
        if (yCoefficient <= 0) {
            throw new DetectException("scaleRect: invalid argument (yCoefficient must be > 0)");
        }
        if (rect.empty()) {
            throw new DetectException("scaleRect: rect is empty");
        }

        Point centrePoint = getCentre(rect);
        double newWidth = rect.width * Math.sqrt(xCoefficient);
        double newHeight = rect.height * Math.sqrt(yCoefficient);

        rect.set(new double[]{
                centrePoint.x - newWidth / 2,
                centrePoint.y - newHeight / 2,
                newWidth,
                newHeight
        });
    }

    public static void correctFaceRect(Rect rect, Mat image) {
        if (rect.x < 0) {
            rect.x = 0;
        } else if (rect.x + rect.width >= image.width()) {
            rect.x = image.width() - rect.width;
        }

        if (rect.y < 0) {
            rect.y = 0;
        } else if (rect.y + rect.height >= image.width()) {
            rect.y = image.width() - rect.height;
        }
    }

    public static Point getCentre(Rect rect) throws DetectException {
        if (rect.empty()) {
            throw new DetectException("getCentre: rect is empty");
        }
        return new Point(rect.x + (double) rect.width / 2, rect.y + (double) rect.height / 2);
    }

    public static byte[] convertIntToByte(int number) throws DetectException {
        if (number > 32_767) {
            throw new DetectException("convertIntToByte: Number is too large");
        } else if (number < 0) {
            throw new DetectException("convertIntToByte: Number is negative");
        }
        byte[] result = new byte[JackalTypes.INT_IMP_BYTES];
        result[0] = (byte) (number & 0xFF);
        result[1] = (byte) ((number >> 8) & 0xFF);

        return result;
    }

    public static byte[] getBytesImageParameters(int rows, int cols, int channels) throws DetectException {
        byte[] rowsBytes = convertIntToByte(rows);
        byte[] colsBytes = convertIntToByte(cols);
        byte[] channelsBytes = convertIntToByte(channels);
        byte[] result = mergeBytes(rowsBytes, colsBytes);

        return mergeBytes(result, channelsBytes);
    }

    public static int getParameterFromBytes(byte[] imageBytes, int indexStart) {
        byte[] numberByte = new byte[2];
        numberByte[0] = imageBytes[indexStart];
        numberByte[1] = imageBytes[indexStart + 1];

        return convertByteToInt(numberByte);
    }

    public static int convertByteToInt(byte[] number) {
        int high = number[1] >= 0 ? number[1] : 256 + number[1];
        int low = number[0] >= 0 ? number[0] : 256 + number[0];

        return low | (high << 8);
    }

    public static byte[] mergeBytes(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static void removeParameters(byte[] imageBytes) {
        System.arraycopy(imageBytes, 6, imageBytes, 0, imageBytes.length - 6);
    }
}
