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

package simzombie.engine.simulations.zombies;

import simzombie.engine.environment.Environment;
import simzombie.engine.Parameters;
import simzombie.engine.Agent;
import simzombie.engine.AgentType;
import simzombie.engine.graph.Graph;
import simzombie.engine.graph.Plot;
import simzombie.engine.simulations.zombies.agents.Infected;
import simzombie.engine.simulations.zombies.agents.Removed;
import simzombie.engine.simulations.zombies.agents.Susceptible;
import simzombie.engine.simulations.zombies.agents.Zombified;
import simzombie.engine.simulations.Simulation;
import simzombie.engine.utils.Helpers;
import java.awt.Color;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Implements the Zombie Simulation
 *
 * Use this class as a model if implementing a new type of simulation
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class ZombieSimulation implements Simulation {

    /**
     * This agent type should be assigned to all healthy individuals
     */
    public final static AgentType SUSCEPTIBLE = new AgentType("Susceptible");

    /**
     * This agent type should be assigned to individuals who are infected but not yet zombified
     */
    public final static AgentType INFECTED = new AgentType("Infected");

    /**
     * This agent type should be assigned to infectious individuals
     */
    public final static AgentType ZOMBIFIED = new AgentType("Monsterified");

    /**
     * This agent type should be assigned to deceased individuals
     */
    public final static AgentType REMOVED = new AgentType("Deceased");

    /**
     * Array containing ALL AgentTypes for this simulation - returned by {@link getAgentTypes()}
     */
    private final static AgentType[] agentTypes = { SUSCEPTIBLE, INFECTED, ZOMBIFIED, REMOVED };

    /**
     * Parameters for this simulation.
     * It is common for a simulation to have unique parameters extending from the parameters
     * base class, in this case we use the ZombieParameters class
     */
    private ZombieParameters parameters;

    /**
     * Our core instantiation of Random, to be used for generating random numbers wherever possible
     */
    private Random random = new Random();

    /**
     * Constructs a ZombieSimulation but does not initialise it
     * @param p Parameters to use for this simulation
     */
    public ZombieSimulation(ZombieParameters p)
    {
	parameters = p;
 	eulerZombified = ((ZombieParameters)parameters).getInitialZombified();
	eulerSusceptibles = parameters.getPopulation() - eulerZombified;
	eulerRemoved = 0;
    }

    @Override
    public void reset()
    {
        configureEnvironment(parameters.getEnvironment());
    }

    @Override
    public String getName()
    {
	return "Zombie Simulation";
    }

    public String getDescription()
    {
	return "The basic Zombie Outbreak simulation";
    }

    @Override
    public Parameters getParameters()
    {
	return parameters;
    }

    @Override
    public Random getRandom()
    {
	return random;
    }

    /**
     * Adds numbers of agents according to the parameters of this simulation
     * Agents are initially randomly distributed
     * @param environment
     */
    @Override
    public void configureEnvironment(Environment environment)
    {
        environment.removeAllAgents();
	for (int i = 0; i < parameters.getPopulation() - parameters.getInitialInfected() - parameters.getInitialZombified(); i++)
        {
            environment.addAgent(new Susceptible(null, parameters, random));
        }
        for (int i = 0; i < parameters.getInitialInfected(); i++)
        {
            environment.addAgent(new Infected(null, parameters, random));
        }
        for (int i = 0; i < parameters.getInitialZombified(); i++)
        {
            environment.addAgent(new Zombified(null, parameters, random));
        }
    }

    /**
     * Helper function that 'rolls the dice' on probabilities
     * @param chance Chance an event should happen
     * @return True if that event should happen, false if not
     */
    private boolean shouldHappen(double chance)
    {
	if (chance > 1)
	{
	    int number = random.nextInt(101);

	    if (number <= chance)
	    {
		return true;
	    }
	    return false;
	}
	else
	{
	    // we round this for a small (acceptable) loss of precision!
	    int number = random.nextInt((int)(100 / chance));
	    if (number == 0)
	    {
		return true;
	    }
	    else
	    {
		return false;
	    }
	}
    }

    /**
     * Keeps track of the current step of the simulation
     */
    private int simStep = 0;

    @Override
    public boolean updateEnvironment(Environment environment)
    {
	Set<Integer> agentOccupied = new HashSet<Integer> ();
	Set<Agent> toInfect = new HashSet<Agent>();
	Set<Agent> toZombify = new HashSet<Agent>();
	Set<Agent> toRemove = new HashSet<Agent>();
	Set<Agent> toIntroduce = new HashSet<Agent>();

        int numberSusceptible = 0;
        int numberZombified = 0;
        int numberInfected = 0;
        int numberRemoved = 0;

	int stepsPerHalfDay = 2;
        int dayOrNight = simStep / stepsPerHalfDay % 2;
        if (dayOrNight == 0) parameters.setTimeOfDay(Parameters.TimeOfDay.DAY);
        else if (dayOrNight == 1) parameters.setTimeOfDay(Parameters.TimeOfDay.NIGHT);

        int lunarPhase = (simStep/(stepsPerHalfDay*2)) % 28;
        parameters.setCurrentLunarPhase(lunarPhase);

        boolean monsterActive = false;
        if (parameters.getTimeOfDay() == Parameters.TimeOfDay.DAY
                && parameters.isMonsterActiveDuringDay())
        {
            monsterActive = true;
        }
        if (parameters.getTimeOfDay() == Parameters.TimeOfDay.NIGHT
                && parameters.isMonsterActiveDuringNight())
        {
            if (parameters.activeDuringLunarPhase(lunarPhase))
            {
                monsterActive = true;
            }
        }

	synchronized(environment.getAgents())
	{
	    for (Agent a : environment.getAgents())
	    {
		environment.attemptToMoveAgent(a);
		switch(random.nextInt(20))
		{
		    case(0) : a.acquireNewDirections();
		}

		if (a.isOfType(SUSCEPTIBLE))
		{
                    numberSusceptible++;
		    // this represents births
		    if (parameters.getChanceOfBirth() > 0 && !agentOccupied.contains(a.getId()))
		    {
			if (shouldHappen(parameters.getChanceOfBirth()))
			{
			    toIntroduce.add(a);
			    agentOccupied.add(a.getId());
			}
		    }

		    // this represents people dying
		    if (parameters.getChanceOfNaturalDeath() > 0 && !agentOccupied.contains(a.getId()))
		    {
			if (shouldHappen(parameters.getChanceOfNaturalDeath()))
			{
			    toRemove.add(a);
			    agentOccupied.add(a.getId());
			}
		    }
		}
		else if (a.isOfType(INFECTED))
		{
                    numberInfected ++;
		    Infected i = (Infected) a;
		    if (i.latencyPeriodElapsed() && !agentOccupied.contains(i.getId()))
		    {
			toZombify.add(i);
			agentOccupied.add(i.getId());
		    }

		    if (parameters.getChanceOfNaturalDeath() > 0 && !agentOccupied.contains(i.getId()))
		    {
			if (shouldHappen(parameters.getChanceOfNaturalDeath()))
			{
			    toRemove.add(i);
			    agentOccupied.add(i.getId());
			}
		    }
		}
		else if (a.isOfType(ZOMBIFIED))
		{
                    numberZombified ++;
//                    float chanceToReproduce = 0.75f;
//                    float chanceToAttack = 0.75f;
//
//                    if (shouldHappen(chanceToReproduce))
//                    {
//                        agentOccupied.add(a.getId());
//                        Agent i = new Zombified(a, parameters, random);
//                        i.acquireNewDirections();
//                        toIntroduce.add(i);
//                    }
//                    else if (shouldHappen(chanceToAttack))
//                    {
		    if (monsterActive)
		    {
                        boolean attacked = false;
//			System.out.println("---Monster " + a.getId() + "---");
                        for (Agent a2 : environment.getAgentsByCell(a.getCellReference()))
                        {
//			    System.out.println("Investigating Agent " + a2.getId() + ", attacked is " + attacked);
//                            if (!attacked && (a2.isOfType(SUSCEPTIBLE) || (a2.isOfType(REMOVED) && parameters.canRemovedBecomeZombies()))  && !agentOccupied.contains(a2.getId()) && !agentOccupied.contains(a.getId()) && !a.equals(a2))
                            if (!attacked && a2.isOfType(SUSCEPTIBLE) && !agentOccupied.contains(a2.getId()) && !agentOccupied.contains(a.getId()) && !a.equals(a2))
                            {
//				System.out.println("Monster " + a.getId() + " attacking!");
                                if (Helpers.distanceBetween(a, a2) < Math.pow(parameters.getInfectionRange(), 2))
                                {
                                    boolean zombieWon = false;
                                    double chanceWinEncounter = parameters.getChanceASusceptibleWinsEncounter();
                                    if (parameters.isAwarenessRaised())
                                    {
                                        chanceWinEncounter = parameters.getAwareChanceASusceptibleWinsEncounter();
                                    }
                                    if (chanceWinEncounter > 0 && !a2.isOfType(REMOVED))
                                    {
                                        attacked = true;
                                        if (shouldHappen(chanceWinEncounter))
                                        {
                                            toRemove.add(a);
                                            agentOccupied.add(a.getId());
                                            zombieWon = false;
                                        }
                                        else
                                        {
                                            zombieWon = true;
                                        }
                                    }
                                    else
                                    {
                                        zombieWon = true;
                                    }

                                    if (zombieWon)
                                    {
                                        double infectionTransmitChance = parameters.getChanceInfectionTransmits();
                                        if (parameters.isAwarenessRaised())
                                        {
                                            infectionTransmitChance = parameters.getAwareChanceInfectionTransmits();
                                        }
                                        if (shouldHappen(infectionTransmitChance))
                                        {
                                            if (parameters.getLatencyPeriod() == 0)
                                            {
                                                toZombify.add(a2);
                                            }
                                            else
                                            {
                                                toInfect.add(a2);
                                            }
                                        }
                                        else
                                        {
                                            toRemove.add(a2);
                                        }
                                        agentOccupied.add(a2.getId());
                                    }
                                }
//                            }
			    }
                        }
		    }
		}
		else if (a.isOfType(REMOVED))
		{
                    numberRemoved ++;
		    if (parameters.getChanceOfNaturalInfection() > 0)
		    {
			if (!agentOccupied.contains(a.getId()))
			{
			    if (shouldHappen(parameters.getChanceOfNaturalInfection()))
			    {
				    toInfect.add(a);
				    agentOccupied.add(a.getId());
			    }
			}
		    }
		}
	    }
	}

        int totalPopulation = numberSusceptible + numberRemoved + numberZombified + numberInfected;
        int totalPopulationAffected = numberRemoved + numberZombified + numberInfected;
        double percentageAffected = 100*(double) totalPopulationAffected / (double) totalPopulation;
        if (!parameters.isAwarenessRaised() && percentageAffected > parameters.getAwarenessRaisedAt())
        {
            parameters.setAwarenessRaised(true);
        }

	for (Agent i : toInfect)
	{
	    environment.removeAgent(i);
	    Agent a = new Infected(i, parameters, random);
	    environment.addAgent(a);
	}
	for (Agent z : toZombify)
	{
	    environment.removeAgent(z);
	    Agent a = new Zombified(z, parameters, random);
	    environment.addAgent(a);
	}
	for (Agent r : toRemove)
	{
	    environment.removeAgent(r);
	    Agent a = new Removed(r, parameters, random);
	    environment.addAgent(a);
	}
	for (Agent i: toIntroduce)
	{
	    Agent a = new Susceptible(i, parameters, random);
	    a.acquireNewDirections();
	    environment.addAgent(a);
	}
        simStep++;
        
//	if (environment.getAgentsOfType(ZOMBIFIED).size() == parameters.getPopulation())
	if (environment.getAgentsOfType(SUSCEPTIBLE).size() == 0)
//		&& environment.getAgentsOfType(INFECTED).size() == 0)
//		&& environment.getAgentsOfType(REMOVED).size() == 0)
	{
	    return true;
	}
	return false;
    }

    public AgentType[] getAgentTypes() {
	return agentTypes;
    }

    private double eulerSusceptibles = 0;
    private double eulerZombified = 0;
    private double eulerRemoved = 0;
    private double step = 0.00000001;
    private int eulerUpdates = 0;

    /**
     * Performs Euler's Numerical Analysis
     *
     * Uses an approximate set of ODEs that the model is based on.  Changes
     * to the simulation have since been implemented (such as activity) that
     * no longer mean these ODEs accurately reflect the model.
     * 
     * @param g
     * @return True if numerical analysis has finished, false otherwise
     */
    @Override
    public boolean updateNumericalAnalysis(Graph g) {
	if (eulerSusceptibles > 1)
	{
	    ZombieParameters p = (ZombieParameters) parameters;

	    // this is the rate at which Zombies become Removed
	    double eulerZombieDestructionRate = p.getChanceASusceptibleWinsEncounter();

	    // this is the rate at which Susceptibles become Zombified
	    double eulerNewZombieRate = (p.getChanceInfectionTransmits() * (100 - p.getChanceASusceptibleWinsEncounter())) / 100d;

	    // this is the rate at which Removed become Zombified
	    double eulerZombieResurrectionRate = p.getChanceOfNaturalInfection() * 10000;

	    // this is the rate at which Susceptibles become Removed
	    double eulerBackgroundDeathRate = p.getChanceOfNaturalDeath() * 10000;

	    // -----------------------------------------------------------------
	    
//	    double newEulerSusceptibles = eulerSusceptibles + (step * (- eulerNewZombieRate * eulerSusceptibles * eulerZombified
//							- eulerBackgroundDeathRate * eulerSusceptibles));
	    double newEulerSusceptibles = eulerSusceptibles + (step * (-eulerNewZombieRate * eulerSusceptibles * eulerZombified
							- eulerBackgroundDeathRate * eulerSusceptibles));

	    double newEulerZombified = eulerZombified + (step * (eulerNewZombieRate * eulerSusceptibles * eulerZombified
							- eulerZombieDestructionRate * eulerSusceptibles * eulerZombified
							+ eulerZombieResurrectionRate * eulerRemoved));

	    double newEulerRemoved = eulerRemoved + (step * (eulerZombieDestructionRate * eulerSusceptibles * eulerZombified
							+ eulerBackgroundDeathRate * eulerSusceptibles
							- eulerZombieResurrectionRate * eulerRemoved));

	    eulerSusceptibles = newEulerSusceptibles;
	    eulerZombified = newEulerZombified;
	    eulerRemoved = newEulerRemoved;

	    g.addPlot(new Plot("Susceptible", eulerUpdates, (int)Math.round(eulerSusceptibles), Color.GREEN));
	    g.addPlot(new Plot("Zombified", eulerUpdates, (int)Math.round(eulerZombified), Color.RED));
	    g.addPlot(new Plot("Removed", eulerUpdates, (int)Math.round(eulerRemoved), Color.YELLOW));
	    g.addPlot(new Plot("Total", eulerUpdates, (int)Math.round(eulerSusceptibles + eulerZombified + eulerRemoved), Color.DARK_GRAY));
	    eulerUpdates++;
	    return false;
	}
	else return true;
    }
}
