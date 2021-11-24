package Model;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v2_4;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.*;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class sourcesModel {

    public static ArrayList<String> mediaSources = new ArrayList<>();
    ArrayList<Mp3File> Mp3s = new ArrayList<>();

    public void createTable(String directory) throws IOException {
        Wini ini = new Wini(new File("settings.ini"));
        String directoryEscape = directory.replaceAll("\\\\", "\\\\\\\\");
        Section section = ini.get("Sources");
        ArrayList<String> keys = new ArrayList<>(section.keySet());
        if (keys.size() == 0) {
            section.put("source1", directoryEscape);
        } else {
            int number = Integer.valueOf(keys.get(keys.size() - 1).replace("source", ""));
            section.put("source" + (number + 1), directoryEscape);
        }
        ini.store();
    }

    public void removeTable(String directory) throws IOException {
        String directoryDoubleSlash = directory.replaceAll("\\\\", "\\\\");
        Wini ini = new Wini(new File("library.ini"));
        ArrayList<String> sectionNames = new ArrayList<>();
        sectionNames.addAll(ini.keySet());
        for (String s : sectionNames) {
            if (s.contains(directoryDoubleSlash)) {
                ini.remove(s);
            }
        }
        ini.store();
        ini = new Wini(new File("settings.ini"));
        Section section = ini.get("Sources");
        ArrayList<String> sections = new ArrayList<>();
        sections.addAll(section.keySet());
        for (String s : sections) {
            String value = section.get(s);
            if (value.equals(directoryDoubleSlash)) {
                section.remove(s);
            }
        }
        ini.store();
    }

    public void getTableNames() throws IOException {
        mediaSources.clear();
        Config config = new Config();
        config.setEscape(false);
        Ini ini = new Ini(new File("settings.ini"));
        ini.setConfig(config);
        if (ini.get("Sources") == null) {
            ini.put("Sources", null, null);
            ini.store();
        }
        ini = new Ini(new File("settings.ini"));
        ini.setConfig(config);
        Section section = ini.get("Sources");
        if (section.size() > 0) {
            for (String s : section.keySet()) {
                String value = section.get(s);
                if (!mediaSources.contains(value)) {
                    String directory = value.replaceAll("\\\\", "\\\\\\\\");
                    mediaSources.add(directory);
                }
            }
        }
    }

    public void importTables(String directory) throws IOException {
        long startTime = System.nanoTime();
        importFiles(directory, true);
        long stopTime = System.nanoTime();
        System.out.println(stopTime - startTime);
        Mp3s.clear();
    }

    private void importFiles(String directory, boolean isImport) throws IOException {
        Wini ini = new Wini(new File("library.ini"));
        File f = new File(directory);
        ArrayList<File> files = new ArrayList<>(Arrays.asList(f.listFiles()));
        for (File file : files) {
            try {
                Mp3File mp3 = new Mp3File(file);
                ID3v2 tags = mp3.getId3v2Tag();
                String fileName = file.getAbsolutePath();
                fileName = fileName.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\[", "sqrBrkL").replaceAll("]", "sqrBrkR");
                String title = tags.getTitle();
                String artist = tags.getArtist();
                String album = tags.getAlbum();
                String year;
                if (tags.getYear() == null) {
                    MP3File mp3file = new MP3File(file);
                    ID3v2_4 tag = new ID3v2_4(mp3file.getID3v2Tag());
                    year = tag.getYearReleased();
                } else {
                    year = tags.getYear();
                }
                String genre = tags.getGenreDescription();
                if (!isImport) {
                    Mp3s.add(new Mp3File(file));
                } else {
                    ini.put(fileName, "title", title);
                    ini.put(fileName, "artist", artist);
                    ini.put(fileName, "album", album);
                    ini.put(fileName, "year", year);
                    ini.put(fileName, "genre", genre);
                    ini.store();
                }
                System.out.println(fileName);
            } catch (Exception e) {

            }
        }
        files.clear();
        System.out.println("Done");
    }

    public void updateTables(String directory) throws IOException, TagException {
        Wini ini = new Wini(new File("library.ini"));
        ini.clear();
        ini.store();
        importFiles(directory, true);
        Mp3s.clear();
    }

}
