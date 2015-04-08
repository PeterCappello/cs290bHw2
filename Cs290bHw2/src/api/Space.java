/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote 
{
    public static int PORT = 8001;
    public static String SERVICE_NAME = "Space";

    /**
     *
     * @param task
     * @throws RemoteException
     */
    void execute( Task task ) throws RemoteException;
    
    void putAll ( List<Task> taskList ) throws RemoteException;

    Result take() throws RemoteException;

    void exit() throws RemoteException;
}
