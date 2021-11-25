package Controller;

import de.androidpit.colorthief.ColorThief;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class helper {

    // Helper method to rescale the icons and images
    public static Image iconRescaled(File file, int size) throws IOException {
        BufferedImage img = Thumbnails.of(file).width(size).height(size).imageType(BufferedImage.TYPE_INT_ARGB).keepAspectRatio(true).outputFormat("png").antialiasing(Antialiasing.ON).asBufferedImage();
        Image image = SwingFXUtils.toFXImage(img, null);
        return image;
    }

    // Helper method to get the main colour of the album art
    public static String getMainColour(ByteArrayInputStream file) throws IOException {
        BufferedImage bImg = ImageIO.read(file);
        int a[] = ColorThief.getColor(bImg);
        String hex = String.format("#%02X%02X%02X", a[0], a[1], a[2]);
        return hex;
    }

}
