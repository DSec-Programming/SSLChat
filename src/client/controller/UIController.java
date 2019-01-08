package client.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import network.NetworkInfo;
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
	@FXML
	private Label certLabel,protocolLabel,serverStatusLabel,clientStatusLabel;
	@FXML
	private Button certRequestButton;

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

	private BooleanProperty isKicked;
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

		this.isKicked = clientDataModel.getkickedBool();

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
		disableCertRequestField();
		this.disconnectButton.disableProperty().set(true);

		labelLoggedInUser.setVisible(false);
		user = new User();

		textIP.setText("Local IP: " + NetworkInfo.getCurrentNetworkIp());

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

	//UNSCHÖN !!!!!! BESSER machen !! 
	public void kickUser()
	{
		handleDisconnectButton(null);
	}

	public void handleClearNotifications(ActionEvent e)
	{
		clientDataModel.clearNotifications();
	}

	public void openAlert()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setX(connectButton.getScene().getWindow().getX() + (connectButton.getScene().getWindow().getWidth()/2) - 200);
		alert.setY(connectButton.getScene().getWindow().getY() + (connectButton.getScene().getWindow().getHeight()/2) - 80);
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
		 * 
		 */
		if (user.getUsername() == null || user.getUsername().equals(""))
		{
			openAlert();
			return;
		}
		/*
		 * checkt inputs
		 */
		String serverIP = this.serverIPField.getText();
		if (!isAIP(serverIP))
		{
			clientDataModel.addNotification("invalid ip");
			return;
		}

		showChatPane();
		disableConnectConfig();
		this.connectButton.disableProperty().set(true);
		this.disconnectButton.disableProperty().set(false);
		clientDataModel.setKicked(false);
		try
		{

			if (this.protokollToggleGroup.getSelectedToggle().equals(this.radioTCP))
			{
				connectionModel.openSocket(serverIP, clientDataModel);
				connectionType.set("TCP");
				serverStatus.set("NOT_AUTHENTICATED");
				clientStatus.set("NOT_AUTHENTICATED");
			} else if (this.protokollToggleGroup.getSelectedToggle().equals(this.radioTLS))
			{
				connectionModel.openSSLSocket(serverIP, clientDataModel);
				enableCertRequestField();
				connectionType.set("TLS");
				String s;
				//---> ab Ändern
				serverStatus.set("AUTHENTICATED");
				clientStatus.set("AUTHENTICATED");
			}
			labelLoggedInUser.setText("Logged in as: " + user.getUsername());
			labelLoggedInUser.setVisible(true);
			clientDataModel.addNotification("Successfully logged in !");

		} catch (ConnectException ce)
		{
			clientDataModel.addNotification("SERVER IS NOT RESPONDING !");
			hideChatPane();
			enableConnectConfig();
			this.connectButton.disableProperty().set(false);
			this.disconnectButton.disableProperty().set(true);
			return;
		} catch (IOException ee)
		{
			clientDataModel.addNotification("SERVER IS NOT RESPONDING !");
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
			if (user.getUsername() != null)
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
					if (Desktop.isDesktopSupported())
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

				if (printerJob != null && printerJob.showPrintDialog(notificationsTextArea.getScene().getWindow()))
				{
					PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
					printArea.setMaxWidth(pageLayout.getPrintableWidth());

					if (printerJob.printPage(printArea))
					{
						printerJob.endJob();
						clientDataModel.addNotification("Notifications printed !");
					} else
					{
						System.out.println("Failed to print");
					}
				} else
				{
					System.out.println("PrintJob Canceled");
				}
			}
		};
		t.start();
	}

	public void handleRequestCertificate(ActionEvent e)
	{
		//TODO
		certLabel.setTextFill(Color.RED);
		certLabel.setText("waiting for cert ...");
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
				final String a = activeUser;
				Platform.runLater(() -> activeUserTextArea.setText(a));
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
				final String n = notifications;

				Platform.runLater(() -> notificationsTextArea.setText(n));

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
				Platform.runLater(() -> protocolLabel.setText(connectionModel.getConnectionType().get()));
				Platform.runLater(() -> serverStatusLabel.setText(connectionModel.getServerStatus().get()));
				Platform.runLater(() -> clientStatusLabel.setText(connectionModel.getClientStatus().get()));
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
				final String i = info;
				Platform.runLater(() -> lokalInfos.setText(i));
			}
		};

		this.existKeystore.addListener(lokalInfoslistener);
		this.haveImportetCert.addListener(lokalInfoslistener);
		this.haveOwnCert.addListener(lokalInfoslistener);

		ChangeListener<Boolean> isKickedListener = new ChangeListener<Boolean>()
		{
			public void changed(ObservableValue<? extends Boolean> b1, Boolean b2, Boolean b3)
			{
				//TODO 
				Platform.runLater(() -> handleDisconnectButton(null));

			}
		};
		this.isKicked.addListener(isKickedListener);

		/*
		 * ChangeListener der chatTextArea -> bei neuem Eintrag wird immer ans Ende gescrolled
		 */
		chatTextArea.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
			{
				Platform.runLater(() -> chatTextArea.setScrollTop(Double.MAX_VALUE));
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
				Platform.runLater(() -> notificationsTextArea.setScrollTop(Double.MAX_VALUE));
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
	
	private void enableCertRequestField()
	{
		this.certLabel.setDisable(false);
		this.certRequestButton.setDisable(false);
	}
	
	private void disableCertRequestField()
	{
		this.certLabel.setDisable(true);
		this.certRequestButton.setDisable(true);
	}

	//UTILS

	public boolean isAIP(String s)
	{
		try
		{
			String[] sss = new String[4];
			char[] c = new char[3];
			int sssCounter = 0;
			int cCounter = 0;
			String temp = "";
			for (int i = 0; i < s.length(); i++)
			{
				char currentChar = s.charAt(i);
				if (currentChar == '.')
				{
					c[cCounter++] = currentChar;
					sss[sssCounter++] = new String(temp);
					temp = "";
					continue;
				} else
				{
					temp += currentChar;
				}
			}
			sss[sssCounter] = temp;
			int[] i = new int[4];
			for (int iii = 0; iii < sss.length; iii++)
			{
				i[iii] = Integer.parseInt(sss[iii]);
			}
			for (int iii = 0; iii < i.length; iii++)
			{
				if (i[iii] < 0 || i[iii] > 255)
				{
					return false;
				}
			}
			for (int iii = 0; iii < c.length; iii++)
			{
				if (!(c[iii] == '.'))
				{
					return false;
				}
			}

			return true;
		} catch (Exception e)
		{
			return false;
		}
	}
	
	public void openNetworkProperties()
	{
		Alert alert = new Alert(AlertType.NONE);
		alert.setX(connectButton.getScene().getWindow().getX() + (connectButton.getScene().getWindow().getWidth() / 2)
				- 200);
		alert.setY(connectButton.getScene().getWindow().getY() + (connectButton.getScene().getWindow().getHeight() / 2)
				- 80);
		alert.setTitle("Network properties");
		alert.setHeaderText("");
		alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
		Button b = (Button)alert.getDialogPane().lookupButton(ButtonType.OK);
		
		Insets insets = new Insets(5, 5, 5, 5);

		VBox expContent = new VBox();
		expContent.setPadding(insets);
		HBox box1 = new HBox();
		box1.setPadding(insets);
		HBox box2 = new HBox();
		box2.setPadding(insets);
		Label l1 = new Label("TCP port:");
		l1.setPadding(insets);
		Label l2 = new Label("TLS port:");
		l2.setPadding(insets);
		TextField txt1 = new TextField();
		txt1.setPadding(insets);
		TextField txt2 = new TextField();
		txt2.setPadding(insets);
		txt1.setText(String.valueOf(connectionModel.getTcpPort()));
		txt2.setText(String.valueOf(connectionModel.getTlsPort()));
		box1.getChildren().addAll(l1, txt1);
		box2.getChildren().addAll(l2, txt2);
		expContent.getChildren().addAll(box1, box2);
		Label warn = new Label();
		warn.setPadding(insets);
		warn.setTextFill(Color.RED);
		expContent.getChildren().add(warn);
		
		txt1.textProperty().addListener((observable, oldValue, newValue) ->
		{
			try
			{
				int port = Integer.parseInt(newValue);
				if (port < 1024 || port > 65535)
				{
					warn.setText("port must be > 1024 and < 65535");
					b.setDisable(true);
				} else
				{
					warn.setText("");
					b.setDisable(false);
				}
			} catch (Exception e)
			{
				warn.setText("port must be a number");
				b.setDisable(true);
			}
		});
		txt2.textProperty().addListener((observable, oldValue, newValue) ->
		{
			try
			{
				int port = Integer.parseInt(newValue);
				if (port < 1024 || port > 65535)
				{
					warn.setText("port must be > 1024 and < 65535");
					b.setDisable(true);
				} else
				{
					warn.setText("");
					b.setDisable(false);
				}
			} catch (Exception e)
			{
				warn.setText("port must be a number");
				b.setDisable(true);
			}
		});

		alert.getDialogPane().setContent(expContent);

		alert.showAndWait();
		connectionModel.setTcpPort(Integer.parseInt(txt1.getText()));
		connectionModel.setTlsPort(Integer.parseInt(txt2.getText()));

	}

}