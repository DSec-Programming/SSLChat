package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UsernameController
{	
	@FXML
	private TextField textField;
	
	@FXML
	private Button applyButton;
	
	private static String username;
	
	private static void setUsername(String name)
	{
		username = name;
	}
	
	public static String getUsername()
	{
		return username;
	}
	
	public void handleSetUsername()
	{
		if(!textField.getText().equals(""))
		{
			 /** Ggf. noch anhand von onlineUserList überprüfen ob Name schon vorhanden
			  * wenn ja --> (2) hinten an Namen anfügen
			  **/
			String s;
			setUsername(textField.getText());
		}
		Stage stage = (Stage) applyButton.getScene().getWindow();
		stage.close();		
	}
	
	public void initialize()
	{
		textField.setText("");
	}
}
