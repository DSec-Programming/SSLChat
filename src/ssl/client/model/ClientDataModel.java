package ssl.client.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

public class ClientDataModel
{
	private ArrayList<String> userOnlineList;
	private ArrayList<String> chatMessages;

	private ObservableList<String> observableUserOnlineList;
	private ObservableList<String> observableChatMessages;

	public ClientDataModel(TextArea chatArea, TextArea onlineListArea)
	{
		// falls nichts gespeichter wurde
		this.userOnlineList = new ArrayList<>();
		this.chatMessages = new ArrayList<>();
		this.observableUserOnlineList = FXCollections.observableList(this.userOnlineList);
		this.observableChatMessages = FXCollections.observableList(this.chatMessages);

		this.observableUserOnlineList.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String onlineList = "";
				for (String s : observableUserOnlineList)
				{
					onlineList += (s + "\n");
				}
				onlineListArea.setText(onlineList);
			}
		});
		this.observableChatMessages.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String chat = "";
				for (String s : observableChatMessages)
				{
					chat += (s + "\n");
				}
				chatArea.setText(chat);
			}
		});

		// ArrayList<String> test = new ArrayList<>();
		// test.add("HAHSHHHDHSHD");
		// test.add("XD");
		// updateMessageToChat(test);

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
		this.observableUserOnlineList.clear();
		for (String s : userOnlineList)
		{
			this.observableUserOnlineList.add(s);
		}
	}

	public void updateMessageToChat(ArrayList<String> msgs)
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
