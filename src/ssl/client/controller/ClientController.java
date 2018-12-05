package ssl.client.controller;

import java.io.IOException;
import java.net.ConnectException;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ssl.client.jobs.ClientConnection;
import ssl.client.jobs.ReceiveThread;
import ssl.client.model.ClientDataModel;

public class ClientController
{
	@FXML
	private TextArea txt_Msg, txt_User;

	@FXML
	private TextField txt_Input;

	@FXML
	private Button btn_send;

	private static String[] params;

	private ObservableList<String> observableUserOnlineList;
	
	private ObservableList<String> observableChatMessages;

	private ClientConnection connection;
	private ClientDataModel model;

	public void initialize()
	{
		this.model = new ClientDataModel();
		this.observableUserOnlineList = this.model.getUserObservableOnlineList();
		this.observableChatMessages = this.model.getObservableChatMessages();
		this.observableUserOnlineList.addListener(new ListChangeListener<String>()
		{
			public void onChanged(ListChangeListener.Change<? extends String> change)
			{
				String onlineList = "";
				for (String s : observableUserOnlineList)
				{
					onlineList += (s + "\n");
				}
				txt_User.setText(onlineList);
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
				txt_Msg.setText(chat);
			}
		});

		try
		{
			/**
			 * Hier werden die Params ausgelesen !! und der Client-technisch
			 * alles gestartet
			 */
			this.connection = new ClientConnection(params[0], Integer.parseInt(params[1]), "Peter", model);
			ReceiveThread rt = new ReceiveThread(this.connection, this.model);
			rt.start();
		} catch (ConnectException e)
		{
			System.out.println("SERVER OFFLINE !!! ");
			System.exit(0);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Führt die Aktion aus, die nach dem Drücken des Senden-Knopfs ausgeführt
	 * werden soll.
	 */
	public void handleSendButton(ActionEvent e)
	{
		String msg = txt_Input.getText();
		txt_Input.setText("");
		try
		{
			this.connection.send(msg);
		} 
		catch (Exception ee)
		{
			ee.printStackTrace();
		}
	}

	/**
	 * Setzt die zum Start mitgegebenen Parameter
	 * 
	 * @param args
	 */
	public static void setParams(String[] args)
	{
		params = args;
	}

}
