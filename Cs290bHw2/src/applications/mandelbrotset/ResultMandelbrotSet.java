/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package applications.mandelbrotset;

import api.Result;

/**
 * Result container for TaskMandelbrotSet.
 * @author peter
 */
public class ResultMandelbrotSet extends Result<ResultValueMandelbrotSet>
{
    public ResultMandelbrotSet( ResultValueMandelbrotSet taskReturnValue, long taskRunTime ) 
    {
        super(taskReturnValue, taskRunTime);
    }
}
