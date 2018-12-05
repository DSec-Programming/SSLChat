package ssl.server.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import ssl.server.jobs.ServerConnection;
import ssl.server.model.ServerDataModel;

public class ServerController
{
	@FXML
	private TextArea txt_Msg, txt_User;

	@FXML
	private Button btn_shutDown;
	
	private static String[] params;

	private ObservableList<String> observableUserOnlineList;
	private ObservableList<String> observableChatMessages;

	public void initialize()
	{
		ServerDataModel model = new ServerDataModel();
		this.observableUserOnlineList = model.getUserObservableOnlineList();
		this.observableChatMessages = model.getObservableChatMessages();
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
			ServerConnection sc = new ServerConnection(Integer.parseInt(params[0]), model);
			sc.start();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	public void shutDown(ActionEvent e)
	{
	    Platform.exit();
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
