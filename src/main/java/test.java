import Model.playerModel;
import Model.sourcesModel;
import com.mpatric.mp3agic.*;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v2_4;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

public class test {

    public static void main(String[] args) throws IOException, InvalidDataException, UnsupportedTagException, TagException {
        Ini ini = new Ini(new File("library.ini"));

        playerModel pM = new playerModel();

        pM.getAll();
        pM.printSearchResults();

//        for (String s : ini.keySet()) {
//            System.out.println(s);
//        }

//        File sourceFile = new File("E:\\OneDrive\\Music\\Japanese\\Afterglow - Don't be ''lazy'' (Game Size Ver.).mp3");
//        //File sourceFile = new File("E:\\OneDrive\\Music\\Japanese\\Afterglow - Don’t say “lazy”.mp3");
//        Mp3File mp3File = new Mp3File(sourceFile.getPath());
//        ID3v2 tags = mp3File.getId3v2Tag();
//        if (tags.getYear() == null) {
//            MP3File mp3 = new MP3File(sourceFile);
//            ID3v2_4 tag = new ID3v2_4(mp3.getID3v2Tag());
//            System.out.println(tag.getYearReleased());
//        } else {
//            System.out.println(tags.getYear());
//        }

//        System.out.println(sourceFile.getAbsoluteFile());
//        byte[] bytes = tags.getSongTitle().getBytes();
//        byte[] decoded = Base64.getDecoder().decode(bytes);
//        System.out.println(decoded);
//        System.out.println(tags.getLeadArtist());
//        System.out.println(tags.getAlbumTitle());
//        System.out.println(tags.getYearReleased());
//        System.out.println(tags.getSongGenre());

        //sM.createTable("E:\\OneDrive\\Music\\Japanese");

        // Get metadata value of song
//        String directory = "E:\\OneDrive\\Music\\Japanese\\\uFEFF黛冬優子 - \uFEFFDestined Rival (黛冬優子 Ver.).mp3";
//        String value = ini.get(directory, "year");
//        System.out.println(value);

        // Remove Directory
//        String directory = "E:\\OneDrive\\Music\\Chinese\\";
//        ArrayList<String> sectionNames = new ArrayList<>();
//        sectionNames.addAll(ini.keySet());
//        for (int i = 0; i < sectionNames.size(); i++) {
//            if (sectionNames.get(i).contains(directory)) {
//                ini.remove(sectionNames.get(i));
//            }
//        }
//        ini.store();

        // Get Album Art of Song
//        String directory = "E:\\OneDrive\\Music\\Chinese\\VirtuaReal - Trial and Error (Ft. 艾因) (Chinese Ver.).mp3";
//        Mp3File mp3 = new Mp3File(new File(directory));
//        ID3v2 tags = mp3.getId3v2Tag();
//        System.out.println(Arrays.toString(tags.getAlbumImage()));
    }

}
