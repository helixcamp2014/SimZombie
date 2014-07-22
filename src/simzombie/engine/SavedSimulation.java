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
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Class that structures a Simulation in a way that it can be saved
 *
 * Simulation IO uses this class, which saves an ordered list of {@link SavedStep} objects
 * which relate to the individual steps of a simulation.
 *
 * From this class, an entire simulation can be recreated, and is used for both storing
 * simulations and allowing for replayability of simulations as they are running
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class SavedSimulation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the simulation
     */
    private String name;

    /**
     * Parameters file associated with this simulation
     */
    private Parameters associatedParameters;

    /**
     * Ordered list of all {@link SavedStep} objects to recreate this simulation
     */
    private List<SavedStep> savedSteps;

    /**
     * Stores the cells of the simulation, so that the environment can also be faithfully recreated
     */
    private Map<CellReference, Cell> cellMap;

    public SavedSimulation(String name, Parameters p, List<SavedStep> steps, Map<CellReference, Cell> cellMap)
    {
	this.name = name;
	associatedParameters = p;
	savedSteps = steps;
	this.cellMap = cellMap;
    }

    public String getName()
    {
	return name;
    }

    public Parameters getParameters()
    {
	return associatedParameters;
    }

    public List<SavedStep> getSavedSteps()
    {
	return savedSteps;
    }

    public Map<CellReference, Cell> getCellMap()
    {
	return cellMap;
    }
}
