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
import api.Result;
import api.Space;
import api.Task;
//import clients.OldClientEuclideanTsp;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Peter Cappello
 */
public class JobEuclideanTsp implements Job<List<Integer>>
{
//    private final double[][] cities;
    private List<Task> taskList;
    private List<Integer> tour;
    
//    public JobEuclideanTsp( double[][] cities ) { this.cities = cities; }
    public JobEuclideanTsp() {}
    
    @Override
    public List<Task> decompose( Space space ) throws RemoteException
    {
        taskList = new LinkedList<>();
        final List<Integer> integerList = new LinkedList<>();
//        for ( int i = 1; i < cities.length; i++ )
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
//            Logger.getLogger(this.getClass().getCanonicalName() ).log(Level.INFO, "Task time: {0} ms.", result.getTaskRunTime() );
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
}
