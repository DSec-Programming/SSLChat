package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UsernameController
{
	@FXML
	private TextField usernameField,organisation,email;

	@FXML
	private Button applyButton;

	@FXML
	private Label label,alert;

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
		if (!usernameField.getText().isEmpty() && !organisation.getText().isEmpty() && !email.getText().isEmpty())
		{
			String username = usernameField.getText();
			if (username.length() > 15)
			{
				alert.setText("length must be < 15 !");
			}
			if (username.contains("<") || username.contains(">") || username.contains("!") || username.contains("?")
					|| username.contains("%") || username.contains("$") || username.contains("\"")
					|| username.contains("'") || username.contains("§") || username.contains("&"))
			{
				alert.setText("no special signs !");
			}

			setUsername(usernameField.getText());
			Stage stage = (Stage) applyButton.getScene().getWindow();
			stage.close();
		}
		else
		{
			alert.setText("please enter all informations");
		}
		
	}

	public void initialize()
	{
		usernameField.setText("");
		label.setText("Forbidden:\n length > 15 and §&<>!?%$\"'");
		alert.setTextFill(Color.RED);
	}
}
