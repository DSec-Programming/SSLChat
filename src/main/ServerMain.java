package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.controller.CertificateController;
import server.controller.UIController;
import server.model.ConnectionModel;
import server.model.ServerDataModel;

public class ServerMain extends Application
{
	private static ConnectionModel connectionModel;
	
	public static void main(String[] args)
	{
		//load server Key Store
		//KeyStoreLoadThread kslt = new KeyStoreLoadThread();
		//kslt.start();
		
		//load server truststore
		
		
		ServerDataModel model = new ServerDataModel();
		connectionModel = new ConnectionModel(model);

		UIController.setModel(model);
		UIController.setConnectionModel(connectionModel);
		
		CertificateController.setModel(model);
		CertificateController.setConnectionModel(connectionModel);

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Pane root = (Pane) FXMLLoader.load(getClass().getResource("/server/ui/ServerUI.fxml"));
		Scene scene = new Scene(root);

		primaryStage.setTitle("SSL Server v0.1");
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
