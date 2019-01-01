package server.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import server.connection.SSLServerSocketEntrace;
import server.connection.ServerSocketEntrace;
import server.connection.SingleClientConnection;
import server.connection.SingleClientConnection2;
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
	
	private ObservableList<String> observableNotificationList;

	public void initialize()
	{
		this.observableUserOnlineList = model.getUserObservableOnlineList();
		this.observableChatMessages = model.getObservableChatMessages();
		this.observableNotificationList = model.getObservableNotificationList();

		setAllListener();

		// nur für test
		// später in buttonHandler
		try
		{
			//INS MODEL AUSLAGERN !!! 
			ServerSocketEntrace sse = new ServerSocketEntrace(55555, connectionModel);
			SSLServerSocketEntrace SSLsse = new SSLServerSocketEntrace(44444, connectionModel);
			connectionModel.setServerSocketEntrace(sse);
			connectionModel.setSSLServerSocketEntrace(SSLsse);
			sse.start();
			SSLsse.start();
			InetAddress address = InetAddress.getLocalHost();			
			model.addNotification("Server successfully started !");
			model.addNotification("Local IP: " + address.getHostAddress());

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
	
	public void clearChat(ActionEvent e)
	{
	    observableChatMessages.clear();
	    model.addNotification("Chat cleared !");
	}
	
	public void kickUser(ActionEvent e)
	{
	    if(!model.getUserOnlineList().isEmpty())
	    {
	        int index = activeUserListView.getSelectionModel().getSelectedIndex();
	        String user = model.getUserOnlineList().get(index);
	        // TODO Client soll Flag gesendet bekommen --> Gezwungenermaßen ausloggen
	        model.removeUserInOnlineList(user);
	        activeUserListView.getSelectionModel().select(0);
	        model.addNotification(user + " successfully kicked !");
	    }
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

						ArrayList<SingleClientConnection2> openConnections = connectionModel
								.getAllOpenSingleClientConnections();
						ArrayList<String> actuelUserOnlineList = model.getUserOnlineList();
						for (SingleClientConnection2 scc : openConnections)
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
				final String c = chat;
				Platform.runLater(() -> chatTextArea.setText(c));
				// trigger die Änderungen bei den Clients
				// ! Model muss synchronisiert werden damit niemand anderes
				// dazwischenspucken
				// kann !
				
				//hallo test ets tets 
				synchronized (model)
				{
					synchronized (connectionModel)
					{

						ArrayList<SingleClientConnection2> openConnections = connectionModel
								.getAllOpenSingleClientConnections();
						ArrayList<String> actuelChat = model.getChatMessages();
						for (SingleClientConnection2 scc : openConnections)
						{
							connectionModel.sendUpdate(scc, SingleClientConnection.CHAT_UPDATE, actuelChat);
						}
					}
				}
			}
		});
		
		this.observableNotificationList.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String notifications = "";
				for (String s : observableNotificationList)
				{
					notifications += (s + "\n");
				}
				final String noti = notifications;
				Platform.runLater(() -> infoTextArea.setText(noti));
				/* 
				 * Dient nur dazu, um den ChangeListener der notificationsTextArea zu triggern,
				 * damit diese immer ans Ende scrolled
				 */
				Platform.runLater(() -> infoTextArea.appendText(""));
			}
		});
		
		infoTextArea.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
			{
				infoTextArea.setScrollTop(Double.MAX_VALUE);
			}
		});
	}

}
