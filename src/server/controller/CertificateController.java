package server.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.Stage;
import server.model.ConnectionModel;
import server.model.ServerDataModel;

public class CertificateController
{
	private static ServerDataModel model;
	
	private static ConnectionModel connectionModel;
	
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private RadioMenuItem menuItemStandard, menuItemCertificate;
	
	@FXML
	private Button allowCertButton;
	
	public void initialize()
	{
		menuItemCertificate.setSelected(true);
	}
	
	public void changeView(ActionEvent e)
	{		
		if(e.getSource() == menuItemStandard)
		{
			try
			{
            	Stage stage = (Stage) menuBar.getScene().getWindow();
				Parent root = FXMLLoader.load(getClass().getResource("/server/ui/ServerUI.fxml"));
				Scene scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}       
		}
		
		if(e.getSource() == menuItemCertificate)
		{
			menuItemCertificate.setSelected(true);
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
}
