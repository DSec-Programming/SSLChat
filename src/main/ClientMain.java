package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ssl.client.controller.ClientController;

public class ClientMain extends Application
{
	public static void main(String[] args)
	{
	    ClientController.setParams(args);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("../ssl/client/ui/ClientUI.fxml"));
		Scene scene = new Scene(root);

		primaryStage.setTitle("SSL Client v0.1");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
