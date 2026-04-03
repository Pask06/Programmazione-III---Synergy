package com.synergy.controller.fx;

import com.synergy.controller.ProjectController;
import com.synergy.model.Activity;
import com.synergy.model.Project;
import com.synergy.model.TaskGroup;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

// controller per la "finestra a comparsa" usata per creare o modificare un'Attività
public class ActivityFormController {

    // componenti grafici (Input dell'utente)
    @FXML private TextField titleField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextArea subtasksArea;

    private Project currentProject;
    private ProjectController projectController = new ProjectController();
    
    // NUOVA VARIABILE: Tiene traccia se stiamo modificando un'attività esistente.
    // se è "null", significa che stiamo creando una NUOVA attività.
    private Activity activityToEdit = null; 

    // metodo eseguito in automatico da JavaFX quando la finestra viene aperta
    @FXML
    public void initialize() {

        // popola il menu a tendina della priorità
        priorityBox.getItems().addAll("BASSA", "MEDIA", "ALTA");

        // imposta "MEDIA" come valore di default
        priorityBox.getSelectionModel().select("MEDIA");
    }

    // riceve il progetto a cui andrà agganciata l'attività
    public void setProject(Project project) {
        this.currentProject = project;
    }

    // METODO MODIFICA: Pre-compila il form se l'utente ha cliccato "Modifica"
    public void setActivityToEdit(Activity a) {
        this.activityToEdit = a; // salva l'attività in memoria
        
        // riempi i campi grafici con i vecchi dati dell'attività
        titleField.setText(a.getTitle());
        priorityBox.getSelectionModel().select(a.getPriority().toString());
        
        
        if (a.getDeadline() != null) {
            deadlinePicker.setValue(a.getDeadline());
        }

        // se l'attività che stiamo modificando è un "TaskGroup" (ha dei sotto-task)
        if (a instanceof TaskGroup) {
            TaskGroup group = (TaskGroup) a;
            StringBuilder sb = new StringBuilder();   // costruttore di stringhe per alte prestazioni
           
            // scorre tutti i sotto-task e li formatta nell'area di testo (uno per riga)
            for (Activity child : group.getChildren()) {
                sb.append(child.getTitle()).append("\n");
            }
            subtasksArea.setText(sb.toString().trim());
        }
    }

    // metodo chiamato quando si preme il tasto "Salva"
    @FXML
    private void handleSave() {

        // prende i valori inseriti dall'utente
        String title = titleField.getText();

        // validazione: Il titolo è obbligatorio! Se è vuoto, fa diventare il bordo rosso e ferma il salvataggio
        if (title == null || title.trim().isEmpty()) {
            titleField.setStyle("-fx-border-color: red;");
            return;
        }

        String priority = priorityBox.getValue();
        LocalDate date = deadlinePicker.getValue();

        // Converte la data in formato testo (se non è nulla)
        String dateStr = (date != null) ? date.toString() : "";

        // gestione dei sotto-task
        String subtasksText = subtasksArea.getText();
        String[] subTasks = null;

        // se l'utente ha scritto qualcosa, divide il testo riga per riga e crea un Array di stringhe
        if (subtasksText != null && !subtasksText.trim().isEmpty()) {
            subTasks = subtasksText.split("\\n");
        }

        // Creazione o Modifica dell'attivtià
        if (activityToEdit == null) {

            // se la variabile è null, creiamo una Nuova Attività nel progetto
            projectController.addActivityToProject(currentProject.getId(), title, priority, dateStr, subTasks);
        } else {
            // se la variabile NON è null, aggiorniamo l'attività esistente
            projectController.updateActivityContent(currentProject.getId(), activityToEdit.getId(), title, priority, dateStr, subTasks);
        }

        // chiude la finestra
        closeWindow();
    }

    // annulla e chiude la finestra senza salvare
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    // metodo di utilità per chiudere la finestra modale
    private void closeWindow() {

        // recupera il "palcoscenico" partendo da un elemento grafico a caso e lo chiude
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
