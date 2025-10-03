package com.zazhi.fxmusic.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlayerController {

    @FXML
    private AnchorPane drawerPane;

    @FXML
    private BorderPane sliderPane;

    private Timeline showAnimation;
    private Timeline hideAnimation;

    private ContextMenu soundPopup;

    @FXML
    private ListView<File> musicListView;

    @FXML
    void initialize() {
        initAnimation();
        initSoundPopup();
        initListView();
    }

    private void initListView() {
        musicListView.setCellFactory((fileListView) -> {
            return new ListCell<>(){
                @Override
                protected void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                       setGraphic(null);
                       return;
                    }

                    BorderPane borderPane = new BorderPane();
                    String fileName = item.getName();
                    Label label = new Label(fileName.substring(0, fileName.lastIndexOf('.')));
                    BorderPane.setAlignment(label, Pos.CENTER_LEFT);
                    Button button = new Button();
                    button.getStyleClass().add("delete-button");
                    button.getStyleClass().add("svg-btn");
                    button.setGraphic(new Region());
                    borderPane.setCenter(label);
                    borderPane.setRight(button);
                    setGraphic(borderPane);

                }
            };
        });
    }

    private void initSoundPopup() {
        soundPopup = new ContextMenu(new SeparatorMenuItem());
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/sound.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        soundPopup.getScene().setRoot(root);
    }

    private void initAnimation() {
        showAnimation = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(sliderPane.translateXProperty(), 0)));
        hideAnimation = new Timeline(
                new KeyFrame(Duration.millis(300), new KeyValue(sliderPane.translateXProperty(), 300)));
    }

    @FXML
    void onCloseMusicList(MouseEvent event) {
        showAnimation.stop();
        hideAnimation.play();
        hideAnimation.setOnFinished(e -> drawerPane.setVisible(false));
    }

    @FXML
    void onShowMusicList(MouseEvent event) {
        hideAnimation.stop();
        drawerPane.setVisible(true);
        showAnimation.play();
    }

    @FXML
    void onFullAction(MouseEvent event) {
        Stage stage = getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    void onMiniAction(MouseEvent event) {
        Stage stage = getWindow();
        stage.setIconified(true);
    }

    @FXML
    void onCloseAction(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void onShowSoundSliderAction(MouseEvent event) {
        Stage stage = getWindow();
        Parent btn = (Parent) event.getSource();
        // Get the screen coordinates of the button
        Bounds bounds = btn.localToScreen(btn.getBoundsInLocal());
        soundPopup.show(stage, bounds.getMinX() - soundPopup.getWidth() + 40,
                bounds.getMinY() - soundPopup.getHeight());
//        soundPopup.show(stage, bounds.getMinX(),
//                bounds.getMinY());
    }

    private Stage getWindow() {
        return (Stage) drawerPane.getScene().getWindow();
    }

    @FXML
    void onAddMusicAction(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        // TODO
//        fileChooser.getExtensionFilters()
//                .add(new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav", "*.flac"));
        List<File> files = fileChooser.showOpenMultipleDialog(getWindow());
        if (files == null || files.isEmpty()) {
            return;
        }
        musicListView.getItems().addAll(files);
    }
}
