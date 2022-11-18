package hu.petrik.peoplerestclientjavafx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdatePersonController extends Controller {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Spinner<Integer> ageField;
    @FXML
    private Button updateButton;

    private Person person;

    public void setPerson(Person person) {
        this.person = person;
        nameField.setText(this.person.getName());
        emailField.setText(this.person.getEmail());
        ageField.getValueFactory().setValue(this.person.getAge());
    }

    @FXML
    private void initialize() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 30);
        ageField.setValueFactory(valueFactory);
    }

    @FXML
    public void updateClick(ActionEvent actionEvent) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        int age = ageField.getValue();
        if (name.isEmpty()) {
            warning("Név megadása kötelező");
            return;
        }
        if (email.isEmpty()) {
            warning("Email megadása kötelező");
            return;
        }
        this.person.setName(name);
        this.person.setEmail(email);
        this.person.setAge(age);
        Gson converter = new Gson();
        String json = converter.toJson(this.person);
        try {
            String url = App.BASE_URL + "/" + this.person.getId();
            Response response = RequestHandler.put(url, json);
            if (response.getResponseCode() == 200) {
                Stage stage = (Stage) this.updateButton.getScene().getWindow();
                stage.close();
            } else {
                error("Hiba történt a módosítás során", response.getContent());
            }
        } catch (IOException e) {
            error("Nem sikerült kapcsolódni a szerverhez");
        }
    }
}
