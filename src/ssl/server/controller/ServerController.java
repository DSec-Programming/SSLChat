package ssl.server.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import ssl.server.connection.CallableSendBroadcastUpdate;
import ssl.server.connection.RunnableObserveSingleClientConnections;
import ssl.server.connection.RunnableRemoveInactivesClients;
import ssl.server.connection.ServerSocketEntrace;
import ssl.server.connection.SingleClientConnection;
import ssl.server.model.ServerDataModel;

public class ServerController
{
    @FXML
    private TextArea txt_Msg, txt_User;

    @FXML
    private Button btn_shutDown;

    private static String[] params;

    private ObservableList<String> observableUserOnlineList;

    private ObservableList<String> observableChatMessages;

    private ThreadPoolExecutor pool;

    public void initialize()
    {
        this.pool = new ThreadPoolExecutor(4, 8, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ServerDataModel model = new ServerDataModel(pool);
        this.observableUserOnlineList = model.getUserObservableOnlineList();
        this.observableChatMessages = model.getObservableChatMessages();
        this.observableUserOnlineList.addListener(new ListChangeListener<String>()
        {
            public void onChanged(ListChangeListener.Change<? extends String> change)
            {
                String onlineList = "";
                for (String s : observableUserOnlineList)
                {
                    onlineList += (s + "\n");
                }
                txt_User.setText(onlineList);

                // trigger die Änderungen bei den Clients
                // ! Model muss synchronisiert werden damit niemand anderes
                // dazwischenspucken
                // kann !
                synchronized (model)
                {

                    ArrayList<SingleClientConnection> openConnections = model.getAllOpenSingleClientConnections();
                    ArrayList<String> actuelUserOnlineList = model.getUserOnlineList();
                    for (SingleClientConnection scc : openConnections)
                    {
                        pool.submit(new CallableSendBroadcastUpdate(scc, SingleClientConnection.USER_UPDATE, actuelUserOnlineList));
                    }
                }
            }
        });
        this.observableChatMessages.addListener(new ListChangeListener<String>()
        {
            public void onChanged(ListChangeListener.Change<? extends String> change)
            {
                String chat = "";
                for (String s : observableChatMessages)
                {
                    chat += (s + "\n");
                }
                txt_Msg.setText(chat);

                // trigger die Änderungen bei den Clients
                // ! Model muss synchronisiert werden damit niemand anderes
                // dazwischenspucken
                // kann !
                synchronized (model)
                {
                    ArrayList<SingleClientConnection> openConnections = model.getAllOpenSingleClientConnections();
                    ArrayList<String> actuelChat = model.getChatMessages();
                    for (SingleClientConnection scc : openConnections)
                    {
                        pool.submit(new CallableSendBroadcastUpdate(scc, SingleClientConnection.CHAT_UPDATE, actuelChat));
                    }
                }
            }
        });

        try
        {
            ServerSocketEntrace sc = new ServerSocketEntrace(Integer.parseInt(params[0]), model);
            sc.start();

            /**
             * Es muss ein Etwas gestartet werden was in Regelmäßigen abstanden
             * abhört ob bei der SingleClientConnectionSAMMLUNG ein Client etwas
             * zum Server gesendet hat
             */

            this.pool.submit(new RunnableObserveSingleClientConnections(model, this.pool));

            /**
             * Inactive Clients müssen ausgetragen werden
             */

            this.pool.submit(new RunnableRemoveInactivesClients(model));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void shutDown(ActionEvent e)
    {
        System.exit(0);
    }

    /**
     * Setzt die zum Start mitgegebenen Parameter
     * 
     * @param args
     */
    public static void setParams(String[] args)
    {
        params = args;
    }
}
