package eleron.jackal;

import java.io.File;

public interface IDetectable {
    /**
        Метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param input - File объект, искомое изображение
        @param output - File объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    void detect(File input, File output, Modes mode) throws DetectException;

    /**
        Метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param input - File объект, искомое изображение
        @return byte[] объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    byte[] detect(File input, Modes mode) throws DetectException;

    /**
        Метод, реализующий поиск лиц/лица на фотографии в зависимости от режима работы делает следующий функционал:
            1. Modes.Blurring - блюр фона за прямоугольника лица
            2. Modes.Cutting - вырезка прямоугольника лица
        @param imageBytes - byte[] объект, искомое изображение
        @return - byte[] объект, результирующее изображение
        @throws DetectException Исключение выбрасывается при отсутсвии input-файла
     */
    byte[] detect(byte[] imageBytes, Modes mode) throws DetectException;
}
