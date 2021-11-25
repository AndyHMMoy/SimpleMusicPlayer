package Controller;

import Model.sourcesModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;

import static Controller.helper.iconRescaled;

public class sourcesController {

    @FXML private AnchorPane anchorid;
    @FXML private Button add;
    @FXML private Button remove;
    @FXML private Button update;
    @FXML private ListView<String> sourceList;

    sourcesModel sM = new sourcesModel();

    String importPath;
    ImageView imgView;

    // Sets the buttons with icons that have been resized and initialise the listView cells
    public void initialize() throws IOException {
        imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/plus.png"), 20));
        add.setGraphic(imgView);
        imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/minus.png"), 20));
        remove.setGraphic(imgView);
        imgView = new ImageView(iconRescaled(new File("src/main/resources/icons/refresh.png"), 20));
        update.setGraphic(imgView);

        sM.getSourceNames();
        ObservableList<String> sources = FXCollections.observableArrayList(sM.mediaSources);
        for (String source : sources) {
            sourceList.getItems().add(source);
        }
    }

    // Adds a source to the 'settings.ini' file
    @FXML
    public void addSource() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) anchorid.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        importPath = selectedDirectory.getAbsolutePath();
        if (selectedDirectory.getAbsolutePath() != null && !sM.mediaSources.contains(importPath)) {
            sM.mediaSources.add(importPath);
            sM.createSource(importPath);
            sourceList.getItems().add(importPath);
            sM.importFiles(importPath);
        }

    }

    // Removes a source from the 'settings.ini' file
    @FXML
    public void removeSource() throws IOException {
        sM.removeSource(sourceList.getSelectionModel().getSelectedItem());
        sM.mediaSources.remove(sourceList.getSelectionModel().getSelectedItem());
        sourceList.getItems().remove(sourceList.getSelectionModel().getSelectedItem());
    }

    // Updates the sources with new files
    @FXML
    public void updateSources() throws IOException, TagException {
        for (String source : sM.mediaSources) {
            sM.updateFiles(source);
        }
    }

}
