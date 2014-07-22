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
import java.util.Random;

/**
 * Agents which are deceased
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Removed extends Agent {

    public Removed(Agent a, Parameters parameters, Random r)
    {
        super(a, parameters, r);
        type = ZombieSimulation.REMOVED;
    }

    /**
     * Overridden to disallow movement of Removed agents
     * @return Always false
     */
    protected boolean shouldMove()
    {
	return false;
    }

    /**
     * Overriden so that Removed agents cannot acquire directions greater than 0
     */
    @Override
    public void acquireNewDirections() {
	dx = 0;
	dy = 0;
    }

    @Override
    public Agent createCopy() {
	Removed returner = new Removed(this, parameters, r);
	Agent.copyStatsFromTo(this, returner);
	return returner;
    }

    
}
