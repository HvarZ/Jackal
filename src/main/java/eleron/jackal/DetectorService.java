package eleron.jackal;

import org.opencv.core.*;

/**
    Класс, содержащий полезные статические функции для детекции, выравнивания
 */
final class DetectorService {
    /**
        Метод, реализующий увеличение размера прямоугольника на соответствующие коэффициенты:
            1.  По оси Х - xCoefficient (не может быть меньше или равен 0)
            2.  По оси Y - yCoefficient (не может быть меньше или равен 0)
        Rect не может иметь нулевую площадь
     */
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

    /**
        Метод, корректирующий координаты прямоугольника при выходе за границы фотографии
     */
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


    /**
        Метод поиска центра прямоугольника
     */
    public static Point getCentre(Rect rect) throws DetectException {
        if (rect.empty()) {
            throw new DetectException("getCentre: rect is empty");
        }
        return new Point(rect.x + (double) rect.width / 2, rect.y + (double) rect.height / 2);
    }
}
