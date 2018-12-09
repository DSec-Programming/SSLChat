package client.model;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import client.connection.ClientConnection;
import client.connection.RunnableReceiveServerBroadcasts;
import client.connection.RunnableSendObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import streamedObjects.MessageFromClient;

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

	private ClientConnection connection;
	private ThreadPoolExecutor pool;

	// Gespeicherte Infos über die Verbindung
	// ===============================================================================

	private StringProperty ServerIP;
	private StringProperty usedProvider;

	private User user;

	// Gespeicherter Inhalt der Oberflaechenelemente
	// ===============================================================================

	private ObservableList<String> observableUserOnlineList;
	private ObservableList<String> observableChatList;

	// TODO An MICH(TIM) bitte überprüfe später gründlich ob alle sachen die im
	// Model verwaltet werden
	// auch wirklich in den Oberflächen verbaut wurden !!
	// Danach lösche diesen KOMMENTAR

	// Notificaions
	private ObservableList<String> observableNotificationList;

	// ConnectionInfos

	// ConnectionType e { "SSL","TCP" }
	private StringProperty connectionType;
	// ServerStatus e {"authorized","unauthorized"}
	private StringProperty serverStatus;
	// ClientStatus e {"authorized","unauthorized"}
	private StringProperty clientStatus;

	// LokalInfos

	// Ist ein KeyStore angemeldet
	private BooleanProperty existKeyStore;
	// Ist ein Certifikat importiert worden
	private BooleanProperty haveAnImportedCert;
	// Ist ein Certifikat vom Server ausgestellt worden
	private BooleanProperty haveAnCertFromServer;

	// Sonstiges
	// ===============================================================================
	private StringProperty authenticationMode;

	public ClientDataModel() throws IOException
	{
		this.pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

		this.observableUserOnlineList = FXCollections.observableList(new ArrayList<>());
		this.observableChatList = FXCollections.observableList(new ArrayList<>());
		this.observableNotificationList = FXCollections.observableList(new ArrayList<>());

		this.ServerIP = new SimpleStringProperty();
		this.usedProvider = new SimpleStringProperty();

		this.user = null;

		this.connectionType = new SimpleStringProperty();
		this.serverStatus = new SimpleStringProperty();
		this.clientStatus = new SimpleStringProperty();

		this.existKeyStore = new SimpleBooleanProperty();
		this.haveAnImportedCert = new SimpleBooleanProperty();
		this.haveAnCertFromServer = new SimpleBooleanProperty();
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

	public synchronized StringProperty getServerIP()
	{
		return ServerIP;
	}

	public synchronized StringProperty getUsedProvider()
	{
		return usedProvider;
	}

	public synchronized User getUser()
	{
		return user;
	}

	public synchronized ObservableList<String> getObservableUserOnlineList()
	{
		return observableUserOnlineList;
	}

	public synchronized StringProperty getConnectionType()
	{
		return connectionType;
	}

	public synchronized StringProperty getServerStatus()
	{
		return serverStatus;
	}

	public synchronized StringProperty getClientStatus()
	{
		return clientStatus;
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
		// Parameter ist der AktuelleChat
		for (int i = this.observableChatList.size(); i < updateList.size(); i++)
		{
			this.observableChatList.add(updateList.get(i));
		}
	}

	public synchronized void addNotification(String notification)
	{
		// Parameter ist eine neue Meldung
		this.observableNotificationList.add(notification);
	}

	public synchronized void clearNotifications()
	{
		// löscht alle Meldungen
		this.observableNotificationList.clear();
	}

	public synchronized void setUser(User user)
	{
		this.user = user;
	}

	public synchronized void setConnectionTyp(String ct)
	{
		this.connectionType.set(ct);
	}

	public synchronized void setServerStatus(String status)
	{
		this.serverStatus.set(status);
	}

	public synchronized void setClientStatus(String status)
	{
		this.clientStatus.set(status);
	}

	public synchronized void setExistKeyStore(boolean bool)
	{
		this.existKeyStore.set(bool);
	}

	public synchronized void sethavAnImportedCert(boolean bool)
	{
		this.haveAnImportedCert.set(bool);
	}

	public synchronized void haveAnCertFromServer(boolean bool)
	{
		this.haveAnCertFromServer.set(bool);
	}

	public synchronized void sendMessageOverClientConnection(MessageFromClient msg)
	{
		this.pool.submit(new RunnableSendObject(this.connection, msg));
	}

	public synchronized void setConnection(ClientConnection c) throws IOException
	{
		this.connection = c;
		this.pool.submit(new RunnableReceiveServerBroadcasts(connection));
		c.send(this.getUser().getUsername());
	}

}
