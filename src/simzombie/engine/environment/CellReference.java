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

import simzombie.engine.utils.Location;
import java.io.Serializable;

/**
 * CellReference is a utility class used for referring to a specific cell within the environment grid.
 *
 * CellReferences hold no information about the actual pixel locations of the cells, rather they
 * are concerned with the relative position of the cell within the grid.
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class CellReference implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * A number corresponding to the location of the cell within the environment
     * grid, specifically referring to the column of the grid
     */
    private int xcell;
    /**
     * A number corresponding to the location of the cell within the environment
     * grid, specifically referring to the row of the grid
     */
    private int ycell;

    /**
     * Creates a CellReference, which converts pixel information from a location
     * into a grid-point reference within the environment
     *
     * @param l Location information of the cell, in pixels
     * @param cellWidth How wide each cell in this environment is
     * @param cellHeight How high each cell in this environment is
     */
    public CellReference(Location l, int cellWidth, int cellHeight)
    {
        xcell = l.getX() / cellWidth;
        ycell = l.getY() / cellHeight;
    }

    /**
     * Constructs a CellReference given coordinates already converted from
     * pixel into grid format
     *
     * @param x The column of the grid this CellReference refers to
     * @param y The row of the grid this CellReference refers to
     */
    public CellReference(int x, int y)
    {
        xcell = x;
        ycell = y;
    }

    /**
     * Gets the X coordinate of the environment grid this CellReference refers to
     * @return The column in the environment grid this CellReference refers to
     */
    public int getX()
    {
        return xcell;
    }

    /**
     * Gets the Y coordinate of the environment grid this CellReference refers to
     * @return The row in the environment grid this CellReference refers to
     */
    public int getY()
    {
        return ycell;
    }

    /**
     *
     * @param obj Object to compare
     * @return true if obj is a CellReference with matching x and y values
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellReference other = (CellReference) obj;
        if (this.xcell != other.xcell) {
            return false;
        }
        if (this.ycell != other.ycell) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7 * xcell;
        hash += 13 * ycell;
        return hash;
    }

    public String toString()
    {
        return "Cell(" + xcell + ", " + ycell + ")";
    }
}
