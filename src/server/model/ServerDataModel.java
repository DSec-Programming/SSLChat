package server.model;

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
		// macht bei onlienlist keinen sinn !!
		this.userOnlineList = new ArrayList<>();
		this.chatMessages = new ArrayList<>();
		this.observableUserOnlineList = FXCollections.observableList(this.userOnlineList);
		this.observableChatMessages = FXCollections.observableList(this.chatMessages);

		// Persistente verläufe einlesen!
		// TODO

	}

	/**
	 * für die(PLURAL) ClientServeThreads Die clientServeThreads aktualisieren
	 * regelmäßig ob User online kommen, offline gehen oder etwas in den Chat
	 * schreiben
	 * 
	 * Anschließend muss eine Änderung TRIGGERN dass alle Clients über den
	 * aktuellen Stand benachrichtigt werden
	 * 
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
	 * für die(PLURAL) ClientServeThreads wenn die Clients aktualisiereungen
	 * abfragen !!
	 */

	// SCHÖNER --> Server sagt Client in Verbindung bescheid
	// das es neue nachrichten gibt !!

	public synchronized ArrayList<String> getUserOnlineList()
	{
		// COPY da ansonsten eine ConcurrentModificationException
		// geworfen werden kann
		// Diese Methode wird ausschließlich aufgerufen wenn ein Client
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
		// Diese Methode wird ausschließlich aufgerufen wenn ein Client
		// den Stand anfordert
		ArrayList<String> copy = new ArrayList<>();
		for (String s : this.chatMessages)
		{
			copy.add(s);
		}
		return copy;
	}

	/**
	 * für den Controller Controller holt sich einmal die Referenz der
	 * Observebale ArrayList und setzt bei sich in der Controller klasse
	 * ChangeListener für die automatische aktualisierung des UI´s
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
