package eleron.jackal;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
    Класс, содержащий статические методы конвертации изображений в байтовое представление и байтовые представления в
    изображения, также вспомогательные методы работы с core.opencv объектами
    (недоступными для использования пользователем). Не может быть инстанцируем.
 */
public final class Converter {
    private Converter() {}

    /**
        Публичный метод конвентации byte[] в File. Поддерживаемые форматы изображений (jpeg, jpg, png, bmp и другие)
        Нет необходимости в преждевременном создании файлов типа File, но при некорректном формате изображения
        выйдет ошибка времени исполнения.
     */
    public static void convertBytesToImage(byte[] imageBytes, File output) {
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_UNCHANGED);
        Imgcodecs.imwrite(output.getPath(), image);
    }


    /**
        Публичный метод конвертации File в byte[] с прямым сохранением байт как по объему, так и по компановке.
        Необходимо наличие файла, при его отсутсвии будет выброшено исключение.
     */
    public static byte[] convertImageToBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }


    /**
        Защищенный метод конвертации объекта типа Mat в byte[].
     */
    static byte[] saveImageBinary(Mat image) throws DetectException {
        if (image == null || image.empty()) {
            throw new DetectException("saveImageBinary: image is empty");
        }
        MatOfByte imageArray = new MatOfByte();
        Imgcodecs.imencode(JackalTypes.SAVE_TYPE, image, imageArray);

        return imageArray.toArray();
    }
}
