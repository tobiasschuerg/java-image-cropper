import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        File f = new File("in/foo.png");

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
                System.out.println("topcrop " + topCrop);
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
                System.out.println("leftcrop " + leftCrop);
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
                System.out.println("bottomCrop " + bottomCrop);
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
                System.out.println("rightCrop " + rightCrop);
                break;
            }
        }

        topCrop--;
        bottomCrop--;
        leftCrop--;
        rightCrop--;
        BufferedImage SubImgage = img.getSubimage(leftCrop, topCrop, img.getWidth() - leftCrop - rightCrop, img.getHeight() - topCrop - bottomCrop);

        File outputfile = new File("out/foo.png");

        try {
            ImageIO.write(SubImgage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
