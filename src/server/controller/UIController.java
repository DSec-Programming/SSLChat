package server.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import network.NetworkInfo;
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
	private MenuItem menuNetwork;
	@FXML
	private Button shutDownButton, clearChatButton, kickUserButton, printButton;

	@FXML
	private Label localip;

	private ObservableList<String> observableUserOnlineList;

	private ObservableList<String> observableChatMessages;

	private ObservableList<String> observableNotificationList;

	@FXML
	private void initialize()
	{

		menuItemStandard.setSelected(true);
		this.observableUserOnlineList = model.getUserObservableOnlineList();
		this.observableChatMessages = model.getObservableChatMessages();
		this.observableNotificationList = model.getObservableNotificationList();

		setAllListener();
		initTextAreas();
		hideChats();
		this.localip.setText("  IP : " + NetworkInfo.getCurrentNetworkIp());

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
		final String c = chat;
		Platform.runLater(() -> chatTextArea.setText(c));

		String info = "";
		for (String s : model.getInfoMessages())
		{
			info += s + "\n";
		}
		final String i = info;
		Platform.runLater(() -> infoTextArea.setText(i));
	}

	private void hideChats()
	{
		this.chatTextArea.setDisable(true);
		this.activeUserListView.setDisable(true);
	}

	private void showChats()
	{
		this.chatTextArea.setDisable(false);
		this.activeUserListView.setDisable(false);
	}

	public void startServer()
	{
		connectionModel.startServer();
		showChats();
	}

	public void stopServer()
	{
		connectionModel.stopServer();
		hideChats();
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
		if (connectionModel.getLoaded())
		{
			stopServer();
			connectionModel.setLoaded(false);
		} else
		{
			startServer();
			connectionModel.setLoaded(true);
		}
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
			connectionModel.kickUser(user);
			model.removeUserInOnlineList(user);
			Platform.runLater(() -> activeUserListView.getSelectionModel().select(0));
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

				if (printerJob != null && printerJob.showPrintDialog(infoTextArea.getScene().getWindow()))
				{
					PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
					printArea.setMaxWidth(pageLayout.getPrintableWidth());

					if (printerJob.printPage(printArea))
					{
						printerJob.endJob();
						model.addNotification("Notifications printed !");
					} else
					{
						System.out.println("Failed to print");
					}
				} else
				{
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
				/* 
				 * Dient nur dazu, um den ChangeListener der chatTextArea zu triggern,
				 * damit diese immer ans Ende scrolled
				 */
				Platform.runLater(() -> chatTextArea.appendText(""));

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

		/*
		 * ChangeListener der chatTextArea -> bei neuem Eintrag wird immer ans Ende gescrolled
		 */
		chatTextArea.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue)
			{
				Platform.runLater(() -> chatTextArea.setScrollTop(Double.MAX_VALUE));
			}
		});
	}

	public void openNetworkProperties()
	{
		Alert alert = new Alert(AlertType.NONE);
		alert.setX(chatTextArea.getScene().getWindow().getX() + (chatTextArea.getScene().getWindow().getWidth() / 2)
				- 200);
		alert.setY(chatTextArea.getScene().getWindow().getY() + (chatTextArea.getScene().getWindow().getHeight() / 2)
				- 80);
		alert.setTitle("Network properties");
		alert.setHeaderText("");
		alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
		Button b = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);

		Insets insets = new Insets(5, 5, 5, 5);

		VBox expContent = new VBox();
		expContent.setPadding(insets);
		HBox box1 = new HBox();
		box1.setPadding(insets);
		HBox box2 = new HBox();
		box2.setPadding(insets);
		Label l1 = new Label("TCP port:");
		l1.setPadding(insets);
		Label l2 = new Label("TLS port:");
		l2.setPadding(insets);
		TextField txt1 = new TextField();
		txt1.setPadding(insets);
		TextField txt2 = new TextField();
		txt2.setPadding(insets);
		txt1.setText(String.valueOf(connectionModel.getTcpPort()));
		txt2.setText(String.valueOf(connectionModel.getTlsPort()));
		box1.getChildren().addAll(l1, txt1);
		box2.getChildren().addAll(l2, txt2);
		expContent.getChildren().addAll(box1, box2);
		Label warn = new Label();
		warn.setPadding(insets);
		warn.setTextFill(Color.RED);
		expContent.getChildren().add(warn);

		txt1.textProperty().addListener((observable, oldValue, newValue) ->
		{
			try
			{
				int port = Integer.parseInt(newValue);
				if (port < 1024 || port > 65535)
				{
					warn.setText("port must be > 1024 and < 65535");
					b.setDisable(true);
				} else
				{
					warn.setText("");
					b.setDisable(false);
				}
			} catch (Exception e)
			{
				warn.setText("port must be a number");
				b.setDisable(true);
			}
		});
		txt2.textProperty().addListener((observable, oldValue, newValue) ->
		{
			try
			{
				int port = Integer.parseInt(newValue);
				if (port < 1024 || port > 65535)
				{
					warn.setText("port must be > 1024 and < 65535");
					b.setDisable(true);
				} else
				{
					warn.setText("");
					b.setDisable(false);
				}
			} catch (Exception e)
			{
				warn.setText("port must be a number");
				b.setDisable(true);
			}
		});

		alert.getDialogPane().setContent(expContent);

		alert.showAndWait();
		connectionModel.setTcpPort(Integer.parseInt(txt1.getText()));
		connectionModel.setTlsPort(Integer.parseInt(txt2.getText()));

	}

}
