package eleron.jackal;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.*;

import static eleron.jackal.DetectorService.scaleRect;
import static eleron.jackal.DetectorService.correctFaceRect;

/**
    Класс реализующий работу с изображениями:
        1. Детекция лица человека
        2. Вырезка прямоугольника лица человека
        3. Блюр фона за прямоугольником лица человека
 */
public final class Detector implements IDetectable {
    static {
        OpenCV.loadLocally();
    }
    private static final CascadeClassifier classifier = new CascadeClassifier();

    static {
        classifier.load(Objects.requireNonNull(Detector.class.getResource("/cascades/haarcascade_1.xml")).getPath());
    }

    /**
        Метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param input - File объект, искомое изображение
        @param output - File объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    @Override
    public void detect(File input, File output, Modes mode) throws DetectException {
        if (!input.exists()) {
            throw new DetectException("File (" + input.getPath() + ") not found");
        }
        Mat image = Imgcodecs.imread(input.getPath());
        switch (Objects.requireNonNull(mode)) {
            case Cutting -> {
                Mat face = faceDetectAndCut(image);
                Imgcodecs.imwrite(output.getPath(), face);
            }
            case Blurring -> {
                Mat blurImage = getBlurBackground(image);
                Imgcodecs.imwrite(output.getPath(), blurImage);
            }
            default -> throw new DetectException("Unknown work mode");
        }
        image.release();
    }

    /**
        Метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param input - File объект, искомое изображение
        @return byte[] объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    @Override
    public byte[] detect(File input, Modes mode) throws DetectException {
        if (!input.exists()) {
            throw new DetectException("File (" + input.getPath() + ") not found");
        }
        Mat image = Imgcodecs.imread(input.getPath());
        return detect(image, Objects.requireNonNull(mode));
    }


    /**
        Метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param imageBytes - byte[] объект, искомое изображение
        @return byte[] объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    @Override
    public byte[] detect(byte[] imageBytes, Modes mode) throws DetectException {
        Mat image = Imgcodecs.imdecode(new MatOfByte(Objects.requireNonNull(imageBytes)), Imgcodecs.IMREAD_UNCHANGED);
        return detect(image, Objects.requireNonNull(mode));
    }


    /**
        Private (вспомогательный) метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param image - Mat объект, искомое изображение
        @return byte[] объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    private byte[] detect(Mat image, Modes mode) throws DetectException {
        switch (mode) {
            case Cutting -> {
                Mat face = faceDetectAndCut(image);
                return Converter.saveImageBinary(face);
            }
            case Blurring -> {
                Mat blurImage = getBlurBackground(image);
                return Converter.saveImageBinary(blurImage);
            }
            default -> throw new DetectException("Unknown work mode");
        }
    }

    /**
        Private (вспомогательный, вызывается при любом режиме работы) метод, реализующий поиск лица
     */
    private Rect faceDetect(Mat imageRead) throws DetectException {
        MatOfRect faceDetections = new MatOfRect();
        classifier.detectMultiScale(imageRead, faceDetections);
        return Arrays.stream(faceDetections.toArray()).max(Comparator.comparingInt(x -> x.height * x.width))
                .orElseThrow(() -> new DetectException("faceDetect: not detected"));
    }

    /**
        Private (вспомогательный, вызывается при определенном режиме работы(Modes)) метод, реализующий поиск лица
        и вырезку его с сохранением только прямоугольника лица
     */
    private Mat faceDetectAndCut(Mat image) throws DetectException {
        Mat imageCopy = image.clone();

        Rect faceRect = faceDetect(imageCopy);
        scaleRect(faceRect, JackalTypes.X_SCALE_COEFFICIENT, JackalTypes.Y_SCALE_COEFFICIENT);
        correctFaceRect(faceRect, imageCopy);

        return new Mat(image, faceRect);
    }


    /**
        Private (вспомогательный, вызывается при определенном режиме работы(Modes)) метод, реализующий поиск лица
        и блюр заднего фона за прямоугольником лица
     */
    private Mat getBlurBackground(Mat image) throws DetectException {
        if (image.empty()) {
            throw new DetectException("getBlurBackground: image is empty");
        }

        Mat imageCopy = image.clone();

        Rect faceRect = faceDetect(imageCopy);
        scaleRect(
                faceRect,
                JackalTypes.X_SCALE_COEFFICIENT,
                JackalTypes.Y_SCALE_COEFFICIENT
        );
        correctFaceRect(faceRect, image);

        Mat blurImage = image.clone();
        Mat face = new Mat(image, faceRect);
        Mat roi = blurImage.submat(new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height));

        Imgproc.GaussianBlur(image, blurImage, JackalTypes.BLUR_SQUARE, JackalTypes.BLUR_SIGMA);
        face.copyTo(roi);

        return blurImage;
    }
}