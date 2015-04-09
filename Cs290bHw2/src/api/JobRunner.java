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
package api;

import java.rmi.RemoteException;
import system.ComputerImpl;
import system.SpaceImpl;

/**
 *
 * @author Peter Cappello
 * @param <T> type if value returned by getValue.
 */
public class JobRunner<T> //extends Thread
{
    final private Job job;
    final private SpaceImpl space;
          private T value;
    
    public JobRunner( Job job, SpaceImpl space ) 
    { 
        this.job = job;
        this.space = space;
    }
    
    public JobRunner( Job job ) throws RemoteException 
    { 
        this.job = job;
        this.space = new SpaceImpl();
        for ( int i = 0; i < Runtime.getRuntime().availableProcessors(); i++ )
        {
            space.register( new ComputerImpl() );
        }
    }
    
//    @Override
    public void run()
    {
        try { space.putAll( job.decompose( space ) ); }
        catch ( RemoteException exception ) 
        { 
            exception.printStackTrace();
            System.exit( 1 );
        }
        try { job.compose( space ); }
        catch( RemoteException exception )
        { 
            exception.printStackTrace();
            System.exit( 1 );
        }
    }
    
    public T getValue() { return value; }
}
