package ssl.client.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ssl.client.connection.ClientConnection;
import ssl.client.connection.RunnableReceiveServerBroadcasts;
import ssl.client.connection.RunnableSendObject;
import ssl.client.model.ClientDataModel;
import ssl.streamedObjects.MessageFromClient;

/**
 * Spezifikation
 * 
 * Der ClientController wird vom MAINThread ausgeführt.
 * 
 * die methode initialize() ersetzt hier den Konstruktor
 * !!! ATTRIBUTE werden VOR Erzeugung des EINEN Objectes
 * statisch über die Methode setParams(String[] args) gesetzt!!!
 * 
 * ------> GEHT DAS NOCH SCHÖNER ?
 *
 *
 * !!! TODO beim terminieren des ClientControllers muss noch der Socket
 * ordentlich geschlossen werden !
 * 
 */

public class ClientController
{
	@FXML
	private TextArea activeUserTextArea;
	private ObservableList<String> activeUserContent;

	@FXML
	private TextArea chatTextArea;
	private ObservableList<String> chatContent;

	@FXML
	private TextField messageInputField;

	@FXML
	private Button sendButton;

	private static String[] params;

	private ClientConnection connection;
	private ClientDataModel model;
	private ThreadPoolExecutor pool;

	public void initialize()
	{
		this.model = new ClientDataModel();

		// für die Automatische UI-Aktualisierung
		this.chatContent = this.model.getObservableChatMessages();
		this.activeUserContent = this.model.getUserObservableOnlineList();

		// add Listener damit der CHAT automatisch Aktualisiert wird
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

		// add Listener damit die USER ONLINE automatisch Aktualisiert wird
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

		// der von allen Klassen genutzte ThreadPool
		this.pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

		try
		{
			/**
			 * Das ist der Hauptteil
			 * 
			 * hier wird die Verbindung zum Server aufgebaut
			 * ! Die Klasse ClientConnection ist PASSIV !
			 * 
			 * für das Überwachen des InStreams wird nun auch das dafür
			 * spezifizierte Runnable in den pool gelegt
			 * 
			 * (Der MainThread darf nicht blockieren) daher wird das überwachen
			 * und senden auf den Threadpool ausgelagert
			 */
			this.connection = new ClientConnection(params[0], Integer.parseInt(params[1]), params[2], model);
			this.pool.submit(new RunnableReceiveServerBroadcasts(connection));

		} catch (ConnectException e)
		{
			System.out.println("SERVER OFFLINE !!! ");
			System.exit(0);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Die handleSendButton Methode ließt das Textfield des UI´s auch und leert
	 * es anschließend
	 * 
	 * der input (die Nachricht) wird nun in Das Runnable SENDOBJECT
	 * gepackt und als Auftrag in den ThreadPool geworfen
	 * 
	 */
	public void handleSendButton(ActionEvent e)
	{
		String msg = messageInputField.getText();
		messageInputField.setText("");
		try
		{
			this.pool.submit(new RunnableSendObject(this.connection, new MessageFromClient(msg)));
		} catch (Exception ee)
		{
			ee.printStackTrace();
		}
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

}
