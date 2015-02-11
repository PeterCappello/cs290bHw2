/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package system;

import api.Computer;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Peter Cappello
 */
public interface Computer2Space extends Remote
{
    void register( Computer computer ) throws RemoteException;
}
