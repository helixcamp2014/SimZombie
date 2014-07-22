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

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Stores ALL the information about a single step of a simulation
 *
 * When stored successively (as in {@link SavedSimulation}) they can be used
 * to recreate a simulation
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class SavedStep implements Comparable, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Orders steps in a list based on their step number, which is their position with a simulation
     */
    protected static class StepComparator implements Comparator
    {
	public int compare(Object o1, Object o2) 
	{
	    SavedStep ss1 = (SavedStep) o1;
	    SavedStep ss2 = (SavedStep) o2;

	    return (ss1.stepNumber - ss2.stepNumber);
	}
    }

    public static StepComparator stepComparator = new StepComparator();

    /**
     * Each step has a number relating to its position with the simulation
     *
     * Simulations begin at step 0 and are incremented for each step that is executed
     */
    private int stepNumber;

    /**
     * List of agents present in the given step of the simulation
     */
    private List<Agent> agents;

    /**
     * Has awareness been raised in this step of the simulation?
     */
    private boolean awarenessRaised;

    public SavedStep(int stepNumber, List<Agent> agents, boolean awarenessRaised)
    {
	this.stepNumber = stepNumber;
	this.agents = agents;
        this.awarenessRaised = awarenessRaised;
    }

    public List<Agent> getAgents()
    {
	return agents;
    }

    public int getStepNumber()
    {
	return stepNumber;
    }

    public boolean isAwarenessRaised()
    {
        return awarenessRaised;
    }

    public int compareTo(Object o) {
	if (o instanceof SavedStep)
	{
	    SavedStep s = (SavedStep) o;
	    return (getStepNumber() - s.getStepNumber());
	}
	return -1;
    }    
}
