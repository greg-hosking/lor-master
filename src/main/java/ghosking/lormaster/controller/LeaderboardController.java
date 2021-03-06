package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRPlayer;
import ghosking.lormaster.lor.LoRRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {

    @FXML
    Button profileButton, liveMatchButton, collectionButton, decksButton, metaButton;
    @FXML
    ListView<String> americasLeaderboardListView, europeLeaderboardListView, seaLeaderboardListView, worldLeaderboardListView;

    private static ArrayList<LoRPlayer> worldLeaderboard;

    private ArrayList<LoRPlayer> getRegionLeaderboard(String region) {
        // Create the request URL for the region.
        String url = "https://" + region.toLowerCase() + ".api.riotgames.com" +
                "/lor/ranked/v1/leaderboards?api_key=" + LoRRequest.API_KEY;

        // Returns a string containing a list of all players in Masters rank for
        // the given region in JSON format, or null if the region does not exist.
        String leaderboardJSONString = LoRRequest.get(url);
        if (leaderboardJSONString == null) throw new IllegalArgumentException("Illegal region: " + region);

        JSONArray leaderboardJSONArray = (JSONArray) new JSONObject(leaderboardJSONString).get("players");
        // Parse each element of the leaderboard JSONArray to create a new LoRPlayer
        // which is added to the leaderboard ArrayList and worldLeaderboard ArrayList.
        ArrayList<LoRPlayer> leaderboard = new ArrayList<>();
        for (Object playerObj : leaderboardJSONArray) {
            JSONObject playerJSONObj = (JSONObject)  playerObj;
            String gameName = playerJSONObj.getString("name");
            int rank = playerJSONObj.getInt("rank") + 1;
            int lp = playerJSONObj.getInt("lp");
            LoRPlayer player = new LoRPlayer(gameName, rank, lp);
            leaderboard.add(player);
            worldLeaderboard.add(player);
        }

        return leaderboard;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Link the other navigation buttons to their respective scenes.
        profileButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        liveMatchButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLiveMatchScene());
        collectionButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        metaButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());

        // Start a separate thread to get and sort the leaderboard data in the background.
        Thread leaderboardThread = new Thread(() -> {
            worldLeaderboard = new ArrayList<>();
            ArrayList<LoRPlayer> americasLeaderboard = getRegionLeaderboard("americas");
            ArrayList<LoRPlayer> europeLeaderboard = getRegionLeaderboard("europe");
            ArrayList<LoRPlayer> seaLeaderboard = getRegionLeaderboard("sea");

            // Sort the world leaderboard by LP descending and reassign rank as necessary.
            for (int i = 0; i < worldLeaderboard.size() - 1; i++) {
                int indexOfMostLP = i;
                for (int j = i + 1; j < worldLeaderboard.size(); j++)
                    if (worldLeaderboard.get(j).getLP() > worldLeaderboard.get(indexOfMostLP).getLP()) {
                        indexOfMostLP = j;
                }
                LoRPlayer placeholder = worldLeaderboard.get(i);
                worldLeaderboard.set(i, worldLeaderboard.get(indexOfMostLP));
                worldLeaderboard.set(indexOfMostLP, placeholder);
            }
            for (int i = 0; i < worldLeaderboard.size(); i++) {
                LoRPlayer player = worldLeaderboard.get(i);
                // Create a new instance of LoRPlayer in place of the original
                // to not modify the player's rank in their respective region.
                worldLeaderboard.set(i, new LoRPlayer(player.getGameName(), i + 1, player.getLP()));
            }

            // Add all players from each leaderboard ArrayList to the corresponding
            // leaderboard ListView to be rendered.
            for (LoRPlayer player : americasLeaderboard) americasLeaderboardListView.getItems().add(player.toString());
            for (LoRPlayer player : europeLeaderboard) europeLeaderboardListView.getItems().add(player.toString());
            for (LoRPlayer player : seaLeaderboard) seaLeaderboardListView.getItems().add(player.toString());
            for (LoRPlayer player : worldLeaderboard) worldLeaderboardListView.getItems().add(player.toString());
        });
        leaderboardThread.start();
    }
}
