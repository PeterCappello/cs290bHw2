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
package applications.euclideantsp;

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
public class JobEuclideanTsp implements Job<List<Integer>>
{
    static final private int NUM_PIXALS = 600;
    static final public  double[][] CITIES = TaskEuclideanTsp.CITIES;
    
    private List<Task> taskList;
    private List<Integer> tour;
    
    public JobEuclideanTsp() {}
    
    @Override
    public List<Task> decompose( Space space ) throws RemoteException
    {
        taskList = new LinkedList<>();
        final List<Integer> integerList = new LinkedList<>();
        for ( int i = 1; i < TaskEuclideanTsp.CITIES.length; i++ )
        {
            integerList.add( i );
        }
        for ( int i = 0; i < integerList.size(); i++ )
        {
            final List<Integer> partialList = new LinkedList<>( integerList );
            partialList.remove( i );
            final Task task = new TaskEuclideanTsp( i + 1, partialList );
            taskList.add( task );
        }
        return taskList;
    }

    @Override
    public void compose( Space space ) throws RemoteException 
    {
        tour = new LinkedList<>();
        double shortestTourDistance = Double.MAX_VALUE;
        for ( Task task : taskList ) 
        {
            Result<List<Integer>> result = space.take();
            Logger.getLogger(this.getClass().getCanonicalName() ).log(Level.INFO, "Task time: {0} ms.", result.getTaskRunTime() );
            double tourDistance = TaskEuclideanTsp.tourDistance( result.getTaskReturnValue() );
            if ( tourDistance < shortestTourDistance )
            {
                tour = result.getTaskReturnValue();
                shortestTourDistance = tourDistance;
            }
        }
    }

    @Override
    public List<Integer> getValue() { return tour; }

    @Override
    public JLabel view( List<Integer> cityList ) 
    {
        Logger.getLogger( this.getClass().getCanonicalName() ).log(Level.INFO, "Tour: {0}", cityList.toString());
        Integer[] tour = cityList.toArray( new Integer[0] );

        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = CITIES[0][0], maxX = CITIES[0][0];
        double minY = CITIES[0][1], maxY = CITIES[0][1];
        for ( double[] cities : CITIES ) 
        {
            if ( cities[0] < minX ) 
                minX = cities[0];
            if ( cities[0] > maxX ) 
                maxX = cities[0];
            if ( cities[1] < minY ) 
                minY = cities[1];
            if ( cities[1] > maxY ) 
                maxY = cities[1];
        }

        // scale points to fit in unit square
        final double side = Math.max( maxX - minX, maxY - minY );
        double[][] scaledCities = new double[CITIES.length][2];
        for ( int i = 0; i < CITIES.length; i++ )
        {
            scaledCities[i][0] = ( CITIES[i][0] - minX ) / side;
            scaledCities[i][1] = ( CITIES[i][1] - minY ) / side;
        }

        final Image image = new BufferedImage( NUM_PIXALS, NUM_PIXALS, BufferedImage.TYPE_INT_ARGB );
        final Graphics graphics = image.getGraphics();

        final int margin = 10;
        final int field = NUM_PIXALS - 2*margin;
        // draw edges
        graphics.setColor( Color.BLUE );
        int x1, y1, x2, y2;
        int city1 = tour[0], city2;
        x1 = margin + (int) ( scaledCities[city1][0]*field );
        y1 = margin + (int) ( scaledCities[city1][1]*field );
        for ( int i = 1; i < CITIES.length; i++ )
        {
            city2 = tour[i];
            x2 = margin + (int) ( scaledCities[city2][0]*field );
            y2 = margin + (int) ( scaledCities[city2][1]*field );
            graphics.drawLine( x1, y1, x2, y2 );
            x1 = x2;
            y1 = y2;
        }
        city2 = tour[0];
        x2 = margin + (int) ( scaledCities[city2][0]*field );
        y2 = margin + (int) ( scaledCities[city2][1]*field );
        graphics.drawLine( x1, y1, x2, y2 );

        // draw vertices
        final int VERTEX_DIAMETER = 6;
        graphics.setColor( Color.RED );
        for ( int i = 0; i < CITIES.length; i++ )
        {
            int x = margin + (int) ( scaledCities[i][0]*field );
            int y = margin + (int) ( scaledCities[i][1]*field );
            graphics.fillOval( x - VERTEX_DIAMETER/2,
                               y - VERTEX_DIAMETER/2,
                              VERTEX_DIAMETER, VERTEX_DIAMETER);
        }
        final ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }
    
    public static void main( String[] args ) throws Exception
    {
        final JobEuclideanTsp job = new JobEuclideanTsp();
        final JobRunner jobRunner = new JobRunner( job, "Euclidean TSP", "" );
        jobRunner.run();
    }
}
