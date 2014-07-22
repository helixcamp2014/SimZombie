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

package simzombie.engine.graph;

import java.awt.Color;
import java.util.Comparator;

/**
 * Contains basic information about a plot (data series, x and y locations, colour)
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Plot {

    /**
     * Compares a plot based on the X axis alone
     *
     * This allows us to organise our plots from left to right, for display purposes
     */
    public static class PlotComparator implements Comparator
    {

	public int compare(Object o1, Object o2) {
	    Plot p1 = (Plot) o1;
	    Plot p2 = (Plot) o2;
	    return (p2.getX() - p1.getX());
	}

    }

    /**
     * Name of the data series this plot belongs to
     */
    private String name;

    /**
     * X location of the plot
     */
    private int xaxis;

    /**
     * Y location of the plot
     */
    private int yaxis;

    /**
     * Color to use to display this plot.
     *
     * This is out of place given object orientation and may be refactored later
     */
    private Color color;

    /**
     * Constructs a plot with a given name, x location, y location and colour
     *
     * @param name Name of the data series this plot point belongs to
     * @param xaxis Position of this plot on the x axis
     * @param yaxis Position of this plot on the y axis
     * @param color Colour of this plot
     */
    public Plot(String name, int xaxis, int yaxis, Color color)
    {
	this.name = name;
        this.xaxis = xaxis;
        this.yaxis = yaxis;
        this.color = color;
    }

    /**
     * Accessor for the x location of this plot
     * @return x location of this plot
     */
    public int getX()
    {
        return xaxis;
    }

    /**
     * Transformer for the x location of this plot
     * @param x Desired x location for this plot
     */
    public void setX(int x)
    {
        xaxis = x;
    }

    /**
     * Accessor for the y location of this plot
     * @return Desired y location for this plot
     */
    public int getY()
    {
        return yaxis;
    }

    /**
     * Accessor method for this plot's colour
     * @return Color of this plot
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Accessor for the name of the data series this plot belongs to
     * @return The name of the data series this plot belongs to
     */
    public String getName()
    {
	return name;
    }
}
