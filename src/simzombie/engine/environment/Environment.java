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
import simzombie.engine.Agent;
import simzombie.engine.AgentType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the environment of the simulation, including which agents are present in particular cells.
 *
 * The Environment class also manages whether agents are 'able' to move from one cell to another,
 * which is established using the edges of the simulation and the walls of the given cells.
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Environment implements Serializable {

    /**
     * A set containing all the agents, regardless of which cell they are in
     */
    private final Set<Agent> agents = Collections.synchronizedSet(new HashSet<Agent>());
    /**
     * A Map that stores all the agents in a given cell, referred to using a CellReference
     */
    private final Map<CellReference, Set<Agent>> agentsByCell = new HashMap<CellReference, Set<Agent>>();
    /**
     * A Map that stores all the cells in the environment, accessible using a CellReference
     */
    private final Map<CellReference, Cell> cells = Collections.synchronizedMap(new HashMap<CellReference, Cell>());

    /**
     * The width of each cell, in pixels
     */
    private int cellWidth = 25;
    /**
     * The height of each cell, in pixels
     */
    private int cellHeight = 25;
    /**
     * How many cells wide the environment is
     */
    private int cellsWide = 20;
    /**
     * How many cells high the environment is
     */
    private int cellsHigh = 20;

    /**
     * Utility constructor which creates a default Environment
     */
    public Environment()
    {
	this(null);
    }

    /**
     * Constructs an environment with a specific set of cells
     * @param givenCells Cells to populate the environment with.  If null, new cells are created.
     */
    public Environment(Map<CellReference, Cell> givenCells)
    {
	if (givenCells == null)
	{
	    for (int i = 0; i < cellsWide; i++)
	    {
		for (int j = 0; j < cellsHigh; j++)
		{
		    CellReference cr = new CellReference(i, j);
		    synchronized(cells)
		    {
			cells.put(cr, new Cell());
			agentsByCell.put(cr, new HashSet<Agent>());
		    }
		}
	    }
	}
	else
	{
	    for (CellReference cr : givenCells.keySet())
	    {
		cells.put(cr, givenCells.get(cr));
		agentsByCell.put(cr, new HashSet<Agent>());
	    }
	}
    }

    /**
     * Gets all agents within the environment
     * @return An iterable set of all agents in the environment
     */
    public Set<Agent> getAgents()
    {
	return agents;
    }

    /**
     * Gets a specific cell given a cell reference
     * @param cr CellReference referring to the desired cell
     * @return Cell given the CellReference.  Will return null if an invalid CellReference is given
     */
    public Cell getCell(CellReference cr)
    {
        synchronized(cells)
        {
            return cells.get(cr);
        }
    }

    // this method takes adjacent cells and works out which edge
    // is being transgressed and verifies the validity of this move
    /**
     * Takes adjacent cells and calculates which edge is being transgressed and verifies the validity of the move.
     *
     * Validity takes into account the boundaries of the environment and also any walls in both the from and to cells
     * If the move is valid, the agent is moved.  If not, the agents trajectory is adjusted and then moved.
     * 
     * @param a The agent moving
     * @param from Indicates which cell the Agent is currently in
     * @param to Indicates which cell the Agent is moving to
     */
    private void moveAgent(Agent a, CellReference from, CellReference to)
    {
	if (from.equals(to))
        {
            a.adjustLocationByDxDy();
            return;
        }

        Cell cellFrom = getCell(from);
        Cell cellTo = getCell(to);

        // transgression must be vertical
        if (from.getX() == to.getX())
        {
	    if (cellTo == null)
	    {
		a.reversedy();
		a.adjustLocationByDxDy();
		return;
	    }
            if (from.getY() > to.getY())
            {
                // we're moving into the cell above
                if (!cellFrom.isNorthWallPresent())
                {
                    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
                }
                else
                {
                    a.reversedy();
                    a.adjustLocationByDxDy();
                }
            }
            else
            {
                // we're moving into the cell below
                if (!cellTo.isNorthWallPresent())
                {
                    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
                }
                else
                {
                    a.reversedy();
                    a.adjustLocationByDxDy();
                }
            }
        }
        // trangression must be horizontal
        else if (from.getY() == to.getY())
        {
	    if (cellTo == null)
	    {
		a.reversedx();
		a.adjustLocationByDxDy();
		return;
	    }
            if (from.getX() > to.getX())
            {
                // we're moving to the west
                if (!cellFrom.isWestWallPresent())
                {
                    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
                }
                else
                {
                    a.reversedx();
                    a.adjustLocationByDxDy();
                }
            }
            else
            {
                // we're moving to the east
                if (!cellTo.isWestWallPresent())
                {
                    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
                }
                else
                {
                    a.reversedx();
                    a.adjustLocationByDxDy();
                }
            }
        }
        else
        {
	    CellReference northReference = new CellReference(from.getX(), from.getY() - 1);
	    CellReference westReference = new CellReference(from.getX() - 1, from.getY());
	    CellReference eastReference = new CellReference(from.getX() + 1, from.getY());
	    CellReference southReference = new CellReference(from.getX(), from.getY() + 1);

	    Cell north = getCell(northReference);
	    Cell west = getCell(westReference);
	    Cell east = getCell(eastReference);
	    Cell south = getCell(southReference);

            // this means an agent is moving diagonally into the next cell
            // note: We need to acquire four cells to calculate these diagonals
            if (to.getX() < from.getX() && to.getY() < from.getY())
            {
		if (north.isWestWallPresent() && cellFrom.isWestWallPresent() && !cellFrom.isNorthWallPresent())
		{
		    a.reversedx();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, northReference);
		}
		else if (cellFrom.isNorthWallPresent() && west.isNorthWallPresent() && !cellFrom.isWestWallPresent())
		{
		    a.reversedy();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, westReference);
		}
		else if (cellFrom.isNorthWallPresent() && cellFrom.isWestWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else if (north.isWestWallPresent() && west.isNorthWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else
		{
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
		}
            }
            else if (to.getX() > from.getX() && to.getY() > from.getY())
            {
		if (south.isNorthWallPresent() && cellTo.isNorthWallPresent() && !east.isWestWallPresent())
		{
		    a.reversedy();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, eastReference);
		}
		else if (cellTo.isWestWallPresent() && east.isWestWallPresent() && !south.isNorthWallPresent())
		{
		    a.reversedx();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, southReference);
		}
		else if (cellTo.isNorthWallPresent() && cellTo.isWestWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else if (south.isNorthWallPresent() && east.isWestWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else
		{
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
		}
            }
            else if (to.getX() > from.getX() && to.getY() < from.getY())
            {
		if (cellFrom.isNorthWallPresent() && east.isNorthWallPresent() && !east.isWestWallPresent())
		{
		    a.reversedy();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, eastReference);
		}
		else if (east.isWestWallPresent() && cellTo.isWestWallPresent() && !cellFrom.isNorthWallPresent())
		{
		    a.reversedx();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, northReference);
		}
		else if (east.isWestWallPresent() && cellFrom.isNorthWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else if (cellTo.isWestWallPresent() && east.isNorthWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else
		{
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
		}
            }
            else if (to.getX() < from.getX() && to.getY() > from.getY())
            {
		if (cellFrom.isWestWallPresent() && south.isWestWallPresent() && !south.isNorthWallPresent())
		{
		    a.reversedx();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, southReference);
		}
		else if (cellTo.isNorthWallPresent() && south.isNorthWallPresent() && !cellFrom.isWestWallPresent())
		{
		    a.reversedy();
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, westReference);
		}
		else if (south.isNorthWallPresent() && cellFrom.isWestWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else if (cellTo.isNorthWallPresent() && south.isWestWallPresent())
		{
		    a.reversedx();
		    a.reversedy();
		    a.adjustLocationByDxDy();
		}
		else
		{
		    a.adjustLocationByDxDy();
		    moveAgentFromTo(a, from, to);
		}
            }
	    else
	    {
		System.out.println("OH DEAR~!");
	    }
        }
    }

    /**
     * Attempts to move a specific agent in the environment based on its current trajectory.
     * @param a Agent to move
     */
    public void attemptToMoveAgent(Agent a)
    {
	Location l = a.getNextLocation();
	if (l.getX() < 0 || l.getX() >= getWidth())
	{
	    a.reversedx();
	}
	if (l.getY() < 0 || l.getY() >= getHeight())
	{
	    a.reversedy();
	}
	
        moveAgent(a,
		a.getCellReference(),
                new CellReference(a.getNextLocation(), cellWidth, cellHeight));
    }

    /**
     * Adds agents to the environment
     * @param agents Array of agents to add
     */
    public void addAgents(Agent[] agents)
    {
	for (Agent a : agents)
	{
	    addAgent(a);
	}
    }

    /**
     * Adds agents to the environment
     * @param agents Collection of agents to add
     */
    public void addAgents(Collection<Agent> agents)
    {
	for (Agent a : agents)
	{
	    addAgent(a);
	}
    }

    /**
     * Adds a single agent to the environment
     * @param a Agent to add
     */
    public void addAgent(Agent a)
    {
	synchronized(agents)
	{
	    agents.add(a);
	}
	agentsByCell.get(a.getCellReference()).add(a);
    }

    /**
     * Removes a single agent from the environment
     * @param a Agent to remove
     */
    public void removeAgent(Agent a)
    {
	synchronized(agents)
	{
	    agents.remove(a);
	}
	agentsByCell.get(a.getCellReference()).remove(a);
    }

    /**
     * Removes all agents from the environment
     */
    public void removeAllAgents()
    {
	synchronized(agents)
	{
	    agents.clear();
	}
	synchronized(agentsByCell)
	{
	    for (Set<Agent> s : agentsByCell.values())
	    {
		s.clear();
	    }
	}
    }

    /**
     * Iterates through all agents and returns only agents of a specified AgentType
     * @param type Type of agent to return
     * @return List of Agents of the given type
     */
    public List<Agent> getAgentsOfType(AgentType type)
    {
	List<Agent> returner = new ArrayList<Agent>();
	synchronized(agents)
	{
	    for (Agent a : agents)
	    {
		if (a.isOfType(type))
		{
		    returner.add(a);
		}
	    }
	}
	return returner;
    }

    /**
     * Moves agent from a cell to another. Private, as there is no checking involved.
     *
     * For public attempts to move an agent, {@link #attemptToMoveAgent(Agent)}
     * @param a Agent to move
     * @param from Cell the agent is currently in
     * @param to Destination cell
     */
    private void moveAgentFromTo(Agent a, CellReference from, CellReference to)
    {
	agentsByCell.get(from).remove(a);
	agentsByCell.get(to).add(a);
	a.setCellReference(to);
    }

    /**
     * Retrieves all agents from a specific cell within the environment
     * @param cr CellReference indicating which cell to get agents from
     * @return All agents in a given cell
     */
    public Set<Agent> getAgentsByCell(CellReference cr)
    {
	return agentsByCell.get(cr);
    }

    /**
     * Retrieves all cells within the enrivonment
     * @return Returns a map of cells, indexed by CellReference
     */
    public Map<CellReference, Cell> getCellMap()
    {
	return cells;
    }

    /**
     * Retrieves the total width of the environment, in pixels
     * @return Total width of the environment, in pixels
     */
    public int getWidth()
    {
	return cellsWide * cellWidth;
    }

    /**
     * Retrieves the total height of the environment, in pixels
     * @return Total height of the environment, in pixels
     */
    public int getHeight()
    {
	return cellsHigh * cellHeight;
    }

    /**
     * Retrieves the width of the environment, in cells
     * @return Total width the environment, in number of cells
     */
    public int getCellsWide()
    {
	return cellsWide;
    }

    /**
     * Retrieves the height of the environment, in cells
     * @return Total height of the environment, in number of cells
     */
    public int getCellsHigh()
    {
	return cellsHigh;
    }

    /**
     * Returns the width of cells in the environment (all cells are the same width)
     * @return Width of each cell within the environment
     */
    public int getCellWidth()
    {
	return cellWidth;
    }

    /**
     * Returns the height of cells in the environment (all cells are the same height)
     * @return Height of each cell within the environment
     */
    public int getCellHeight()
    {
	return cellHeight;
    }

    /**
     * Adjusts the number of cell columns in the environment.
     * All cells are deleted by this method and then recreated to the new specification.
     * This loses agent data, and should ONLY be used during initialisation.
     * 
     * @param cellsWide Desired width of environment, in cells
     */
    public void setCellsWide(int cellsWide)
    {
	if (this.cellsWide != cellsWide)
	{
	    this.cellsWide = cellsWide;
	    cells.clear();
	    agentsByCell.clear();
	    for (int i = 0; i < this.cellsWide; i++)
	    {
		for (int j = 0; j < cellsHigh; j++)
		{
		    CellReference cr = new CellReference(i, j);
		    cells.put(cr, new Cell());
		    agentsByCell.put(cr, new HashSet<Agent>());
		}
	    }
	}
    }

    /**
     * Adjusts the number of cell rows in the environment.
     * All cells are deleted by this method and then recreated to the new specification.
     * This loses agent data, and should ONLY be used during initialisation.
     *
     * @param cellsHigh Desired height of environment, in cells
     */
    public void setCellsHigh(int cellsHigh)
    {
	if (this.cellsHigh != cellsHigh)
	{
	    this.cellsHigh = cellsHigh;
	    cells.clear();
	    agentsByCell.clear();
	    for (int i = 0; i < cellsWide; i++)
	    {
		for (int j = 0; j < this.cellsHigh; j++)
		{
		    CellReference cr = new CellReference(i, j);
		    cells.put(cr, new Cell());
		    agentsByCell.put(cr, new HashSet<Agent>());
		}
	    }
	}
    }

    /**
     * Sets the width of all cells in the environment (each cell has the same width)
     * @param cellWidth Desired width of cells in the environment, in pixels
     */
    public void setCellWidth(int cellWidth)
    {
	this.cellWidth = cellWidth;
    }

    /**
     * Sets the height of all cells in the environment (each cell has the same height)
     * @param cellHeight Desired height of the cells in the environment, in pixels
     */
    public void setCellHeight(int cellHeight)
    {
	this.cellHeight = cellHeight;
    }

}
