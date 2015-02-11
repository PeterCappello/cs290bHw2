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
import api.Computer;
import api.Result;
import api.Space;
import api.Task;
import applications.euclideantsp.TaskEuclideanTsp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import applications.mandelbrotset.TaskMandelbrotSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.Computer2Space;
import system.ComputerImpl;

/**
 *
 * @author Peter Cappello
 */
public class ClientMandelbrotSet extends Client<Integer[][]>
{
    public static final double LOWER_LEFT_X = -2.0;
    public static final double LOWER_LEFT_Y = -2.0;
    public static final double EDGE_LENGTH = 4.0;
    public static final int N_PIXELS = 256;
    public static final int ITERATION_LIMIT = 64;
    
    public ClientMandelbrotSet() throws RemoteException 
    { 
        super( "Mandelbrot Set Visualizer" );
    }
    
    /**
     * Run the MandelbrotSet visualizer client.
     * @param args unused 
     * @throws java.rmi.RemoteException 
     */
    public static void main( String[] args ) throws Exception
    {  
        System.setSecurityManager( new SecurityManager() );
        final Client client = new ClientMandelbrotSet();
        
        // get Remote reference to computing system
        Space space = client.getSpace( "localhost" );
        Computer2Space computer2space = (Computer2Space) space;
        Computer computer = new ComputerImpl();
        computer2space.register( computer );
        
        long startTime = System.nanoTime();
        // decompose problem into subproblem tasks; put tasks in space
        Task task = new TaskMandelbrotSet( LOWER_LEFT_X, LOWER_LEFT_Y, EDGE_LENGTH, N_PIXELS, ITERATION_LIMIT );
        space.put( task );
        
        // retrieve subproblem solutions; compose subporoblem solutions into to problem solution.
        
        Result<Integer[][]> result = space.take();
        Logger.getLogger( ClientMandelbrotSet.class.getCanonicalName() ).log(Level.INFO, "Task time: {0} ms.", result.getTaskRunTime() );
        client.add( client.getLabel( result.getTaskReturnValue() ) );
        long totalTime = System.nanoTime() - startTime;
        Logger.getLogger( ClientMandelbrotSet.class.getCanonicalName() ).log(Level.INFO, "Total client time: {0} ms.", totalTime / 1000000 );
    }
    
    @Override
    public JLabel getLabel( Integer[][] counts )
    {
        final Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();
        for ( int i = 0; i < counts.length; i++ )
            for ( int j = 0; j < counts.length; j++ )
            {
                graphics.setColor( getColor( counts[i][j] ) );
                graphics.fillRect( i, N_PIXELS - j, 1, 1 );
            }
        final ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }
    
    private Color getColor( int iterationCount )
    {
        return iterationCount == ITERATION_LIMIT ? Color.BLACK : Color.WHITE;
    }
}
