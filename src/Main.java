import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {

    /**
     * Crops all images in the "in/" folder to their bounding box.
     */
    public static void main(String[] ignored) {
        Instant start = Instant.now();

        Path path = Paths.get("in/");
        cropImages(path);

        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);

        System.out.println("Cropping took " + timeElapsed.getSeconds() + " seconds.");

    }

    private static void cropImages(Path directoryPath) {
        System.out.println("Cropping images in: " + directoryPath);
        int borderPx = 3;
        boolean verbose = false;
        ImageCropper cropper = new ImageCropper(borderPx, verbose, Paths.get("cropped/"));
        // cropper.setLogEvery(100);
        try {
            Files.walk(directoryPath).parallel().forEach(filePath -> {
                if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".png")) {
                    cropper.crop(filePath.toFile());
                } else {
                    System.out.println("Skipping: '" + filePath.toFile().getAbsolutePath() + "'");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<File> skipped = cropper.getSkipped();
        for (int i = 0; i < skipped.size(); i++) {
            File f = skipped.get(i);
            System.out.println("! Skipped (" + i + "): " + f);
        }
    }
}
