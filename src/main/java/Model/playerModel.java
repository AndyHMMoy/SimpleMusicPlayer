package Model;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Controller.playerController.playlist;

public class playerModel {

    public static ArrayList<mp3tag> searchResult = new ArrayList<>();

    public void searchByKeyword(String keyword) throws IOException {
        if (searchResult.size() > 0) {
            searchResult.clear();
        }
        Wini ini = new Wini(new File("library.ini"));
        for (String s : ini.keySet()) {
            if (s.contains(".mp3")) {
                if (ini.get(s, "title").equals(keyword) || ini.get(s, "artist").equals(keyword) || ini.get(s, "album").equals(keyword) || ini.get(s, "year").equals(keyword) || ini.get(s, "genre").equals(keyword)) {
                    String title = ini.get(s, "title");
                    String artist = ini.get(s, "artist");
                    String album = ini.get(s, "album");
                    String year = ini.get(s, "year");
                    String genre = ini.get(s, "genre");
                    searchResult.add(new mp3tag(s, title, artist, album, year, genre, null));
                }
            }
        }
        for (mp3tag mp3 : searchResult) {
            System.out.println(mp3.getFileName());
        }
    }

    public void getAll() throws IOException {
        if (searchResult.size() > 0) {
            searchResult.clear();
        }
        Wini ini = new Wini(new File("library.ini"));
        for (String s : ini.keySet()) {
            String title = ini.get(s, "title");
            String artist = ini.get(s, "artist");
            String album = ini.get(s, "album");
            String year = ini.get(s, "year");
            String genre = ini.get(s, "genre");
            if (s.contains(".mp3")) {
                searchResult.add(new mp3tag(s, title, artist, album, year, genre, null));
            }
            System.out.println(artist + " - " + title);
        }
        for (mp3tag mp3 : searchResult) {
            System.out.println(mp3.getFileName());
        }
    }

    public void printSearchResults() {
        for (Model.mp3tag mp3tag : searchResult) {
            System.out.println(mp3tag.getArtist() + " - " + mp3tag.getTitle());
        }
    }

    public void addToPlaylist(mp3tag tag) throws IOException {
        Wini ini = new Wini(new File("playlist.ini"));
        ini.put(tag.getFileName(), "title", tag.getTitle());
        ini.put(tag.getFileName(), "artist", tag.getArtist());
        ini.put(tag.getFileName(), "album", tag.getAlbum());
        ini.put(tag.getFileName(), "year", tag.getYear());
        ini.put(tag.getFileName(), "genre", tag.getGenre());
        ini.store();
    }

    public void removeFromPlaylist(String fileName) throws IOException {
        Wini ini = new Wini(new File("playlist.ini"));
        ini.remove(fileName);
        ini.store();
    }

    public void getPlaylist() throws IOException {
        Wini ini = new Wini(new File("playlist.ini"));
        if (playlist.size() > 0) {
            playlist.clear();
        }
        for (String s : ini.keySet()) {
            String title = ini.get(s, "title");
            String artist = ini.get(s, "artist");
            String album = ini.get(s, "album");
            String year = ini.get(s, "year");
            String genre = ini.get(s, "genre");
            if (s.contains(".mp3")) {
                playlist.add(new mp3tag(s, title, artist, album, year, genre, null));
            }
        }
    }

}
