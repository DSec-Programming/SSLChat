package ssl.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClientController
{
    @FXML
    private TextArea txt_Msg, txt_User;
    
    @FXML
    private TextField txt_Input;
    
    @FXML
    private Button btn_send;
    
    private static String[] params;
    
    /**
     * Führt die Aktion aus, die nach dem Drücken des Senden-Knopfs ausgeführt
     * werden soll.
     */
    public void handleSendButton(ActionEvent e)
    {
        
    }
    
    /**
     * Aktualisiert den Nachrichten-Verlauf
     */
    public void updateMsgList()
    {
        
    }
    
    /**
     * Aktualisiert die Liste mit den aktuellen Benutzern die online sind
     */
    public void updateUserList()
    {
        
    }
    
    /**
     * Setzt die zum Start mitgegebenen Parameter
     * @param args
     */
    public static void setParams(String[] args)
    {
        params = args;
    }  
    
    public void initialize()
    {
        
    }
}
