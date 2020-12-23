package controllers;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import classes.Audit;
import classes.Bericht;
import classes.CheckList;
import classes.Massnahme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("serial")
public class MassnahmeController extends AuditController implements Serializable {

	@FXML
	private TableColumn<Audit, String> tableColumnDatum = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> tableColumnUhrzeit = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> tableColumnAbteilung = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> tableColumnAuditart = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> tableColumnHausherr = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> tableColumnAuditorEins = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> tableColumnAuditorZwei = new TableColumn<Audit, String>();
	@FXML
	protected TableView<Audit> tableViewAudit = new TableView<Audit>();

	@FXML
	private TextField textAreaStandart = new TextField();
	@FXML
	private TextField textAreaKapitel = new TextField();
	@FXML
	private TextField textAreaKurzBeschreibung = new TextField();
	@FXML
	private ChoiceBox<String> massnahmenartBox = new ChoiceBox<String>();
	@FXML
	private TextField textAreaVerantwortlich = new TextField();
	@FXML
	private DatePicker datePickerMassnahme = new DatePicker();

	@FXML
	public Button insertMassnahme = new Button();
	@FXML
	private Button cancelButton = new Button();

	@FXML
	private TableView<CheckList> tableViewChecklist = new TableView<>();
	@FXML
	private TableColumn<CheckList, String> tableColumnNormKapitel = new TableColumn<>();
	@FXML
	private TableColumn<CheckList, String> tableColumnNormForderung = new TableColumn<>();
	@FXML
	private TableColumn<CheckList, String> tableColumnFragen = new TableColumn<>();

	@FXML
	private TableView<Bericht> tableViewBericht = new TableView<>();
	@FXML
	private TableColumn<Bericht, String> tableColumnEingesehene = new TableColumn<>();
	@FXML
	private TableColumn<Bericht, String> tableColumnErfuellt = new TableColumn<>();
	@FXML
	private TableColumn<Bericht, String> tableColumnBemerkungen = new TableColumn<>();

	@FXML
	private TableView<Massnahme> tableViewMassnahme = new TableView<Massnahme>();
	@FXML
	private TableColumn<Massnahme, String> tableColumnStandart = new TableColumn<Massnahme, String>();
	@FXML
	private TableColumn<Massnahme, String> tableColumnKapitel = new TableColumn<Massnahme, String>();
	@FXML
	private TableColumn<Massnahme, String> tableColumnMassnahmenArt = new TableColumn<Massnahme, String>();
	@FXML
	private TableColumn<Massnahme, String> tableColumnVerantwortlich = new TableColumn<Massnahme, String>();
	@FXML
	private TableColumn<Massnahme, String> tableColumnKurzBeschreibung = new TableColumn<Massnahme, String>();
	@FXML
	private TableColumn<Massnahme, String> tableColumnUnsetzung = new TableColumn<Massnahme, String>();

	Massnahme dataMassnahme = null;
	String id = AuditController.checklistId.getId();

	protected void returnAudit() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("audit");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<Audit> auditList = new ArrayList<Audit>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		Audit audit;
		String datum = result.get(0).getString("datum");
		tableColumnDatum.setCellValueFactory(new PropertyValueFactory<Audit, String>("datum"));
		String uhrzeit = result.get(0).getString("uhrzeit");
		tableColumnUhrzeit.setCellValueFactory(new PropertyValueFactory<Audit, String>("uhrzeit"));
		String abteilung = result.get(0).getString("abteilung");
		tableColumnAbteilung.setCellValueFactory(new PropertyValueFactory<Audit, String>("abteilung"));
		String auditart = result.get(0).getString("auditart");
		tableColumnAuditart.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditart"));
		String hausherr = result.get(0).getString("hausherr");
		tableColumnHausherr.setCellValueFactory(new PropertyValueFactory<Audit, String>("hausherr"));
		String auditorEins = result.get(0).getString("auditoreins");
		tableColumnAuditorEins.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditorEins"));
		String auditorZwei = result.get(0).getString("auditorzwei");
		tableColumnAuditorZwei.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditorZwei"));

		audit = new Audit(id, datum, uhrzeit, abteilung, auditart, hausherr, auditorEins, auditorZwei);
		auditList.add(audit);

		ObservableList<Audit> observableListAudit = FXCollections.observableArrayList(auditList);
		tableViewAudit.setItems(observableListAudit);

	}

	public void returnChecklist() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("checklist");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<CheckList> checklistList = new ArrayList<CheckList>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		CheckList checklist;
		for (int i = 0; i < result.size(); i++) {

			String normKapitel = result.get(i).getString("standartnormkapitel");
			tableColumnNormKapitel
					.setCellValueFactory(new PropertyValueFactory<CheckList, String>("standartNormKapitel"));
			String normForderung = result.get(i).getString("normforderung");
			tableColumnNormForderung.setCellValueFactory(new PropertyValueFactory<CheckList, String>("normForderung"));
			String fragen = result.get(i).getString("fragen");
			tableColumnFragen.setCellValueFactory(new PropertyValueFactory<CheckList, String>("fragen"));
			checklist = new CheckList(id, normKapitel, normForderung, fragen);
			checklistList.add(checklist);
			ObservableList<CheckList> observableListChecklist = FXCollections.observableArrayList(checklistList);
			tableViewChecklist.setItems(observableListChecklist);
		}

	}

	public void returnBericht() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("bericht");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<Bericht> berichtList = new ArrayList<Bericht>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		Bericht bericht;
		for (int i = 0; i < result.size(); i++) {

			String eingesehene = result.get(i).getString("eingesehene");
			tableColumnEingesehene.setCellValueFactory(new PropertyValueFactory<Bericht, String>("eingesehene"));
			String erfuellt = result.get(i).getString("erfuellt");
			tableColumnErfuellt.setCellValueFactory(new PropertyValueFactory<Bericht, String>("erfuellt"));
			String bemerkungen = result.get(i).getString("bemerkungen");
			tableColumnBemerkungen.setCellValueFactory(new PropertyValueFactory<Bericht, String>("bemerkungen"));
			bericht = new Bericht(id, eingesehene, erfuellt, bemerkungen);
			berichtList.add(bericht);
			ObservableList<Bericht> observableListBericht = FXCollections.observableArrayList(berichtList);
			tableViewBericht.setItems(observableListBericht);
		}

	}

	public void insertMassnahme() throws ClassNotFoundException, SQLException, IOException {
		Document massnahme = new Document();
		String standart = textAreaStandart.getText();
		String kapitel = textAreaKapitel.getText();
		String kurzbeschreibung = textAreaKurzBeschreibung.getText();
		String massnahmenart = massnahmenartBox.getValue().toString();
		String verantwortlich = textAreaVerantwortlich.getText();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String unsetzung = datePickerMassnahme.getValue().format(formatter);

		MongoCollection<Document> collection = getConnect("massnahme");

		if (standart.trim().length() != 0 && kapitel.trim().length() != 0 && kurzbeschreibung.trim().length() != 0
				&& verantwortlich.trim().length() != 0) {
			massnahme.append("id", id).append("standart", standart).append("kapitel", kapitel)
					.append("massnahmenart", massnahmenart).append("verantwortlich", verantwortlich)
					.append("kurzbeschreibung", kurzbeschreibung).append("unsetzung", unsetzung);

			collection.insertOne(massnahme);
			returnMassnahme();
			textAreaStandart.setText("");
			textAreaKapitel.setText("");
			textAreaKurzBeschreibung.setText("");
			massnahmenartBox.setValue(" ");
			textAreaVerantwortlich.setText("");
			datePickerMassnahme.setValue(null);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Alle Felder müssen ausgefüllt sein!");
			alert.showAndWait();
		}

	}

	public void returnMassnahme() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("massnahme");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<Massnahme> massnahmeList = new ArrayList<Massnahme>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		Massnahme massnahme;
		for (int i = 0; i < result.size(); i++) {

			String standart = result.get(i).getString("standart");
			tableColumnStandart.setCellValueFactory(new PropertyValueFactory<Massnahme, String>("standart"));
			String kapitel = result.get(i).getString("kapitel");
			tableColumnKapitel.setCellValueFactory(new PropertyValueFactory<Massnahme, String>("kapitel"));
			String massnahmenart = result.get(i).getString("massnahmenart");
			tableColumnMassnahmenArt.setCellValueFactory(new PropertyValueFactory<Massnahme, String>("massnahmenart"));
			String verantwortlich = result.get(i).getString("verantwortlich");
			tableColumnVerantwortlich
					.setCellValueFactory(new PropertyValueFactory<Massnahme, String>("verantwortlich"));
			String kurzbeschreibung = result.get(i).getString("kurzbeschreibung");
			tableColumnKurzBeschreibung
					.setCellValueFactory(new PropertyValueFactory<Massnahme, String>("kurzbeschreibung"));
			String unsetzung = result.get(i).getString("unsetzung");
			tableColumnUnsetzung.setCellValueFactory(new PropertyValueFactory<Massnahme, String>("unsetzung"));

			massnahme = new Massnahme(id, standart, kapitel, massnahmenart, verantwortlich, kurzbeschreibung,
					unsetzung);
			massnahmeList.add(massnahme);
			ObservableList<Massnahme> observableListMassnahme = FXCollections.observableArrayList(massnahmeList);
			tableViewMassnahme.setItems(observableListMassnahme);
		}
	}

	public void deleteMassnahmeButton() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<Massnahme, Void> colBtn = new TableColumn<Massnahme, Void>("Löschen");
		colBtn.setMinWidth(170);
		Callback<TableColumn<Massnahme, Void>, TableCell<Massnahme, Void>> cellFactory = new Callback<TableColumn<Massnahme, Void>, TableCell<Massnahme, Void>>() {
			public TableCell<Massnahme, Void> call(final TableColumn<Massnahme, Void> param) {
				final TableCell<Massnahme, Void> cell = new TableCell<Massnahme, Void>() {

					private Button btn = new Button(" Löschen ");
					{
						btn.setOnAction((ActionEvent event) -> {
							try {
								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Information");
								alert.setHeaderText("Maßnahme löschen?");
								alert.setContentText("Sind Sie sicher?");
								Optional<ButtonType> result = alert.showAndWait();
								if (result.get() == ButtonType.OK) {
									dataMassnahme = getTableView().getItems().get(getIndex());
									deleteMassnahme(dataMassnahme.getId());
									tableViewMassnahme.getItems().remove(dataMassnahme);
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setMinWidth(140);
							btn.setStyle("-fx-border-color: #1d7eaa; -fx-border-width: 2px;");
							btn.setStyle("-fx-text-fill: #1d7eaa;");
							setGraphic(btn);
							super.updateItem(item, empty);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setCellFactory(cellFactory);
		tableViewMassnahme.getColumns().add(colBtn);
	}

	public void deleteMassnahme(String id) throws ClassNotFoundException, SQLException {
		MongoCollection<Document> collection = getConnect("massnahme");
		Bson filter = eq("id", id);
		collection.deleteOne(filter);
	}

	public void initialize(java.net.URL location, ResourceBundle resources) {

		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stage = (Stage) cancelButton.getScene().getWindow();
				stage.close();
			}
		});

		try {
			returnMassnahme();
			returnAudit();
			returnBericht();
			returnChecklist();
			deleteMassnahmeButton();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> massnahmenartList = new ArrayList<>();
		massnahmenartList.add("Sofortmaßnahme");
		massnahmenartList.add("Korrekturmaßnahme");
		massnahmenartList.add("Vorbeugemaßnahme");
		massnahmenartList.add("Information");

		ObservableList<String> observableListMassnahmenart = FXCollections.observableArrayList(massnahmenartList);
		massnahmenartBox.setTooltip(new Tooltip("Maßnahmenart"));
		massnahmenartBox.setItems(observableListMassnahmenart);

	}
}
