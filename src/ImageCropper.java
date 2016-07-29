import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cropps an image to its bounding box.
 * <p>
 * Created by Tobias on 29.07.2016.
 */
class ImageCropper {

    private int border = 1;
    private List<File> skipped = new ArrayList<>();

    public List<File> getSkipped() {
        return skipped;
    }


    /**
     * @param border additional background padding to preserver color.
     */
    ImageCropper(int border) {
        this.border = border;
    }


    void crop(File f) {

        BufferedImage img = null;
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
                System.out.print(" - t: " + topCrop);
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
                System.out.print("l: " + leftCrop);
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
                System.out.print(", b: " + bottomCrop);
                break;
            }
        }

        int rightCrop = 0;
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
                rightCrop++;
            } else {
                System.out.println(", r: " + rightCrop);
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

        BufferedImage SubImgage = img.getSubimage(leftCrop, topCrop, img.getWidth() - leftCrop - rightCrop, img.getHeight() - topCrop - bottomCrop);

        File outputfile = new File("cropped/" + f.getName());

        try {
            ImageIO.write(SubImgage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
