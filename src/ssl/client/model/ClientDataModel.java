package ssl.client.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientDataModel
{
	private ArrayList<String> userOnlineList;
	private ArrayList<String> chatMessages;

	private ObservableList<String> observableUserOnlineList;
	private ObservableList<String> observableChatMessages;

	public ClientDataModel()
	{
		// falls nichts gespeichter wurde
		this.userOnlineList = new ArrayList<>();
		this.chatMessages = new ArrayList<>();
		this.observableUserOnlineList = FXCollections.observableList(this.userOnlineList);
		this.observableChatMessages = FXCollections.observableList(this.chatMessages);

		// Persistente verläufe einlesen!
		// TODO
	}

	/**
	 * für den ReceiveThread der ReceiveThread aktualisiert das Model sobald es
	 * neue Nachtichten vom Server gibt
	 */

	public synchronized void updateUserInOnlineList(ArrayList<String> userOnlineList)
	{
		// Parameter ist die Akutelle OnlineList
		for (String s : userOnlineList)
		{
			if (this.observableUserOnlineList.contains(s))
			{
				continue;
			} else
			{
				this.observableUserOnlineList.add(s);
			}
		}
	}

	public synchronized void updateMessageToChat(ArrayList<String> msgs)
	{
		// Parameter ist der AktuelleChat
		for (String s : msgs)
		{
			if (this.observableChatMessages.contains(s))
			{
				continue;
			} else
			{
				this.observableChatMessages.add(s);
			}
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
