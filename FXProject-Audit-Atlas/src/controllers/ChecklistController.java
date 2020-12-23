package controllers;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import classes.Audit;
import classes.CheckList;
import classes.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("serial")
public class ChecklistController extends AuditController implements Serializable {

	@FXML
	private TableColumn<Audit, String> columnDatum = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> columnUhrzeit = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> columnAbteilung = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> columnAuditart = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> columnHausherr = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> columnAuditorEins = new TableColumn<Audit, String>();
	@FXML
	private TableColumn<Audit, String> columnAuditorZwei = new TableColumn<Audit, String>();
	@FXML
	protected TableView<Audit> tableViewAudit = new TableView<Audit>();

	@FXML
	private TextField textAreaStandartNormKapitel = new TextField();
	@FXML
	private TextField textAreaNormForderung = new TextField();
	@FXML
	private TextField textAreaFragen = new TextField();
	@FXML
	private Button insertChecklistButton = new Button();
	@FXML
	private Button cancelButton = new Button();

	@FXML
	private TableView<CheckList> tableViewChecklist = new TableView<CheckList>();
	@FXML
	private TableColumn<CheckList, String> tableColumnNormKapitel = new TableColumn<CheckList, String>();
	@FXML
	private TableColumn<CheckList, String> tableColumnNormForderung = new TableColumn<CheckList, String>();
	@FXML
	private TableColumn<CheckList, String> tableColumnFragen = new TableColumn<CheckList, String>();

	CheckList checklistId = null;

	Audit checkListAudit;
	String id = AuditController.checklistId.getId();
	User userAudit = LoginController.user;

	protected void returnAudit() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("audit");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<Audit> auditList = new ArrayList<Audit>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		String datum = result.get(0).getString("datum");
		columnDatum.setCellValueFactory(new PropertyValueFactory<Audit, String>("datum"));
		String uhrzeit = result.get(0).getString("uhrzeit");
		columnUhrzeit.setCellValueFactory(new PropertyValueFactory<Audit, String>("uhrzeit"));
		String abteilung = result.get(0).getString("abteilung");
		columnAbteilung.setCellValueFactory(new PropertyValueFactory<Audit, String>("abteilung"));
		String auditart = result.get(0).getString("auditart");
		columnAuditart.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditart"));
		String hausherr = result.get(0).getString("hausherr");
		columnHausherr.setCellValueFactory(new PropertyValueFactory<Audit, String>("hausherr"));
		String auditorEins = result.get(0).getString("auditoreins");
		columnAuditorEins.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditorEins"));
		String auditorZwei = result.get(0).getString("auditorzwei");
		columnAuditorZwei.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditorZwei"));
		checkListAudit = new Audit(id, datum, uhrzeit, abteilung, auditart, hausherr, auditorEins, auditorZwei);
		auditList.add(checkListAudit);
		ObservableList<Audit> observableListCheckList = FXCollections.observableArrayList(auditList);
		tableViewAudit.setItems(observableListCheckList);

	}

	public void insertChecklist() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("checklist");

		if (!userAudit.getRolle().contentEquals("GAST")) {
			Document checklist = new Document();
			String standartNormKapitel = textAreaStandartNormKapitel.getText();
			String normForderung = textAreaNormForderung.getText();
			String fragen = textAreaFragen.getText();

			if (standartNormKapitel.trim().length() != 0 && normForderung.trim().length() != 0
					&& fragen.trim().length() != 0) {

				checklist.append("id", id).append("normforderung", normForderung).append("fragen", fragen)
						.append("standartnormkapitel", standartNormKapitel);

				collection.insertOne(checklist);
				returnChecklist();
				textAreaStandartNormKapitel.setText("");
				textAreaNormForderung.setText("");
				textAreaFragen.setText("");
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information");
				alert.setHeaderText(null);
				alert.setContentText("Alle Felder müssen ausgefüllt sein!");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Gast");
			alert.setHeaderText(null);
			alert.setContentText("Sie sind Gast und dürfen keine Checkliste einfügen!");
			alert.showAndWait();
		}

	}

	public void returnChecklist() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("checklist");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<CheckList> checklistList = new ArrayList<CheckList>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		for (int i = 0; i < result.size(); i++) {
			String normKapitel = result.get(i).getString("standartnormkapitel");
			tableColumnNormKapitel
					.setCellValueFactory(new PropertyValueFactory<CheckList, String>("standartNormKapitel"));
			String normForderung = result.get(i).getString("normforderung");
			tableColumnNormForderung.setCellValueFactory(new PropertyValueFactory<CheckList, String>("normForderung"));
			String fragen = result.get(i).getString("fragen");
			tableColumnFragen.setCellValueFactory(new PropertyValueFactory<CheckList, String>("fragen"));
			CheckList checkList = new CheckList(id, normKapitel, normForderung, fragen);
			checklistList.add(checkList);
			ObservableList<CheckList> observableListChecklist = FXCollections.observableArrayList(checklistList);
			tableViewChecklist.setItems(observableListChecklist);
		}
	}

	public void deleteChecklistButton() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<CheckList, Void> colBtn = new TableColumn<CheckList, Void>("Löschen");
		colBtn.setMinWidth(175);
		Callback<TableColumn<CheckList, Void>, TableCell<CheckList, Void>> cellFactory = new Callback<TableColumn<CheckList, Void>, TableCell<CheckList, Void>>() {
			public TableCell<CheckList, Void> call(final TableColumn<CheckList, Void> param) {
				final TableCell<CheckList, Void> cell = new TableCell<CheckList, Void>() {

					private Button btn = new Button("Löschen");
					{
						btn.setOnAction((ActionEvent event) -> {
							try {
								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Information");
								alert.setHeaderText("Checkliste Löschen?");
								alert.setContentText("Sind Sie sicher?");
								Optional<ButtonType> result = alert.showAndWait();

								if (result.get() == ButtonType.OK && !userAudit.getRolle().contentEquals("GAST")) {
									checklistId = getTableView().getItems().get(getIndex());
									deleteChecklist(checklistId.getId());
									tableViewChecklist.getItems().remove(checklistId);

								} else if (userAudit.getRolle().contentEquals("GAST")) {
									Alert alert1 = new Alert(AlertType.WARNING);
									alert1.setTitle("Gast");
									alert1.setHeaderText(null);
									alert1.setContentText("Sie sind Gast und dürfen keine Checkliste löschen!");
									alert1.showAndWait();
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
		tableViewChecklist.getColumns().add(colBtn);
	}

	public void deleteChecklist(String id) throws ClassNotFoundException, SQLException {
		MongoCollection<Document> collection = getConnect("checklist");
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
			returnAudit();
			deleteChecklistButton();
			returnChecklist();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}