package ssl.client.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import ssl.client.connection.ClientConnection;

/**
 * Spezifikation
 *
 * Das ClientDataModel verwaltet alle und speichert den AKTUELLEN auf dem Server
 * hinterlegten CHAT und die AKTUELL verbundenen USERN
 * 
 * ZUERST bekommt JEDE Datenstrucktur welche Daten speichtert einen OBSERVER
 * an Diese fügt dann der jewalige CONTROLLER des UI´s
 * Listener an. Diese werden im CONTROLLER dann genau implementiert
 * 
 * Alle Methoden des Models sind synchronisiert!
 * Bei Aktueller Imlementierung nicht unbedingt notwendig..
 * 
 * Für eventuelle Erweiterungen zur vorsicht hinzugefügt. Sicher ist Sicher
 * 
 * 
 */

public class ClientDataModel
{
	private ArrayList<String> userOnlineList;
	private ArrayList<String> chatMessages;

	private ObservableList<String> observableUserOnlineList;
	private ObservableList<String> observableChatMessages;

	public ClientDataModel()
	{
		this.userOnlineList = new ArrayList<>();
		this.chatMessages = new ArrayList<>();
		this.observableUserOnlineList = FXCollections.observableList(this.userOnlineList);
		this.observableChatMessages = FXCollections.observableList(this.chatMessages);
	}

	/**
	 * Die Methode waitReceiveAndUpdateModel()
	 * der PASSIVEN Klasse ClientConnection aktualisiert das Model
	 * 
	 */
	public synchronized void updateUserInOnlineList(ArrayList<String> userOnlineList)
	{
		// Parameter ist die Akutelle OnlineList
		this.observableUserOnlineList.clear();
		for (String s : userOnlineList)
		{
			this.observableUserOnlineList.add(s);
		}
	}

	public synchronized void updateMessageToChat(ArrayList<String> msgs)
	{
		// Parameter ist der AktuelleChat
		for (int i = this.observableChatMessages.size(); i < msgs.size(); i++)
		{
			this.observableChatMessages.add(msgs.get(i));
		}
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
