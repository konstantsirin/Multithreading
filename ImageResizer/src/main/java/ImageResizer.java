import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageResizer extends Thread {
// public class ImageResizer implements Runnable {
    private File[] files;
    private int targetSize;
    private String dstFolder;
    private long start;

    public ImageResizer(File[] files, int targetSize, String dstFolder, long start) {
        this.files = files;
        this.targetSize = targetSize;
        this.dstFolder = dstFolder;
        this.start = start;
    }

    @Override
    public void run() {
        try {
            for (File file : files) {
                BufferedImage image = ImageIO.read(file);

                if (image == null) {
                    continue;
                }
                String dstPath = dstFolder + '/' + file.getName();
                resizeImage(file.getAbsolutePath(), dstPath, targetSize);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - start) + " ms");
    }

    private void resizeImage(String originalFilePath, String targetFilePath, int targetSize) {
        try {
            File sourceFile = new File(originalFilePath);
            BufferedImage originalImage = ImageIO.read(sourceFile);
            if (originalImage != null) {
                BufferedImage resizedImage = Scalr.resize(originalImage, targetSize);

                File resizedFile = new File(targetFilePath);
                ImageIO.write(resizedImage, "jpg", resizedFile);

                originalImage.flush();
                resizedImage.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
