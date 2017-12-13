import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
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

        BufferedImage inputImage;
        BufferedImage img;
        try {
            // https://stackoverflow.com/questions/28593941/in-java-converting-an-image-to-srgb-makes-the-image-too-bright
            // https://docs.oracle.com/javase/7/docs/api/java/awt/color/ColorSpace.html
            inputImage = ImageIO.read(f);
            ColorSpace ics = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            ColorConvertOp cco = new ColorConvertOp( ics, null );
            img = cco.filter( inputImage, null );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int topCrop = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            int color = img.getRGB(0, y);
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
            int color = img.getRGB(x, 0);
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
        for (int y = img.getHeight() - 1; y > topCrop; y--) {
            int color = img.getRGB(0, y);
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
        for (int x = img.getWidth() - 1; x > leftCrop; x--) {
            int color = img.getRGB(x, 0);
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

        int horizontalCrop = Math.min(leftCrop, rightCrop);
        int verticalCrop = Math.min(topCrop, bottomCrop);

        BufferedImage croppedImage = img.getSubimage(
                horizontalCrop, verticalCrop,
                img.getWidth() - 2 * horizontalCrop,
                img.getHeight() - 2 * verticalCrop);

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
