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
 * Zombified agents, which spread the infection
 *
 * Occasionally change direction and speed through {@link #acquireNewDirections()}
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Zombified extends Agent {

    public Zombified(Agent a, Parameters parameters, Random r)
    {
        super(a, parameters, r);
        type = ZombieSimulation.ZOMBIFIED;
    }

    /**
     * Acquires a random speed in the zombie movement range, and sometimes changes directions also
     */
    @Override
    public void acquireNewDirections()
    {
	ZombieParameters zParameters = (ZombieParameters) parameters;

        if (zParameters.getZombifiedMaxSpeed() == zParameters.getZombifiedMinSpeed())
        {
            dx = zParameters.getZombifiedMaxSpeed();
            dy = zParameters.getZombifiedMinSpeed();
        }
        else
        {
            int max = Math.max(zParameters.getZombifiedMaxSpeed(), zParameters.getZombifiedMinSpeed());
            int min = Math.min(zParameters.getZombifiedMaxSpeed(), zParameters.getZombifiedMinSpeed());
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

    @Override
    public Agent createCopy() {
	Zombified returner = new Zombified(this, parameters, r);
	Agent.copyStatsFromTo(this, returner);
	return returner;
    }
}
