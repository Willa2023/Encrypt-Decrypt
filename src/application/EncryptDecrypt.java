package application;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import javax.crypto.SecretKey;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class EncryptDecrypt extends Application {

	private BorderPane root = new BorderPane();
	private TextField usernameField;
	private PasswordField passwordField;
	
	//connect to local database
	String JDBC_URL = "jdbc:mysql://localhost:3306/security";
	String USERNAME = "root";
	String PASSWORD = "";
	String chosenData = "Localhost";
	String chosenMethod;
	Color chosenColor = new Color(0.5,0.5,0.5,1);
	String chosenSetting;
	String desMasterKey = "T5vC6tUgRYU=";
	Method method1 = new Method();
	

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		loginPage(primaryStage);
		Scene scene = new Scene(root, 1600, 1600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void loginPage(Stage primaryStage) {
		primaryStage.setTitle("Login Page");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(150, 10, 10, 10));
		grid.setVgap(20);
		grid.setHgap(10);

		Label usernameLabel = new Label("UserName:");
		usernameField = new TextField();
		Label passwordLabel = new Label("Password:");
		passwordField = new PasswordField();
		Button loginButton = new Button("Login");
		Button createUserButton = new Button("Create");
		
		Label chooseDataLabel = new Label("Choose Database: ");
		MenuButton dataMenuButton = new MenuButton();
		MenuItem localData = new MenuItem("Localhost");
		MenuItem cloudData = new MenuItem("AWS Cloud");
		dataMenuButton.getItems().addAll(localData,cloudData);
		dataMenuButton.setText(chosenData);
		
		GridPane.setConstraints(chooseDataLabel, 0, 0);
		GridPane.setConstraints(dataMenuButton, 1, 0);
		GridPane.setConstraints(usernameLabel, 0, 3);
		GridPane.setConstraints(usernameField, 1, 3);
		GridPane.setConstraints(passwordLabel, 0, 4);
		GridPane.setConstraints(passwordField, 1, 4);
		GridPane.setConstraints(loginButton, 1, 5);
		GridPane.setConstraints(createUserButton, 1, 6);
		
		grid.getChildren().addAll(chooseDataLabel, dataMenuButton, usernameLabel, usernameField, passwordLabel, passwordField, loginButton,
				createUserButton);
		grid.setAlignment(Pos.CENTER);
		root.getChildren().clear();
		root.setTop(grid);

		loginButton.setOnAction(e -> {
			login(primaryStage);
		});
		createUserButton.setOnAction(e -> {
			createUser(primaryStage);
		});
		
		//change to local database
		localData.setOnAction(e ->{
			chosenData = localData.getText();
			dataMenuButton.setText(chosenData);
			JDBC_URL = "jdbc:mysql://localhost:3306/security";
			USERNAME = "root";
			PASSWORD = "";
		});
		
		//change to cloud database （original database）
//		cloudData.setOnAction(e ->{
//			chosenData = cloudData.getText();
//			dataMenuButton.setText(chosenData);
//			JDBC_URL = "jdbc:mysql://myfirstdatabase.cf8sld5urrxi.ap-southeast-2.rds.amazonaws.com:3306/security";
//			USERNAME = "admin";
//			PASSWORD = "willawilla";
//		});
		  
		//change to another cloud database （new database）
		cloudData.setOnAction(e ->{
			chosenData = cloudData.getText();
			dataMenuButton.setText(chosenData);
			JDBC_URL = "jdbc:mysql://reactblogdatabase.cf8sld5urrxi.ap-southeast-2.rds.amazonaws.com:3306/security";
			USERNAME = "admin";
			PASSWORD = "willawilla";
		});
		
	}

	public void mainPage(Stage primaryStage, String userName) {
		primaryStage.setTitle("Main Page");

		GridPane grid = new GridPane();
		grid.setVgap(20);
		grid.setHgap(60);

		Button logoutButton = new Button("log out");
		// choose method menu
		Label chooseMethodLabel = new Label("Method: ");
		MenuButton methodMenuButton = new MenuButton("choose one method");
		MenuItem methodItemCaesar = new MenuItem("Caesar");
		MenuItem methodItemDES = new MenuItem("DES");
		MenuItem methodItemAES = new MenuItem("AES");
		methodMenuButton.getItems().addAll(methodItemCaesar, methodItemDES, methodItemAES);
		methodMenuButton.setAlignment(Pos.CENTER);
		methodMenuButton.setPrefWidth(200);
		
		Button clearButton = new Button("Clear All");
		
		Label chooseColorLabel = new Label("Change Background Color: ");
		ColorPicker colorPicker = new ColorPicker(chosenColor);
		
		Label inputMessageIdLabel = new Label("Input Message Id: ");
		TextField inputMessageIdField = new TextField();
		inputMessageIdField.setPromptText("Can enter message ID to load text and key for decryption");
		
		Label inputTextLabel = new Label("Input Text:");
		TextArea inputTextField = new TextArea();
		Button loadTextButton = new Button("load encrypted text from database");
		loadTextButton.setStyle("-fx-color: rgba(216, 235, 150, 1);");

		Label inputKeyLabel = new Label("Input Key:");
		PasswordField inputKeyField = new PasswordField();
		Button loadKeyButton = new Button("load key from file");
		loadKeyButton.setStyle("-fx-color: rgba(216, 235, 150, 1);");

		Button encryptBtn = new Button("Encrypt");
		encryptBtn.setStyle("-fx-color: rgba(230, 121, 48, 1);");
		encryptBtn.setPrefWidth(100);
		encryptBtn.setPrefHeight(40);
		
		Button decryptBtn = new Button("Decrypt");
		decryptBtn.setStyle("-fx-color: rgba(49, 126, 181, 1);");
		decryptBtn.setPrefWidth(100);
		decryptBtn.setPrefHeight(40);

		Label outputTextLabel = new Label("Result Text:");
		TextArea resultText = new TextArea();
		Label outputKeyLabel = new Label("Result Key:");
		PasswordField resultKey = new PasswordField();
		
		Label saveMessageIdLabel = new Label("Save Message Id: ");
		TextField saveMessageIdField = new TextField();
		saveMessageIdField.setPromptText("Please enter message ID before saving encrypted text");
		Button saveMessageButton = new Button("Save encrypted text to database & save key to file");
		saveMessageButton.setStyle("-fx-color: rgba(216, 235, 150, 1);");
		
		GridPane.setConstraints(chooseMethodLabel, 0, 0);
		GridPane.setConstraints(chooseColorLabel, 3, 0);
		
		GridPane.setConstraints(methodMenuButton, 0, 1);
		GridPane.setConstraints(clearButton, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
		GridPane.setConstraints(colorPicker, 3, 1, 1, 1);
		GridPane.setConstraints(logoutButton, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
		
		GridPane.setConstraints(inputMessageIdLabel, 0, 2);
		GridPane.setConstraints(saveMessageIdLabel, 3, 2);
		
		clearButton.setOnAction(e ->{
			inputMessageIdField.clear();
			saveMessageIdField.clear();
			inputTextField.clear();
			resultText.clear();
			inputKeyField.clear();
			resultKey.clear();
		});
		GridPane.setConstraints(inputMessageIdField, 0, 3, 2, 1);
		GridPane.setConstraints(saveMessageIdField, 3, 3, 2, 1);
		
		GridPane.setConstraints(inputTextLabel, 0, 4);
		GridPane.setConstraints(encryptBtn, 2, 4);
		GridPane.setConstraints(outputTextLabel, 3, 4);
		
		GridPane.setConstraints(inputTextField, 0, 5, 2, 1);
		GridPane.setConstraints(decryptBtn, 2, 5);
		GridPane.setConstraints(resultText, 3, 5, 2, 1);
		
		GridPane.setConstraints(loadTextButton, 0, 6);
		
		GridPane.setConstraints(inputKeyLabel, 0, 7);
		GridPane.setConstraints(outputKeyLabel, 3, 7);
		
		GridPane.setConstraints(inputKeyField, 0, 8, 2, 1);
		GridPane.setConstraints(resultKey, 3, 8, 2, 1);
		
		GridPane.setConstraints(loadKeyButton, 0, 9);
		GridPane.setConstraints(saveMessageButton, 3, 9);
		
		grid.getChildren().addAll(chooseMethodLabel, chooseColorLabel, methodMenuButton, clearButton, colorPicker, logoutButton, inputMessageIdLabel, inputMessageIdField, inputTextLabel, inputTextField, loadTextButton,
				inputKeyLabel, inputKeyField, loadKeyButton, encryptBtn, decryptBtn, outputTextLabel, resultText,
				outputKeyLabel, resultKey, saveMessageIdLabel, saveMessageIdField, saveMessageButton);
		grid.setAlignment(Pos.CENTER);
		root.getChildren().clear();
		root.setCenter(grid);
		
		logoutButton.setOnAction(e -> {
			loginPage(primaryStage);
		});
		
		methodItemCaesar.setOnAction(e -> {
			chosenMethod = methodItemCaesar.getText();
			methodMenuButton.setText(chosenMethod);
			inputKeyField.setPromptText("Caesar Key should be a valid number");
			saveMethodToDatabase(userName, chosenMethod);
		});
		methodItemDES.setOnAction(e -> {
			chosenMethod = methodItemDES.getText();
			methodMenuButton.setText(chosenMethod);
			inputKeyField.setPromptText("DES Encrypt Don't need key");
			saveMethodToDatabase(userName, chosenMethod);
			
		});
		methodItemAES.setOnAction(e -> {
			chosenMethod = methodItemAES.getText();
			methodMenuButton.setText(chosenMethod);
			inputKeyField.setPromptText("AES Encrypt Don't need key");
			saveMethodToDatabase(userName, chosenMethod);
		});
		
		colorPicker.setOnAction(e -> {
			Color newColor = colorPicker.getValue();
			grid.setBackground(new Background(new BackgroundFill(newColor, null, null)));
			saveColorToDatabase(userName, newColor);
		});
		
		saveMessageButton.setOnAction(e -> {
			String message = resultText.getText();
			String key = resultKey.getText();
			String secretKey = method1.hashEncrypt(key);
			String messageIdString = saveMessageIdField.getText();
			String methodName = chosenMethod;
			
			String saveKeyString = resultKey.getText();
			byte[] secretKeyBytes = method1.desEncrypt(desMasterKey, saveKeyString);

			if (!messageIdString.trim().isEmpty()) {
				saveMessageById(primaryStage, userName, message, secretKey, messageIdString, methodName, secretKeyBytes);
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Please input save id.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
		});
		
		loadTextButton.setOnAction(e -> {
			inputTextField.clear();
			inputTextField.setPromptText("");
			// get the correct key name by userName and input id 
			String inputIdText = inputMessageIdField.getText();
			String textString = getMessageTextById(primaryStage, userName, inputIdText);
			inputTextField.setText(textString);

		});

		loadKeyButton.setOnAction(e -> {
			inputKeyField.clear();
			inputTextField.setPromptText("");
			
			// get the correct key name by userName and input id 
			String inputIdText = inputMessageIdField.getText();
			String idString = getKeyFileIndexById(primaryStage, userName, inputIdText);

			if (!idString.trim().isEmpty()) {
				// read and decrypt key
				String file  = userName + idString+".txt";
				String loadKey = "";
				try {
					loadKey = method1.loadKeyFile(file);
				} catch (Exception f) {
					f.printStackTrace();
				}
				String keyString = method1.desDecrypt(desMasterKey, loadKey);
				System.out.println("2key string " + keyString);
				inputKeyField.setText(keyString);
				if (!chosenMethod.isEmpty()) {
					methodMenuButton.setText(chosenMethod);
				}
			} else {
				System.out.println("The key file doesn't exist");
			}
		});

		encryptBtn.setOnAction(e -> {
			String inputTextString = inputTextField.getText();
			String inputKeyString = inputKeyField.getText();

			if (inputTextString.trim().isEmpty()) {
				inputTextField.setPromptText("Please input text");
			} else if (chosenMethod.equals("Caesar")) {
				try {
					int key = Integer.parseInt(inputKeyString);
					String decryptString = method1.caesarEncrypt(inputTextString, key);
					resultText.setText(decryptString);
					resultKey.setText(String.valueOf(key));
				} catch (NumberFormatException f) {
					inputKeyField.clear();
					inputKeyField.setPromptText("Please input a valid number");
					f.printStackTrace();
				}
			} else if (chosenMethod.equals("DES")) {
				try {
					DES des = new DES();
					SecretKey secretKey = des.getSecretkey();
					String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
					String decryptString = method1.desEncrypt(secretKey, inputTextString);
					resultText.setText(decryptString);
					resultKey.setText(secretKeyString);
				} catch (NumberFormatException | NoSuchAlgorithmException f) {
					f.printStackTrace();
				}
			} else if (chosenMethod.equals("AES")) {
				try {
					AES aes = new AES();
					SecretKey secretKey = aes.getSecretkey();
					String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
					String decryptString = method1.aesEncrypt(secretKey, inputTextString);
					resultText.setText(decryptString);
					resultKey.setText(secretKeyString);
				} catch (NoSuchAlgorithmException f) {
					f.printStackTrace();
				}
			}
		});

		decryptBtn.setOnAction(e -> {
			String inputTextString = inputTextField.getText();
			String inputKeyString = inputKeyField.getText();
			
			if (inputTextString.trim().isEmpty()) {
				inputTextField.setPromptText("Please input text");
			} else if (chosenMethod.equals("Caesar")) {
				try {
					int key = Integer.parseInt(inputKeyField.getText());
					try {
						String encryptString = method1.caesarDecrypt(inputTextString, key);
						resultText.setText(encryptString);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} catch (Exception f) {
					inputKeyField.clear();
					inputKeyField.setPromptText("Please input a valid number");
					f.printStackTrace();
				}
			} else if (chosenMethod.equals("DES")) {
				try {
					String decryptString = method1.desDecrypt(inputKeyString, inputTextString);
					resultText.setText(decryptString);
				} catch (Exception f) {
					f.printStackTrace();
				}
			} else if (chosenMethod.equals("AES")) {
				try {
					String decryptString = method1.aesDecrypt(inputKeyString, inputTextString);
					resultText.setText(decryptString);
				} catch (Exception f) {
					f.printStackTrace();
				}
			}
		});

		loadColorFromDatabase(userName, grid);
		colorPicker.setValue(chosenColor);
		loadMethodFromDatabase(userName);
		if (!chosenMethod.isEmpty()) {
			methodMenuButton.setText(chosenMethod);
			String method111 = "Caesar";
			String method222 = "DES";
			String method333 = "AES";
			if(chosenMethod.equals(method111)) {
				inputKeyField.setPromptText("Caesar Key should be a valid number");
			} else if(chosenMethod.equals(method222)) {
				inputKeyField.setPromptText("DES Encrypt Don't need key");
			} else if(chosenMethod.equals(method333)) {
				inputKeyField.setPromptText("AES Encrypt Don't need key");
			} 
		}
		
	}
	
	public void login(Stage primaryStage) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			Statement statement = connection.createStatement();

			String userName = usernameField.getText();
			String secretUserName = method1.caesarEncrypt(userName, 3);
			String password = passwordField.getText();
			String secretPassword = method1.hashEncrypt(password);
			
			String sql = "SELECT * FROM login where userName = \"" + secretUserName + "\"";
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				String hashPassword = resultSet.getString("password");
				if (secretPassword.equals(hashPassword)) {
					mainPage(primaryStage, secretUserName);
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Password is wrong.");
					alert.initOwner(primaryStage);
					alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "UserName doesn't exist.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createUser(Stage primaryStage) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String userName = usernameField.getText();
			String secretUserName = method1.caesarEncrypt(userName, 3); // when create, use caeser method to encrypt username (key = 3)
			String password = passwordField.getText();
			String secretPassword = method1.hashEncrypt(password); //hashed password can't be decrypted, store hashed password in database more security. When log in, hash input password to match database.

			String checkUserNameSql = "SELECT * FROM login WHERE userName = ?";
			PreparedStatement checkStatement = connection.prepareStatement(checkUserNameSql);
			checkStatement.setString(1, secretUserName);
			ResultSet checkResult = checkStatement.executeQuery();

			if (userName.trim().isEmpty() || password.trim().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill up both name and password.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			} else if (checkResult.next()) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "This userName exists. Please input a new userName.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			} else {
				String sql = "INSERT INTO login (`userName`, `password`) VALUES (?, ?)";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
					preparedStatement.setString(1, secretUserName);
					preparedStatement.setString(2, secretPassword);
					try {
						int rowsAffected = preparedStatement.executeUpdate();
						if (rowsAffected > 0) {
							Alert alert = new Alert(Alert.AlertType.INFORMATION, "Create account Successfully.");
							alert.initOwner(primaryStage);
							alert.showAndWait();
							createUserSetting(secretUserName); // when create account successfully, create a related user setting row in settings table
							mainPage(primaryStage, secretUserName);
						} 
					} catch (Exception e) {
						Alert alert = new Alert(Alert.AlertType.ERROR, "Exceeding the length limit. Please input appropriate UserName and Password.");
						alert.initOwner(primaryStage);
						alert.showAndWait();
					}
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			checkStatement.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("createUser Error");
		}
	}

	public void createUserSetting(String userNameString) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "INSERT INTO settings (`setUserName`,`setBackColor`,`setDefaultMethod`) VALUES (?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			String color = "0.9,0.9,0.9";   //default background color
			String method = "choose one method";  
			statement.setString(1, userNameString);
			statement.setString(2, color);
			statement.setString(3, method);
			statement.executeUpdate();
			statement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("createUserSetting Error");
		}
	}

	public void saveMessageById(Stage primaryStage, String userName, String message, String key, String idString,
			String methodName, byte[] secretKeyB) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			//first check if this message id already exists in this userName's message row.
			String checkMessageIdSql = "SELECT * FROM message WHERE loginUserName = ? AND messageId = ? ";
			PreparedStatement checkMessageIdStatement = connection.prepareStatement(checkMessageIdSql);
			checkMessageIdStatement.setString(1, userName);
			checkMessageIdStatement.setString(2, idString);
			ResultSet checkMessageIdResult = checkMessageIdStatement.executeQuery();

			if (checkMessageIdResult.next()) {
				Alert alert = new Alert(Alert.AlertType.WARNING, "The message Id already exists. Please input a new message Id.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			} else {
				String updateMessageSql = "INSERT INTO message (`methodName`,`encryptText`,`hashKey`,`loginUserName`,`messageId`) VALUES (?, ?, ?, ?, ?) ";
				PreparedStatement updateStatement = connection.prepareStatement(updateMessageSql);
				updateStatement.setString(1, methodName);
				updateStatement.setString(2, message);
				updateStatement.setString(3, key);
				updateStatement.setString(4, userName);
				updateStatement.setString(5, idString);
				int rowsAffected = updateStatement.executeUpdate();
				if (rowsAffected > 0) {
					String saveFileName = userName + idString+ ".txt";
					method1.saveKeyFile(saveFileName, secretKeyB);
					Alert alert = new Alert(Alert.AlertType.INFORMATION, "Save message successfully.");
					alert.initOwner(primaryStage);
					alert.showAndWait();
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Unsuccessfully.");
					alert.initOwner(primaryStage);
					alert.showAndWait();
				}
				updateStatement.close();
			}
			checkMessageIdStatement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("saveMessageToDatabaseById Error");
		}
	}

	public String getKeyFileIndexById(Stage primaryStage, String userName, String inputId) {
		String keyFileIndex = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String checkSql = "SELECT * FROM message WHERE loginUserName = ? AND messageId = ? ";
			PreparedStatement checkStatement = connection.prepareStatement(checkSql);
			checkStatement.setString(1, userName);
			checkStatement.setString(2, inputId);
			ResultSet checkResult = checkStatement.executeQuery();
			if (checkResult.next()) {
				keyFileIndex = checkResult.getString("messageId");
				// change the default method when call
				chosenMethod = checkResult.getString("methodName");
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Don't have related key.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
			checkStatement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("checkMessageId Error");
		}
		return keyFileIndex;
	}
	
	public String getMessageTextById(Stage primaryStage, String userName, String inputId) {
		String getMessageText = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String checkSql = "SELECT * FROM message WHERE loginUserName = ? AND messageId = ? ";
			PreparedStatement checkStatement = connection.prepareStatement(checkSql);
			checkStatement.setString(1, userName);
			checkStatement.setString(2, inputId);
			ResultSet checkResult = checkStatement.executeQuery();
			if (checkResult.next()) {
				getMessageText = checkResult.getString("encryptText");
				chosenMethod = checkResult.getString("methodName");
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Don't have related Text.");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
			checkStatement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("getMessageTextById Error");
		}
		return getMessageText;
	}

	// save this user's set color to database
	public void saveColorToDatabase(String userName, Color c) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String updateColorSql = "UPDATE settings SET setBackColor = ? WHERE setUserName = ? ";
			PreparedStatement updateStatement = connection.prepareStatement(updateColorSql);
			String colorString = c.getRed() + "," + c.getGreen() + "," + c.getBlue();
			System.out.println("print color :" + colorString);
			updateStatement.setString(1, colorString);
			updateStatement.setString(2, userName);
			updateStatement.executeUpdate();

			updateStatement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("saveColorToDatabase Error");
		}
	}

	// load user's set color from database
	public void loadColorFromDatabase(String userName, GridPane grid) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			Statement statement = connection.createStatement();
			String sql = "SELECT setBackColor FROM settings WHERE setUserName = \"" + userName + "\"";
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				String colorString = resultSet.getString("setBackColor");
				String colors[] = colorString.split(",");
				Color newColor = new Color(Double.parseDouble(colors[0]), Double.parseDouble(colors[1]),
						Double.parseDouble(colors[2]), 1);
				grid.setBackground(new Background(new BackgroundFill(newColor, null, null)));
				chosenColor = newColor;
			}
		} catch (Exception e2) {
			System.out.println("loadColorFromDatabase Error");
		}
	}

	// save this user's set method to database
	public void saveMethodToDatabase(String userName, String methodString) {
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

			String updateColorSql = "UPDATE settings SET setDefaultMethod = ? WHERE setUserName = ? ";
			PreparedStatement updateStatement = connection.prepareStatement(updateColorSql);
			updateStatement.setString(1, methodString);
			updateStatement.setString(2, userName);
			updateStatement.executeUpdate();
			
			updateStatement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("saveMethodToDatabase Error");
		}
	}

	// load user's set method from database
	public void loadMethodFromDatabase(String userName) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			Statement statement = connection.createStatement();
			String sql = "SELECT setDefaultMethod FROM settings WHERE setUserName = \"" + userName + "\"";
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				String methodString = resultSet.getString("setDefaultMethod");
				chosenMethod = methodString;
				
			}
			statement.close();
			connection.close();
		} catch (Exception e2) {
			System.out.println("loadMethodFromDatabase Error");
		}
	}

}
