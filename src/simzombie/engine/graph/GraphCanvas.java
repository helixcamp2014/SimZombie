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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/**
 * An extension of JPanel supplied to display a representation of a Graph object
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class GraphCanvas extends JPanel {

    /**
     * Toggles to display the Legend on the east and south of the graph
     */
    public enum LegendPosition
    {
        /**
         * Display the legend to the south of (below) the graph
         */
	SOUTH,
        /**
         * Display the legend to the east (right-hand side) of the graph
         */
	EAST;
    }

    /**
     * BackBuffer, provided for double buffering (for smooth animation of graphs)
     */
    private Image backBuffer;

    /**
     * The Graph object displayed by this GraphCanvas
     */
    private Graph graph;

    /**
     * Whether to display printer friendly options or not
     */
    private boolean printerFriendly = true;

    /**
     * Whether to display printer friendly options with hollow markers, or not
     */
    private boolean hollowNodules = false;

    /**
     * The current legend position (East or South)
     */
    private LegendPosition legendPosition = LegendPosition.EAST;

    /**
     * Constructs a GraphCanvas with a given Graph, width and height
     *
     * @param g Graph to display
     * @param width Desired width of the graph (in pixels)
     * @param height Desired height of the graph (in pixels)
     */
    public GraphCanvas(Graph g, int width, int height)
    {
        super();
        setSize(width, height);
        graph = g;
    }


    /**
     * Also creates image for the back buffer
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	backBuffer = createImage(getWidth(), getHeight());
    }

    /**
     * White space above the graph, in pixels
     */
    private int topPadding = 25;

    /**
     * White space to the right of the graph, in pixels
     */
    private int rightPadding = 15;

    /**
     * Amount of space to 'save' for the y axis
     */
    private int yAxisSpace = 40;

    /**
     * Amount of space to 'save' for the x axis
     */
    private int xAxisSpace = 20;

    /**
     * Desired height of the legend
     */
    private int legendHeight = 90;

    /**
     * Count of the desired number of measures on the y axis
     */
    private int yAxisNumbers = 5;

    /**
     * Count of the desired number of measures on the x axis
     */
    private int xAxisNumbers = 5;

    /**
     * An extra line is drawn at the position of pointer.
     * This allows us to highlight a step of the graph when replaying a simulation
     */
    private int pointer = 0;

    /**
     * Controls whether the extra line at pointer is drawn
     */
    private boolean displayPointer = false;

    @Override
    public void paintComponent(Graphics g)
    {
	if (backBuffer == null)
	{
	    g.setColor(Color.LIGHT_GRAY);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    return;
	}

	int verticalLegendSpace;
	int horizontalLegendSpace;
	int xEdgeAlteration = 0;
	int xPosAlteration = 0;

	yAxisSpace = getFontWidth(g, graph.getYMax() + "") + 12;
	if (printerFriendly)
	{
	    xEdgeAlteration = 20;
	    xPosAlteration = 10;
	}

	if (legendPosition == LegendPosition.EAST)
	{
	    horizontalLegendSpace = legendHeight - 15 + xEdgeAlteration;
	    verticalLegendSpace = 0;
	}
	else
	{
	    verticalLegendSpace = legendHeight;
	    horizontalLegendSpace = 0;
	}

	Graphics bgg = backBuffer.getGraphics();
	Graphics2D g2 = (Graphics2D)bgg;
	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	bgg.setColor(Color.WHITE);
        bgg.fillRect(0, 0, getWidth(), getHeight());

	double xGraphSpace = graph.getXMax() - graph.getXMin();
	double yGraphSpace = graph.getYMax() - graph.getYMin();

	if (displayPointer)
	{
	    double xPoint = pointer / xGraphSpace;
	    int xPos = (int)(xPoint * (getWidth() - yAxisSpace - rightPadding - horizontalLegendSpace));

	    bgg.setColor(Color.LIGHT_GRAY);
	    float distanceToDraw = Math.abs(getHeight() - xAxisSpace - verticalLegendSpace) / 30.0f;
	    for (int i = 0; i < 30; i ++)
	    {
		if (i % 2 == 0)
		{
		    bgg.drawLine(xPos + yAxisSpace, Math.round(i * distanceToDraw) + topPadding, 
			    xPos + yAxisSpace, Math.round((i + 1) * distanceToDraw) + topPadding);
		}
	    }

	    bgg.setColor(Color.DARK_GRAY);
	    bgg.drawLine(xPos + yAxisSpace, Math.round(30 * distanceToDraw) + 4,
		    xPos + 4 + yAxisSpace, Math.round(30 * distanceToDraw) + 8);
	    bgg.drawLine(xPos + yAxisSpace, Math.round(30 * distanceToDraw) + 4,
		    xPos - 4 + yAxisSpace, Math.round(30 * distanceToDraw) + 8);
	}

	bgg.setColor(Color.LIGHT_GRAY);
	bgg.drawLine(yAxisSpace, topPadding, yAxisSpace, getHeight() - xAxisSpace - verticalLegendSpace);
	bgg.drawLine(yAxisSpace, getHeight() - xAxisSpace - verticalLegendSpace, getWidth() - rightPadding - horizontalLegendSpace, getHeight() - xAxisSpace - verticalLegendSpace);
	bgg.drawLine(getWidth() - rightPadding - horizontalLegendSpace, getHeight() - xAxisSpace - verticalLegendSpace, getWidth() - rightPadding - horizontalLegendSpace, topPadding);

	double yNumberingInterval = ((getHeight() - xAxisSpace - verticalLegendSpace - topPadding) / (double)yAxisNumbers);
	for (int i = 0; i <= yAxisNumbers; i++)
	{
	    String number = "" + Math.round(i * (graph.getYMax() / (double)yAxisNumbers));
	    int y = (int) Math.round(-xAxisSpace - verticalLegendSpace + getHeight() - (yNumberingInterval * i) + getFontHeight(g, number)/2);
	    bgg.drawString(number, yAxisSpace - 10 - getFontWidth(g, number), y);
	    bgg.drawLine(yAxisSpace - 6, y - getFontHeight(g, number) / 2, yAxisSpace, y - getFontHeight(g, number) / 2);
	}

	double xNumberingInterval = ((getWidth() - yAxisSpace - rightPadding - horizontalLegendSpace) / (double)xAxisNumbers);
	for (int i = 0; i <= xAxisNumbers; i++)
	{
	    String number = "" + Math.round(i * (graph.getXMax() / (double)xAxisNumbers));
	    int x = (int) Math.round(yAxisSpace + (xNumberingInterval * i) - getFontWidth(g, number)/2);
	    bgg.drawString(number, x, getHeight() - 5 - verticalLegendSpace);

	    int pointx = (int)Math.round(yAxisSpace + (xNumberingInterval * i));
	    bgg.drawLine(pointx, getHeight() - verticalLegendSpace - xAxisSpace,
		    pointx, getHeight() - verticalLegendSpace - xAxisSpace + 3);
	}

	synchronized(graph.getPlots())
        {
	    for (Color c : graph.getPlots().keySet())
	    {
		synchronized(graph.getPlots().get(c))
		{
		    // we could use bgg.drawPolyline() here, but that means using non-Collection
		    // arrays and it's just generally a bit ugly, so we'll stick with what
		    // we've got
		    Point previousPlot = null;

		    Map<Color, List<Plot>> plots = graph.getPlots();

		    for (Plot p : plots.get(c))
		    {
			double xPoint = p.getX() / xGraphSpace;
			double yPoint = p.getY() / yGraphSpace;

			int xPos = (int)(xPoint * (getWidth() - yAxisSpace - rightPadding - horizontalLegendSpace));
			int yPos = getHeight() - (int)(yPoint * (getHeight() - xAxisSpace - verticalLegendSpace -  topPadding));

			bgg.setColor(p.getColor());
			if (previousPlot == null)
			{
			    bgg.drawLine(xPos + yAxisSpace - horizontalLegendSpace, yPos - xAxisSpace - verticalLegendSpace, xPos + yAxisSpace - horizontalLegendSpace, yPos - xAxisSpace - verticalLegendSpace);
			}
			else
			{
			    bgg.drawLine(previousPlot.x + yAxisSpace, previousPlot.y - xAxisSpace - verticalLegendSpace, xPos + yAxisSpace, yPos - xAxisSpace - verticalLegendSpace);
			}

			long drawInterval = Math.round(xGraphSpace / 10.0);
			if (printerFriendly && drawInterval > 1 && (p.getX() % drawInterval == 0))
			{
			    PrinterFriendly.draw(bgg, p.getColor(), graph.getPrinterFriendlyOptionForColor(p.getColor()), xPos + yAxisSpace, yPos - xAxisSpace - verticalLegendSpace, 8, hollowNodules);
			}

			previousPlot = new Point(xPos, yPos);
		    }
		}
	    }
        }

	if (legendPosition == LegendPosition.SOUTH)
	{
	    bgg.setColor(Color.LIGHT_GRAY);
	    int yOffset = 70;

	    for (String s : graph.getLegend().keySet())
	    {
		Color c = graph.getLegend().get(s);
		bgg.setColor(c);
		bgg.drawString(s, getWidth() / 2 - 35 + xPosAlteration, getHeight() - yOffset);

		if (printerFriendly)
		{
		    bgg.drawLine(getWidth() / 2 - 54 + xPosAlteration, getHeight() - yOffset - 5, getWidth() / 2 - 38 + xPosAlteration, getHeight() - yOffset - 5);
		    PrinterFriendly.draw(bgg, c, graph.getPrinterFriendlyOptionForColor(c),
			    getWidth() / 2 - 46 + xPosAlteration, getHeight() - yOffset - 5, 8, hollowNodules);
		}
		yOffset -= 15;
	    }

	    bgg.setColor(Color.LIGHT_GRAY);
	    bgg.drawRect(getWidth() / 2 - 40 - xPosAlteration, getHeight() - 50 - 35, 80 + xEdgeAlteration, graph.getCountOfDataSeries() * 16);
	}
	else
	{
	    bgg.setColor(Color.LIGHT_GRAY);
	    int yOffset = 0;

	    for (String s : graph.getLegend().keySet())
	    {
		Color c = graph.getLegend().get(s);
		bgg.setColor(c);
		bgg.drawString(s, getWidth() - horizontalLegendSpace + xPosAlteration, getHeight() / 2 - 25 - yOffset);

		if (printerFriendly)
		{
		    bgg.drawLine(getWidth() - horizontalLegendSpace - 3 + xPosAlteration, getHeight() / 2 - 30 - yOffset, getWidth() - horizontalLegendSpace - 3 + xPosAlteration - 15, getHeight() / 2 - 30 - yOffset);
		    PrinterFriendly.draw(bgg, c, graph.getPrinterFriendlyOptionForColor(c),
			    getWidth() - horizontalLegendSpace - 11 + xPosAlteration, getHeight() / 2 - 30 - yOffset, 8, hollowNodules);
		}
		yOffset -= 15;
	    }

	    bgg.setColor(Color.LIGHT_GRAY);
	    bgg.drawRect(getWidth() - horizontalLegendSpace - 10, getHeight() / 2 - 40, 80 + xEdgeAlteration, graph.getCountOfDataSeries() * 16);
	}

	bgg.setColor(Color.BLACK);
	bgg.drawString(graph.getTitle(), getWidth() / 2 - getFontWidth(bgg, graph.getTitle()) / 2, 15);

	g.drawImage(backBuffer, 0, 0, this);
    }

    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    /**
     * Returns the image from the back buffer, used for saving graphs
     * @return Image representation of the Graph
     */
    public Image getImage()
    {
	BufferedImage back = (BufferedImage) backBuffer;
	return back.getSubimage(0, 0, getWidth(), getHeight());
    }

    /**
     * Controls whether to display the pointer or not.
     *
     * The pointer is controlled using {@link #setPointer(int)}
     * @param displayPointer Display pointer or not
     */
    public void setDisplayPointer(boolean displayPointer) {
	this.displayPointer = displayPointer;
    }

    /**
     * Sets the step to display a pointer at, if {@link #setDisplayPointer(boolean)} is set to show the pointer
     * @param pointer Step at which the pointer should reside
     */
    public void setPointer(int pointer) {
	this.pointer = pointer;
    }

    /**
     * Retrieves whether the GraphCanvas is currently displaying printer friendly graphics or not
     * @return Boolean indicating whether printer friendly graphics are currently being displayed
     */
    public boolean isPrinterFriendly()
    {
	return printerFriendly;
    }

    /**
     * Sets whether the GraphCanvas should display printer friendly graphics or not
     * @param printerFriendly Boolean indicating whether printer friendly graphis should be displayed
     */
    public void setPrinterFriendly(boolean printerFriendly)
    {
	this.printerFriendly = printerFriendly;
    }

    /**
     * Retrieves the state of the boolean that controls the drawing of 'hollow nodules' on a printer friendly graph
     * @return True indicates nodules are being drawn, false indicates they are not
     */
    public boolean useHollowNodules()
    {
	return hollowNodules;
    }

    /**
     * Sets whether the GraphCanvas should display 'hollow nodules' when displaying printer friendly graphics.
     *
     * If printer friendly graphics are not currently being displayed, altering
     * the value of hollowNodules through this method will have no effect,
     * until printer friendly graphics are turned on.
     * @param hollowNodules True to display hollow nodules, false to not
     */
    public void setUseHollowNodules(boolean hollowNodules)
    {
	this.hollowNodules = hollowNodules;
    }

    /**
     * Controls the positioning of the legend
     * @param lp Acceptable values are South (below the graph) and East (to the right of the graph)
     */
    public void setLegendPosition(LegendPosition lp)
    {
	legendPosition = lp;
    }

    /**
     * Get the current position of the legend.  Legends default to East.
     * @return Current position of the legend
     */
    public LegendPosition getLegendPosition()
    {
	return legendPosition;
    }

    /**
     * Switches the legend position between East and South, depending on which it currently isn't set to
     */
    public void toggleLegendPosition()
    {
	if (legendPosition == LegendPosition.EAST)
	{
	    legendPosition = LegendPosition.SOUTH;
	}
	else
	{
	    legendPosition = LegendPosition.EAST;
	}
    }

    /**
     * Gets the width (in pixels) of the given String, as displayed in this GraphCanvas
     * @param g Graphics context
     * @param s String to determine width of
     * @return Width of string, as displayed, in pixels
     */
    private int getFontWidth(Graphics g, String s)
    {
	Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
	return (int) r.getWidth();
    }

    /**
     * Gets the height (in pixels) of the given String, as displayed in this GraphCanvas
     * @param g Graphics context
     * @param s String to determine height of
     * @return Height of string, as displayed, in pixels
     */
    private int getFontHeight(Graphics g, String s)
    {
	return g.getFontMetrics().getFont().getSize(); //.getHeight();
    }
}
