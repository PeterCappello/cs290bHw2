/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package applications.euclideantsp;

import api.Result;
import java.util.List;

/**
 *
 * @author Peter Cappello
 */
public class ResultEuclideanTsp extends Result<List<Integer>>
{

    /**
     * Result container for TaskEuclideanTsp.
     * @param taskReturnValue
     * @param taskRunTime
     */
    public ResultEuclideanTsp( List<Integer> taskReturnValue, long taskRunTime) 
    {
        super(taskReturnValue, taskRunTime);
    } 
}
