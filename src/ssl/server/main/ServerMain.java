package ssl.server.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ServerMain extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Pane root = (Pane) FXMLLoader.load(getClass().getResource("../ui/ServerUI.fxml"));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("SSL Server v0.1");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();        
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
}
