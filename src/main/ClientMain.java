package main;

import client.controller.UIController;
import client.model.ClientDataModel;
import client.model.ConnectionModel;
import client.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientMain extends Application
{
	private static ConnectionModel connectionModel;

	public static void main(String[] args)
	{
		ClientDataModel clientDatamodel = new ClientDataModel();
		connectionModel = new ConnectionModel();

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
	public void stop() throws Exception
	{
		connectionModel.killConnectoin();
		System.out.println("Kill connection");
		connectionModel.shutdownThreadPool();
		System.out.println("Shutdown pool");

	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("../client/ui/current.fxml"));
		Scene scene = new Scene(root);

		primaryStage.setTitle("SSL Client v0.1");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		// primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
		// {
		// @Override
		// public void handle(WindowEvent e)
		// {
		// connectionModel.killConnectoin();
		// System.out.println("Kill connection");
		// }
		// });
		System.out.println("show");
		primaryStage.show();
	}
}
