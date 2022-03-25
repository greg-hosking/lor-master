package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    Button communityNavigationButton;
    @FXML
    Button collectionNavigationButton;
    @FXML
    Button decksNavigationButton;
    @FXML
    Button leaderboardNavigationButton;
    @FXML
    Button metaNavigationButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Link the other navigation buttons to their respective scenes.
        communityNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCommunityScene());
        collectionNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());
    }

}