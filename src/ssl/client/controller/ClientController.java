package ssl.client.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ssl.client.connection.CallableReceiveServerInfos;
import ssl.client.connection.CallableSendObject;
import ssl.client.connection.ClientConnection;
import ssl.client.model.ClientDataModel;
import ssl.streamedObjects.MessageFromClient;

public class ClientController
{
	@FXML
	private TextArea txt_Msg, txt_User;

	@FXML
	private TextField txt_Input;

	@FXML
	private Button btn_send;

	private static String[] params;

	private ClientConnection connection;
	private ClientDataModel model;
	private ThreadPoolExecutor pool;

	public void initialize()
	{
		this.model = new ClientDataModel(this.txt_Msg,this.txt_User);
		this.pool = new ThreadPoolExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		try
		{
			/**
			 * Hier werden die Params ausgelesen !! und der Client-technisch
			 * alles gestartet
			 */
			this.connection = new ClientConnection(params[0], Integer.parseInt(params[1]), params[2], model);
			this.pool.submit(new CallableReceiveServerInfos(connection));

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
			// this.connection.send(msg);
			this.pool.submit(new CallableSendObject(this.connection, new MessageFromClient(msg)));
		} catch (Exception ee)
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
