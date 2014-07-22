/*
 * © 2011 by Matthew Crossley
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

package simzombie.engine.environment;

import java.io.Serializable;

/**
 * Environments are constructed of Cell objects
 *
 * Cells specifically contain data on whether or not their have boundaries
 * at two entry/exit points - the northern wall and western wall.
 * Since every cell is next to another cell with no gaps, it would be redundant
 * to have potential walls on each edge.
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Cell implements Serializable {

    /**
     * Sets whether the cell has a wall on the northern boundary or not.
     *
     * @param  b true indicates a wall is present, false indicates it is not
     */
    public void setNorthWall(boolean b)
    {
	northWall = b;
    }

    /**
     * Sets whether the cell has a wall on the western boundary or not.
     *
     * @param  b true indicates a wall is present, false indicates it is not
     */
    public void setWestWall(boolean b)
    {
	westWall = b;
    }

    /**
     * Type relates to the background information of each cell, i.e. the type of landscape it represents.
     *
     * This is not actually implemented in the simulation.
     */
    public enum Type
    {
        RESIDENTIAL,
        COMMERCIAL,
        INDUSTRIAL,
        NO_SPAWN;
    }

    /**
     * Whether or not this cell has a wall at the northern entrance/exit
     */
    private boolean northWall = false;

    /**
     * Whether or not this cell has a wall at the western entrance/exit
     */
    private boolean westWall = false;

    /**
     * The type of the cell.  This was intended to allow us to create maps
     * with a diverse landscape, each offering different properties in terms
     * of defensiveness, speed, etc.  This is not yet implemented.
     */
    private Type type;

    /**
     * Defaults to a Residential cell, with no walls at either boundary point
     */
    public Cell()
    {
        type = Type.RESIDENTIAL;
    }

    /**
     * Gets the type of the cell
     *
     * @return  A member of the Type enum corresponding to the type of cell
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Sets whether the cell has a wall on the northern boundary or not.
     *
     * @return  true if the cell is impassable at the northern entrance/exit
     */
    public boolean isNorthWallPresent()
    {
        return northWall;
    }

    /**
     * Sets whether the cell has a wall on the western boundary or not.
     *
     * @return  true if the cell is impassable at the western entrance/exit
     */
    public boolean isWestWallPresent()
    {
        return westWall;
    }
}
