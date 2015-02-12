/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package applications.mandelbrotset;

/**
 *
 * @author Peter Cappello
 */
final public class ResultValueMandelbrotSet 
{
    final private Integer[][] counts;
    final private int blockRow;
    final private int blockCol;
    
    public ResultValueMandelbrotSet( Integer[][] counts, int blockRow, int blockCol)
    {
        this.counts  = counts;
        this.blockRow = blockRow;
        this.blockCol = blockCol;
    }
    
    public Integer[][] counts() { return counts; }
    public int blockRow() { return blockRow; }
    public int blockCol() { return blockCol; }
}
