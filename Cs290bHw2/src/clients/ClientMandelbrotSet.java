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
import api.Result;
import api.Space;
import api.Task;
import applications.mandelbrotset.ResultValueMandelbrotSet;
import applications.mandelbrotset.TaskMandelbrotSet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Peter Cappello
 */
public class ClientMandelbrotSet extends Client<Integer[][]>
{
    public static final double LOWER_LEFT_X = -0.7510975859375;
    public static final double LOWER_LEFT_Y = 0.1315680625;
    public static final double EDGE_LENGTH = 0.01611;
    public static final int N_PIXELS = 1024;
    public static final int ITERATION_LIMIT = 512;
    public static final int BLOCK_SIZE = 256;
    
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
        client.begin();
        Space space = client.getSpace( 2 );
        List<Task> tasks = client.decompose();
        for ( Task task : tasks ) { space.put( task ); }
        
        // compose solution from collected results. 
        Integer[][] counts = new Integer[N_PIXELS][N_PIXELS];
        for (Task task : tasks) 
        {
            final Result<ResultValueMandelbrotSet> result = ( Result<ResultValueMandelbrotSet> ) space.take();
            final ResultValueMandelbrotSet resultValue = result.getTaskReturnValue();
            
            // copy blockCounts into counts array
            Integer[][] blockCounts = resultValue.counts();
            int blockRow = resultValue.blockRow();
            int blockCol = resultValue.blockCol();
            for ( int row = 0; row < BLOCK_SIZE; row++ )
            {
                System.arraycopy( blockCounts[row], 0, counts[blockRow * BLOCK_SIZE + row], blockCol * BLOCK_SIZE, BLOCK_SIZE );
            }
            Logger.getLogger( ClientMandelbrotSet.class.getCanonicalName() ).log(Level.INFO, "Task time: {0} ms.", result.getTaskRunTime() );
        }
        
        // display solution
        client.add( client.getLabel( counts ) );
        client.end();
    }
    
    @Override
    List<Task> decompose() 
    {
        final List<Task> tasks = new LinkedList<>();
        final int numBlocks = N_PIXELS / BLOCK_SIZE;
        double edgeLength = EDGE_LENGTH / numBlocks;
        for ( int blockRow = 0; blockRow < numBlocks; blockRow++ )
        {
            for ( int blockCol = 0; blockCol < numBlocks; blockCol++ )
            {
                final double lowerLeftX = LOWER_LEFT_X + edgeLength * blockRow;
                final double lowerLeftY = LOWER_LEFT_Y + edgeLength * blockCol ;
                Task task = new TaskMandelbrotSet( lowerLeftX, lowerLeftY, edgeLength , BLOCK_SIZE, ITERATION_LIMIT, blockRow, blockCol );
                tasks.add( task );
            }
        }
        return tasks;
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
