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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import system.Computer;

/**
 *
 * @author Peter Cappello
 */
public interface Space extends Remote 
{

    /**
     * Port on which RMI registry is running for this service.
     */
    public static int PORT = 8001;

    /**
     * String name for service used in RMI registry.
     */
    public static String SERVICE_NAME = "Space";

    /**
     * Put a list of Tasks into the task queue.
     * @param taskList the list of Tasks.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    void putAll ( List<Task> taskList ) throws RemoteException;

    /**
     * Take (i.e., remove and return) a result from the result queue.
   
     * @return the removed result.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    Result take() throws RemoteException;

    /**
     *
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    void exit() throws RemoteException;
    
    /**
     *
     * @param computer a remote reference to a Computer that is requesting
     * participation as a worker.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    void register( Computer computer ) throws RemoteException;
}
