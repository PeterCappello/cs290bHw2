/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package system;

import api.Computer;
import api.Result;
import api.Space;
import api.Task;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peter
 */
public class SpaceImpl extends UnicastRemoteObject implements Space, Computer2Space
{
    private final BlockingQueue<Task> taskQ     = new LinkedBlockingQueue<>();
    private final BlockingQueue<Result> resultQ = new LinkedBlockingQueue<>();
    private final Map<Computer,ComputerProxy> computerProxies = new HashMap<>();
    private static int computerIds = 0;
    
    public SpaceImpl() throws RemoteException 
    {
        Logger.getLogger( SpaceImpl.class.getName() ).log( Level.INFO, "Space started." );
    }
    
    /**
     * Put a task into the Task queue.
     * @param task
     */
    @Override
    synchronized public void put(Task task) { taskQ.add( task ); }

    /**
     * Take a Result from the Result queue.
     * @return a Result object.
     */
    @Override
    synchronized public Result take() 
    {
        try 
        {
            return resultQ.take();
        } 
        catch ( InterruptedException exception ) 
        {
            Logger.getLogger(SpaceImpl.class.getName()).log(Level.INFO, null, exception);
        }
        assert false; // should never reach this point
        return null;
    }

    @Override
    public void exit() throws RemoteException 
    {
        computerProxies.values().forEach( proxy -> proxy.exit() );
        System.exit( 0 );
    }

    /**
     * Register Computer with Space.  
     * Will override existing key-value pair, if any.
     * @param computer - Remote reference to computer.
     * @throws RemoteException
     */
    @Override
    synchronized public void register( Computer computer ) throws RemoteException 
    {
        final ComputerProxy computerproxy = new ComputerProxy( computer );
        computerProxies.put( computer, computerproxy );
        computerproxy.start();
        Logger.getLogger(SpaceImpl.class.getName()).log(Level.INFO, "Computer {0} started.", computerproxy.computerId);
    }
    
    public static void main( String[] args ) throws Exception
    {
        System.setSecurityManager( new SecurityManager() );
        LocateRegistry.createRegistry( Space.PORT )
                      .rebind( Space.SERVICE_NAME, new SpaceImpl() );
    }
    
    private class ComputerProxy extends Thread implements Computer 
    {
        final private Computer computer;
        final private int computerId = computerIds++;

        ComputerProxy( Computer computer ) { this.computer = computer; }

        @Override
        public Result execute( Task task ) throws RemoteException
        { 
            return computer.execute( task );
        }
        
        @Override
        public void exit() 
        { 
            try { computer.exit(); } 
            catch ( RemoteException ignore ) {} 
        }

        @Override
        public void run() 
        {
            while ( true ) 
            {
                Task task = null;
                try 
                { 
                    task = taskQ.take();  
                    resultQ.add( execute( task ) );
                }
                catch ( RemoteException ignore )
                {
                    taskQ.add( task );
                    computerProxies.remove( computer );
                    Logger.getLogger( SpaceImpl.class.getName() ).log( Level.WARNING, "Computer {0} failed.", computerId );
                } 
                catch ( InterruptedException ex ) 
                {
                    Logger.getLogger( SpaceImpl.class.getName()).log( Level.INFO, null, ex );
                }
            }
        }
    }
}
