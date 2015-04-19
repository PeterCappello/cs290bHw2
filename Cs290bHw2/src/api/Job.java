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
package api;

import java.rmi.RemoteException;
import java.util.List;
import javax.swing.JLabel;

/**
 *
 * @author Peter Cappello
 * @param <T>
 */
public interface Job<T> 
{

    /**
     * Decompose a problem into a List of Tasks.
     * @param space the Space to which the tasks are sent to be executed.
     * @return the List of generated Tasks
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    List<Task> decompose() throws RemoteException;
    
    /**
     * Take a result for each generated task, and composes these results into
     * a solution to the original problem.
     * This result is stored, so that its value can be obtained via the Job
     * value method.
     * (according to each implementation of this interface).
     * @param space the Space from which the results are taken.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    void compose( Space space ) throws RemoteException;
    
    /**
     * Returns the solution to the problem represented by the Job.
     * @return the value of the Job.
     */
    T value();
    
    /**
     * Display the solution as a JLabel.
     * @param returnValue the solution. (See value method above.)
     * @return the JLabel that contains some representation of the solution.
     */
    abstract JLabel viewResult( final T returnValue );
}
