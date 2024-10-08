package controller;

import au.edu.uts.ap.javafx.ViewLoader;
import au.edu.uts.ap.javafx.Controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.stage.*;
import javafx.scene.image.Image;
import javafx.scene.control.*;
import model.Team;
import model.Teams;
import model.Player;

public class ManageTeamController extends Controller<List<Object>> {

    // --------------------------------------------- //
    @FXML private TextField nameTf;
    @FXML private TableView<Player> groupsTv;
    @FXML private TableColumn<?, ?> playerNameColumn;
    @FXML private TableColumn<?, ?> playerCreditColumn;
    @FXML private TableColumn<?, ?> playerAgeColumn;
    @FXML private TableColumn<?, ?> playerNumberColumn;
    // |                                           | //
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleButton;
    @FXML private Button saveAndCloseButton;
    // --------------------------------------------- //


    public Team getTeam(){
        Team team = (Team) model.get(1);
        return team;
    }
    public Teams getTeams(){
        return (Teams) model.get(0);
    }

    private Player getPlayer() { 
        return groupsTv.getSelectionModel().getSelectedItem();
    }

 
    private String getName() { return nameTf.getText(); }
	private void setName(String name) { nameTf.setText(name); }

    @FXML private void initialize() {
        // UI: 1/4 width for each col.
        Double w = 0.245;
        playerNameColumn.prefWidthProperty().bind(groupsTv.widthProperty().multiply(w));
        playerCreditColumn.prefWidthProperty().bind(groupsTv.widthProperty().multiply(w));
        playerAgeColumn.prefWidthProperty().bind(groupsTv.widthProperty().multiply(w));
        playerNumberColumn.prefWidthProperty().bind(groupsTv.widthProperty().multiply(w));
        
        // INIT BTNS DISABLED STATE
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleButton.setDisable(true);
        // UPDATED DISABLED STATE
        groupsTv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isTeamSelected = newSelection != null;
            updateButton.setDisable(!isTeamSelected);
            deleButton.setDisable(!isTeamSelected);
            addButton.setDisable(isTeamSelected);
        });

        // ADD DATA
        groupsTv.setItems(getTeam().getCurrentPlayers());
        nameTf.setText(getTeam().getName());

        final Player[] lastClicked = {null};
        // 2nd click DESELECTION
        groupsTv.setRowFactory(tv -> {
            TableRow<Player> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    if (event.getClickCount() == 1 && row.getItem().equals(lastClicked[0])) {
                        groupsTv.getSelectionModel().clearSelection();
                        lastClicked[0] = null;
                    } else {
                        lastClicked[0] = row.getItem();
                    }
                }
            });
            return row;
        });
        
        // UNSELECT WHEN manage or delete BTN CLICKED
        updateButton.setOnMouseClicked(event -> {
            groupsTv.getSelectionModel().clearSelection();
            lastClicked[0] = null;
        });
        deleButton.setOnMouseClicked(event -> {
            groupsTv.getSelectionModel().clearSelection();
            lastClicked[0] = null;
        });
    }

    // UTILITY METHOD   --------------------------------------
    public static Stage newStage(String imageName) {
        Stage stage = new Stage();
        stage.setX(ViewLoader.X + 601);
        stage.setY(ViewLoader.Y);
        stage.getIcons().add(new Image("/view/" + imageName));
        return stage;
    }
    // -------------------------------------------------------

    @FXML
    public void addPlayer() {
        try {
            Stage stage = newStage("edit.png");
            Player emptyPLayer = new Player("", -1.0, -1, -1);
            List<Object> data = new ArrayList<>();
            data.add(getTeam().getPlayers());
            data.add(emptyPLayer);
            ViewLoader.showStage(data, "/view/PlayerUpdateView.fxml", "Adding New Player", stage);
        } catch (IOException ex) {
            Logger.getLogger(SeasonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void updatePlayer() {
        try {
            Stage stage = newStage("edit.png");
            String title = "Updating Player: "+getPlayer().getName();
            List<Object> data = new ArrayList<>();
            data.add(getTeam().getPlayers());
            data.add(getPlayer());
            ViewLoader.showStage(data, "/view/PlayerUpdateView.fxml", title, stage);
        } catch (IOException ex) {
            Logger.getLogger(SeasonController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void deletePlayer() {
        getTeam().getPlayers().removePlayer(getPlayer());
    }

    public Boolean newNameIsValid(String newName) {
        String errorMsg = "";
        Validator v = new Validator();
        v.clear();
        v.generateErrors(newName);
        if (v.errors().size() > 0) {
            errorMsg = v.errors().toArray(new String[0])[v.errors().size() - 1];
        }
        if (getTeams().hasTeam(getName()) && !getName().equals(getTeam().getName())) {
            errorMsg = getName() + " Team already exists";
        }
        if (!errorMsg.isEmpty()) {
            try {
                Stage stage = newStage("error.png");
                ViewLoader.showStage(errorMsg, "/view/error.fxml", "Error!", stage);
            } catch (IOException ex) {
                Logger.getLogger(SeasonController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        return true;
    }

    @FXML
    public void saveAndClose() {
        String newName = nameTf.getText();
        if (!newNameIsValid(newName)) {
            return;
        }
        if (newName != null && !newName.equals(getTeam().getName())) {
            getTeam().setName(newName);
        }
        stage.close();
    }
}
