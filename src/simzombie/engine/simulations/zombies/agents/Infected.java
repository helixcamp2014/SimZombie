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
import simzombie.engine.simulations.zombies.ZombieParameters;
import simzombie.engine.simulations.zombies.ZombieSimulation;
import java.util.Random;

/**
 * These agents are infected but not yet zombified
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Infected extends Agent {

    /**
     * Countdown to becoming zombified
     */
    private int latencyPeriodRemaining;

    /**
     * Infected agents are capable of moving by default
     *
     * This was a planned parameter, but was never implemented
     */
    private boolean ableToMove = true;

    public Infected(Agent a, Parameters parameters, Random r)
    {
        super(a, parameters, r);
        type = ZombieSimulation.INFECTED;
	ZombieParameters zParameters = (ZombieParameters) parameters;
        latencyPeriodRemaining = zParameters.getLatencyPeriod();
	if (a != null && a.isOfType(ZombieSimulation.REMOVED))
	{
	    ableToMove = false;
	    acquireNewDirections();
	}
    }

    /**
     * Also decrements latencyPeriodRemaining upon moving
     */
    @Override
    public void adjustLocationByDxDy()
    {
        super.adjustLocationByDxDy();
        latencyPeriodRemaining --;
    }

    /**
     *
     * @return True if latencyPeriodRemaining has reached 0
     */
    public boolean latencyPeriodElapsed()
    {
        return latencyPeriodRemaining <= 0;
    }

    /**
     * Only acquires directions greater than 0, 0 if ableToMove is true
     */
    @Override
    public void acquireNewDirections() {
	if (ableToMove)
	{
	    super.acquireNewDirections();
	}
	else
	{
	    dx = 0;
	    dy = 0;
	}
    }

    @Override
    public Agent createCopy() {
	Infected returner = new Infected(this, parameters, r);
	Agent.copyStatsFromTo(this, returner);
	returner.ableToMove = this.ableToMove;
	returner.latencyPeriodRemaining = this.latencyPeriodRemaining;
	return returner;
    }
}
