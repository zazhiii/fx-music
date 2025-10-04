package com.zazhi.fxmusic.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

    private MediaPlayer mediaPlayer;

    private Slider soundSlider;

    @FXML
    private Slider progressSlider;

    @FXML
    private Label timeLabel;

    @FXML
    private ToggleButton playBtn;

    @FXML
    void OnPlayOrPauseAction(MouseEvent event) {
        if (mediaPlayer == null) {
            return;
        }
        MediaPlayer.Status status = mediaPlayer.getStatus();
        if (status == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

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

//                    BorderPane borderPane = new BorderPane();
                    String fileName = item.getName();
                    Label label = new Label(fileName.substring(0, fileName.lastIndexOf('.')));
//                    BorderPane.setAlignment(label, Pos.CENTER_LEFT);
//                    Button button = new Button();
//                    button.getStyleClass().add("delete-button");
//                    button.getStyleClass().add("svg-btn");
//                    button.setGraphic(new Region());
//                    borderPane.setCenter(label);
//                    borderPane.setRight(button);
                    setGraphic(label);

                }
            };
        });

        musicListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                Media media = new Media(newValue.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();

                // 绑定音量
                mediaPlayer.volumeProperty().bind(soundSlider.valueProperty().divide(100));

                // 绑定进度
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    double progress = newTime.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100.0;
                    progressSlider.setValue(progress);
                    // 更新时间显示
                    Duration total = mediaPlayer.getTotalDuration();
                    int currentSeconds = (int) newTime.toSeconds();
                    int totalSeconds = (int) total.toSeconds();
                    timeLabel.setText(String.format("%02d:%02d / %02d:%02d",
                            currentSeconds / 60, currentSeconds % 60,
                            totalSeconds / 60, totalSeconds % 60));
                });
                // 拖动/点击进度条
                progressSlider.setOnMouseClicked(event -> {
                    double progress = progressSlider.getValue() / 100.0;
                    Duration seekTime = mediaPlayer.getTotalDuration().multiply(progress);
                    mediaPlayer.seek(seekTime);
                });
                progressSlider.setOnMouseDragged(event -> {
                    double progress = progressSlider.getValue() / 100.0;
                    Duration seekTime = mediaPlayer.getTotalDuration().multiply(progress);
                    mediaPlayer.seek(seekTime);
                });

                playBtn.setSelected(true);
            }
        });
    }

    private void initSoundPopup() {
        soundPopup = new ContextMenu(new SeparatorMenuItem());
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sound.fxml"));
            root = loader.load();
            ObservableMap<String, Object> namespace = loader.getNamespace();
            soundSlider = (Slider) namespace.get("soundSlider");
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
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav", "*.flac"));
        List<File> files = fileChooser.showOpenMultipleDialog(getWindow());
        if (files == null || files.isEmpty()) {
            return;
        }
        musicListView.getItems().addAll(files);
    }
}
