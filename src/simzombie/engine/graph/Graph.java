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

import simzombie.engine.graph.PrinterFriendly.PrinterFriendlyOption;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Graph class contains a series of data points.
 *
 * These data points can be plotted using the {@link GraphCanvas} class
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Graph {

    /**
     * Current smallest x value this graph contains
     */
    private int xmin;

    /**
     * Current largest x value this graph contains
     */
    private int xmax;

    /**
     * Current smallest y value this graph contains
     */
    private int ymin;

    /**
     * Current largest y value this graph contains
     */
    private int ymax;

    /**
     * Initial smallest x value, allowing graphs to be reset
     */
    private final int originalxmin;

    /**
     * Initial largest x value, allowing graphs to be reset
     */
    private final int originalxmax;

    /**
     * Initial smallest y value, allowing graphs to be reset
     */
    private final int originalymin;

    /**
     * Initial largest y value, allowing graphs to be reset
     */
    private final int originalymax;

    /**
     * The title of the graph
     */
    private String title = "";

    /**
     * Maps a series of plots to a given title (thus providing legend functionality)
     */
    private final Map<String, Color> legend = new HashMap<String, Color>();

    /**
     * Maps desired colour to a list of plots.
     * 
     * This is technically a GUI thing, and doesn't really belong on this class. May be refactored later.
     */
    private final Map<Color, List<Plot>> plots = Collections.synchronizedMap(new HashMap<Color, List<Plot>>());

    /**
     * Maps colours to printer friendly variants.
     *
     * This is technically a GUI thing, and doesn't really belong on this class. May be refactored later.
     */
    private Map<Color, PrinterFriendlyOption> colorsToOptions = new HashMap<Color, PrinterFriendlyOption>();

    /**
     * Constructs a Graph object, with initial boundaries as provided by the parameters.
     *
     * The boundaries are adjusted automatically, so these are not hard and fast rules,
     * they're merely provided so that graphs do not look awkward when first displayed with 
     * no data points.  An implementation that allows control over the min and max boundaries
     * may be useful, but at this point it's probably easier to export the data and use a 
     * third-party graph package.
     *
     * @param xmin Smallest initial x value the graph may hold
     * @param xmax Largest initial x value the graph may hold
     * @param ymin Smallest initial y value the graph may hold
     * @param ymax Largest initial y value the graph may hold
     */
    public Graph(int xmin, int xmax, int ymin, int ymax)
    {
        originalxmin = xmin;
        originalxmax = xmax;
        originalymin = ymin;
        originalymax = ymax;
        this.xmin = originalxmin;
        this.xmax = originalxmax;
        this.ymin = originalymin;
        this.ymax = originalymax;
    }

    /**
     * Adds a plot to the current graph.
     *
     * Legend is filled in based on the getName() method of the given Plot, and similarly
     * a colour is assigned based on that.  Graph boundaries are also updated as appropriate.
     *
     * @param p The plot to add to the Graph.
     */
    public void addPlot(Plot p)
    {
        synchronized(plots)
        {
	    if (!plots.keySet().contains(p.getColor()))
	    {
		legend.put(p.getName(), p.getColor());
		plots.put(p.getColor(), Collections.synchronizedList(new ArrayList<Plot>()));
		if (colorsToOptions.values().isEmpty())
		{
		    colorsToOptions.put(p.getColor(), PrinterFriendlyOption.CIRCLE);
		}
		else
		{
		    boolean placed = false;
		    for (PrinterFriendlyOption pfo : PrinterFriendlyOption.values())
		    {
			if (!placed)
			{
			    if (!colorsToOptions.values().contains(pfo))
			    {
				colorsToOptions.put(p.getColor(), pfo);
				placed = true;
			    }
			}
		    }
		    if (!placed)
		    {
			colorsToOptions.put(p.getColor(), PrinterFriendlyOption.CIRCLE);
		    }
		}
	    }
	    synchronized(plots.get(p.getColor()))
	    {
		List<Plot> thisList = plots.get(p.getColor());
		thisList.add(p);
		Collections.sort(thisList, new Plot.PlotComparator());
	    }
        }

	xmax = Math.max(p.getX(), xmax);
	xmin = Math.min(p.getX(), xmin);

	ymax = Math.max(p.getY(), ymax);
	ymin = Math.min(p.getY(), ymin);
    }

    /**
     * Accessor for the plots, indexable by colour
     *
     * @return All plots from this graph
     */
    public Map<Color, List<Plot>> getPlots()
    {
        return plots;
    }

    /**
     * Returns the current graph x maximum
     * @return The current graph x maximum
     */
    public int getXMax()
    {
	return xmax;
    }

    /**
     * Returns the current graph x minimum
     * @return The current graph x minimum
     */
    public int getXMin()
    {
	return xmin;
    }

    /**
     * Returns the current graph y maximum
     * @return The current graph y maximum
     */
    public int getYMax()
    {
	return ymax;
    }

    /**
     * Returns the current graph y minimum
     * @return The current graph y minimum
     */
    public int getYMin()
    {
	return ymin;
    }

    /**
     * Returns the Legend of the graph, mapping Strings onto Colors
     * @return Map of Legend information, mapping Strings to Color
     */
    public Map<String, Color> getLegend()
    {
	return legend;
    }

    /**
     * Converts a Color into a PrinterFriendlyOption, for display purposes
     * @param c The requested Color
     * @return A PrinterFriendlyOption that is synonymous with the supplied Color
     */
    public PrinterFriendlyOption getPrinterFriendlyOptionForColor(Color c)
    {
	return colorsToOptions.get(c);
    }

    /**
     * Accessor method for the title of the graph
     * @return The title of the graph
     */
    public String getTitle()
    {
	return title;
    }

    /**
     * Transformer method for the title of the graph
     * @param title The new title of the graph
     */
    public void setTitle(String title)
    {
	this.title = title;
    }

    /**
     * Returns the number of different data series in the graph
     * @return Number of different data series in the graph
     */
    public int getCountOfDataSeries()
    {
	return plots.keySet().size();
    }

    /**
     * Resets a graph to its initial state - no plots and boundaries as supplied to the constructor
     */
    public void reset() 
    {
        plots.clear();
        xmin = originalxmin;
        xmax = originalxmax;
        ymin = originalymin;
        ymax = originalymax;

    }
}
