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

package simzombie.engine.simulations;

import simzombie.engine.environment.Environment;
import simzombie.engine.Parameters;
import simzombie.engine.AgentType;
import simzombie.engine.graph.Graph;
import java.util.Random;


/**
 * Interface for simulation types
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public interface Simulation {

    /**
     *
     * @return An array of all possible agent types for this particular simulation
     */
    public AgentType[] getAgentTypes();

    /**
     *
     * @return Name of this simulation, for identification/selection within a menu purposes
     */
    public String getName();

    /**
     * 
     * @return A short description of the simulation, for use in a menu/selector/etc.
     */
    public String getDescription();

    /**
     *
     * @return Relevant parameters for this simulation (which may be specific to this simulation also)
     */
    public Parameters getParameters();

    /**
     * Configures an environment in the initial stages of setting up a simulation
     * @param environment Environment to configure
     */
    public void configureEnvironment(Environment environment);

    /**
     * This is where the main rules of the simulation should go
     * @param environment Environment the simulation is currently using
     * @return True if the simulation should terminate, false otherwise
     */
    public boolean updateEnvironment(Environment environment);

    /**
     * If a numerical model is provided for this simulation, the details should be placed here.
     *
     * The zombie simulation uses Euler's numerical analysis
     * @param g Graph to update with the results
     * @return True if terminated, false if not
     */
    public boolean updateNumericalAnalysis(Graph g);

    /**
     * This method is provided in an attempt to centralise a Random class, so that numbers are as random as possible
     * @return An instance of the Random class that should be used to generate all random numbers possible
     */
    public Random getRandom();

    /**
     * Reset the simulation to the initial conditions
     */
    public void reset();
}
