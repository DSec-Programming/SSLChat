package server.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import server.connection.ServerSocketEntrace;
import server.connection.SingleClientConnection;
import server.model.ConnectionModel;
import server.model.ServerDataModel;

public class UIController
{
	private static ServerDataModel model;
	private static ConnectionModel connectionModel;

	@FXML
	private TextArea chatTextArea, infoTextArea, certTextArea;
	
	@FXML
	private ListView<String> activeUserListView, certReqListView;
	
	@FXML
	private Button shutDownButton, clearChatButton, kickUserButton, allowCertButton;

	private ObservableList<String> observableUserOnlineList;

	private ObservableList<String> observableChatMessages;

	public void initialize()
	{
		this.observableUserOnlineList = model.getUserObservableOnlineList();
		this.observableChatMessages = model.getObservableChatMessages();

		setAllListener();

		// nur für test
		// später in buttonHandler
		try
		{
			ServerSocketEntrace sse = new ServerSocketEntrace(55555, connectionModel);
			connectionModel.setServerSocketEntrace(sse);
			sse.start();

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Setzt die zum Start mitgegebenen Parameter
	 * 
	 * @param args
	 */
	public static void setModel(ServerDataModel m)
	{
		model = m;
	}

	public static void setConnectionModel(ConnectionModel m)
	{
		connectionModel = m;
	}

	public void shutDown(ActionEvent e)
	{
		System.exit(0);
	}
	
	public void clearChat()
	{
	    // TODO
	    // textArea der Clients aktualisiert sich nicht
	    // observableChatMessages.clear() --> update wird nicht getriggered
	}
	
	public void kickUser()
	{
	    int index = activeUserListView.getSelectionModel().getSelectedIndex();
	    String user = model.getUserOnlineList().get(index);
	    // TODO Client soll Flag gesendet bekommen --> Gezwungenermaßen ausloggen
	    model.removeUserInOnlineList(user);
	    activeUserListView.getSelectionModel().select(0);
	}

	private void setAllListener()
	{
		this.observableUserOnlineList.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
			    /*
			     * onlineList dient als tmp Liste
			     * Platform.runLater -> gibt Änderung der Oberfläche an Main-Thread weiter
			     * FXCollections.observableList(onlineList) -> wandelt tmp-Liste in ObservableList um
			     */
				List<String> onlineList = new ArrayList<String>();
				for (String s : observableUserOnlineList)
				{
					onlineList.add(s);
				}
				Platform.runLater(() -> activeUserListView.setItems(FXCollections.observableList(onlineList)));

				// trigger die Änderungen bei den Clients
				// ! Model muss synchronisiert werden damit niemand anderes
				// dazwischenspucken
				// kann !
				synchronized (model)
				{
					synchronized (connectionModel)
					{

						ArrayList<SingleClientConnection> openConnections = connectionModel
								.getAllOpenSingleClientConnections();
						ArrayList<String> actuelUserOnlineList = model.getUserOnlineList();
						for (SingleClientConnection scc : openConnections)
						{
							connectionModel.sendUpdate(scc, SingleClientConnection.USER_UPDATE, actuelUserOnlineList);
							connectionModel.sendUpdate(scc, SingleClientConnection.CHAT_UPDATE,
									model.getChatMessages());
						}
					}
				}
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
				chatTextArea.setText(chat);

				// trigger die Änderungen bei den Clients
				// ! Model muss synchronisiert werden damit niemand anderes
				// dazwischenspucken
				// kann !
				
				//hallo test ets tets 
				synchronized (model)
				{
					synchronized (connectionModel)
					{

						ArrayList<SingleClientConnection> openConnections = connectionModel
								.getAllOpenSingleClientConnections();
						ArrayList<String> actuelChat = model.getChatMessages();
						for (SingleClientConnection scc : openConnections)
						{
							connectionModel.sendUpdate(scc, SingleClientConnection.CHAT_UPDATE, actuelChat);
						}
					}
				}
			}
		});
	}

}
