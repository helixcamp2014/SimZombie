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
import java.awt.Graphics;

/**
 * Class containing information on which printer friendly options are available for graphs and how to draw them
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class PrinterFriendly {

    /**
     * Potential printer friendly options for a data series
     */
    public enum PrinterFriendlyOption
    {
        /**
         * Draw circles for markers
         */
	CIRCLE,
        /**
         * Draw squares for markers
         */
	SQUARE,
        /**
         * Draw equilateral triangles for markers
         */
	TRIANGLE,
        /**
         * Draw 'upside down' equilateral triangles for markers
         */
	REVERSETRIANGLE,
        /**
         * Draws diamonds for markers
         */
	DIAMOND;
    }

    /**
     * Code for drawing each of the markers
     * @param g Graphics to draw to
     * @param c Colour to draw in
     * @param pfo Which printer friendly option to draw
     * @param x x position of the marker
     * @param y y position of the marker
     * @param size size of the marker
     * @param hollow If true, markers are drawn as outlines, if false, markers are drawn solid
     */
    public static void draw(Graphics g, Color c, PrinterFriendlyOption pfo, int x, int y, int size, boolean hollow)
    {
	g.setColor(c);
	if (pfo == PrinterFriendlyOption.CIRCLE)
	{
	    if (hollow)
	    {
		g.drawOval(x - size/2, y - size/2, size, size);
	    }
	    else
	    {
		g.fillOval(x - size/2, y - size/2, size, size);
	    }
	}
	else if (pfo == PrinterFriendlyOption.TRIANGLE)
	{
	    int halfSize = (int)Math.ceil(size/2.0) + 1;
	    int[] xPoints = { x, x - halfSize, x + halfSize };
	    int[] yPoints = { y - halfSize, y + halfSize, y + halfSize};

	    if (hollow)
	    {
		g.drawPolygon(xPoints, yPoints, 3);
	    }
	    else
	    {
		g.fillPolygon(xPoints, yPoints, 3);
	    }
	}
	else if (pfo == PrinterFriendlyOption.REVERSETRIANGLE)
	{
	    int halfSize = (int)Math.ceil(size/2.0) + 1;
	    int[] xPoints = { x, x + halfSize, x - halfSize };
	    int[] yPoints = { y + halfSize, y - halfSize, y - halfSize};

	    if (hollow)
	    {
		g.drawPolygon(xPoints, yPoints, 3);
	    }
	    else
	    {
		g.fillPolygon(xPoints, yPoints, 3);
	    }
	}
	else if (pfo == PrinterFriendlyOption.SQUARE)
	{
	    if (hollow)
	    {
		g.drawRect(x - size/2, y - size/2, size, size);
	    }
	    else
	    {
		g.fillRect(x - size/2, y - size/2, size, size);
	    }
	}
	else if (pfo == PrinterFriendlyOption.DIAMOND)
	{
	    int halfSize = (int)Math.ceil(size/2.0) + 1;
	    int[] xPoints = { x - halfSize, x, x + halfSize, x};
	    int[] yPoints = { y, y - halfSize, y, y + halfSize};

	    if (hollow)
	    {
		g.drawPolygon(xPoints, yPoints, 4);
	    }
	    else
	    {
		g.fillPolygon(xPoints, yPoints, 4);
	    }
	}
	else
	{
	    throw new UnsupportedOperationException("Invalid Printer Friendly Option");
	}
    }

}
