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

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JInternalFrame;

/**
 * The GUI component for editing environments (adding/removing walls)
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class EnvironmentEditor extends JInternalFrame {

    /**
     * The environment this EnvironmentEditor is in charge of editing
     */
    private Environment environment;

    /**
     * This boolean is set to true while drawing walls.
     * This allows us to implement a 'click and drag' style drawing tool
     */
    private boolean makingWalls = false;

    /**
     * The current direction of drawing (if not horizontally, it must be vertically)
     */
    private boolean drawingHorizontally = false;

    /**
     * Constructs an extension of a JInternalFrame which implements behaviours for editing an Environment.
     *
     * The panel within allows for clicking and dragging to create walls in a straight line
     * along the nearest given edge.
     *
     * @param e Environment to be edited
     */
    public EnvironmentEditor(Environment e)
    {
	setClosable(true);
	environment = e;
	setLayout(new BorderLayout());
	final EnvironmentPanel ep = new EnvironmentPanel(environment);
	add(ep, BorderLayout.CENTER);

	ep.addMouseMotionListener(new MouseMotionListener(){

	    @Override
	    public void mouseDragged(MouseEvent e) {
		if (e.getX() > environment.getWidth() || e.getX() < 0) return;
		if (e.getY() > environment.getHeight() || e.getY() < 0) return;
		
		int cellx = e.getX() / environment.getCellWidth();
		int celly = e.getY() / environment.getCellHeight();

		int locWithinX = e.getX() % environment.getCellWidth();
		int locWithinY = e.getY() % environment.getCellHeight();

		int distanceToEastEdge = environment.getCellWidth() - locWithinX;
		int distanceToSouthEdge = environment.getCellHeight() - locWithinY;

		if (locWithinX <= locWithinY && locWithinX <= distanceToEastEdge && locWithinX <= distanceToSouthEdge)
		{
		    if (drawingHorizontally)
		    {
			CellReference cr = new CellReference(cellx, celly);
			Cell c = environment.getCell(cr);
			c.setWestWall(makingWalls);
		    }
		}
		if (locWithinY <= locWithinX && locWithinY <= distanceToEastEdge && locWithinY <= distanceToSouthEdge)
		{
		    if (!drawingHorizontally)
		    {
			CellReference cr = new CellReference(cellx, celly);
			Cell c = environment.getCell(cr);
			c.setNorthWall(makingWalls);
		    }
		}
		if (distanceToEastEdge <= locWithinX && distanceToEastEdge <= locWithinY && distanceToEastEdge <= distanceToSouthEdge)
		{
		    if (drawingHorizontally)
		    {
			CellReference cr = new CellReference(Math.min(cellx + 1, environment.getCellsWide() - 1), celly);
			Cell c = environment.getCell(cr);
			c.setWestWall(makingWalls);
		    }
		}
		if (distanceToSouthEdge <= locWithinX && distanceToSouthEdge <= locWithinY && distanceToSouthEdge <= distanceToEastEdge)
		{
		    if (!drawingHorizontally)
		    {
			CellReference cr = new CellReference(cellx, Math.min(celly + 1, environment.getCellsHigh() - 1));
			Cell c = environment.getCell(cr);
			c.setNorthWall(makingWalls);
		    }
		}

		ep.repaint();
	    }

	    @Override
	    public void mouseMoved(MouseEvent e) {
	    }
	});

	ep.addMouseListener(new MouseListener(){

	    @Override
	    public void mouseReleased(MouseEvent e)
	    {
		makingWalls = false;
	    }
	    
	    @Override
	    public void mousePressed(MouseEvent e)
	    {
		int cellx = e.getX() / environment.getCellWidth();
		int celly = e.getY() / environment.getCellHeight();

		int locWithinX = e.getX() % environment.getCellWidth();
		int locWithinY = e.getY() % environment.getCellHeight();
		
		int distanceToEastEdge = environment.getCellWidth() - locWithinX;
		int distanceToSouthEdge = environment.getCellHeight() - locWithinY;

		if (locWithinX <= locWithinY && locWithinX <= distanceToEastEdge && locWithinX <= distanceToSouthEdge)
		{
		    CellReference cr = new CellReference(cellx, celly);
		    Cell c = environment.getCell(cr);
		    if (c.isWestWallPresent())
		    {
			makingWalls = false;
		    }
		    else
		    {
			makingWalls = true;
		    }
		    c.setWestWall(makingWalls);
		    drawingHorizontally = true;
		}
		if (locWithinY <= locWithinX && locWithinY <= distanceToEastEdge && locWithinY <= distanceToSouthEdge)
		{
		    CellReference cr = new CellReference(cellx, celly);
		    Cell c = environment.getCell(cr);
		    if (c.isNorthWallPresent())
		    {
			makingWalls = false;
		    }
		    else
		    {
			makingWalls = true;
		    }
		    c.setNorthWall(makingWalls);
		    drawingHorizontally = false;
		}
		if (distanceToEastEdge <= locWithinX && distanceToEastEdge <= locWithinY && distanceToEastEdge <= distanceToSouthEdge)
		{
		    CellReference cr = new CellReference(cellx + 1, celly);
		    Cell c = environment.getCell(cr);
		    if (c.isWestWallPresent())
		    {
			makingWalls = false;
		    }
		    else
		    {
			makingWalls = true;
		    }
		    c.setWestWall(makingWalls);
		    drawingHorizontally = true;
		}
		if (distanceToSouthEdge <= locWithinX && distanceToSouthEdge <= locWithinY && distanceToSouthEdge <= distanceToEastEdge)
		{
		    CellReference cr = new CellReference(cellx, celly + 1);
		    Cell c = environment.getCell(cr);
		    if (c.isNorthWallPresent())
		    {
			makingWalls = false;
		    }
		    else
		    {
			makingWalls = true;
		    }
		    c.setNorthWall(makingWalls);
		    drawingHorizontally = false;
		}

		ep.repaint();
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {}

	    @Override
	    public void mouseEntered(MouseEvent e) {}

	    @Override
	    public void mouseExited(MouseEvent e) {}
	});
    }

    /**
     * Getter method for the environment being edited
     *
     * @return The current environment open for editing
     */
    public Environment getEnvironment()
    {
	return environment;
    }

}
