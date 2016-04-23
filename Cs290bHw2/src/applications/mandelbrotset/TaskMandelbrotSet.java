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
package applications.mandelbrotset;
import api.Task;
import util.Complex;

/**
 *
 * @author Peter Cappello
 */
public class TaskMandelbrotSet implements Task<ResultValueMandelbrotSet>
{
    final private double lowerLeftX;
    final private double lowerLeftY;
    final private double edgeLength;
    final private int numPixels;
    final private int iterationLimit;
    final private int blockRow;
    final private int blockCol;
            
    /**
     *
     * @param lowerLeftX the x coordinate of the lower left corner of a 
     * square region of the complex plane.
     * @param lowerLeftY the y coordinate of the lower left corner of a 
     * square region of the complex plane.
     * @param edgeLength the length of the square in the complex plane.
     * @param numPixels the square region of the complex plane is subdivided into
     *  numPixels X numPixels square subregions.
     * @param iterationLimit the limit of the Mandelbrot set recurrence:
     * Points whose size is less than or equal to 2 after this many iterations
     * of the recurrence are defined to be in the Mandelbrot set (this is
     * an approximation of the actual Mandelbrot set).
     */
    public TaskMandelbrotSet( double lowerLeftX, double lowerLeftY, double edgeLength, int numPixels, int iterationLimit, int blockRow, int blockCol )
    {
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLeftY;
        this.edgeLength = edgeLength;
        this.numPixels = numPixels;
        this.iterationLimit = iterationLimit;
        this.blockRow = blockRow;
        this.blockCol = blockCol;
    }
    
    /**
     * 
     * @return an Integer[row][col] array of iteration counts, where row and
     * col correspond to regions in the complex plane.
     */
    @Override
    public ResultValueMandelbrotSet call() 
    {
        final Integer[][] counts = new Integer[numPixels][numPixels];
        final double delta = edgeLength / numPixels;
        for ( int row = 0; row < numPixels; row++ )
            for ( int col = 0; col < numPixels; col++ )
            {
                counts[row][col] = getIterationCount( row, col, delta );
            }
        return new ResultValueMandelbrotSet( counts, blockRow, blockCol );
    }
    
    @Override
    public String toString()
    {
        return String.format( "%s \n\t x: %e \n\t y: %e \n\t length: %e \n\t pixels: %d \n\t iteration limit: %d \n\t blockRow: %d \n\t blockCol: %d\n", 
               getClass(), lowerLeftX, lowerLeftY, edgeLength, numPixels, iterationLimit, blockRow, blockCol );
    }
    
    private int getIterationCount( int row, int col, double delta )
    {
        return getIterationCount( new Complex( lowerLeftX + row * delta, lowerLeftY + col * delta ) );
    }
    
    private int getIterationCount( Complex c ) {
        int iteration = 0;
        for ( Complex z = new Complex( c ); z.sizeSquared() <= 4.0 && iteration < iterationLimit; iteration++ ) 
        {
            z.square().add( c );
        }
        return iteration;
    }
}
