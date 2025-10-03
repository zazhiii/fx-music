package com.zazhi.fxmusic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author zazhi
 * @date 2025/10/3
 * @description: FxMusicApp
 */
public class FxMusicApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/player.fxml"));
        stage.setScene(new Scene(root, null));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
