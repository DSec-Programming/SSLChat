package main;


import client.controller.ClientController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientMain extends Application
{
	public static void main(String[] args)
	{
		args = new String[]
		{ "localhost", "55555", "Tim" };
		ClientController.setParams(args);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("../client/ui/ClientUI.fxml"));
		Scene scene = new Scene(root);

		primaryStage.setTitle("SSL Client v0.1");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent e)
			{
				System.exit(0);
			}
		});
		primaryStage.show();
	}
}
