package client.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import client.model.ClientDataModel;
import client.model.ConnectionModel;
import client.model.User;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import streamedObjects.MessageFromClient;

/**
 * Spezifikation
 * 
 * Der ClientController wird vom MAINThread ausgeführt.
 * 
 * die methode initialize() ersetzt hier den Konstruktor !!! ATTRIBUTE werden
 * VOR Erzeugung des EINEN Objectes statisch über die Methode setParams(String[]
 * args) gesetzt!!!
 * 
 * ------> GEHT DAS NOCH SCHÖNER ?
 * 
 */

public class UIController
{
	private static ClientDataModel clientDataModel;
	private static ConnectionModel connectionModel;

	// FXML
	// ======================================================================
	@FXML
	private TextArea activeUserTextArea;
	@FXML
	private TextArea chatTextArea;
	@FXML
	private TextArea notificationsTextArea;
	@FXML
	private TextField messageInputField;
	@FXML
	private Button sendButton;

	@FXML
	private VBox rigthVbox;
	@FXML
	private HBox leftHbox;
	@FXML
	private VBox parentVboxChatNotify;
	@FXML
	private VBox vboxNotofications;
	@FXML
	private VBox ChatPaneBox;
	@FXML
	private Pane ChatPaneBoxPane;
	@FXML
	private HBox hboxbottominfos;
	@FXML
	private VBox vboxConfigurations;
	@FXML
	private VBox vboxClientAuth;
	@FXML
	private TextArea connectionInfos;
	@FXML
	private TextArea lokalInfos;
	@FXML
	private Button connectButton, disconnectButton, printButton;

	@FXML
	private TextField serverIPField;

	private ToggleGroup protokollToggleGroup;
	@FXML
	private RadioButton radioTCP;
	@FXML
	private RadioButton radioTLS;

	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu menuFile, menuProperties, menuInfo;
	@FXML
	private MenuItem itemUsername;
	@FXML
	private Label labelLoggedInUser;
	@FXML 
	private Text textIP;

	private User user;

	// zu überwachende Dinge aus dem Model!
	// welche gleichzeitig auch im UI angezegit werden
	// ======================================================================
	private ObservableList<String> activeUserContent;
	private ObservableList<String> chatContent;
	private ObservableList<String> notificationContent;

	private StringProperty connectionType;
	private StringProperty serverStatus;
	private StringProperty clientStatus;

	private BooleanProperty existKeystore;
	private BooleanProperty haveImportetCert;
	private BooleanProperty haveOwnCert;
	// ENDE==================================================================

	public void initialize()
	{
		// für die Automatische UI-Aktualisierung
		this.chatContent = clientDataModel.getObservableChatList();
		this.activeUserContent = clientDataModel.getUserObservableOnlineList();
		this.notificationContent = clientDataModel.getObservableNotificationList();

		this.connectionType = connectionModel.getConnectionType();
		this.serverStatus = connectionModel.getServerStatus();
		this.clientStatus = connectionModel.getClientStatus();

		this.existKeystore = clientDataModel.getExistKeyStore();
		this.haveImportetCert = clientDataModel.getHaveAnImportedCert();
		this.haveOwnCert = clientDataModel.getHaveAnCertFromServer();

		ToggleGroup group = new ToggleGroup();
		group.getToggles().add(radioTCP);
		group.getToggles().add(radioTLS);
		this.protokollToggleGroup = group;
		this.protokollToggleGroup.selectToggle(radioTCP);

		// add Listener damit der CHAT automatisch Aktualisiert wird
		this.setAllListener();
		//
		this.setAllBorders();
		//
		hideChatPane();
		//
		this.disconnectButton.disableProperty().set(true);

		labelLoggedInUser.setVisible(false);
		user = new User();
		try
		{
			InetAddress address = InetAddress.getLocalHost();
			textIP.setText("Local IP: " + address.getHostAddress());
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}			
		
	}

	/**
	 * Setzt die zum Start mitgegebenen Parameters
	 * 
	 * @param args
	 */
	public static void setClientDataModel(ClientDataModel m)
	{
		clientDataModel = m;
	}

	public static void setConnectionModel(ConnectionModel m)
	{
		connectionModel = m;
	}

	/**
	 * Die handleSendButton Methode ließt das Textfield des UI´s auch und leert
	 * es anschließend
	 * 
	 * der input (die Nachricht) wird nun in Das Runnable SENDOBJECT gepackt und
	 * als Auftrag in den ThreadPool geworfen
	 * 
	 */
	public void handleSendButton(ActionEvent e)
	{
		String msg = messageInputField.getText();
		messageInputField.setText("");
		if (!msg.equals(""))
		{
			try
			{
				connectionModel.sendMessageOverClientConnection(new MessageFromClient(msg));
			} catch (Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}

	public void handleClearNotifications(ActionEvent e)
	{
		clientDataModel.clearNotifications();
	}

	public void openAlert()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setX(connectButton.getScene().getWindow().getX() + 100);
		alert.setY(connectButton.getScene().getWindow().getY() + 100);
		alert.setTitle("Info: Set Username !");
		alert.setHeaderText("");
		alert.setContentText("Before you loggin, please visit the menu and go to \n"
				+ "'Properties' --> 'Username', and set your Username !");
		alert.showAndWait();
	}

	public void handleConnectButton(ActionEvent e)
	{
		/*
		 * Wenn der Benutzername noch nicht gesetzt ist,
		 * wird eine Warnung angezeigt
		 */
		if (user.getUsername() == null || user.getUsername().equals(""))
		{
			openAlert();
			return;
		}
		showChatPane();
		disableConnectConfig();
		this.connectButton.disableProperty().set(true);
		this.disconnectButton.disableProperty().set(false);
		try
		{
			String serverIP = this.serverIPField.getText();

			if (this.protokollToggleGroup.getSelectedToggle().equals(this.radioTCP))
			{
				connectionModel.openSocket(serverIP, clientDataModel);
			} else if (this.protokollToggleGroup.getSelectedToggle().equals(this.radioTLS))
			{
				connectionModel.openSSLSocket(serverIP, clientDataModel);
			}
			labelLoggedInUser.setText("Logged in as: " + user.getUsername());
			labelLoggedInUser.setVisible(true);
			clientDataModel.addNotification("Successfully logged in !");

		} 
		catch(ConnectException ce)
		{
			clientDataModel.addNotification("SERVER NOT RESPONDING !");
			hideChatPane();
			enableConnectConfig();
			this.connectButton.disableProperty().set(false);
			this.disconnectButton.disableProperty().set(true);
			return;
		}
		catch (IOException ee)
		{
			clientDataModel.addNotification("SERVER NOT RESPONDING !");
			ee.printStackTrace();
			hideChatPane();
			enableConnectConfig();
			this.connectButton.disableProperty().set(false);
			this.disconnectButton.disableProperty().set(true);
			return;
		}
	}

	public void handleDisconnectButton(ActionEvent e)
	{		
		labelLoggedInUser.setVisible(false);
		hideChatPane();
		enableConnectConfig();
		this.disconnectButton.disableProperty().set(true);
		this.connectButton.disableProperty().set(false);
		try
		{
			connectionModel.killConnectoin();
			// chat oberfläche clearen
			// chat oberfläche ausschalten
			this.activeUserContent.clear();
			this.chatContent.clear();
			clientDataModel.addNotification("Successfully logged out !");
		} catch (IOException ee)
		{
			ee.printStackTrace();
		}
	}

	/**
	 * öffnet eine neue Stage. --> Wenn der Button "Apply" 
	 * gedrückt wurde, wird der Benutzername gesetzt
	 * @param e
	 */
	public void handleSetUsername(ActionEvent e)
	{
		try
		{
			Parent root = FXMLLoader.load(getClass().getResource("/client/ui/username.fxml"));
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Change username");
			stage.setScene(scene);
			stage.setX(connectButton.getScene().getWindow().getX() + 200);
			stage.setY(connectButton.getScene().getWindow().getY() + 100);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
			stage.showAndWait();
			
			user.setUsername(UsernameController.getUsername());		
			connectionModel.setUser(user);
			if(user.getUsername() != null)
			{
				clientDataModel.addNotification("Username set in: " + user.getUsername());
			}
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/*
	 * Öffnet eine neue Stage die die Entwickler anzeigt.
	 */
	public void handleDeveloperInfo(ActionEvent e)
	{
		Parent root;
		try
		{
			root = FXMLLoader.load(getClass().getResource("/client/ui/developerInfo.fxml"));
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Developer-Info");
			stage.setScene(scene);
			stage.setX(connectButton.getScene().getWindow().getX() + 200);
			stage.setY(connectButton.getScene().getWindow().getY() + 100);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}		
	}

	/*
	 * Öffnet die README-Datei im Standardprogramm
	 */
	public void handleShowReadme(ActionEvent e)
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					if(Desktop.isDesktopSupported())
					{
						InputStream resource = getClass().getResourceAsStream("/info/README.txt");					
						File file = File.createTempFile("README", ".txt");
						System.out.println(file.getName());
						Files.copy(resource, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
						file.deleteOnExit();		
						Desktop.getDesktop().open(file);
						resource.close();						
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	/*
	 * Druckt den Inhalt der Notification-TextArea
	 */
	public void handlePrintNotifications(ActionEvent e)
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{				
				TextFlow printArea = new TextFlow(new Text(notificationsTextArea.getText()));

			    PrinterJob printerJob = PrinterJob.createPrinterJob();

			    if (printerJob != null && printerJob.showPrintDialog(notificationsTextArea.getScene().getWindow())) {
			        PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
			        printArea.setMaxWidth(pageLayout.getPrintableWidth());

			        if (printerJob.printPage(printArea)) {
			            printerJob.endJob();
			            clientDataModel.addNotification("Notifications printed !");
			        } else {
			            System.out.println("Failed to print");
			        }
			    } else {
			        System.out.println("PrintJob Canceled");
			    }
			}
		};
		t.start();
	}

	public void handleImportCertificate(ActionEvent e)
	{
		// TODO
		String s;
	}

	/*
	 * Die setAllListener-Methode setzt ALLE benötigten Listener
	 * Fast alle sind OnChange listener um Alles auf der Oberfläche
	 * automatisch Aktualisieren lassen zu können falls der ThreadPool
	 * Informationen im Model ändert
	 */
	private void setAllListener()
	{
		// Aktualisiert das Chatfenster
		this.chatContent.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String chat = "";
				for (String s : chatContent)
				{
					chat += (s + "\n");
				}
				final String c = chat;
				Platform.runLater(() -> chatTextArea.setText(c));
				/* 
				 * Dient nur dazu, um den ChangeListener der chatTextArea zu triggern,
				 * damit diese immer ans Ende scrolled
				 */
				Platform.runLater(() -> chatTextArea.appendText(""));

			}
		});

		// aktualisiert die OnlineListe
		this.activeUserContent.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String activeUser = "";
				for (String s : activeUserContent)
				{
					activeUser += (s + "\n");
				}
				activeUserTextArea.setText(activeUser);
			}
		});

		// aktualisiert der notifications
		this.notificationContent.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String notifications = "";
				for (String s : notificationContent)
				{
					notifications += (s + "\n");
				}
				
				notificationsTextArea.setText(notifications);
				
				/* 
				 * Dient nur dazu, um den ChangeListener der notificationsTextArea zu triggern,
				 * damit diese immer ans Ende scrolled
				 */
				Platform.runLater(() -> notificationsTextArea.appendText(""));
			}
		});

		ChangeListener<String> connectionInfoslistener = new ChangeListener<String>()
		{
			public void changed(ObservableValue<? extends String> s1, String s2, String s3)
			{
				String info = "";
				info += "Protokoll: " + connectionModel.getConnectionType().get() + "\n";
				info += "Server status: " + connectionModel.getServerStatus().get() + "\n";
				info += "Client status : " + connectionModel.getClientStatus().get() + "\n";
				connectionInfos.setText(info);
			}
		};

		this.connectionType.addListener(connectionInfoslistener);
		this.serverStatus.addListener(connectionInfoslistener);
		this.clientStatus.addListener(connectionInfoslistener);

		ChangeListener<Boolean> lokalInfoslistener = new ChangeListener<Boolean>()
		{
			public void changed(ObservableValue<? extends Boolean> b1, Boolean b2, Boolean b3)
			{
				String info = "";
				info += "keyStore: " + clientDataModel.getExistKeyStore().get() + "\n";
				info += "import cert : " + clientDataModel.getHaveAnImportedCert().get() + "\n";
				info += "own server cert: " + clientDataModel.getHaveAnCertFromServer() + "\n";
				lokalInfos.setText(info);
			}
		};

		this.existKeystore.addListener(lokalInfoslistener);
		this.haveImportetCert.addListener(lokalInfoslistener);
		this.haveOwnCert.addListener(lokalInfoslistener);

		/*
		 * ChangeListener der chatTextArea -> bei neuem Eintrag wird immer ans Ende gescrolled
		 */
		chatTextArea.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
			{
				chatTextArea.setScrollTop(Double.MAX_VALUE);
			}
		});
		
		/*
		 * ChangeListener der notificationTextArea -> bei neuem Eintrag wird immer ans Ende gescrolled
		 */
		notificationsTextArea.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
			{
				notificationsTextArea.setScrollTop(Double.MAX_VALUE);
			}
		});
		
	}

	private void setAllBorders()
	{
		this.rigthVbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");

		this.leftHbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");

		this.vboxNotofications
				.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");

		this.ChatPaneBox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");
		this.hboxbottominfos.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");

		this.vboxClientAuth.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");
		this.vboxConfigurations
				.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");
		this.ChatPaneBoxPane.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: grey;");
	}

	private void hideChatPane()
	{
		VBox emptyCopyOfChatPaneBox = new VBox();
		emptyCopyOfChatPaneBox.setPrefHeight(ChatPaneBox.getPrefHeight());
		emptyCopyOfChatPaneBox.setPrefWidth(ChatPaneBox.getPrefWidth());

		Text offlineInformation = new Text();
		offlineInformation.setFont(Font.font("Verdana", 30));
		offlineInformation.setFill(Color.LIGHTGRAY);
		offlineInformation.setText("You are Offline");

		emptyCopyOfChatPaneBox.setAlignment(Pos.CENTER);
		emptyCopyOfChatPaneBox.getChildren().add(offlineInformation);

		parentVboxChatNotify.getChildren().clear();
		parentVboxChatNotify.getChildren().add(emptyCopyOfChatPaneBox);
		parentVboxChatNotify.getChildren().add(vboxNotofications);
	}

	private void showChatPane()
	{
		parentVboxChatNotify.getChildren().clear();
		parentVboxChatNotify.getChildren().add(ChatPaneBoxPane);
		parentVboxChatNotify.getChildren().add(vboxNotofications);
	}

	private void disableConnectConfig()
	{
		this.itemUsername.setDisable(true);
		this.serverIPField.disableProperty().set(true);
		this.protokollToggleGroup.getToggles().forEach(toggle ->
		{
			RadioButton node = (RadioButton) toggle;
			node.setDisable(true);
		});
	}

	private void enableConnectConfig()
	{
		this.itemUsername.setDisable(false);
		this.serverIPField.disableProperty().set(false);
		this.protokollToggleGroup.getToggles().forEach(toggle ->
		{
			RadioButton node = (RadioButton) toggle;
			node.setDisable(false);
		});
	}

}