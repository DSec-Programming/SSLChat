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
		if(!textField.getText().isEmpty())
		{
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
