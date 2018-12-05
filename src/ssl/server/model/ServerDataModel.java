package ssl.server.model;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerDataModel
{
	private ArrayList<String> userOnlineList;
	private ArrayList<String> chatMessages;

	private ObservableList<String> observableUserOnlineList;
	private ObservableList<String> observableChatMessages;

	public ServerDataModel()
	{
		// falls nichts gespeichter wurde
		this.userOnlineList = new ArrayList<>();
		this.chatMessages = new ArrayList<>();
		this.observableUserOnlineList = FXCollections.observableList(this.userOnlineList);
		this.observableChatMessages = FXCollections.observableList(this.chatMessages);

		// Persistente verl�ufe einlesen!
		// TODO

	}

	/**
	 * f�r die(PLURAL) ClientServeThreads Die clientServeThread aktualisieren
	 * regelm��ig ob User online kommen, offline gehen oder etwas in den Chat
	 * schreiben
	 */

	public synchronized void addUserInOnlineList(String s)
	{
		this.observableUserOnlineList.add(s);
	}

	public synchronized void removeUserInOnlineList(String s)
	{
		this.observableUserOnlineList.remove(s);
	}

	public synchronized void addMsgAtChat(String s)
	{
		this.observableChatMessages.add(s);
	}

	/**
	 * f�r die(PLURAL) ClientServeThreads wenn die Clients aktualisiereungen
	 * abfragen !!
	 */

	// SCH�NER --> Server sagt Client in Verbindung bescheid
	// das es neue nachrichten gibt !!

	public synchronized ArrayList<String> getUserOnlineList()
	{
		// COPY da ansonsten eine ConcurrentModificationException
		// geworfen werden kann
		// Diese Methode wird ausschlie�lich aufgerufen wenn ein Client
		// den Stand anfordert
		ArrayList<String> copy = new ArrayList<>();
		for (String s : this.userOnlineList)
		{
			copy.add(s);
		}
		return copy;
	}

	public synchronized ArrayList<String> getChatMessages()
	{
		// COPY da ansonsten eine ConcurrentModificationException
		// geworfen werden kann
		// Diese Methode wird ausschlie�lich aufgerufen wenn ein Client
		// den Stand anfordert
		ArrayList<String> copy = new ArrayList<>();
		for (String s : this.chatMessages)
		{
			copy.add(s);
		}
		return copy;
	}

	/**
	 * f�r den Controller Controller holt sich einmal die Referenz der
	 * Observebale ArrayList und setzt bei sich in der Controller klasse
	 * ChangeListener f�r die automatische aktualisierung des UI�s
	 */
	public synchronized ObservableList<String> getUserObservableOnlineList()
	{
		return this.observableUserOnlineList;
	}

	public synchronized ObservableList<String> getObservableChatMessages()
	{
		return this.observableChatMessages;
	}
}
