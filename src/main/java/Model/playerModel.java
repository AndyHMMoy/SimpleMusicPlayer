package Model;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Controller.playerController.playlist;

public class playerModel {

    public static ArrayList<mp3tag> searchResult = new ArrayList<>();

    // Gets all songs with a title, artist, album, year, genre that matches the keyword
    public void searchByKeyword(String keyword) throws IOException {
        if (searchResult.size() > 0) {
            searchResult.clear();
        }
        Wini ini = new Wini(new File("library.ini"));
        for (String s : ini.keySet()) {
            if (s.contains(".mp3")) {
                if (ini.get(s, "title").toLowerCase().contains(keyword.toLowerCase()) ||
                    ini.get(s, "artist").toLowerCase().contains(keyword.toLowerCase()) ||
                    ini.get(s, "album").toLowerCase().contains(keyword.toLowerCase()) ||
                    ini.get(s, "year").toLowerCase().contains(keyword.toLowerCase()) ||
                    ini.get(s, "genre").toLowerCase().contains(keyword.toLowerCase())) {
                    String title = ini.get(s, "title");
                    String artist = ini.get(s, "artist");
                    String album = ini.get(s, "album");
                    String year = ini.get(s, "year");
                    String genre = ini.get(s, "genre");
                    searchResult.add(new mp3tag(s, title, artist, album, year, genre, null));
                }
            }
        }
    }

    // Gets all songs in 'library.ini'
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
    }

    // Adds a song's tags in 'playlist.ini'
    public void addToPlaylist(mp3tag tag) throws IOException {
        Wini ini = new Wini(new File("playlist.ini"));
        ini.put(tag.getFileName(), "title", tag.getTitle());
        ini.put(tag.getFileName(), "artist", tag.getArtist());
        ini.put(tag.getFileName(), "album", tag.getAlbum());
        ini.put(tag.getFileName(), "year", tag.getYear());
        ini.put(tag.getFileName(), "genre", tag.getGenre());
        ini.store();
    }

    // Removes a song from 'playlist.ini'
    public void removeFromPlaylist(String fileName) throws IOException {
        Wini ini = new Wini(new File("playlist.ini"));
        ini.remove(fileName);
        ini.store();
    }

    // Gets all songs in 'playlist.ini' and adds them to playlist arraylist
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
