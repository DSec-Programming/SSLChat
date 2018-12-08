package ssl.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ssl.client.model.ClientDataModel;
import ssl.streamedObjects.UpdateFromServer;

/**
 * Spezifikation
 * 
 * Die Klasse ClientConnection ist eine PASSIVE Klasse --> Sie wird von
 * verschiedenen Runnables als GEMEINSAMES Object benutzt -->synchronisieren!
 * 
 * Ein TreadPool steutert den Zugriff auf die Daten
 * 
 * RUNNABLES: - Runnable-SendObject - Runnable-ReceiveSerferBroadcasts
 * 
 * 
 */

public class ClientConnection
{
    private Socket socket;

    private ObjectOutputStream toServer;

    private ObjectInputStream fromServer;

    private ClientDataModel model;

    public static final String USER_UPDATE = "userupdate";

    public static final String CHAT_UPDATE = "chatupdate";

    /**
     * Der Kontruckter wird ...
     * 
     * UMBAUEN !! vielleicht Model nachträglich setzen als im Konstuktor
     * mitzugeeeben
     * 
     */

    public ClientConnection(String host, int port, String username, ClientDataModel model) throws IOException
    {
        this.socket = new Socket(host, port);
        this.toServer = new ObjectOutputStream(this.socket.getOutputStream());
        this.fromServer = new ObjectInputStream(this.socket.getInputStream());
        this.model = model;

        // Beim Server anmelden !
        this.send(username);
    }

    /**
     * schreibt ein Object auf den Stream und sendet in entgültig (spüht den
     * Stream durch) ! Das übergebene Object muss das INTERFACE Serilizable
     * implementieren !
     */
    public void send(Object o) throws IOException
    {
        this.toServer.writeObject(o);
        this.toServer.flush();
    }

    /**
     * schaut nach ob etwas auf dem Stream liegt falls ja -> return sofort
     * Ansonsten ließt die Methode EIN Object vom Stream durch instanceof kann
     * entschieden werden wie es Gecastet werden muss. Empfohlene Verbesserung:
     * Benutzung INTERPRETER TODO
     */

    public synchronized void waitReceiveAndUpdateModel() throws IOException, ClassNotFoundException, InterruptedException
    {
        if (this.socket.getInputStream().available() == 0)
        {
            return;
        }
        Object data = this.fromServer.readObject();

        // geht noch schöner und Dynamischer
        // hier jetzt ehr statisch ...

        if (data instanceof UpdateFromServer)
        {
            UpdateFromServer update = (UpdateFromServer) data;
            if (update.getUpdateType().equals(CHAT_UPDATE))
            {
                // chatUpdate
                this.model.updateMessageToChat(update.getUpDate());

            }
            else if (update.getUpdateType().equals(USER_UPDATE))
            {
                // userupdate
                this.model.updateUserInOnlineList(update.getUpDate());
            }
            else
            {
                System.out.println("RUNTIMEEXECEPTION");
                throw new RuntimeException("NICHT ERWARTETES OBJECT VOM SERVER");
            }
        }
    }

    /*
     * schließt den Socket
     */
    public synchronized void close() throws IOException
    {
        this.socket.close();
    }

}
