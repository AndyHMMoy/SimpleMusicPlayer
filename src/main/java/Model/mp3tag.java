package Model;

public class mp3tag {

    private String fileName;
    private String title;
    private String artist;
    private String album;
    private String year;
    private String genre;
    private byte[] albumArt;

    public mp3tag(String fileName, String title, String artist, String album, String year, String genre, byte[] albumArt) {
        this.fileName = fileName;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.genre = genre;
        this.albumArt = albumArt;
    }

    public String getFileName() { return fileName; }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public byte[] getAlbumArt() { return albumArt; }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAlbumArt(byte[] albumArt) {
        this.albumArt = albumArt;
    }
}
