/*
 * The MIT License
 *
 * Copyright 2015 petercappello.
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
package applications.mandelbrotset;

import api.Job;
import api.JobRunner;
import api.Result;
import api.Space;
import api.Task;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Peter Cappello
 */
public class JobMandelbrotSet implements Job<Integer[][]>
{
    public static final double LOWER_LEFT_X = -0.7510975859375;
    public static final double LOWER_LEFT_Y = 0.1315680625;
    public static final double EDGE_LENGTH = 0.01611;
    public static final int N_PIXELS = 1024;
    public static final int ITERATION_LIMIT = 512;
    public static final int BLOCK_SIZE = 256;
          private List<Task> taskList;
          private Integer[][] counts;
    
    public JobMandelbrotSet() {}
    
    @Override
    public List<Task> decompose( Space space ) throws RemoteException
    {
        taskList = new LinkedList<>();
        final int numBlocks = N_PIXELS / BLOCK_SIZE;
        double edgeLength = EDGE_LENGTH / numBlocks;
        for ( int blockRow = 0; blockRow < numBlocks; blockRow++ )
        {
            for ( int blockCol = 0; blockCol < numBlocks; blockCol++ )
            {
                final double lowerLeftX = LOWER_LEFT_X + edgeLength * blockRow;
                final double lowerLeftY = LOWER_LEFT_Y + edgeLength * blockCol ;
                Task task = new TaskMandelbrotSet( lowerLeftX, lowerLeftY, edgeLength , BLOCK_SIZE, ITERATION_LIMIT, blockRow, blockCol );
                taskList.add( task );
            }
        }
        return taskList;
    }

    @Override
    public void compose( Space space ) throws RemoteException 
    {
        counts = new Integer[N_PIXELS][N_PIXELS];
        for ( Task task : taskList ) 
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
            Logger.getLogger(this.getClass().getCanonicalName() ).log(Level.INFO, "Task time: {0} ms.", result.getTaskRunTime() );
        }
    }

    @Override
    public Integer[][] value() { return counts; }

    @Override
    public JLabel viewResult( Integer[][] counts ) 
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
    
    public static void main( String[] args ) throws Exception
    {
        final JobMandelbrotSet job = new JobMandelbrotSet();
        final JobRunner jobRunner = new JobRunner( job, "Mandelbrot Set Visualizer", "" );
        jobRunner.run();
    }
}
