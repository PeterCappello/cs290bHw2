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
 * FITNESS FOR ONE PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package applications.euclideantsp;

import api.Task;
import static clients.ClientEuclideanTsp.CITIES;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import util.PermutationEnumerator;

/**
 * Find a tour of minimum cost among those that start with city 0, 
 * followed by city secondCity.
 * @author Peter Cappello
 */
public class TaskEuclideanTsp implements Task<List<Integer>>
{ 
    static final public double[][] DISTANCES = initializeDistances();
    final static Integer ONE = 1;
    final static Integer TWO = 2;
    
    final private int secondCity;
    final private List<Integer> partialCityList;
//    final private double cost;
        
    public TaskEuclideanTsp( int secondCity, List<Integer> partialCityList )
    {
        this.secondCity = secondCity;
        this.partialCityList = partialCityList;
    }
    
    /**
     * Compute a tour of minimum cost.
     * @return a tour of minimum cost.
     */
    @Override
    public List<Integer> call() 
    {
        // initial value for shortestTour and its distance.
        List<Integer> shortestTour = addPrefix( new LinkedList<>( partialCityList ) );
        double shortestTourDistance = tourDistance( shortestTour );
        
        // Use my permutation enumerator
        PermutationEnumerator<Integer> permutationEnumerator = new PermutationEnumerator<>( partialCityList );
        for ( List<Integer> subtour = permutationEnumerator.next(); subtour != null; subtour = permutationEnumerator.next() ) 
        {
            List<Integer> tour = new ArrayList<>( subtour );
            addPrefix( tour );
            if ( tour.indexOf( ONE ) >  tour.indexOf( TWO ) )
            {
                continue; // skip tour; it is the reverse of another.
            }
            double tourDistance = tourDistance( tour );
            if ( tourDistance < shortestTourDistance )
            {
                shortestTour = tour;
                shortestTourDistance = tourDistance;
            }
        }
        return shortestTour;
    }
    
    private List<Integer> addPrefix( List<Integer> partialTour )
    {
        partialTour.add( 0, secondCity );
        partialTour.add( 0, 0 );
        return partialTour;
    }
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( getClass() );
        stringBuilder.append( "\n\tCities: " );
        stringBuilder.append( 0 ).append( " " );
        stringBuilder.append( secondCity ).append( " " );
        partialCityList.stream().forEach(( city ) -> 
        {
            stringBuilder.append( city ).append( " " );
        } );
        return stringBuilder.toString();
    }
    
    /**
     *
     * @param tour
     * @return
     */
    static public double tourDistance( final List<Integer> tour  )
    {
        double cost = DISTANCES[ tour.get( tour.size() - 1 ) ][ tour.get( 0 ) ];
        for ( int city = 0; city < tour.size() - 1; city ++ )
        {
            cost += DISTANCES[ tour.get( city ) ][ tour.get( city + 1 ) ];
        }
        return cost;
    }
    
    static private double[][] initializeDistances()
    {
        double[][] distances = new double[ CITIES.length][ CITIES.length];
        for ( int i = 0; i < CITIES.length; i++ )
        for ( int j = 0; j < i; j++ )
        {
            distances[ i ][ j ] = distances[ j ][ i ] = distance( CITIES[ i ], CITIES[ j ] );
        }
       return distances;
    }
    
    private static double distance( final double[] city1, final double[] city2 )
    {
        final double deltaX = city1[ 0 ] - city2[ 0 ];
        final double deltaY = city1[ 1 ] - city2[ 1 ];
        return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
    }
}
