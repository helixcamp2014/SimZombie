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

package simzombie.engine.simulations.zombies.agents;

import simzombie.engine.Parameters;
import simzombie.engine.Agent;
import simzombie.engine.simulations.zombies.ZombieSimulation;
import simzombie.engine.utils.AgentCounter;
import java.util.Random;

/**
 * 'Healthy' individuals, ripe for infection
 *
 * Move randomly 1 in 10 steps, no overridden change of direction code
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Susceptible extends Agent {


    public Susceptible(Agent a, Parameters parameters, Random r)
    {
	this(a, parameters, r, false);
    }

    public Susceptible(Agent a, Parameters parameters, Random r, boolean isClone)
    {
	super(a, parameters, r);
        type = ZombieSimulation.SUSCEPTIBLE;
	if (a != null && !isClone && a.isOfType(ZombieSimulation.SUSCEPTIBLE))
	{
	    id = AgentCounter.getAgentId();
	}
        type = ZombieSimulation.SUSCEPTIBLE;
    }

    /**
     * A susceptible moves 1 in 10 steps
     * @return True if the susceptible moves this step, false otherwise
     */
    @Override
    protected boolean shouldMove()
    {
	// occasionally a Susceptible doesn't move, for a little more free will
	switch(r.nextInt(10))
	{
	    case(0): return false;
	    default: return true;
	}
    }

    @Override
    public Agent createCopy() {
	Susceptible returner = new Susceptible(this, parameters, r, true);
	Agent.copyStatsFromTo(this, returner);
	return returner;
    }

}
