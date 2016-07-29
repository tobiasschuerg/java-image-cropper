import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    /**
     * Crops all images in the "in/" folder to their bounding box.
     */
    public static void main(String[] ignored) {
        Path path = Paths.get("in/");
        System.out.println("Cropping images in: " + path);
        ImageCropper cropper = new ImageCropper(3);
        final AtomicInteger count = new AtomicInteger();
        try {
            Files.walk(path).parallel().forEach(pathConsumer -> {
                if (Files.isRegularFile(pathConsumer)) {
                    System.out.print(count.incrementAndGet() + ": processing: " + pathConsumer);
                    cropper.crop(pathConsumer.toFile());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File f :
                cropper.getSkipped()) {
            System.out.println("Skipped: " + f);
        }

    }
}
