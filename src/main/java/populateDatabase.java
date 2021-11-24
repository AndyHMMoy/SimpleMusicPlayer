import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.ID3v2_4;
import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class populateDatabase {

    public static void main(String[] args) throws IOException {

        //ArrayList<Mp3File> Mp3s = new ArrayList<>();

        String directory = "E:\\OneDrive\\Music\\Japanese";

        Config config = new Config();
        config.setEscape(false);
        Ini ini = new Ini(new File("library.ini"));
        ini.setConfig(config);

        File f = new File(directory);
        ArrayList<File> files = new ArrayList<>(Arrays.asList(f.listFiles()));
        for (File file : files) {
            try {
                Mp3File mp3 = new Mp3File(file);
                ID3v2 tags = mp3.getId3v2Tag();
                String fileName = file.getAbsolutePath();
                fileName = fileName.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\[", "sqrBrkL").replaceAll("]", "sqrBrkR");
                ini.put(fileName, "title", tags.getTitle());
                ini.put(fileName, "artist", tags.getArtist());
                ini.put(fileName, "album", tags.getAlbum());
                if (tags.getYear() == null) {
                    MP3File mp3file = new MP3File(file);
                    ID3v2_4 tag = new ID3v2_4(mp3file.getID3v2Tag());
                    ini.put(fileName, "year", tag.getYearReleased());
                } else {
                    ini.put(fileName, "year", tags.getYear());
                }
                ini.put(fileName, "genre", tags.getGenreDescription());
                ini.store();
                System.out.println(fileName);
            } catch (Exception e) {

            }
        }
        files.clear();

        System.out.println("Done");
    }

}
