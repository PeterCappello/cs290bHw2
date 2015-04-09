/*
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
package clients;

import api.Job;
import api.JobRunner;
import api.Space;
import api.Space;
import api.Task;
import api.Task;
import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import system.ComputerImpl;
import system.SpaceImpl;

/**
 *
 * @author Peter Cappello
 * @param <T> return type the Task that this Client executes.
 */
abstract public class NewClient<T> extends JFrame
{    
    final private long startTime;
    
    public NewClient( final String title ) throws RemoteException
    {     
        System.setSecurityManager( new SecurityManager() );
        startTime = System.nanoTime();
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    /**
     *
     * @param job
     * @throws RemoteException
     */
    public void run( Job<T> job ) throws RemoteException
    {
        JobRunner jobRunner = new JobRunner( job );
        jobRunner.run();
        add( getLabel( job.getValue() ) );
        Logger.getLogger( Client.class.getCanonicalName() ).log(Level.INFO, "Client time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );
    }
    
    public void end( Job<T> job ) 
    { 
//        add( getLabel( job.getValue() ) );
//        Logger.getLogger( Client.class.getCanonicalName() ).log(Level.INFO, "Client time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );
    }
    
    public void add( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );
    }
    
    public Space getSpace( String domainName ) throws RemoteException, NotBoundException, MalformedURLException
    {
        final String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
        return (Space) Naming.lookup( url );
    }
    
    public Space getSpace( int numComputers ) throws RemoteException
    {
        SpaceImpl space = new SpaceImpl();
        for ( int i = 0; i < numComputers; i++ )
        {
            space.register( new ComputerImpl() );
        }
        return space;
    }
    
    abstract JLabel getLabel( final T returnValue );
    
//    abstract List<Task> decompose();
}
 