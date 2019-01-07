package client.model;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
	// Gespeichterte ..

	// Gespeicherter Inhalt der Oberflaechenelemente
	// ===============================================================================

	// onlinelist
	private ObservableList<String> observableUserOnlineList;
	// chat
	private ObservableList<String> observableChatList;
	// Notificaions
	private ObservableList<String> observableNotificationList;

	// LokalInfos
	// Ist ein KeyStore angemeldet
	private BooleanProperty existKeyStore;
	// Ist ein Certifikat importiert worden
	private BooleanProperty haveAnImportedCert;
	// Ist ein Certifikat vom Server ausgestellt worden
	private BooleanProperty haveAnCertFromServer;

	// Sonstiges
	private StringProperty authenticationMode;
	private BooleanProperty isKicked;

	public ClientDataModel()
	{

		this.observableUserOnlineList = FXCollections.observableList(new ArrayList<>());
		this.observableChatList = FXCollections.observableList(new ArrayList<>());
		this.observableNotificationList = FXCollections.observableList(new ArrayList<>());

		this.existKeyStore = new SimpleBooleanProperty();
		this.haveAnImportedCert = new SimpleBooleanProperty();
		this.haveAnCertFromServer = new SimpleBooleanProperty();

		this.authenticationMode = new SimpleStringProperty();
		this.isKicked = new SimpleBooleanProperty(false);
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

	public synchronized ObservableList<String> getObservableChatList()
	{
		return this.observableChatList;
	}

	public synchronized ObservableList<String> getObservableNotificationList()
	{
		return this.observableNotificationList;
	}

	public synchronized ObservableList<String> getObservableUserOnlineList()
	{
		return observableUserOnlineList;
	}

	public synchronized BooleanProperty getExistKeyStore()
	{
		return existKeyStore;
	}

	public synchronized BooleanProperty getHaveAnImportedCert()
	{
		return haveAnImportedCert;
	}

	public synchronized BooleanProperty getHaveAnCertFromServer()
	{
		return haveAnCertFromServer;
	}

	public synchronized StringProperty getAuthenticationMode()
	{
		return authenticationMode;
	}
	
	public synchronized BooleanProperty getkickedBool()
	{
		return isKicked;
	}

	/**
	 * Die Methode waitReceiveAndUpdateModel()
	 * der PASSIVEN Klasse ClientConnection aktualisiert das Model
	 * 
	 */
	public synchronized void updateUserInOnlineList(ArrayList<String> updateList)
	{
		// Parameter ist die Akutelle OnlineList
		this.observableUserOnlineList.clear();
		for (String s : updateList)
		{
			this.observableUserOnlineList.add(s);
		}
	}

	public synchronized void updateMessageToChat(ArrayList<String> updateList)
	{
		this.observableChatList.clear();
		for (int i = 0; i < updateList.size(); i++)
		{
			this.observableChatList.add(updateList.get(i));
		}
	}

	public synchronized void addNotification(String notification)
	{
		// Parameter ist eine neue Meldung
		DateTime date = new DateTime();
		this.observableNotificationList.add(date.getTimeWithSec() + notification);
	}

	public synchronized void clearNotifications()
	{
		// löscht alle Meldungen
		this.observableNotificationList.clear();
	}

	public synchronized void setExistKeyStore(boolean bool)
	{
		this.existKeyStore.set(bool);
	}

	public synchronized void setHavAnImportedCert(boolean bool)
	{
		this.haveAnImportedCert.set(bool);
	}

	public synchronized void setHaveAnCertFromServer(boolean bool)
	{
		this.haveAnCertFromServer.set(bool);
	}
	
	public synchronized void setKicked(boolean bool)
	{
		this.isKicked.set(bool);
	}

}
