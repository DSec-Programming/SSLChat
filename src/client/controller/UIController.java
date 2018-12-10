package client.controller;

import java.io.IOException;
import java.net.Socket;

import client.connection.ClientConnection;
import client.model.ClientDataModel;
import client.model.ConnectionModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
	private Button connectButton;
	@FXML
	private Button disconnectButton;

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

		// add Listener damit der CHAT automatisch Aktualisiert wird
		this.setAllListener();
		//
		this.setAllBorders();
		//
		hideChatPane();
		//
		this.disconnectButton.disableProperty().set(true);

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
		try
		{
			connectionModel.sendMessageOverClientConnection(new MessageFromClient(msg));
		} catch (Exception ee)
		{
			ee.printStackTrace();
		}
	}

	public void handleClearNotifications(ActionEvent e)
	{
		clientDataModel.clearNotifications();
	}

	public void handleConnectButton(ActionEvent e)
	{
		showChatPane();
		this.connectButton.disableProperty().set(true);
		this.disconnectButton.disableProperty().set(false);
		try
		{
			ClientConnection connection = new ClientConnection(new Socket("localhost", 55555), clientDataModel,
					connectionModel);
			connectionModel.setConnection(connection);
			connectionModel.startWorkingConnection();
		} catch (IOException ee)
		{
			clientDataModel.addNotification("IOException " + ee.getMessage());
			ee.printStackTrace();
			hideChatPane();
			this.connectButton.disableProperty().set(false);
			this.disconnectButton.disableProperty().set(true);
			return;
		}
	}

	public void handleDisconnectButton(ActionEvent e)
	{
		hideChatPane();
		this.disconnectButton.disableProperty().set(true);
		this.connectButton.disableProperty().set(false);
		try
		{
			connectionModel.killConnectoin();
			// chat oberfläche clearen
			// chat oberfläche ausschalten
			this.activeUserContent.clear();
			this.chatContent.clear();
		} catch (IOException ee)
		{
			ee.printStackTrace();
		}
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
				chatTextArea.setText(chat);
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

}