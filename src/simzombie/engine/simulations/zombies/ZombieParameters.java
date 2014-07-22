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

import simzombie.engine.Parameters;
import simzombie.engine.utils.CommitPanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Parameters that relate to a zombie simulation ONLY, not generic parameters for all simulations
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class ZombieParameters extends Parameters {

    public ZombieParameters()
    {
    }

    public static ZombieParameters getDefaultParameters()
    {
	return new ZombieParameters();
    }

    // if set to 0, these features are turned off
    // these doubles are percentages, numbers over 100 will cause unexpected results
    // Accuracy is to two decimal places
    // this will need to factor in age and such at some point

    /**
     * Chance of a natural birth occuring, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double chanceOfBirth = 0.1;

    /**
     * Chance of a natural death occuring, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double chanceOfNaturalDeath = 0.1;

    /**
     * Chance of a natural outbreak occuring, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double chanceOfNaturalInfection = 0.1;

    /**
     * Chance a susceptible successfully defeats a zombie in combat, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double chanceASusceptibleWinsEncounter = 5.0;

    /**
     * Chance an individual that falls victim to a zombie becomes a zombie, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double chanceInfectionTransmits = 75;

    /**
     * Number of steps between becoming infected and becoming zombified
     */
    private int latencyPeriod = 0;

    /**
     * The range at which zombies can interact with susceptibles, in pixels
     */
    private int infectionRange = 10;

    /**
     * Number of infected individuals initially in the simulation
     */
    private int initialInfected = 0;

    /**
     * Number of zombified individuals initially in the simulation
     */
    private int initialZombified = 1;

    /**
     * Minimum speed of susceptible agents
     */
    private int susceptibleMinSpeed = 1;

    /**
     * Maximum speed of susceptible agents
     */
    private int susceptibleMaxSpeed = 3;

    /**
     * Minimum speed of zombified agents
     */
    private int zombifiedMinSpeed = 1;

    /**
     * Maximum speed of zombified agents
     */
    private int zombifiedMaxSpeed = 2;

    /**
     * This is toggled to true once awareness is raised - set to true for a simulation that begins aware
     */
    private boolean awarenessRaised = false;

    /**
     * Amount of the population that need to be affected before awareness is raised, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double awarenessRaisedAt = 25;

    /**
     * Chance a susceptible wins in combat against a zombie after awareness has been raised, represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double awareChanceASusceptibleWinsEncounter = 25.0;

    /**
     * Chance a susceptible that loses combat with a zombie becomes a zombie after awareness has been raised,  represented as a percentage (1 is 1%, 0.1 is 0.001%)
     */
    private double awareChanceInfectionTransmits = 75;

    @Override
    public List<CommitPanel> getPanels()
    {
	List<CommitPanel> returner = new ArrayList<CommitPanel> ();

	for (CommitPanel cp : super.getPanels())
	{
	    returner.add(cp);
	}
        
		// ===================================================================

	JLabel latencyPeriodLabel = new JLabel("Latency Period: ");
	final JTextField latencyPeriodField = new JTextField();
	latencyPeriodField.setText(getLatencyPeriod() + "");
	latencyPeriodLabel.setLabelFor(latencyPeriodField);
        latencyPeriodField.setToolTipText("The duration, in steps, in which an individual is carrying the disease dormant");

	JLabel infectionRangeLabel = new JLabel("Infection Range: ");
	final JTextField infectionRangeField = new JTextField();
	infectionRangeField.setText(getInfectionRange() + "");
	infectionRangeLabel.setLabelFor(infectionRangeField);
        infectionRangeField.setToolTipText("The distance, in pixels, at which the infection can be spread");

	JLabel infectionRateLabel = new JLabel("Infection Rate: ");
	final JTextField infectionRateField = new JTextField();
	infectionRateField.setText(getChanceOfNaturalInfection() + "");
	infectionRateLabel.setLabelFor(infectionRateField);
        infectionRateField.setToolTipText("The percentage chance of a natural infection occuring - that is, a healthy person becomes infected without contracting the infection from another");

	JLabel transmissionChanceLabel = new JLabel("Transmission Rate: ");
	final JTextField transmissionChanceField = new JTextField();
	transmissionChanceField.setText(getChanceInfectionTransmits() + "");
	transmissionChanceLabel.setLabelFor(transmissionChanceField);
        transmissionChanceField.setToolTipText("The percentage chance that an individual will contract the disease rather than dying when losing an encounter with a monster");

	final JCheckBox removedCanReanimate = new JCheckBox();
	removedCanReanimate.setText("Removed can Reanimate");
//	removedCanReanimate.setSelected(canRemovedBecomeZombies());
        removedCanReanimate.setToolTipText("Not currently functional");
        

	CommitPanel infectionPanel = new CommitPanel("Infection") {
	    @Override
	    public boolean save() {
			// -----
			try
			{
			    setLatencyPeriod(Integer.parseInt(latencyPeriodField.getText()));
			    setInfectionRange(Integer.parseInt(infectionRangeField.getText()));
			    setChanceOfNaturalInfection(Double.parseDouble(infectionRateField.getText()));
			    setChanceInfectionTransmits(Double.parseDouble(transmissionChanceField.getText()));
//			    setRemovedCanBecomeZombies(removedCanReanimate.isSelected());
			    return true;
			}
			catch (NumberFormatException nfe)
			{
			    return false;
			}
	    }
	};
	
	GroupLayout infectionGroupLayout = new GroupLayout(infectionPanel);
	infectionPanel.setLayout(infectionGroupLayout);

	infectionGroupLayout.setAutoCreateContainerGaps(true);
	infectionGroupLayout.setAutoCreateGaps(true);

	infectionGroupLayout.setHorizontalGroup(
	    infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
		    .addGroup(infectionGroupLayout.createSequentialGroup()
			    .addGroup(infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(latencyPeriodLabel)
				.addComponent(infectionRangeLabel)
				.addComponent(infectionRateLabel)
				.addComponent(transmissionChanceLabel))
			    .addGroup(infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(latencyPeriodField)
				.addComponent(infectionRangeField)
				.addComponent(infectionRateField)
				.addComponent(transmissionChanceField)))
		    .addComponent(removedCanReanimate)
		);

	infectionGroupLayout.setVerticalGroup(
		infectionGroupLayout.createSequentialGroup()
		    .addGroup(infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(latencyPeriodLabel)
			.addComponent(latencyPeriodField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(infectionRangeLabel)
			.addComponent(infectionRangeField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(infectionRateLabel)
			.addComponent(infectionRateField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(infectionGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(transmissionChanceLabel)
			.addComponent(transmissionChanceField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addComponent(removedCanReanimate)
		);


	returner.add(infectionPanel);
	
	// ===================================================================

	JLabel populationSizeLabel = new JLabel("Population Size: ");
	final JTextField populationSizeField = new JTextField();
	populationSizeLabel.setLabelFor(populationSizeField);
	populationSizeField.setText(getPopulation() + "");
        populationSizeField.setToolTipText("The total size of the initial population");

	JLabel initialInfectedLabel = new JLabel("Infected at Start: ");
	final JTextField initialInfectedField = new JTextField();
	initialInfectedLabel.setLabelFor(initialInfectedField);
	initialInfectedField.setText(getInitialInfected() + "");
        initialInfectedField.setToolTipText("The number of people in the population who start the simulation infected with a dormant infection");

	JLabel initialZombiesLabel = new JLabel("Monsterified at Start: ");
	final JTextField initialZombiesField = new JTextField();
	initialZombiesLabel.setLabelFor(initialZombiesField);
	initialZombiesField.setText(getInitialZombified() + "");
        initialZombiesField.setToolTipText("The number of people in the population who start the simulation as a monster");

	CommitPanel populationPanel = new CommitPanel("Population") {

	    @Override
	    public boolean save() {
		// -----
		try
		{
		    setPopulation(Integer.parseInt(populationSizeField.getText()));
		    setInitialInfected(Integer.parseInt(initialInfectedField.getText()));
		    setInitialZombified(Integer.parseInt(initialZombiesField.getText()));
		    return true;
		}
		catch (NumberFormatException nfe)
		{
		    return false;
		}
	    }
	};
	GroupLayout populationGroupLayout = new GroupLayout(populationPanel);
	populationPanel.setLayout(populationGroupLayout);

	populationGroupLayout.setAutoCreateContainerGaps(true);
	populationGroupLayout.setAutoCreateGaps(true);

	populationGroupLayout.setHorizontalGroup(
		populationGroupLayout.createSequentialGroup()
		    .addGroup(populationGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			.addComponent(populationSizeLabel)
			.addComponent(initialInfectedLabel)
			.addComponent(initialZombiesLabel))
		    .addGroup(populationGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(populationSizeField)
			.addComponent(initialInfectedField)
			.addComponent(initialZombiesField)));

	populationGroupLayout.setVerticalGroup(
		populationGroupLayout.createSequentialGroup()
		    .addGroup(populationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(populationSizeLabel)
			.addComponent(populationSizeField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(populationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(initialInfectedLabel)
			.addComponent(initialInfectedField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(populationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(initialZombiesLabel)
			.addComponent(initialZombiesField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));

	returner.add(populationPanel);

	// ===================================================================

	JLabel susceptibleMinSpeedLabel = new JLabel("Min. Speed: ");
	final JTextField susceptibleMinSpeedField = new JTextField();
	susceptibleMinSpeedLabel.setLabelFor(susceptibleMinSpeedField);
	susceptibleMinSpeedField.setText(getSusceptibleMinSpeed() + "");
        susceptibleMinSpeedField.setToolTipText("The minimum speed of uninfected, non-monster individuals, in pixels");

	JLabel susceptibleMaxSpeedLabel = new JLabel("Max. Speed: ");
	final JTextField susceptibleMaxSpeedField = new JTextField();
	susceptibleMaxSpeedLabel.setLabelFor(susceptibleMaxSpeedField);
	susceptibleMaxSpeedField.setText(getSusceptibleMaxSpeed() + "");
        susceptibleMinSpeedField.setToolTipText("The maximum speed of uninfected, non-monster individuals, in pixels");

	JLabel zombieMinSpeedLabel = new JLabel("Monster Min. Speed: ");
	final JTextField zombieMinSpeed = new JTextField();
	zombieMinSpeedLabel.setLabelFor(zombieMinSpeed);
	zombieMinSpeed.setText(getZombifiedMinSpeed() + "");
        susceptibleMinSpeedField.setToolTipText("The minimum speed of monster individuals, in pixels");

	JLabel zombieMaxSpeedLabel = new JLabel("Monster Max. Speed: ");
	final JTextField zombieMaxSpeed = new JTextField();
	zombieMaxSpeedLabel.setLabelFor(zombieMaxSpeed);
	zombieMaxSpeed.setText(getZombifiedMaxSpeed() + "");
        susceptibleMinSpeedField.setToolTipText("The maximum speed of monster individuals, in pixels");

	JLabel birthChanceLabel = new JLabel("Birth Chance: ");
	final JTextField birthChance = new JTextField();
	birthChanceLabel.setLabelFor(birthChance);
	birthChance.setText(getChanceOfBirth() + "");
        birthChance.setToolTipText("The percentage chance that a healthy individual will spawn a new healthy individual at each step");

	JLabel deathChanceLabel = new JLabel("Death Chance: ");
	final JTextField deathChance = new JTextField();
	deathChanceLabel.setLabelFor(deathChance);
	deathChance.setText(getChanceOfNaturalDeath() + "");
        deathChance.setToolTipText("The percentage chance that a healthy individual will die each step");

	JLabel winCombatLabel = new JLabel ("Defeat Monster Chance: ");
	final JTextField winCombat = new JTextField();
	winCombatLabel.setLabelFor(winCombat);
	winCombat.setText(getChanceASusceptibleWinsEncounter() + "");
        winCombat.setToolTipText("The percentage chance that a healthy person will kill a monster when encountered");

	CommitPanel agentsPanel = new CommitPanel("Agents") {

	    @Override
	    public boolean save() {
			// -----
		try
		{
		    setSusceptibleMinSpeed(Integer.parseInt(susceptibleMinSpeedField.getText()));
		    setSusceptibleMaxSpeed(Integer.parseInt(susceptibleMaxSpeedField.getText()));
		    setZombifiedMinSpeed(Integer.parseInt(zombieMinSpeed.getText()));
		    setZombifiedMaxSpeed(Integer.parseInt(zombieMaxSpeed.getText()));
		    setChanceOfBirth(Double.parseDouble(birthChance.getText()));
		    setChanceOfNaturalDeath(Double.parseDouble(deathChance.getText()));
		    setChanceASusceptibleWinsEncounter(Double.parseDouble(winCombat.getText()));
		    return true;
		}
		catch (NumberFormatException nfe)
		{
		    return false;
		}
	    }
	};

	GroupLayout agentsGroupLayout = new GroupLayout(agentsPanel);
	agentsPanel.setLayout(agentsGroupLayout);

	agentsGroupLayout.setAutoCreateContainerGaps(true);
	agentsGroupLayout.setAutoCreateGaps(true);

	agentsGroupLayout.setHorizontalGroup(
		agentsGroupLayout.createSequentialGroup()
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			.addComponent(susceptibleMinSpeedLabel)
			.addComponent(susceptibleMaxSpeedLabel)
			.addComponent(zombieMinSpeedLabel)
			.addComponent(zombieMaxSpeedLabel)
			.addComponent(birthChanceLabel)
			.addComponent(deathChanceLabel)
			.addComponent(winCombatLabel))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(susceptibleMinSpeedField)
			.addComponent(susceptibleMaxSpeedField)
			.addComponent(zombieMinSpeed)
			.addComponent(zombieMaxSpeed)
			.addComponent(birthChance)
			.addComponent(deathChance)
			.addComponent(winCombat)));

	agentsGroupLayout.setVerticalGroup(
		agentsGroupLayout.createSequentialGroup()
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(susceptibleMinSpeedLabel)
			.addComponent(susceptibleMinSpeedField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(susceptibleMaxSpeedLabel)
			.addComponent(susceptibleMaxSpeedField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(zombieMinSpeedLabel)
			.addComponent(zombieMinSpeed, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(zombieMaxSpeedLabel)
			.addComponent(zombieMaxSpeed, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(birthChanceLabel)
			.addComponent(birthChance, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(deathChanceLabel)
			.addComponent(deathChance, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(agentsGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(winCombatLabel)
			.addComponent(winCombat, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    );

	returner.add(agentsPanel);

	// ===================================================================

        JLabel awarenessRaisedAtLabel = new JLabel("Awareness Raised at: ");
        final JTextField awarenessRaisedAtField = new JTextField();
        awarenessRaisedAtField.setText(getAwarenessRaisedAt() + "");
        awarenessRaisedAtLabel.setLabelFor(awarenessRaisedAtField);
        awarenessRaisedAtField.setToolTipText("The percentage of the population that must be affected by monsters before awareness of the outbreak is raised");

	final JCheckBox awarenessRaisedBox = new JCheckBox();
	awarenessRaisedBox.setText("Awareness Raised?");
//	awarenessRaisedBox.setSelected(canRemovedBecomeZombies());
        awarenessRaisedBox.setToolTipText("Check this box to start the population already aware of the outbreak");

        JLabel awareChanceSusceptibleLabel = new JLabel("Aware Chance Susceptible Wins: ");
        final JTextField awareChanceSusceptibleField = new JTextField();
        awareChanceSusceptibleField.setText(getAwareChanceASusceptibleWinsEncounter() + "");
        awareChanceSusceptibleLabel.setLabelFor(awareChanceSusceptibleField);
        awareChanceSusceptibleField.setToolTipText("Percentage chance a healthy individual will defeat a monster in an encounter after awareness has been raised");

        JLabel awareChanceInfectionTransmitsLabel = new JLabel("Aware Chance Infection Transmits: ");
        final JTextField awareChanceInfectionTransmitsField = new JTextField();
        awareChanceInfectionTransmitsField.setText(getAwareChanceInfectionTransmits() + "");
        awareChanceInfectionTransmitsLabel.setLabelFor(awareChanceInfectionTransmitsField);
        awareChanceInfectionTransmitsField.setToolTipText("Percentage chance infection transmits when a healthy person is defeated by a monster, after awareness has been raised");

        CommitPanel awarePanel = new CommitPanel("Awareness") {

            @Override
            public boolean save() {

                try {

                    setAwarenessRaised(awarenessRaisedBox.isSelected());
                    setAwarenessRaisedAt(Double.parseDouble(awarenessRaisedAtField.getText()));
                    setAwareChanceASusceptibleWinsEncounter(Double.parseDouble(awareChanceSusceptibleField.getText()));
                    setAwareChanceInfectionTransmits(Double.parseDouble(awareChanceInfectionTransmitsField.getText()));
                    return true;
                }
                catch (NumberFormatException e)
                {
                    return false;
                }

            }
        };

        GroupLayout awarenessGroupLayout = new GroupLayout(awarePanel);
	awarePanel.setLayout(awarenessGroupLayout);

	awarenessGroupLayout.setAutoCreateContainerGaps(true);
	awarenessGroupLayout.setAutoCreateGaps(true);

	awarenessGroupLayout.setHorizontalGroup(
	    awarenessGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
		    .addGroup(awarenessGroupLayout.createSequentialGroup()
			    .addGroup(awarenessGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(awarenessRaisedAtLabel)
				.addComponent(awareChanceSusceptibleLabel)
				.addComponent(awareChanceInfectionTransmitsLabel))
			    .addGroup(awarenessGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(awarenessRaisedAtField)
				.addComponent(awareChanceSusceptibleField)
				.addComponent(awareChanceInfectionTransmitsField)))
		    .addComponent(awarenessRaisedBox)
		);

	awarenessGroupLayout.setVerticalGroup(
		awarenessGroupLayout.createSequentialGroup()
		    .addGroup(awarenessGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(awarenessRaisedAtLabel)
			.addComponent(awarenessRaisedAtField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(awarenessGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(awareChanceSusceptibleLabel)
			.addComponent(awareChanceSusceptibleField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(awarenessGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(awareChanceInfectionTransmitsLabel)
			.addComponent(awareChanceInfectionTransmitsField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addComponent(awarenessRaisedBox)
		);


	returner.add(awarePanel);

	return returner;
    }
    
    public int getLatencyPeriod()
    {
        return latencyPeriod;
    }

    public int getSusceptibleMaxSpeed() {
        return susceptibleMaxSpeed;
    }

    public int getSusceptibleMinSpeed() {
        return susceptibleMinSpeed;
    }

    public int getZombifiedMaxSpeed() {
        return zombifiedMaxSpeed;
    }

    public int getZombifiedMinSpeed() {
        return zombifiedMinSpeed;
    }

    public int getInfectionRange() {
	return infectionRange;
    }

    public double getChanceOfNaturalDeath()
    {
	return chanceOfNaturalDeath;
    }

    public double getChanceOfNaturalInfection()
    {
	return chanceOfNaturalInfection;
    }

    public int getInitialInfected()
    {
	return initialInfected;
    }

    public int getInitialZombified()
    {
	return initialZombified;
    }

    public double getChanceASusceptibleWinsEncounter()
    {
	return chanceASusceptibleWinsEncounter;
    }

    public double getChanceOfBirth()
    {
	return chanceOfBirth;
    }

    public void setChanceASusceptibleWinsEncounter(double chanceASusceptibleWinsEncounter) {
	this.chanceASusceptibleWinsEncounter = chanceASusceptibleWinsEncounter;
    }

    public void setChanceOfBirth(double chanceOfBirth) {
	this.chanceOfBirth = chanceOfBirth;
    }

    public void setChanceOfNaturalDeath(double chanceOfNaturalDeath) {
	this.chanceOfNaturalDeath = chanceOfNaturalDeath;
    }

    public void setChanceOfNaturalInfection(double chanceOfNaturalInfection) {
	this.chanceOfNaturalInfection = chanceOfNaturalInfection;
    }

    public void setInfectionRange(int infectionRange) {
	this.infectionRange = infectionRange;
    }

    public void setInitialInfected(int initialInfected) {
	this.initialInfected = initialInfected;
    }

    public void setInitialZombified(int initialZombified) {
	this.initialZombified = initialZombified;
    }

    public void setLatencyPeriod(int latencyPeriod) {
	this.latencyPeriod = latencyPeriod;
    }

    public void setSusceptibleMaxSpeed(int susceptibleMaxSpeed) {
	this.susceptibleMaxSpeed = susceptibleMaxSpeed;
	this.setAgentMaxSpeed(susceptibleMaxSpeed);
    }

    public void setSusceptibleMinSpeed(int susceptibleMinSpeed) {
	this.susceptibleMinSpeed = susceptibleMinSpeed;
	this.setAgentMinSpeed(susceptibleMinSpeed);
    }

    public void setZombifiedMaxSpeed(int zombifiedMaxSpeed) {
	this.zombifiedMaxSpeed = zombifiedMaxSpeed;
    }

    public void setZombifiedMinSpeed(int zombifiedMinSpeed) {
	this.zombifiedMinSpeed = zombifiedMinSpeed;
    }

    public double getChanceInfectionTransmits()
    {
	return chanceInfectionTransmits;
    }

    public void setChanceInfectionTransmits(double chance)
    {
	chanceInfectionTransmits = chance;
    }

    public double getAwareChanceASusceptibleWinsEncounter() {
        return awareChanceASusceptibleWinsEncounter;
    }

    public void setAwareChanceASusceptibleWinsEncounter(double awareChanceASusceptibleWinsEncounter) {
        this.awareChanceASusceptibleWinsEncounter = awareChanceASusceptibleWinsEncounter;
    }

    public double getAwareChanceInfectionTransmits() {
        return awareChanceInfectionTransmits;
    }

    public void setAwareChanceInfectionTransmits(double awareChanceInfectionTransmits) {
        this.awareChanceInfectionTransmits = awareChanceInfectionTransmits;
    }

    public boolean isAwarenessRaised() {
        return awarenessRaised;
    }

    public void setAwarenessRaised(boolean awarenessRaised) {
        this.awarenessRaised = awarenessRaised;
    }

    public double getAwarenessRaisedAt() {
        return awarenessRaisedAt;
    }

    public void setAwarenessRaisedAt(double awarenessRaisedAt) {
        this.awarenessRaisedAt = awarenessRaisedAt;
    }

}
