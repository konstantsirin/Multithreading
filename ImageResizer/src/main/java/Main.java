import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class Main {
    private static int targetSize = 600;
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Количество доступных ядер: " + cores);

        String srcFolder = "/users/1/Desktop/picture/src";
        String dstFolder = "/users/1/Desktop/picture/dst";
        File srcDir = new File(srcFolder);

        File[] files = srcDir.listFiles();
        int sizeArr = (int) Math.ceil((double) files.length / (double) cores);
        int startPosition = 0;
        int endPosition = sizeArr;
        System.out.println("Количество файлов: " + files.length);
        System.out.println("Размер массива файлов для потока: " + sizeArr);
        System.out.println();

        while (true)  {
            boolean flag = false;
            File[] temp;
            if (startPosition + sizeArr > files.length) {
                temp = new File[files.length - startPosition];
                flag = true;
            } else {
                temp = new File[sizeArr];
            }
            System.arraycopy(files, startPosition, temp, 0, temp.length);
            startPosition += sizeArr;

            ImageResizer resizer = new ImageResizer(temp, targetSize, dstFolder, start);
            resizer.start();
            // new Thread(resizer).start();

            if (flag) break;
        }
    }
}
