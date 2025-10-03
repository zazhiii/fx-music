package com.zazhi.fxmusic.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * @author zazhi
 * @date 2025/10/3
 * @description: TODO
 */
public class SoundController {
    @FXML
    private Slider soundSlider;

    @FXML
    private Label soundSliderLabel;

    @FXML
    void initialize() {
        soundSliderLabel.textProperty()
                .bind(soundSlider.valueProperty().asString("%.0f%%"));
    }
}
