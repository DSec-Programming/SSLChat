package client.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import client.connection.ClientConnection;
import client.connection.RunnableReceiveServerBroadcasts;
import client.connection.RunnableSendObject;
import client.model.ClientDataModel;
import client.model.User;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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
	// ClientInformationen
	// ======================================================================
	private static String[] params;
	// private ClientConnection connection;
	private ClientDataModel model;
	// private ThreadPoolExecutor pool;

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
	private VBox ChatPaneBox;

	// ! set not Editibar
	private TextArea connectionInfos;
	// ! set not Editibar
	private TextArea lokalInfos;

	private VBox configurationVBox;

	private VBox authenticationVBox;

	// zu überwachende Dinge aus dem Model!
	// welche gleichzeitig auch im UI angezegit werden
	// ======================================================================
	private ObservableList<String> activeUserContent;
	private ObservableList<String> chatContent;
	private ObservableList<String> notificationContent;
	// ENDE==================================================================

	public void initialize()
	{
		// UI
		// this.ChatPaneBox.setVisible(true);
		// for (Node n : this.ChatPaneBox.getChildren())
		// {
		// n.setMouseTransparent(true);
		// n.setStyle("-fx-background-color: transparent;");
		// }

		// this.sendButton.setStyle("-fx-background-color: transparent;");

		try
		{
			System.out.println(params[0]);
			System.out.println(params[1]);
			this.model = new ClientDataModel();

			User u = new User();
			u.setUsername(params[2]);
			this.model.setUser(u);

			this.model.setConnection(
					new ClientConnection(new Socket(params[0], Integer.parseInt(params[1])), this.model));

		} catch (IOException e)
		{
			System.out.println("SERVER OFFLINE !!! ");
			System.out.println(e.getMessage());
			System.exit(0);
		}

		// hier noch aus dem Params geholt...
		// geht schöner ....
		User user = new User();
		user.setUsername(params[2]);
		// später noch .. holen uns bevor das Programm startet schon
		// die wichtigen Parameter für das erstellen eines Zertifikats
		// user.set()
		// user.set()

		this.model.setUser(user);

		// für die Automatische UI-Aktualisierung
		this.chatContent = this.model.getObservableChatList();
		this.activeUserContent = this.model.getUserObservableOnlineList();
		this.notificationContent = this.model.getObservableNotificationList();

		// add Listener damit der CHAT automatisch Aktualisiert wird
		this.setAllListener();

		// der von allen Klassen genutzte ThreadPool
		// this.pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS,
		// new LinkedBlockingQueue<>());

	}

	/**
	 * Setzt die zum Start mitgegebenen Parameter
	 * 
	 * @param args
	 */
	public static void setParams(String[] args)
	{
		params = args;
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
			this.model.sendMessageOverClientConnection(new MessageFromClient(msg));
		} catch (Exception ee)
		{
			ee.printStackTrace();
		}
	}

	public void handleClearNotifications(ActionEvent e)
	{
		this.model.clearNotifications();
	}

	public void handleConnectButton(ActionEvent e)
	{
		// TODO
		String s;
	}

	public void handleDisconnectButton(ActionEvent e)
	{
		// TODO
		String s;
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
	}

}