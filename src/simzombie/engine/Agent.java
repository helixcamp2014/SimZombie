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

import simzombie.engine.utils.Location;
import simzombie.engine.environment.CellReference;
import simzombie.engine.utils.AgentCounter;
import java.io.Serializable;
import java.util.Random;

/**
 * Basic template for the Agent class
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public abstract class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Type determines the behaviour and treatment of the agent
     * @return Type of the agent
     */
    public AgentType getType() {
        return type;
    }

    /**
     * Number of pixels the agent is currently moving in the x direction
     * @return Number of pixels the agent is currently moving in the x direction
     */
    public int getdx() {
        return dx;
    }

    /**
     * Number of pixels the agent is currently moving in the y direction
     * @return Number of pixels the agent is currently moving in the y direction
     */
    public int getdy() {
        return dy;
    }

    /**
     * The AgentType of the agent
     */
    protected AgentType type;

    /**
     * The current position of the agent
     */
    protected Location location;

    /**
     * The number of pixels in the x direction the agent is currently moving
     */
    protected int dx;

    /**
     * The number of pixels in the y direction the agent is currently moving
     */
    protected int dy;

    /**
     * The age of the agent.
     *
     * This was going to be an additional parameter, allowing age to affect
     * likelihood of fighting back, death by natural causes, etc. but is
     * not yet implemented
     */
    protected int age;
    
    /**
     * A unique identifier generated for every agent.
     *
     * This just makes it easier to compare later.
     */
    protected int id;

    /**
     * For random numbers!
     */
    protected Random r;

    /**
     * Parameters of the simulation this agent belongs to
     */
    protected Parameters parameters;

    /**
     * CellReference to the cell this agent is currently in
     */
    protected CellReference cr;

    /**
     * Constructs a random default agent from only parameters and a seeded Randomiser
     *
     * @param p Parameters of the simulation this agent is destined for
     * @param r A given Random object, for consistent randomisation
     */
    protected Agent(Parameters p, Random r)
    {
	this(null, p, r);
    }

    /**
     * Constructs an agent, borrowing variables from a where possible
     *
     * @param a Agent to copy variables from.  If null, defaults are substituted
     * @param parameters Parameters of the simulation this agent will be placed into
     * @param r Random object for consistent randomisation
     */
    protected Agent(Agent a, Parameters parameters, Random r)
    {
	this.r = r;
	this.parameters = parameters;
	if (a == null)
	{
	    id = AgentCounter.getAgentId();
	    location = new Location(r.nextInt(parameters.getEnvironmentWidth()), r.nextInt(parameters.getEnvironmentHeight()));
	    cr = new CellReference(location, parameters.getCellWidth(), parameters.getCellHeight());
	}
	else
	{
	    id = a.id;
	    location = new Location(a.getLocation().getX(), a.getLocation().getY());
	    cr = a.getCellReference();
	    dx = a.getdx();
	    dy = a.getdy();
	}
	acquireNewDirections();
    }

    /**
     * Randomly change direction
     *
     * Override this method to provide alternate methods of moving
     */
    public void acquireNewDirections()
    {

        if (parameters.getAgentMaxSpeed() == parameters.getAgentMinSpeed())
        {
            dx = parameters.getAgentMaxSpeed();
            dy = parameters.getAgentMaxSpeed();
        }
        else
        {
            int max = Math.max(parameters.getAgentMaxSpeed(), parameters.getAgentMinSpeed());
            int min = Math.min(parameters.getAgentMaxSpeed(), parameters.getAgentMinSpeed());
            dx = r.nextInt(max - min) + min;
            dy = r.nextInt(max - min) + min;
        }

	switch (r.nextInt(4))
	{
	    case(0): break;
	    case(1): reversedx(); break;
	    case(2): reversedy(); break;
	    case(3): reversedx(); reversedy(); break;
	}
    }

    /**
     * Method which controls whether an agent is allowed to move or not
     *
     * A class can override this method (which defaults to return true) to either
     * return false (turning movement off altogether) or to return true/false
     * conditionally
     * @return True if an agent can move, false if an agent cannot move
     */
    protected boolean shouldMove()
    {
	return true;
    }

    /**
     * Adjusts the location of the agent by dx and dy, if it is allowed to move
     *
     * This method can be called alone to guarantee movement, or used in conjuction
     * with the Environment class which puts some extra restrictions on movement
     */
    public void adjustLocationByDxDy()
    {
	if (shouldMove())
	{
	    location.setX(location.getX() + dx);
	    location.setY(location.getY() + dy);
	}
    }

    /**
     * Multiply the current x direction by -1, moving in the opposite direction
     */
    public void reversedx()
    {
        dx *= -1;
    }

    /**
     * Multiply the current y direction by -1, moving in the opposite direction
     */
    public void reversedy()
    {
        dy *= -1;
    }

    /**
     * Get the current location of the agent
     * @return The current location of the agent
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Get the next location of the agent, as determined by dx and dy
     * @return A modified Location object pointing at the location of this agent after the next step
     */
    public Location getNextLocation()
    {
        return new Location(location.getX() + dx, location.getY() + dy);
    }

    /**
     * Determine whether this agent is of a specified type
     * @param at AgentType to test for
     * @return True if this agent is the AgentType, false if not
     */
    public boolean isOfType(AgentType at)
    {
        return (at.getUniqueIdentifier() == type.getUniqueIdentifier());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Agent other = (Agent) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.id;
        return hash;
    }

    /**
     * Clone method for Agent
     * @return New agent with the same variables (including id)
     */
    public abstract Agent createCopy();

    /**
     * Copy variables from one agent to another
     * @param from Agent to copy variables from
     * @param to Agent to copy variables to
     */
    protected static void copyStatsFromTo(Agent from, Agent to)
    {
	to.id = from.id;
	to.dx = from.dx;
	to.dy = from.dy;
	to.age = from.age;
	to.location = new Location(from.location.getX(), from.location.getY());
	to.cr = from.cr;
	to.parameters = from.parameters;
	to.type = from.type;
    }

    /**
     * Accessor for ID
     * @return ID of this agent
     */
    public int getId()
    {
	return id;
    }

    /**
     * Accessor for CellReference
     * @return Returns a CellReference pointing to the cell this agent is currently occupying
     */
    public CellReference getCellReference()
    {
	return cr;
    }

    /**
     * Set the CellReference this agent is currently occupying
     *
     * This is poorly programmed and needs refactoring later
     * @param cellReference New CellReference this agent should occupy
     */
    public void setCellReference(CellReference cellReference)
    {
	cr = cellReference;
    }
}
