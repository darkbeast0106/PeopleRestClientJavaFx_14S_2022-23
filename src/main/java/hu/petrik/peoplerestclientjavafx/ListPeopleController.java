package hu.petrik.peoplerestclientjavafx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class ListPeopleController {

    @FXML
    private Button insertButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<Person> peopleTable;
    @FXML
    private TableColumn<Person, Integer> idCol;
    @FXML
    private TableColumn<Person, String> nameCol;
    @FXML
    private TableColumn<Person, String> emailCol;
    @FXML
    private TableColumn<Person, Integer> ageCol;

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id")); //getId() függvény eredményét jeleníti meg
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        Platform.runLater(() -> {
            try {
                loadPeopleFromServer();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hiba");
                alert.setHeaderText("Hiba történt az adatok lekérése során");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                Platform.exit();
            }
        });
    }

    private void loadPeopleFromServer() throws IOException {
        Response response = RequestHandler.get(App.BASE_URL);
        String content = response.getContent();
        Gson converter = new Gson();
        Person[] people = converter.fromJson(content, Person[].class);
        peopleTable.getItems().clear();
        for (Person person : people) {
            peopleTable.getItems().add(person);
        }
    }

    @FXML
    public void insertClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("create-person-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 480);
            Stage stage = new Stage();
            stage.setTitle("Create person");
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                try {
                    loadPeopleFromServer();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba");
                    alert.setHeaderText("Nem sikerült kapcsolódni a szerverhez");
                    alert.showAndWait();
                }
            });
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Hiba történt az űrlap betöltése során");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateClick(ActionEvent actionEvent) {
    }

    @FXML
    public void deleteClick(ActionEvent actionEvent) {
        Person selected = peopleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Figyelmeztetés");
            alert.setHeaderText("Törléshez előbb válasszon ki egy elemet!");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Biztos?");
        confirmation.setHeaderText("Biztos, hogy törölni szeretné az alábbi rekordot: "
                + selected.getName());
        Optional<ButtonType> optionalButtonType = confirmation.showAndWait();
        if (optionalButtonType.isPresent() &&
                optionalButtonType.get().equals(ButtonType.OK)
        ) {
            String url = App.BASE_URL + "/" + selected.getId();
            try {
                RequestHandler.delete(url);
                loadPeopleFromServer();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hiba");
                alert.setHeaderText("Nem sikerült kapcsolódni a szerverhez");
                alert.showAndWait();
            }
        }
    }
}