package controllers;

import static com.mongodb.client.model.Filters.eq;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import classes.Audit;
import classes.Bericht;
import classes.CheckList;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("serial")
public class BerichtController extends AuditController implements Serializable {

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
	private TextField textAreaEingesehene = new TextField();
	@FXML
	private ChoiceBox<String> erfuelltBox = new ChoiceBox<String>();
	@FXML
	private TextField textAreaBemerkungen = new TextField();
	@FXML
	private Button insertBerichtButton = new Button();
	@FXML
	private Button cancelButton = new Button();
	@FXML
	private Button berichtEmailSchicken = new Button();

	@FXML
	private TableView<Bericht> tableViewBericht = new TableView<Bericht>();
	@FXML
	private TableColumn<Bericht, String> tableColumnEingesehene = new TableColumn<Bericht, String>();
	@FXML
	private TableColumn<Bericht, String> tableColumnErfuellt = new TableColumn<Bericht, String>();
	@FXML
	private TableColumn<Bericht, String> tableColumnBemerkungen = new TableColumn<Bericht, String>();

	@FXML
	private TableView<CheckList> tableViewChecklist = new TableView<CheckList>();
	@FXML
	private TableColumn<CheckList, String> tableColumnNormKapitel = new TableColumn<CheckList, String>();
	@FXML
	private TableColumn<CheckList, String> tableColumnNormForderung = new TableColumn<CheckList, String>();
	@FXML
	private TableColumn<CheckList, String> tableColumnFragen = new TableColumn<CheckList, String>();

	protected Bericht dataBericht = null;
	String id = AuditController.checklistId.getId();

	protected Audit returnAudit() throws ClassNotFoundException, SQLException, IOException {
		Audit audit;
		MongoCollection<Document> collection = getConnect("audit");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<Audit> auditList = new ArrayList<Audit>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);
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
		ObservableList<Audit> observableListBericht = FXCollections.observableArrayList(auditList);
		tableViewAudit.setItems(observableListBericht);
		return audit;
	}

	public void insertBericht() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("bericht");
		Document bericht = new Document();
		if (!userAudit.getRolle().contentEquals("GAST")) {
			String eingesehene = textAreaEingesehene.getText();
			String erfuellt = erfuelltBox.getValue().toString();
			String bemerkungen = textAreaBemerkungen.getText();

			if (eingesehene.trim().length() != 0 && erfuellt.trim().length() != 0 && bemerkungen.trim().length() != 0) {

				bericht.append("id", id).append("eingesehene", eingesehene).append("erfuellt", erfuellt)
						.append("bemerkungen", bemerkungen);

				collection.insertOne(bericht);
				returnBericht();
				textAreaEingesehene.setText("");
				erfuelltBox.setValue("");
				textAreaBemerkungen.setText("");
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

	public void returnBericht() throws ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("bericht");
		Bson filter = eq("id", id);
		FindIterable<Document> query = collection.find(filter);
		List<Bericht> berichtList = new ArrayList<Bericht>();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		for (int i = 0; i < result.size(); i++) {
			String eingesehene = result.get(i).getString("eingesehene");
			tableColumnEingesehene.setCellValueFactory(new PropertyValueFactory<Bericht, String>("eingesehene"));
			String erfuellt = result.get(i).getString("erfuellt");
			tableColumnErfuellt.setCellValueFactory(new PropertyValueFactory<Bericht, String>("erfuellt"));
			String bemerkungen = result.get(i).getString("bemerkungen");
			tableColumnBemerkungen.setCellValueFactory(new PropertyValueFactory<Bericht, String>("bemerkungen"));
			Bericht bericht = new Bericht(id, eingesehene, erfuellt, bemerkungen);
			berichtList.add(bericht);
			ObservableList<Bericht> observableListBericht = FXCollections.observableArrayList(berichtList);
			tableViewBericht.setItems(observableListBericht);
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

	public void deleteBerichtButton() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<Bericht, Void> colBtn = new TableColumn<Bericht, Void>("Löschen");
		colBtn.setMinWidth(170);
		Callback<TableColumn<Bericht, Void>, TableCell<Bericht, Void>> cellFactory = new Callback<TableColumn<Bericht, Void>, TableCell<Bericht, Void>>() {
			public TableCell<Bericht, Void> call(final TableColumn<Bericht, Void> param) {
				final TableCell<Bericht, Void> cell = new TableCell<Bericht, Void>() {

					private Button btn = new Button(" Löschen ");
					{
						btn.setOnAction((ActionEvent event) -> {
							try {
								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Information");
								alert.setHeaderText("Bericht Löschen?");
								alert.setContentText("Sind Sie sicher?");
								Optional<ButtonType> result = alert.showAndWait();
								if (result.get() == ButtonType.OK && !userAudit.getRolle().contentEquals("GAST")) {
									deleteBericht(checklistId.getId());
									tableViewChecklist.getItems().remove(checklistId);
								} else {
									Alert alert1 = new Alert(AlertType.WARNING);
									alert1.setTitle("Gast");
									alert1.setHeaderText(null);
									alert1.setContentText("Sie sind Gast und dürfen Keinen Bericht löschen!");
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
		tableViewBericht.getColumns().add(colBtn);
	}

	public void deleteBericht(String id) throws ClassNotFoundException, SQLException {
		MongoCollection<Document> collection = getConnect("bericht");
		Bson filter = eq("id", id);
		collection.deleteMany(filter);
	}

	public void screenShot() throws ClassNotFoundException, SQLException {
		try {
			Robot robot = new Robot();
			String format = "jpg";
			String fileName = "E-MailScreenShot." + format;
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
			ImageIO.write(screenFullImage, format, new File(fileName));
		} catch (AWTException | IOException ex) {
			System.err.println(ex);
		}
	}

	public void AlertEmailBericht() throws ClassNotFoundException, MessagingException, IOException, SQLException {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText("Audit E-Mail");
		dialog.setContentText("Please enter E-Mail:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			sendBerichtMail(result.get());
		}
		screenShot();
	}

	public void sendBerichtMail(String email)
			throws MessagingException, ClassNotFoundException, SQLException, IOException {
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
			String subject = "Auditbereich: " + returnAudit().getAuditart();
			String ein = "<p><strong>Sehr geehrte Damen und Herren,</strong></p>";
			String zwei = "<p><strong> gerne sende ich ihnen im Anhang den Auditbericht inklusive Ma&szlig;nahmen vom "
					+ returnAudit().getAuditart() + " im Bereich " + returnAudit().getAbteilung()
					+ " zu. Bitte achten Sie darauf die Ma&szlig;nahmen bis zur angegebenen Frist umzusetzen und im Audit-Tool unter 'Ma&szlig;nahme einf&uuml;gen' abzuschliessen.</strong></p>";
			String drei = "<p><strong> Ich bedanke mich im Voraus f&uuml;r Ihre Bereitschaft.</strong></p>";
			String vier = "<p><strong>Freundliche Gr&uuml;&szlig;e, " + returnAudit().getAuditorEins()
					+ " (1. Auditor) und " + returnAudit().getAuditorZwei() + "(2. Auditor).</strong></p>";
			String emailBody = ein + zwei + drei + vier;
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.setSubject(subject);
			MimeMultipart multipart = new MimeMultipart("related");
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(emailBody, "text/html; charset=utf-8");
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			String filename = "E-MailScreenShot.jpg";
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			multipart.addBodyPart(messageBodyPart);
			try {
				message.setContent(multipart);
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				Transport.send(message);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information");
				alert.setHeaderText("Email erfolgreich an " + email + " gesendet");
				alert.showAndWait();
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
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
			deleteBerichtButton();
			returnBericht();
			returnChecklist();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> erfuelltList = new ArrayList<>();
		erfuelltList.add("Voll erfüllt");
		erfuelltList.add("Teilweise erfüllt");
		erfuelltList.add("Nicht erfüllt");

		ObservableList<String> observableListErfuellt = FXCollections.observableArrayList(erfuelltList);
		erfuelltBox.setItems(observableListErfuellt);
		erfuelltBox.setValue(" ");
	}
}
