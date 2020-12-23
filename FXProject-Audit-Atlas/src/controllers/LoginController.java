package controllers;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
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

import classes.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML
	private TextField textFieldEmail = new TextField();
	@FXML
	private PasswordField textFieldPassword = new PasswordField();
	@FXML
	private Button buttonLogin = new Button();
	@FXML
	private Button buttonPasswortVergessen = new Button();

	public static User user;

	public static User getUser() {
		return user;
	}

	@SuppressWarnings("resource")
	public MongoCollection<Document> getConnect(String collection) {
		MongoClientURI uri = new MongoClientURI(
				"mongodb+srv://bananastaut:020881Banana@bananacluster.gzaux.mongodb.net/<BananaDatabase>?retryWrites=true&w=majority");
		MongoClient mongoClient = new MongoClient(uri);
		MongoCollection<Document> coll = mongoClient.getDatabase("BananaDatabase").getCollection(collection);
		return coll;
	}

	public void loginActionButton() throws ClassNotFoundException, SQLException, IOException {
		String password = textFieldPassword.getText();
		String email = textFieldEmail.getText();
		MongoCollection<Document> collection = getConnect("users");
		Bson filter = eq("email", email);
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		if (!result.isEmpty()) {
			for (int i = 0; i < result.size(); i++) {
				if (email.contentEquals(result.get(i).getString("email"))
						&& password.contentEquals(result.get(i).getString("password"))) {
					user = new User(result.get(i).getString("email"), result.get(i).getString("password"),
							result.get(i).getString("rolle"));
					Stage stageLogin = (Stage) buttonLogin.getScene().getWindow();
					stageLogin.close();

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Willkommen");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(("file:icon.png")));
					alert.setHeaderText(null);
					alert.setContentText(user.getEmail() + " ist als " + user.getRolle() + " angemeldet.");
					alert.showAndWait();

					Parent parent = FXMLLoader.load(getClass().getResource("/controllers/AuditView.fxml"));
					Scene scene = new Scene(parent);
					Stage window = new Stage();
					window.initModality(Modality.APPLICATION_MODAL);
					window.getIcons().add(new Image(("file:icon.png")));
					window.setTitle("Audit Tool");
					window.setScene(scene);
					window.show();

				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().add(new Image(("file:icon.png")));
					alert.setHeaderText(null);
					alert.setContentText("Passwort ist falsch");
					alert.showAndWait();
				}
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(email + " ist nicht registriert");
			alert.showAndWait();
		}
	}

	public void onButtonAuditView() throws IOException, ClassNotFoundException, SQLException {
		Stage stage = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/controllers/AuditView.fxml"));
		stage.getIcons().add(new Image(("file:icon.png")));
		Scene scene = new Scene(parent);
		stage.setTitle("Audit-Tool-2020");
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	public void passwortVergessen() throws ClassNotFoundException, MessagingException, IOException, SQLException {
		TextInputDialog dialog = new TextInputDialog();
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(("file:icon.png")));
		dialog.setContentText("Bitte, E-Mail-Adresse eingeben:");
		Optional<String> resultEmail = dialog.showAndWait();
		if (resultEmail.isPresent()) {
			sendEmail(resultEmail.toString().substring(9, resultEmail.toString().length() - 1));
		}
	}

	public void sendEmail(String email) throws MessagingException, ClassNotFoundException, SQLException, IOException {
		MongoCollection<Document> collection = getConnect("users");
		Bson filter = eq("email", email);
		FindIterable<Document> query = collection.find(filter);
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		if (result.isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(("file:icon.png")));
			alert.setTitle("Information");
			alert.setContentText("E-Mail-Konto ist nicht registriert!");
			alert.showAndWait();
		} else {
			for (int i = 0; i < result.size(); i++) {

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(("file:icon.png")));
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
					String passwordAdmin = "vchjvqkbbcdynval";

					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(emailAdmin, passwordAdmin);
					}
				});

				try {
					String subject = "Audit Passwort";
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(email));
					message.setSubject(subject);
					message.setContent("Your Password is: " + result.get(i).getString("password"),
							"text/html; charset=utf-8");
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(result.get(i).getString("email")));
					Transport.send(message);
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}

			}
		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		buttonLogin.setDefaultButton(true);
	}

}
