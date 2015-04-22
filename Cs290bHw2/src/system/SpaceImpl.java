/**
 * The MIT License
 *
 * Copyright 2015 peter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package system;

import api.Result;
import api.Space;
import api.Task;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peter
 */
public class SpaceImpl extends UnicastRemoteObject implements Space
{
    private final BlockingQueue<Task>     taskQ = new LinkedBlockingQueue<>();
    private final BlockingQueue<Result> resultQ = new LinkedBlockingQueue<>();
    private final Map<Computer,ComputerProxy> computerProxies = new HashMap<>();
    private static int computerIds = 0;
    
    public SpaceImpl() throws RemoteException 
    {
        Logger.getLogger( this.getClass().getName() )
              .log( Level.INFO, "Space started." );
    }
    
    @Override
    synchronized public void putAll( List<Task> taskList )
    {
        for ( Task task : taskList )
        {
            taskQ.add( task );
        }
    }

    /**
     * Take a Result from the Result queue.
     * @return a Result object.
     */
    @Override
    synchronized public Result take() 
    {
        try { return resultQ.take(); } 
        catch ( InterruptedException exception ) 
        {
            Logger.getLogger( this.getClass().getName())
                  .log(Level.INFO, null, exception);
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
        Logger.getLogger( this.getClass().getName())
              .log(Level.INFO, "Computer {0} started.", computerproxy.computerId);
    }
    
    private void unregister( Task task, Computer computer )
    {
        taskQ.add( task );
        ComputerProxy computerProxy = computerProxies.remove( computer );
        Logger.getLogger( this.getClass().getName() )
              .log( Level.WARNING, "Computer {0} failed.", computerProxy.computerId );
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
                    SpaceImpl.this.unregister( task, computer );
                    break;
                } 
                catch ( InterruptedException ex ) 
                {
                    Logger.getLogger( this.getClass().getName())
                          .log( Level.INFO, null, ex );
                }
            }
        }
    }
}
