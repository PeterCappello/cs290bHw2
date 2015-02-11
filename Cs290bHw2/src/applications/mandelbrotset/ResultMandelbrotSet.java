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
public class ResultMandelbrotSet extends Result<Integer[][]>
{
    public ResultMandelbrotSet( Integer[][] taskReturnValue, long taskRunTime ) 
    {
        super(taskReturnValue, taskRunTime);
    }
}
