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

package simzombie.engine;

import simzombie.engine.environment.Cell;
import simzombie.engine.environment.CellReference;
import simzombie.engine.environment.Environment;
import simzombie.engine.simulations.zombies.ZombieParameters;
import simzombie.engine.utils.Icons;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.JPanel;

/**
 * GUI component that displays a simulation
 *
 * Is back buffered for smooth animation, but requires a call to {@link #repaint()}
 * to update after the simulation has been updated
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class SimCanvas extends JPanel
{
    private Image backBuffer;

    private Image sunIcon = Icons.getImage(Icons.sunIconLocation);
    private Image bellIcon = Icons.getImage(Icons.bellIconLocation);
    private Image[] moonIcons = new Image[28];
    private Environment environment;
    private Parameters parameters;
    private Map<AgentType, Color> typeToColour = Collections.synchronizedMap(new HashMap<AgentType, Color> ());

    public SimCanvas(Parameters p, AgentType[] agentTypes)
    {
        environment = p.getEnvironment();
        parameters = p;
        
        List<Color> potentialColors = new ArrayList<Color> ();
	potentialColors.add(Color.GREEN);
	potentialColors.add(Color.ORANGE);
	potentialColors.add(Color.RED);
	potentialColors.add(Color.LIGHT_GRAY);
	ListIterator<Color> colors = potentialColors.listIterator(0);

	for (AgentType t : agentTypes)
	{
	    typeToColour.put(t, colors.next());
	}

        Dimension size = new Dimension(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width, parameters.getEnvironmentWidth()),
                Math.min(Toolkit.getDefaultToolkit().getScreenSize().height, parameters.getEnvironmentHeight()));
        setSize(size);
        setPreferredSize(size);
        setBackground(Color.WHITE);
        for (int i = 0; i < 28; i++)
        {
            moonIcons[i] = Icons.getImage(Icons.lunarPhaseLocations[i]);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        backBuffer = createImage(getWidth(), getHeight());
    }

    public void setBackBuffer(Image image)
    {
        backBuffer = image;
    }

    public Image getImage()
    {
        return backBuffer;
    }

    @Override
    public void paintComponent(Graphics g)
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
        Graphics2D g2 = (Graphics2D)gbb;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        gbb.setColor(Color.WHITE);
        gbb.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < parameters.getCellsWide(); i++)
        {
            for (int j = 0; j < parameters.getCellsHigh(); j++)
            {
                Cell thisCell = environment.getCell(new CellReference(i, j));

                // recolours a cell when an agent enters or leaves
                // for testing purposes
//		    if (thisCell.numberOfAgentsEverChanged)
//		    {
//			gbb.setColor(Color.WHITE);
//			gbb.fillRect(i * parameters.getCellWidth(), j * parameters.getCellHeight(), parameters.getCellWidth(), parameters.getCellHeight());
//			gbb.setColor(Color.LIGHT_GRAY);
//		    }

                gbb.setColor(Color.BLACK);
                if (thisCell.isWestWallPresent())
                {
                    gbb.drawLine(i * parameters.getCellWidth(), j * parameters.getCellHeight(), i * parameters.getCellWidth(), (j + 1) * parameters.getCellHeight());
                }
                if (thisCell.isNorthWallPresent())
                {
                    gbb.drawLine(i * parameters.getCellWidth(), j * parameters.getCellHeight(), (i + 1) * parameters.getCellWidth(), j * parameters.getCellHeight());
                }

                  // Draws the number of agents in each cell, for testing purposes
//                    int numberOfAgents = thisCell.getNumberOfAgentsPresent();
//                    gbb.drawString(numberOfAgents + "", i * parameters.getCellWidth() + (parameters.getCellWidth() / 2),
//                            j * parameters.getCellHeight() + (parameters.getCellHeight() / 2));
            }
        }

        synchronized(environment.getAgents())
        {
            for (Agent a : environment.getAgents())
            {
//		    gbb.drawLine(a.getLocation().getX() - 2, a.getLocation().getY() - 2,
//			    a.getLocation().getX() + a.getdx() - 2, a.getLocation().getY() + a.getdy() - 2);

                gbb.setColor(typeToColour.get(a.getType()));
                gbb.fillOval(a.getLocation().getX() - 2, a.getLocation().getY() - 2, 4, 4);
                gbb.setColor(Color.BLACK);
                gbb.drawOval(a.getLocation().getX() - 2, a.getLocation().getY() - 2, 4, 4);
            }
        }

        gbb.setColor(Color.BLACK);
        gbb.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (parameters.getTimeOfDay() == Parameters.TimeOfDay.DAY)
        {
            gbb.drawImage(sunIcon, 8, 8, this);
        }
        else
        {
            gbb.drawImage(moonIcons[parameters.getCurrentLunarPhase()], 8, 8, this);
        }

        if (parameters instanceof ZombieParameters)
        {
            ZombieParameters zp = (ZombieParameters)parameters;
            if (zp.isAwarenessRaised())
            {
                gbb.drawImage(bellIcon, getWidth() - bellIcon.getWidth(this) - 8, 8, this);
            }
        }

        g.drawImage(backBuffer, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
}
