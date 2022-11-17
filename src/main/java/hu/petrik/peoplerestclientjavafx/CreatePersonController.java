package hu.petrik.peoplerestclientjavafx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class CreatePersonController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Spinner<Integer> ageField;
    @FXML
    private Button submitButton;

    @FXML
    private void initialize() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 30);
        ageField.setValueFactory(valueFactory);
    }

    @FXML
    public void submitClick(ActionEvent actionEvent) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        int age = ageField.getValue();
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Figyelmeztetés");
            alert.setHeaderText("Név megadása kötelező");
            alert.showAndWait();
            return;
        }
        if (email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Figyelmeztetés");
            alert.setHeaderText("Email megadása kötelező");
            alert.showAndWait();
            return;
        }
        Person newPerson = new Person(0, name, email, age);
        Gson converter = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = converter.toJson(newPerson);
        try {
            Response response = RequestHandler.post(App.BASE_URL, json);
            if (response.getResponseCode() == 201) {
                nameField.setText("");
                emailField.setText("");
                ageField.getValueFactory().setValue(30);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hiba");
                alert.setHeaderText("Hiba történt a felvétel során");
                alert.setContentText(response.getContent());
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Nem sikerült kapcsolódni a szerverhez");
            alert.showAndWait();
        }
    }
}
