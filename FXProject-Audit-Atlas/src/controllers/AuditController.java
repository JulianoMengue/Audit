package controllers;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import classes.Audit;
import classes.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AuditController implements Initializable {

	@FXML
	private DatePicker datePickerAudit = new DatePicker();
	@FXML
	private TextField textFieldAuditAbteilung = new TextField();
	@FXML
	private ChoiceBox<String> auditartBox = new ChoiceBox<String>();
	@FXML
	private TextField textFieldAuditHausherr = new TextField();
	@FXML
	private TextField textFieldAuditorEins = new TextField();
	@FXML
	private TextField textFieldAuditorZwei = new TextField();

	@FXML
	private Button sendButton = new Button();

	@FXML
	private Button insertUsers = new Button();
	@FXML
	private TextField textFieldInsertEmail = new TextField();
	@FXML
	private TextField textFieldInsertPasswort = new TextField();

	@FXML
	private TableColumn<Audit, String> columnDatum = new TableColumn<Audit, String>();
	@FXML
	private ChoiceBox<String> uhrzeitBox = new ChoiceBox<String>();
	@FXML
	private TableColumn<Audit, String> columnId = new TableColumn<Audit, String>();
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
	private TableView<Audit> tableViewUbersicht = new TableView<Audit>();

	@FXML
	private Button insertButton = new Button();
	@FXML
	private Button showUsers = new Button();

	public static Audit checklistId = null;
	public static Audit berichtId = null;
	public static Audit massnahmeId = null;

	Audit dataDeleteAudit = null;
	User dataDeleteUser = null;

	@FXML
	private Label labelSofortmassnahme = new Label();
	@FXML
	private Label labelKorrekturmassnahme = new Label();
	@FXML
	private Label labelVorbeugemassnahme = new Label();
	@FXML
	private Label labelInformation = new Label();
	@FXML
	private Button button = new Button();
	@FXML
	private Label labelNameZwei = new Label();
	@FXML
	private Label labelNameEins = new Label();
	@FXML
	private TextField textFieldName = new TextField();
	@FXML
	private Button buttonName = new Button();
	@FXML
	private TableView<User> tableViewUsers = new TableView<User>();
	@FXML
	private TableColumn<User, String> columnEmail = new TableColumn<User, String>();
	@FXML
	private TableColumn<User, String> columnPassword = new TableColumn<User, String>();
	@FXML
	private TableColumn<User, String> columnRolle = new TableColumn<User, String>();
	@FXML
	private ChoiceBox<String> rolleChoiceBox = new ChoiceBox<String>();
	@FXML
	private Label emailLabel = new Label();
	@FXML
	private Label rolleLabel = new Label();
	@FXML
	private Button buttonAusloggen = new Button();

	public static String auditId;

	public static String getId() {
		return auditId;
	}

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	Random rand = new Random();
	int upperbound = 999999999;

	User userAudit = LoginController.user;

	@SuppressWarnings("resource")
	public static MongoCollection<Document> getConnect(String collection) {
		MongoClientURI uri = new MongoClientURI(
				"mongodb+srv://bananastaut:""@bananacluster.gzaux.mongodb.net/<"">?retryWrites=true&w=majority");
		MongoClient mongoClient = new MongoClient(uri);
		MongoCollection<Document> coll = mongoClient.getDatabase("BananaDatabase").getCollection(collection);
		return coll;
	}

	public void insertAudit() throws SQLException, ClassNotFoundException, MessagingException, IOException {
		int random = rand.nextInt(upperbound);
		MongoCollection<Document> collection = getConnect("audit");
		Document audit = new Document();
		if (!userAudit.getRolle().contentEquals("GAST")) {
			String datum = datePickerAudit.getValue().format(formatter);
			String uhrzeit = uhrzeitBox.getValue().toString();
			String abteilung = textFieldAuditAbteilung.getText();
			String auditart = auditartBox.getValue().toString();
			String hausherr = textFieldAuditHausherr.getText();
			String auditorEins = textFieldAuditorEins.getText().toLowerCase();
			String auditorZwei = textFieldAuditorZwei.getText().toLowerCase();
			String id = String.valueOf(random);
			auditId = id;
			audit.append("id", id).append("datum", datum).append("uhrzeit", uhrzeit).append("abteilung", abteilung)
					.append("auditart", auditart).append("hausherr", hausherr).append("auditoreins", auditorEins)
					.append("auditorzwei", auditorZwei);

			collection.insertOne(audit);
			clearFields();
			AlertEmail();

		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setTitle("Gast E-Mail Konto");
			alert.setHeaderText(null);
			alert.setContentText(userAudit.getEmail() + " ist als " + userAudit.getRolle()
					+ " angemeldet und darf kein Audit inserieren");
			alert.showAndWait();
		}
		ubersicht();
	}

	public void ubersicht() throws ClassNotFoundException, SQLException, IOException {
		Audit audit;
		MongoCollection<Document> collection = getConnect("audit");
		FindIterable<Document> query = collection.find();
		List<Audit> auditList = new ArrayList<Audit>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		for (int i = 0; i < result.size(); i++) {
			String id = result.get(i).getString("id");
			String datum = result.get(i).getString("datum");
			columnDatum.setCellValueFactory(new PropertyValueFactory<Audit, String>("datum"));
			String uhrzeit = result.get(i).getString("uhrzeit");
			columnUhrzeit.setCellValueFactory(new PropertyValueFactory<Audit, String>("uhrzeit"));
			String abteilung = result.get(i).getString("abteilung");
			columnAbteilung.setCellValueFactory(new PropertyValueFactory<Audit, String>("abteilung"));
			String auditart = result.get(i).getString("auditart");
			columnAuditart.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditart"));
			String hausherr = result.get(i).getString("hausherr");
			columnHausherr.setCellValueFactory(new PropertyValueFactory<Audit, String>("hausherr"));
			String auditorEins = result.get(i).getString("auditoreins");
			columnAuditorEins.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditorEins"));
			String auditorZwei = result.get(i).getString("auditorzwei");
			columnAuditorZwei.setCellValueFactory(new PropertyValueFactory<Audit, String>("auditorZwei"));
			audit = new Audit(id, datum, uhrzeit, abteilung, auditart, hausherr, auditorEins, auditorZwei);
			auditList.add(audit);
			ObservableList<Audit> observableListAtlas = FXCollections.observableArrayList(auditList);
			tableViewUbersicht.setItems(observableListAtlas);
		}

	}

	public void deleteAudit(String id) throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collectionAudit = getConnect("audit");
		MongoCollection<Document> collectionChecklist = getConnect("checklist");
		MongoCollection<Document> collectionBericht = getConnect("bericht");
		MongoCollection<Document> collectionMassnahme = getConnect("massnahme");
		Bson filter = eq("id", id);
		collectionAudit.deleteOne(filter);
		collectionChecklist.deleteMany(filter);
		collectionBericht.deleteMany(filter);
		collectionMassnahme.deleteMany(filter);
	}

	public void clearFields() throws IOException {
		uhrzeitBox.setValue("");
		datePickerAudit.setValue(null);
		textFieldAuditAbteilung.clear();
		auditartBox.setValue("");
		auditartBox.setStyle("-fx-base: WHITE;");
		textFieldAuditHausherr.clear();
		textFieldAuditorEins.clear();
		textFieldAuditorZwei.clear();
	}

	public void buttonDeleteAudit() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<Audit, Void> colBtn = new TableColumn<Audit, Void>("Audit löschen");
		Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>> cellFactory = new Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>>() {
			public TableCell<Audit, Void> call(final TableColumn<Audit, Void> param) {
				final TableCell<Audit, Void> cell = new TableCell<Audit, Void>() {
					private Button btn = new Button("Löschen");
					{
						btn.setOnAction((ActionEvent event) -> {
							try {
								if (!userAudit.getRolle().contentEquals("GAST")) {
									dataDeleteAudit = getTableView().getItems().get(getIndex());
									try {
										Alert alert = new Alert(AlertType.CONFIRMATION);
										Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
										stage.getIcons().add(new Image(("file:icon.png")));
										alert.setHeaderText("Audit löschen?");
										alert.setContentText("Sind Sie sicher?");
										Optional<ButtonType> result = alert.showAndWait();
										if (result.get() == ButtonType.OK) {
											deleteAudit(dataDeleteAudit.getId());
											tableViewUbersicht.getItems().remove(dataDeleteAudit);
										}
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								} else {
									Alert alert = new Alert(AlertType.INFORMATION);
									Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
									stage.getIcons().add(new Image(("file:icon.png")));
									alert.setTitle("Gast E-Mail Konto");
									alert.setHeaderText(null);
									alert.setContentText(userAudit.getEmail() + " ist als " + userAudit.getRolle()
											+ " angemeldet und darf kein Audit löschen");
									alert.showAndWait();
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							} catch (IOException e) {

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
							btn.setMinWidth(135);
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
		tableViewUbersicht.getColumns().add(colBtn);
	}

	public void checklistButtonToTable() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<Audit, Void> colBtn = new TableColumn<Audit, Void>("Checkliste");
		Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>> cellFactory = new Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>>() {
			public TableCell<Audit, Void> call(final TableColumn<Audit, Void> param) {
				final TableCell<Audit, Void> cell = new TableCell<Audit, Void>() {

					private Button btn = new Button("Checkliste");
					{
						btn.setOnAction((ActionEvent event) -> {
							checklistId = getTableView().getItems().get(getIndex());
							try {
								checklistView();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
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
							btn.setMinWidth(135);
							btn.setStyle("-fx-border-color: #1d7eaa; -fx-border-width: 2px;");
							btn.setStyle("-fx-text-fill: #1d7eaa;");
							try {
								checklistId = getTableView().getItems().get(getIndex());

								if (returnCheckliste(checklistId.getId())) {
									btn.setStyle("-fx-background-color: #E06B5B ; ");
								} else {
									btn.setStyle("-fx-background-color: #1BD320 ; ");
								}
							} catch (ClassNotFoundException | IOException e) {

								e.printStackTrace();
							}

							setGraphic(btn);
							super.updateItem(item, empty);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setCellFactory(cellFactory);
		tableViewUbersicht.getColumns().add(colBtn);
	}

	public void berichtButtonToTable() throws IOException, ClassNotFoundException {
		TableColumn<Audit, Void> colBtn = new TableColumn<Audit, Void>("Bericht");
		Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>> cellFactory = new Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>>() {
			public TableCell<Audit, Void> call(final TableColumn<Audit, Void> param) {
				final TableCell<Audit, Void> cell = new TableCell<Audit, Void>() {
					private Button btn = new Button("Bericht");
					{
						btn.setOnAction((ActionEvent event) -> {
							berichtId = getTableView().getItems().get(getIndex());
							try {
								berichtView();
							} catch (ClassNotFoundException | IOException | SQLException e) {
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
							btn.setMinWidth(135);
							btn.setStyle("-fx-border-color: #1d7eaa; -fx-border-width: 2px;");
							btn.setStyle("-fx-text-fill: #1d7eaa;");
							try {
								berichtId = getTableView().getItems().get(getIndex());
								if (returnBericht(berichtId.getId())) {
									btn.setStyle("-fx-background-color: #E06B5B ; ");
								} else {
									btn.setStyle("-fx-background-color: #1BD320 ; ");
								}
							} catch (ClassNotFoundException | SQLException | IOException e) {
								e.printStackTrace();
							}
							setGraphic(btn);
							super.updateItem(item, empty);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setCellFactory(cellFactory);
		tableViewUbersicht.getColumns().add(colBtn);
	}

	public void massnahmeButtonToTable() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<Audit, Void> colBtn = new TableColumn<Audit, Void>("Maßnahme");
		Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>> cellFactory = new Callback<TableColumn<Audit, Void>, TableCell<Audit, Void>>() {
			public TableCell<Audit, Void> call(final TableColumn<Audit, Void> param) {
				final TableCell<Audit, Void> cell = new TableCell<Audit, Void>() {

					private Button btn = new Button("Maßnahme");
					{
						btn.setOnAction((ActionEvent event) -> {
							massnahmeId = getTableView().getItems().get(getIndex());
							try {
								massnahmeView();
							} catch (ClassNotFoundException | IOException | SQLException e) {
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
							btn.setMinWidth(135);
							btn.setStyle("-fx-border-color: #1d7eaa; -fx-border-width: 2px;");
							btn.setStyle("-fx-text-fill: #1d7eaa;");
							try {
								massnahmeId = getTableView().getItems().get(getIndex());
								if (returnMassnahme(massnahmeId.getId())) {
									btn.setStyle("-fx-background-color: #E06B5B ; ");
								} else {
									btn.setStyle("-fx-background-color: #1BD320 ; ");
								}
							} catch (ClassNotFoundException | SQLException | IOException e) {
								e.printStackTrace();
							}
							setGraphic(btn);
							super.updateItem(item, empty);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setCellFactory(cellFactory);
		tableViewUbersicht.getColumns().add(colBtn);
	}

	public void searchMassnahmenart() throws ClassNotFoundException, SQLException {

		int sofortmassnahme = 0;
		int korrekturmaßnahme = 0;
		int vorbeugemassnahme = 0;
		int information = 0;

		MongoCollection<Document> collection = getConnect("massnahme");
		FindIterable<Document> query = collection.find();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).getString("massnahmenart").contentEquals("Sofortmaßnahme")) {
				sofortmassnahme = sofortmassnahme + 1;
			}
			if (result.get(i).getString("massnahmenart").contentEquals("Korrekturmaßnahme")) {
				korrekturmaßnahme = korrekturmaßnahme + 1;

			}
			if (result.get(i).getString("massnahmenart").contentEquals("Vorbeugemaßnahme")) {
				vorbeugemassnahme = vorbeugemassnahme + 1;
			}
			if (result.get(i).getString("massnahmenart").contentEquals("Information")) {
				information = information + 1;
			}
		}
		String s = String.valueOf(sofortmassnahme);
		labelSofortmassnahme.setText(s);
		String k = String.valueOf(korrekturmaßnahme);
		labelKorrekturmassnahme.setText(k);
		String v = String.valueOf(vorbeugemassnahme);
		labelVorbeugemassnahme.setText(v);
		String i = String.valueOf(information);
		labelInformation.setText(i);
	}

	public boolean returnMassnahme(String id) throws SQLException, ClassNotFoundException, IOException {
		MongoCollection<Document> collection = getConnect("massnahme");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		boolean empty = false;
		if (result.isEmpty()) {
			empty = true;
		} else {
			empty = false;
		}
		return empty;
	}

	public boolean returnBericht(String id) throws SQLException, ClassNotFoundException, IOException {
		MongoCollection<Document> collection = getConnect("bericht");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		boolean empty = false;
		if (result.isEmpty()) {
			empty = true;
		} else {
			empty = false;
		}
		return empty;
	}

	public boolean returnCheckliste(String id) throws ClassNotFoundException, IOException {
		MongoCollection<Document> collection = getConnect("checklist");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		boolean empty = false;
		if (result.isEmpty()) {
			empty = true;
		} else {
			empty = false;
		}
		return empty;
	}

	public void checklistView() throws IOException, ClassNotFoundException, SQLException {
		ubersicht();
		Parent parent = FXMLLoader.load(getClass().getResource("/controllers/ChecklistView.fxml"));
		Scene scene = new Scene(parent);
		Stage window = new Stage();
		window.getIcons().add(new Image(("file:icon.png")));
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Checkliste");
		window.setScene(scene);
		window.showAndWait();
		ubersicht();
	}

	public void berichtView() throws IOException, ClassNotFoundException, SQLException {
		ubersicht();
		Parent parent = FXMLLoader.load(getClass().getResource("/controllers/BerichtView.fxml"));
		Scene scene = new Scene(parent);
		Stage window = new Stage();
		window.getIcons().add(new Image(("file:icon.png")));
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Bericht");
		window.setScene(scene);
		window.showAndWait();
		ubersicht();
	}

	public void massnahmeView() throws IOException, ClassNotFoundException, SQLException {
		ubersicht();
		Parent parent = FXMLLoader.load(getClass().getResource("/controllers/MassnahmeView.fxml"));
		Scene scene = new Scene(parent);
		Stage window = new Stage();
		window.getIcons().add(new Image(("file:icon.png")));
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Maßnahme");
		window.setScene(scene);
		window.showAndWait();
		ubersicht();
	}

	@FXML
	public void changeAuditor() throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(("file:icon.png")));
		alert.setHeaderText("Ausloggen");
		alert.setContentText("Sind Sie sicher?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Stage stageLogin = (Stage) buttonAusloggen.getScene().getWindow();
			stageLogin.close();
			Parent parent = FXMLLoader.load(getClass().getResource("/controllers/loginView.fxml"));
			Scene scene = new Scene(parent);
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.getIcons().add(new Image(("file:icon.png")));
			window.setTitle("Login");
			window.setScene(scene);
			window.show();
		}
	}

	public void insertUsers() throws SQLException, ClassNotFoundException, MessagingException, IOException {
		String email = textFieldInsertEmail.getText();
		String password = textFieldInsertPasswort.getText();
		String rolle = rolleChoiceBox.getValue();
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Bson filter = eq("email", email);
		MongoCollection<Document> collection = getConnect("users");
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		if (!userAudit.getRolle().contentEquals("GAST")) {
			if (result.isEmpty()) {
				if (email.matches(regex) && password.trim().length() > 8) {
					Document user = new Document();
					user.append("email", email).append("password", password).append("rolle", rolle);
					collection.insertOne(user);
					Alert alert = new Alert(AlertType.INFORMATION);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(("file:icon.png")));
					alert.setTitle("Information");
					alert.setHeaderText(null);
					alert.setContentText("E-Mail " + email.toString() + " erfolgreich registriert!");
					alert.showAndWait();
					sendMail(email, password);
					textFieldInsertEmail.setText("");
					textFieldInsertPasswort.setText("");

				} else if (!email.matches(regex)) {
					Alert alert = new Alert(AlertType.WARNING);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(("file:icon.png")));
					alert.setTitle("Information");
					alert.setContentText("E-Mail muss gültig sein.");
					alert.showAndWait();

				} else if (password.trim().length() < 8) {
					Alert alert = new Alert(AlertType.WARNING);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(("file:icon.png")));
					alert.setTitle("Information");
					alert.setContentText("Passwort muss mindestens acht Zeichen haben.");
					alert.showAndWait();

				}

			} else {
				Alert alert = new Alert(AlertType.WARNING);
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(("file:icon.png")));
				alert.setTitle("Information");
				alert.setContentText("E-Mail Konto ist schon registriert!");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setTitle("Gast E-Mail Konto");
			alert.setContentText(userAudit.getEmail() + " ist ein Gast konto und darf kein User inserieren");
			alert.showAndWait();
		}

	}

	public void sendMail(String email, String password) throws MessagingException, ClassNotFoundException, IOException {
		Alert alert = new Alert(AlertType.INFORMATION);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(("file:icon.png")));
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText("Passwort erfolgreich an " + email + " gesendet.");
		alert.showAndWait();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			String emailAdmin = "audittool2020@gmail.com";
			String passwordAdmin = "";

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailAdmin, passwordAdmin);
			}
		});

		try {
			String subject = "Audit Passwort";
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.setSubject(subject);
			message.setContent("Your Password is: " + password, "text/html; charset=utf-8");
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}

	public void showUsers() throws SQLException, ClassNotFoundException {
		MongoCollection<Document> collection = getConnect("users");
		FindIterable<Document> query = collection.find();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		User user;
		List<User> usersList = new ArrayList<User>();
		if (userAudit.getRolle().contentEquals("ADMIN")) {
			for (int i = 0; i < result.size(); i++) {
				String email = result.get(i).getString("email");
				String password = result.get(i).getString("password");
				String rolle = result.get(i).getString("rolle");
				columnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
				columnPassword.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
				columnRolle.setCellValueFactory(new PropertyValueFactory<User, String>("rolle"));
				user = new User(email, password, rolle);
				usersList.add(user);
				ObservableList<User> observableListUsers = FXCollections.observableArrayList(usersList);
				tableViewUsers.setItems(observableListUsers);
			}

		} else {

			Alert alert = new Alert(AlertType.WARNING);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setHeaderText("Sie nutzen kein Admin E-Mail-Konto!");
			alert.setContentText("Nur Admins dürfen registrierte Teilnehmer sehen.");
			alert.showAndWait();
		}
	}

	public void deleteUsersButtonToTable() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<User, Void> colBtn = new TableColumn<User, Void>("User löschen");
		colBtn.setMinWidth(170);
		Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
			public TableCell<User, Void> call(final TableColumn<User, Void> param) {
				final TableCell<User, Void> cell = new TableCell<User, Void>() {
					private Button btn = new Button("Löschen");
					{
						btn.setOnAction((ActionEvent event) -> {
							Alert alert = new Alert(AlertType.CONFIRMATION);
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image(("file:icon.png")));
							alert.setTitle("User löschen?");
							alert.setHeaderText("Sind Sie sicher?");
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK) {
								dataDeleteUser = getTableView().getItems().get(getIndex());
								String email = dataDeleteUser.getEmail();
								try {
									if (deleteUser(email)) {
										User selectedItem = dataDeleteUser;
										tableViewUsers.getItems().remove(selectedItem);
									}
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setMinWidth(160);
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
		tableViewUsers.getColumns().add(colBtn);
	}

	public boolean deleteUser(String email) throws ClassNotFoundException, SQLException {
		boolean ok = false;

		if (userAudit.getRolle().contains("ADMIN") && !email.contains("audittool2020@gmail.com")) {
			MongoCollection<Document> collection = getConnect("users");
			Bson filter = eq("email", email);
			collection.deleteOne(filter);
			ok = true;
		} else if (email.contains("audittool2020@gmail.com")) {
			Alert alert = new Alert(AlertType.WARNING);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setTitle("Information");
			alert.setHeaderText("Das Admin-Konto kann nicht gelöscht werden!");
			alert.showAndWait();
			ok = false;
		} else if (!userAudit.getRolle().contains("ADMIN")) {
			Alert alert = new Alert(AlertType.WARNING);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setTitle("Information");
			alert.setHeaderText("Sie nutzen kein Admin-Konto!");
			alert.showAndWait();
			ok = false;
		}
		return ok;
	}

	public void searchName() throws SQLException, ClassNotFoundException {
		String name = textFieldName.getText();
		MongoCollection<Document> collection = getConnect("audit");
		FindIterable<Document> query = collection.find();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
		int auditorEins = 0;
		int auditorZwei = 0;
		for (int i = 0; i < result.size(); i++) {

			if (result.get(i).getString("auditoreins").contentEquals(name)) {
				auditorEins = auditorEins + 1;
			}

			if (result.get(i).getString("auditorzwei").contentEquals(name)) {
				auditorZwei = auditorZwei + 1;
			}
		}

		labelNameEins.setText(Integer.toString(auditorEins));
		labelNameZwei.setText(Integer.toString(auditorZwei));
	}

	public void returnAngemeldet() throws ClassNotFoundException, SQLException {
		emailLabel.setText(userAudit.getEmail());
		rolleLabel.setText(userAudit.getRolle());
	}

	public void AlertEmail() throws ClassNotFoundException, MessagingException, SQLException, IOException {
		TextInputDialog dialog = new TextInputDialog();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(("file:icon.png")));
		dialog.setHeaderText("Audit E-Mail");
		dialog.setContentText("Please enter E-Mail:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			sendAuditEmail(result.get());
		}

	}

	public void sendAuditEmail(String email) {
		String datum = null;
		String uhrzeit = null;
		String abteilung = null;
		String auditArt = null;
		String auditorEins = null;
		String auditorZwei = null;

		Locale defloc = Locale.getDefault();
		MongoCollection<Document> collection = getConnect("audit");
		Bson filter = eq("id", auditId);
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		for (int i = 0; i < result.size(); i++) {
			datum = result.get(i).getString("datum");
			uhrzeit = result.get(i).getString("uhrzeit");
			abteilung = result.get(i).getString("abteilung");
			auditArt = result.get(i).getString("auditart");
			auditorEins = result.get(i).getString("auditoreins");
			auditorZwei = result.get(i).getString("auditorzwei");
		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("audittool2020@gmail.com", "");
			}
		});

		try {

			String ein = "<p><strong>Sehr geehrte Damen und Herren,</strong></p>";
			String zwei = "<p>&nbsp;</p>";
			String drei = "<p><strong>gerne lade ich Sie zum Audit in der Abteilung " + abteilung + " am " + datum
					+ " um " + uhrzeit
					+ " ein. Audits sind zum Bestehen externer Zertifizierungen obligatorisch umzusetzen.</strong></p>";
			String vier = "<p><strong> Sollten Sie zu diesem Termin verhindert sein, k&ouml;nnen Sie einen Stellvertreter bestimmen oder einen anderen Terminvorschlag machen. Bitte geben Sie mir innerhalb von sieben Tagen R&uuml;ckmeldung.</strong></p>";
			String funf = "<p><strong> Ich bedanke mich f&uuml;r Ihre Bereitschaft.</strong></p>";
			String sechs = "<p><strong> Freundliche Gr&uuml;&szlig;e senden " + auditorEins.toUpperCase(defloc)
					+ " (1. Auditor) und " + auditorZwei.toUpperCase(defloc) + " (2. Auditor).</strong></p>";

			String subject = "Einladung zu Liferantenaudit in Abteilung " + auditArt;

			String emailBody = ein + zwei + drei + vier + funf + sechs;
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.setSubject(subject);

			try {
				message.setContent(emailBody, "text/html; charset=utf-8");
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				Transport.send(message);
				Alert alert = new Alert(AlertType.INFORMATION);
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(("file:icon.png")));
				alert.setTitle("Information");
				alert.setHeaderText("Email erfolgreich an " + email + " gesendet.");
				alert.showAndWait();

			} catch (MessagingException e1) {
				e1.printStackTrace();
			}

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			ubersicht();
			returnAngemeldet();
			checklistButtonToTable();
			berichtButtonToTable();
			deleteUsersButtonToTable();
			massnahmeButtonToTable();
			buttonDeleteAudit();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
		}

		List<String> list = new ArrayList<>();
		list.add("");
		list.add("07:00 - 08:00");
		list.add("07:30 - 08:30");
		list.add("08:00 - 09:00");
		list.add("08:30 - 09:30");
		list.add("09:00 - 10:00");
		list.add("09:30 - 10:30");
		list.add("10:00 - 11:00");
		list.add("10:30 - 11:30");
		list.add("11:00 - 12:00");
		list.add("11:30 - 12:30");
		list.add("12:00 - 13:00");
		list.add("12:30 - 13:30");
		list.add("13:00 - 14:00");
		list.add("13:30 - 14:30");
		list.add("14:00 - 15:00");
		list.add("14:30 - 15:30");
		list.add("15:00 - 16:00");
		list.add("15:30 - 16:30");
		list.add("16:00 - 17:00");
		list.add("16:30 - 17:30");
		list.add("17:00 - 18:00");
		list.add("17:30 - 18:30");
		list.add("18:00 - 19:00");
		list.add("18:30 - 19:30");
		list.add("19:00 - 20:00");
		list.add("19:30 - 20:30");
		list.add("20:00 - 21:00");
		list.add("20:30 - 21:30");
		ObservableList<String> observableListUhrzeitBox = FXCollections.observableArrayList(list);
		uhrzeitBox.setItems(observableListUhrzeitBox);

		List<String> auditartList = new ArrayList<>();
		auditartList.add("");
		auditartList.add("Prozessaudit");
		auditartList.add("Hygiene/Sicherheitsaudit");
		auditartList.add("Lieferantenaudit");
		auditartList.add("Kundenaudit");
		ObservableList<String> observableListAuditart = FXCollections.observableArrayList(auditartList);
		auditartBox.setItems(observableListAuditart);

		List<String> rolleBoxList = new ArrayList<>();
		rolleBoxList.add("ADMIN");
		rolleBoxList.add("GAST");
		rolleBoxList.add("AUDITOR");
		ObservableList<String> observableListRolleBox = FXCollections.observableArrayList(rolleBoxList);
		rolleChoiceBox.setItems(observableListRolleBox);

		auditartBox.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (auditartBox.getSelectionModel().getSelectedItem().contains("Prozessaudit")) {
					auditartBox.setStyle("-fx-base: GREEN ;");
				} else if (auditartBox.getSelectionModel().getSelectedItem().contains("Hygiene/Sicherheitsaudit")) {
					auditartBox.setStyle("-fx-base: YELLOW;");
				} else if (auditartBox.getSelectionModel().getSelectedItem().contains("Lieferantenaudit")) {
					auditartBox.setStyle("-fx-base: ORANGE;");
				} else if (auditartBox.getSelectionModel().getSelectedItem().contains("Kundenaudit")) {
					auditartBox.setStyle("-fx-base: RED;");
				} else {
					auditartBox.setValue("");
					auditartBox.setStyle("-fx-base: WHITE;");
				}
			}
		});

	}

}
