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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 * An extension of JPanel that displays a given Environment object.
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class EnvironmentPanel extends JPanel {

    private Environment environment;
    private Image backBuffer;

    /**
     * Primary constructor to make a JPanel which displays enrivonment provided as the parameter
     *
     * @param e Environment to be displayed
     */
    public EnvironmentPanel(Environment e)
    {
	environment = e;

	Dimension size = new Dimension(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width, e.getWidth()),
		Math.min(Toolkit.getDefaultToolkit().getScreenSize().height, e.getHeight()));
	setSize(size);
	setPreferredSize(size);
	setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
	if (backBuffer == null)
	{
	    backBuffer = createImage(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width, getWidth()),
					Math.min(Toolkit.getDefaultToolkit().getScreenSize().height, getHeight()));
	    if (backBuffer == null)
	    {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		return;
	    }
	}
	Graphics gbb = backBuffer.getGraphics();

	gbb.setColor(Color.WHITE);
	gbb.fillRect(0, 0, getWidth(), getHeight());

	int cellWidth = environment.getCellWidth();
	int cellHeight = environment.getCellHeight();
	for (int i = 0; i < environment.getCellsWide(); i++)
	{
	    for (int j = 0; j < environment.getCellsHigh(); j++)
	    {
		Cell thisCell = environment.getCell(new CellReference(i, j));

		if (thisCell.isWestWallPresent())
		{
		    gbb.setColor(Color.BLACK);
		}
		else
		{
		    gbb.setColor(Color.LIGHT_GRAY);
		}
		gbb.drawLine(i * cellWidth, j * cellHeight, i * cellWidth, (j + 1) * cellHeight);
		
		if (thisCell.isNorthWallPresent())
		{
		    gbb.setColor(Color.BLACK);
		}
		else
		{
		    gbb.setColor(Color.LIGHT_GRAY);
		}
		gbb.drawLine(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, j * cellHeight);
	    }
	}

	g.drawImage(backBuffer, 0, 0, this);
    }
}
