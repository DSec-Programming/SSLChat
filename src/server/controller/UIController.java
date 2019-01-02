package server.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
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
	private TextArea chatTextArea, infoTextArea;

	@FXML
	private ListView<String> activeUserListView, certReqListView;

	@FXML
	private MenuBar menuBar;

	@FXML
	private RadioMenuItem menuItemStandard, menuItemCertificate;

	@FXML
	private Button shutDownButton, clearChatButton, kickUserButton, printButton;

	private ObservableList<String> observableUserOnlineList;

	private ObservableList<String> observableChatMessages;

	private ObservableList<String> observableNotificationList;

	private static boolean loaded = false;

	@FXML
	private void initialize()
	{

		menuItemStandard.setSelected(true);
		this.observableUserOnlineList = model.getUserObservableOnlineList();
		this.observableChatMessages = model.getObservableChatMessages();
		this.observableNotificationList = model.getObservableNotificationList();

		setAllListener();
		initTextAreas();

		// nur für test
		// später in buttonHandler
		try
		{
			//INS MODEL AUSLAGERN !!! 
			if (!loaded)
			{
				loaded = true;
				ServerSocketEntrace sse = new ServerSocketEntrace(55555, connectionModel);
				SSLServerSocketEntrace SSLsse = new SSLServerSocketEntrace(44444, connectionModel);
				connectionModel.setServerSocketEntrace(sse);
				connectionModel.setSSLServerSocketEntrace(SSLsse);
				sse.start();
				SSLsse.start();
				InetAddress address = InetAddress.getLocalHost();
				model.addNotification("Server successfully started !");
				model.addNotification("Local IP: " + address.getHostAddress());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Füllt die TextAreas mit den vorhandenen Werten des Modells
	 */
	private void initTextAreas()
	{
		activeUserListView.setItems(FXCollections.observableList(model.getUserOnlineList()));
		String chat = "";
		for (String s : model.getChatMessages())
		{
			chat += s + "\n";
		}
		chatTextArea.setText(chat);

		String info = "";
		for (String s : model.getInfoMessages())
		{
			info += s + "\n";
		}
		infoTextArea.setText(info);
	}

	public void changeView(ActionEvent e)
	{
		if (e.getSource() == menuItemCertificate)
		{
			try
			{
				Stage stage = (Stage) menuBar.getScene().getWindow();
				Parent root = FXMLLoader.load(getClass().getResource("/server/ui/CertificateUI.fxml"));
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}

		if (e.getSource() == menuItemStandard)
		{
			menuItemStandard.setSelected(true);
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
		if (!model.getUserOnlineList().isEmpty())
		{
			int index = activeUserListView.getSelectionModel().getSelectedIndex();
			String user = model.getUserOnlineList().get(index);
			// TODO Client soll Flag gesendet bekommen --> Gezwungenermaßen ausloggen
			model.removeUserInOnlineList(user);
			activeUserListView.getSelectionModel().select(0);
			model.addNotification(user + " successfully kicked !");
		}
	}
	
	/*
	 * Druckt den Inhalt der Notification-TextArea
	 */
	public void handlePrintNotifications(ActionEvent e)
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{				
				TextFlow printArea = new TextFlow(new Text(infoTextArea.getText()));

			    PrinterJob printerJob = PrinterJob.createPrinterJob();

			    if (printerJob != null && printerJob.showPrintDialog(infoTextArea.getScene().getWindow())) {
			        PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
			        printArea.setMaxWidth(pageLayout.getPrintableWidth());

			        if (printerJob.printPage(printArea)) {
			            printerJob.endJob();
			            model.addNotification("Notifications printed !");
			        } else {
			            System.out.println("Failed to print");
			        }
			    } else {
			        System.out.println("PrintJob Canceled");
			    }
			}
		};
		t.start();
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
	}

}
