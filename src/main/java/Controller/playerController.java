package Controller;

import Model.mp3tag;
import Model.playerModel;
import Model.sourcesModel;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static Controller.helper.iconRescaled;

public class playerController {

    @FXML private Button play;
    @FXML private Button stop;
    @FXML private Button previous;
    @FXML private Button next;
    @FXML private Button search;
    @FXML private Button loopTrack;
    @FXML private Button editSource;
    @FXML private Label volumeLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label maxTimeLabel;
    @FXML private Slider timeSlider;
    @FXML private Slider volumeSlider;
    @FXML private ProgressBar volumeBar;
    @FXML private ProgressBar timeBar;
    @FXML private ToggleButton shuffleTrack;
    @FXML private TextField searchBar;
    @FXML private TitledPane searchResultPane;
    @FXML private TitledPane playlistPane;
    @FXML private AnchorPane anchorPane;
    @FXML private ListView<mp3tag> playlistView;
    @FXML private ListView<mp3tag> searchResultView;
    @FXML private ImageView albumArt;
    @FXML private ImageView volumeImg;
    @FXML private MediaPlayer mediaPlayer;

    playerModel pM = new playerModel();
    sourcesModel sM = new sourcesModel();

    public static ObservableList<mp3tag> playlist = FXCollections.observableArrayList();

    String loopSetting = "noRpt";
    boolean isShuffle = false;
    boolean playing = false;

    Button button = new Button("Play");
    Label playlistLabel = new Label("Playlist");

    ImageView imgView;

    @FXML
    public void initialize() throws IOException {

        // Set images for buttons
        try {
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/play.png"), 40));
            play.setGraphic(imgView);
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/rewind.png"), 30));
            previous.setGraphic(imgView);
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/fast-forward.png"), 30));
            next.setGraphic(imgView);
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/no-replay.png"), 25));
            loopTrack.setGraphic(imgView);
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/stop.png"), 20));
            stop.setGraphic(imgView);
            stop.setVisible(false);
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/shuffle-off.png"), 25));
            shuffleTrack.setGraphic(imgView);
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/search.png"), 18));
            search.setGraphic(imgView);
            volumeImg.setImage(iconRescaled(new File("src/main/resources/icons/volume.png"), 30));
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/plus.png"), 25));
            editSource.setGraphic(imgView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates a curved border for album art
        Rectangle rectangle = new Rectangle(123, 123);
        rectangle.setArcHeight(13);
        rectangle.setArcWidth(13);
        albumArt.setClip(rectangle);

        // Helps form the size of the listviews
        searchResultPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.sizeToScene();
        });

        playlistPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.sizeToScene();
        });

        // Listener to help change the volume and icon when slider value is changed
        volumeSlider.valueProperty().addListener(observable -> {
            volumeLabel.setText(String.valueOf((int)(volumeSlider.getValue())));
            volumeBar.setProgress(volumeSlider.getValue()/100);
            try {
                if ((int) volumeSlider.getValue() == 0) {
                    volumeImg.setImage(iconRescaled(new File("src/main/resources/icons/mute.png"), 30));
                } else {
                    volumeImg.setImage(iconRescaled(new File("src/main/resources/icons/volume.png"), 30));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.setVolume(volumeSlider.getValue()/100);
        });

        // Activates search on enter key press
        searchBar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    searchKeyword();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Changes text of number of songs in playlist by listening for updates in the number of items
        playlistView.getItems().addListener((ListChangeListener) observable -> playlistLabel.setText("Playlist (" + playlistView.getItems().size() + " Songs Added)"));

        // Gets tables names for querying
        sM.getSourceNames();

        // Populates list and listview with initial playlist items
        pM.getPlaylist();
        populatePlaylistView();

        // Creates a template for each cell in the search result to include play button, title, artist and an 'add to playlist' button
        searchResultView.setCellFactory(param -> new ListCell<mp3tag>(){
            ImageView imageView = new ImageView();
            final Button button = new Button();
            final Button button2 = new Button("Add To Playlist");
            final Label label = new Label();
            final AnchorPane music = new AnchorPane(button, label);
            final HBox hBox = new HBox(music, button2);

            @Override
            public void updateItem(mp3tag tag, boolean empty) {
                super.updateItem(tag, empty);
                if (empty || tag == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        imageView = new ImageView(iconRescaled(new File("src/main/resources/icons/play.png"), 20));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    button.setTranslateY(3);
                    button.setGraphic(imageView);
                    button.setOnAction(event -> {
                        try {
                            loadMedia(tag);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    label.setText(tag.getTitle() + "\n" + tag.getArtist());
                    label.setTranslateX(50);
                    label.setTranslateY(2);
                    button2.setOnAction(event -> {
                        if (!playlist.contains(tag)) {
                            try {
                                modifyPlaylist(tag, true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    HBox.setHgrow(music, Priority.ALWAYS);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hBox);
                }
            }
        });

        // Creates a template for each cell in the playlist view to include play button, title, artist and an 'remove to playlist' button
        playlistView.setCellFactory(param -> new ListCell<mp3tag>(){
            ImageView imageView = new ImageView();
            final Button button = new Button();
            final Button button3 = new Button("Remove from playlist");
            final Label label = new Label();
            final AnchorPane music = new AnchorPane(button, label);
            final HBox hBox = new HBox(music, button3);

            @Override
            public void updateItem(mp3tag tag, boolean empty) {
                super.updateItem(tag, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        imageView = new ImageView(iconRescaled(new File("src/main/resources/icons/play.png"), 20));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    button.setGraphic(imageView);
                    button.setOnAction(event -> {
                        try {
                            loadMedia(tag);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    button.setTranslateY(3);
                    label.setText(tag.getTitle() + "\n" + tag.getArtist());
                    label.setTranslateX(50);
                    label.setTranslateY(2);
                    button3.setOnAction(event -> {
                        try {
                            modifyPlaylist(tag, false);
                        } catch (IOException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                    HBox.setHgrow(music, Priority.ALWAYS);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hBox);
                }
            }
        });

        // Gives properties for the 'play all' button on the playlist view header
        button.setStyle("-fx-background-color: ffffff; -fx-border-color: ffffff");
        button.setOnAction(event -> {
            ObservableList<mp3tag> tagList = FXCollections.observableArrayList(playlist);
            try {
                playPlaylist(tagList, 0);
            } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                e.printStackTrace();
            }
        });
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(0, 40, 0, 0));
        hBox.minWidthProperty().bind(searchResultPane.widthProperty());
        HBox region = new HBox();
        region.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(region, Priority.ALWAYS);
        hBox.getChildren().addAll(playlistLabel, region, button);
        playlistPane.setGraphic(hBox);
    }

    // Plays the playlist, shuffles depending on if shuffle mode is active
    @FXML
    public void playPlaylist(ObservableList<mp3tag> tagList, int start) throws IOException, InvalidDataException, UnsupportedTagException {
        if (isShuffle) {
            ArrayList<Integer> order = createShufflePlaylist(tagList.size());
            playPlaylistShuffle(tagList, order.get(0), order);
        } else {
            playPlaylistNoShuffle(tagList, start);
        }
    }

    // Adds or removes songs from the playlist
    @FXML
    public void modifyPlaylist(mp3tag tag, boolean add) throws IOException {
        if (add) {
            playlistView.getItems().add(tag);
            playlist.add(tag);
            pM.addToPlaylist(tag);
        } else {
            playlistView.getItems().remove(tag);
            playlist.remove(tag);
            pM.removeFromPlaylist(tag.getFileName());
        }
    }

    // Gets the search results and sets it into the view
    @FXML
    public void searchKeyword() throws IOException {
        // If there is text in the search bar, then search with the keyword
        if (!searchBar.getText().isEmpty()) {
            pM.searchByKeyword(searchBar.getText());
            searchResultView.getItems().clear();
            for (int i = 0; i < playerModel.searchResult.size(); i++) {
                searchResultView.getItems().add(playerModel.searchResult.get(i));
            }
        // Otherwise get all songs from all sources
        } else {
            pM.getAll();
            searchResultView.getItems().clear();
            for (int i = 0; i < playerModel.searchResult.size(); i++) {
                searchResultView.getItems().add(playerModel.searchResult.get(i));
            }
        }
    }

    // Changes the playback settings
    @FXML
    public void toggleLoop() throws IOException {
        ImageView imgView;
        switch(loopSetting) {
            case "noRpt":
                loopSetting = "RptOne";
                System.out.println("Current Setting: Repeat One");
                imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/replay.png"), 25));
                loopTrack.setGraphic(imgView);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                return;
            case "RptOne":
                loopSetting = "RptAll";
                System.out.println("Current Setting: Repeat All");
                imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/replay-all.png"), 25));
                loopTrack.setGraphic(imgView);
                mediaPlayer.setCycleCount(0);
                return;
            case "RptAll":
                loopSetting = "noRpt";
                System.out.println("Current Setting: No Repeat");
                imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/no-replay.png"), 25));
                loopTrack.setGraphic(imgView);
        }
    }

    // Toggles on/off the shuffle setting
    @FXML
    public void toggleShuffle() throws IOException {
        if (isShuffle) {
            isShuffle = false;
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/shuffle-off.png"), 25));
        } else {
            isShuffle = true;
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/shuffle.png"), 25));

        }
        shuffleTrack.setGraphic(imgView);
    }

    // Plays or pauses the current track
    @FXML
    public void playMedia() throws IOException {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.UNKNOWN || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/pause.png"), 40));
            play.setGraphic(imgView);
        } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/play.png"), 40));
            play.setGraphic(imgView);
        }
    }

    // Stops the player
    @FXML
    public void stopMedia() {
        mediaPlayer.stop();
        playing = false;
    }

    // Sets the duration back to the start of the track
    @FXML
    public void previousTrack() {
        mediaPlayer.seek(Duration.seconds(0));
    }

    // Skips to the next track
    @FXML
    public void nextTrack() {
        mediaPlayer.seek(mediaPlayer.getTotalDuration());
    }

    // Opens a window to a list of sources
    @FXML
    public void openSources() throws IOException {
        Parent newRoot = FXMLLoader.load(getClass().getClassLoader().getResource("sources.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setScene(new Scene(newRoot));
        primaryStage.show();
    }

    // Prepares all graphic elements to match the current song
    private void loadMedia(mp3tag tag) throws IOException, InvalidDataException, UnsupportedTagException {
        if (mediaPlayer != null) { mediaPlayer.stop(); }
        prepareTrack(tag.getFileName());
        Mp3File mp3 = new Mp3File(new File(tag.getFileName()));
        ID3v2 tags = mp3.getId3v2Tag();
        BufferedImage img = Thumbnails.of(new ByteArrayInputStream(tags.getAlbumImage())).width(112).height(112).keepAspectRatio(true).outputQuality(1).antialiasing(Antialiasing.ON).asBufferedImage();
        Image image = SwingFXUtils.toFXImage(img, null);
        searchResultPane.setText("Now Playing: " + tag.getTitle() + " - " + tag.getArtist());
        albumArt.setImage(image);
        mediaPlayer.play();
        ImageView imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/pause.png"), 40));
        play.setGraphic(imgView);
        String colour = helper.getMainColour(new ByteArrayInputStream(tags.getAlbumImage()));
        volumeBar.setStyle(String.format("-fx-accent: derive(%s, 50%%);", colour));
        timeBar.setStyle(String.format("-fx-accent: derive(%s, 50%%);", colour));
    }

    // Loads the current song into the mediaplayer and updates UI
    private void prepareTrack(String fileName) {
        String filePath = "file:\\\\\\" + fileName;
        filePath = filePath.replace("\\", "/").replaceAll(" ", "%20");
        System.out.println(filePath);
        mediaPlayer = new MediaPlayer(new Media(filePath));
        playing = true;
        UIHelper();
    }

    // Formats the time to a minute:seconds format
    private String formatTime(double time) {
        int minutes = (int) time/60;
        String seconds;
        if (((int)time % 60) < 10) {
            seconds = "0" + (int)time % 60;
        } else {
            seconds = String.valueOf((int)time % 60);
        }
        return minutes + ":" + seconds;
    }

    // A UI helper method to prepare the UI to match the current song
    private void UIHelper() {
        mediaPlayer.setOnReady(() -> {
            timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            maxTimeLabel.setText(formatTime(mediaPlayer.getTotalDuration().toSeconds()));
            mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
                timeSlider.setValue(newValue.toSeconds());
                timeBar.setProgress(timeSlider.getValue()/mediaPlayer.getTotalDuration().toSeconds());
                currentTimeLabel.setText(formatTime(mediaPlayer.getCurrentTime().toSeconds()));
            });
            timeSlider.valueProperty().addListener(ov -> {
                if (timeSlider.isPressed()) {
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                    timeBar.setProgress(timeSlider.getValue()/mediaPlayer.getTotalDuration().toSeconds());
                }
            });
            mediaPlayer.setVolume(volumeSlider.getValue()/100);
        });
    }

    // Creates a random order for the shuffle playlist
    private ArrayList<Integer> createShufflePlaylist(int size) {
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            order.add(i);
        }
        Collections.shuffle(order);
        return order;
    }

    // Helper method to initiate the playlist with no shuffle
    private void playPlaylistNoShuffle(ObservableList<mp3tag> tagList, int start) throws IOException, InvalidDataException, UnsupportedTagException {
        loadMedia(tagList.get(start));
        start++;
        int finalStart = start;
        mediaPlayer.setOnEndOfMedia(() -> {
            if (finalStart < tagList.size()) {
                try {
                    playPlaylistNoShuffle(tagList, finalStart);
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    e.printStackTrace();
                }
            }
            if (finalStart == tagList.size() && loopSetting.equals("RptAll")) {
                try {
                    playPlaylistNoShuffle(tagList, 0);
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Helper method to initiate the playlist with shuffle
    private void playPlaylistShuffle(ObservableList<mp3tag> tagList, int start, ArrayList<Integer> order) throws IOException, InvalidDataException, UnsupportedTagException {
        loadMedia(tagList.get(order.get(start)));
        start++;
        int finalStart = start;
        mediaPlayer.setOnEndOfMedia(() -> {
            if (finalStart < tagList.size()) {
                try {
                    playPlaylistShuffle(tagList, finalStart, order);
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    e.printStackTrace();
                }
            }
            if (finalStart == tagList.size() && loopSetting.equals("RptAll")) {
                try {
                    playPlaylistShuffle(tagList, 0, order);
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Refreshes the playlist view
    public void populatePlaylistView() {
        playlistView.getItems().clear();
        for (mp3tag tags : playlist) {
            playlistView.getItems().add(tags);
        }
    }
}
