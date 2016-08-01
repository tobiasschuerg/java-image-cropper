import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cropps an image to its bounding box.
 * <p>
 * Created by Tobias on 29.07.2016.
 */
class ImageCropper {

    private final static AtomicInteger filesProcessedCount = new AtomicInteger();
    private final boolean verbose;
    private final Path outputPath;
    private int border = 1;
    private List<File> skipped = new ArrayList<>();
    private int logEvery = 1;

    /**
     * @param border additional background padding to preserver color.
     */
    public ImageCropper(int border, boolean verbose, Path outputPath) {
        this.border = border;
        this.verbose = verbose;
        this.outputPath = outputPath;
    }

    public ImageCropper(int border, Path outputPath) {
        this(border, false, Paths.get("temp/"));
    }

    public void setLogEvery(int logEvery) {
        this.logEvery = logEvery;
    }

    List<File> getSkipped() {
        return skipped;
    }

    void crop(File f) {

        BufferedImage img;
        try {
            img = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int topCrop = 0;
        int color = img.getRGB(0, 0);
        for (int y = 0; y < img.getHeight(); y++) {
            boolean removableLine = true;
            for (int x = 0; x < img.getWidth(); x++) {
                int c = img.getRGB(x, y);
                if (c != color) {
                    removableLine = false;
                    break;
                }
            }
            if (removableLine) {
                topCrop++;
            } else {
                break;
            }
        }

        int leftCrop = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            boolean removableLine = true;
            for (int y = 0; y < img.getHeight(); y++) {
                int c = img.getRGB(x, y);
                if (c != color) {
                    removableLine = false;
                    break;
                }
            }
            if (removableLine) {
                leftCrop++;
            } else {
                break;
            }
        }

        int bottomCrop = 0;
        for (int y = img.getHeight() - 1; y > 0; y--) {
            boolean removableLine = true;
            for (int x = 0; x < img.getWidth(); x++) {
                int c = img.getRGB(x, y);
                if (c != color) {
                    removableLine = false;
                    break;
                }
            }
            if (removableLine) {
                bottomCrop++;
            } else {
                break;
            }
        }

        int rightCrop = 0;
        for (int x = img.getWidth() - 1; x > 0; x--) {
            boolean removableLine = true;
            for (int y = 0; y < img.getHeight(); y++) {
                int c = img.getRGB(x, y);
                if (c != color) {
                    removableLine = false;
                    break;
                }
            }
            if (removableLine) {
                rightCrop++;
            } else {
                break;
            }
        }

        topCrop -= border;
        topCrop = topCrop < 0 ? 0 : topCrop;

        bottomCrop -= border;
        bottomCrop = bottomCrop < 0 ? 0 : bottomCrop;

        leftCrop -= border;
        leftCrop = leftCrop < 0 ? 0 : leftCrop;

        rightCrop -= border;
        rightCrop = rightCrop < 0 ? 0 : rightCrop;

        if (img.getHeight() - topCrop - bottomCrop < 1) {
            skipped.add(f);
            return;
        }

        BufferedImage croppedImage = img.getSubimage(leftCrop, topCrop, img.getWidth() - leftCrop - rightCrop, img.getHeight() - topCrop - bottomCrop);

        File outputFile = new File(outputPath.toFile() + "/" + f.getName());

        try {
            ImageIO.write(croppedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((filesProcessedCount.incrementAndGet() % logEvery) == 0 || verbose) {
            String processedFile = filesProcessedCount.get() + ". processed: " + f;
            String cropped = " -> t: " + topCrop
                    + ", r: " + rightCrop
                    + ", b: " + bottomCrop
                    + ", l: " + leftCrop;
            System.out.printf("%-60s  %-30s%n", processedFile, cropped);
        }
    }
}
