package main;

import client.controller.UIController;
import client.model.ClientDataModel;
import client.model.ConnectionModel;
import client.model.User;
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
		ClientDataModel clientDatamodel = new ClientDataModel();
		ConnectionModel connectionModel = new ConnectionModel();
		
		clientDatamodel.setExistKeyStore(false);
		clientDatamodel.setHavAnImportedCert(false);
		clientDatamodel.setHaveAnCertFromServer(false);

		User user = new User();
		user.setUsername("Tim");
		connectionModel.setUser(user);

		UIController.setClientDataModel(clientDatamodel);
		UIController.setConnectionModel(connectionModel);

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("../client/ui/current.fxml"));
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
