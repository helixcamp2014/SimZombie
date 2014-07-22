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

import simzombie.engine.environment.Environment;
import simzombie.engine.environment.EnvironmentEditor;
import simzombie.engine.utils.CommitPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * Contains all the standard parameters that all simulations executed in SimZombie should implement
 *
 * This class is Abstract, as is it recommended that simulation types extend this
 * class to include parameters which are specific to that simulation only.
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public abstract class Parameters implements Serializable {

    private static final long serialVersionUID = 1L;

    void setEnvironment(Environment tempEnvironment) {
        environment = tempEnvironment;
    }

    /**
     * This may be specific to zombie simulations only, but also may not.
     *
     * Out of a choice between the two, I've opted to include it in all simulations,
     * although that does not necessarily mean this will be used
     */
    public enum TimeOfDay
    {
        /**
         * Day time
         */
        DAY,
        /**
         * Night time
         */
        NIGHT;
    }

    /**
     * All simulations store an environment in their parameters, for easy access
     */
    private Environment environment = new Environment();

    /**
     * If true, simulations are logged so they can be saved or replayed.
     *
     * If this behaviour is not desirable, setting this to false will run simulations
     * with significantly less ram usage
     */
    private boolean loggingEnabled = true;

    /**
     * If true, graphics are displayed initially
     */
    private boolean displayGraphics = true;

    /**
     * If true, the graph is displayed initially
     */
    private boolean displayGraph = true;

    /**
     * Number of milliseconds to pause between each step - usually only used for debugging purposes
     */
    private int stepDelay = 0;

    /**
     * If this is true, the simulation pauses after each step.  Usage is mainly for debugging.
     */
    private boolean timeStepping = false;

    /**
     * Initial TOTAL population of the simulation
     *
     * Any non-standard agents are subtracted from this total,
     * for example a simulation with 5 Zombified agents will start with
     * 5995 Susceptible and 5 Zombified agents
     */
    private int population = 6000;

    /**
     * Maximum speed of standard agents
     */
    private int agentMaxSpeed = 3;

    /**
     * Minumum speed of standard agents
     */
    private int agentMinSpeed = 1;

    /**
     * Current lunar phase depicted in this simulation.
     *
     * While not strictly a parameter, this seemed like the most sensible place
     * for this to be stored - arguably, it should be stored in some sort of
     * Collection in the Simulation class, consider this later.
     */
    private int currentLunarPhase = 0;

    /**
     * Number of times the simulation repeats.  This was partially implemented
     * to average over a large number of simulation executions, but was never
     * entirely completed.  I believe MATLAB output is still created when
     * repeats run, but there is no in-program support for graphing this
     */
    private int repeats = 1;

    /**
     * Current time of the day
     */
    private TimeOfDay timeOfDay = TimeOfDay.DAY;

    /**
     * Whether monster agents are active during the day
     *
     * This should, strictly, be part of {@link simzombie.engine.simulations.zombies.ZombieParameters}, refactor later
     */
    private boolean monsterActiveDuringDay = true;

    /**
     * Whether monsters agents are active during the night
     *
     * This should, strictly, be part of {@link simzombie.engine.simulations.zombies.ZombieParameters}, refactor later
     */
    private boolean monsterActiveDuringNight = true;

    /**
     * Whether a monster is active during a specified lunar phase
     *
     * The index of the array corresponds to the integer for lunar phase.
     * This should, strictly, be part of {@link simzombie.engine.simulations.zombies.ZombieParameters}, refactor later
     */
    private boolean[] activeDuringLunarPhase =
    {   true, true, true, true,
        true, true, true, true,
        true, true, true, true,
        true, true, true, true,
        true, true, true, true,
        true, true, true, true,
        true, true, true, true};

    /**
     * Name of this specific simulation
     */
    private String name = "Simulation";

    public List<CommitPanel> getPanels()
    {
	List<CommitPanel> returner = new ArrayList<CommitPanel> ();

	// ===================================================================

	JLabel titleLabel = new JLabel("Simulation Title: ", JLabel.RIGHT);
	final JTextField title = new JTextField();
	titleLabel.setLabelFor(title);
	title.setText("Simulation");
        title.setToolTipText("The title of the simulation");

	JLabel stepDelayLabel = new JLabel("Delay Between Steps: ", JLabel.RIGHT);
	final JTextField stepDelayField = new JTextField();
	stepDelayLabel.setLabelFor(stepDelayField);
	stepDelayField.setText(getStepDelay() + "");
        stepDelayField.setToolTipText("Number of milliseconds to wait between each step - increase this if you want the simulation to run slower");

	final JCheckBox showGraphics = new JCheckBox();
	showGraphics.setText("Show Graphics");
	showGraphics.setSelected(displayGraphics());
        showGraphics.setToolTipText("Uncheck this to stop graphical windows opening by default");

	final JCheckBox loggingEnabledBox = new JCheckBox();
	loggingEnabledBox.setText("Logging Enabled");
	loggingEnabledBox.setSelected(getLoggingEnabled());
        loggingEnabledBox.setToolTipText("Unchecking this uses less memory, but the simulation will not be replayable or saveable");

        JLabel repeatsLabel = new JLabel("Repeats: ", JLabel.RIGHT);
        final JTextField repeatsField = new JTextField();
        repeatsLabel.setLabelFor(repeatsField);
        repeatsField.setText(getRepeats() + "");
        repeatsField.setToolTipText("This will restart the simulation with a new random seed immediately after simulation termination - useful for averaging over a number of runs");

	CommitPanel simulationPanel = new CommitPanel("Simulation") {

	    @Override
	    public boolean save() {
		    // -----
		try
		{
		    setStepDelay(Integer.parseInt(stepDelayField.getText()));
		    setDisplayGraphics(showGraphics.isSelected());
		    setLoggingEnabled(loggingEnabledBox.isSelected());
                    setRepeats(Integer.parseInt(repeatsField.getText()));
		    return true;
		}
		catch (NumberFormatException nfe)
		{
		    return false;
		}
	    }
	    
	};
	
	GroupLayout simulationGroupLayout = new GroupLayout(simulationPanel);
	simulationPanel.setLayout(simulationGroupLayout);

	simulationGroupLayout.setAutoCreateGaps(true);
	simulationGroupLayout.setAutoCreateContainerGaps(true);

	simulationGroupLayout.setHorizontalGroup(
		simulationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
		    .addGroup(simulationGroupLayout.createSequentialGroup()
			.addGroup(simulationGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			    .addComponent(titleLabel)
			    .addComponent(stepDelayLabel)
                            .addComponent(repeatsLabel))
			.addGroup(simulationGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			    .addComponent(title)
			    .addComponent(stepDelayField)
                            .addComponent(repeatsField)))
		    .addGroup(simulationGroupLayout.createSequentialGroup()
			.addComponent(showGraphics))
	            .addGroup(simulationGroupLayout.createSequentialGroup()
			.addComponent(loggingEnabledBox))
		    );

	simulationGroupLayout.setVerticalGroup(
		simulationGroupLayout.createSequentialGroup()
		    .addGroup(simulationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(titleLabel)
			.addComponent(title, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(simulationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(stepDelayLabel)
			.addComponent(stepDelayField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(simulationGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(repeatsLabel)
                        .addComponent(repeatsField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(simulationGroupLayout.createParallelGroup()
			.addComponent(showGraphics))
		    .addGroup(simulationGroupLayout.createParallelGroup()
			.addComponent(loggingEnabledBox))
			);

	returner.add(simulationPanel);

	// ===================================================================

	JLabel cellsWideLabel = new JLabel("Cells Wide: ", JLabel.RIGHT);
	final JSpinner cellsWideSpinner = new JSpinner();
	cellsWideLabel.setLabelFor(cellsWideSpinner);
	((JSpinner.DefaultEditor)cellsWideSpinner.getEditor()).getTextField().setName("Cells Wide");
	((JSpinner.DefaultEditor)cellsWideSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
	cellsWideSpinner.setValue(getCellsWide());
        cellsWideSpinner.setToolTipText("Number of Cells in the environment's width");

	JLabel cellsHighLabel = new JLabel("Cells High: ", JLabel.RIGHT);
	final JSpinner cellsHighSpinner = new JSpinner();
	cellsHighLabel.setLabelFor(cellsHighSpinner);
	cellsHighSpinner.setValue(getCellsHigh());
	((JSpinner.DefaultEditor)cellsHighSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
        cellsHighSpinner.setToolTipText("Number of Cells in the environment's height");

	JLabel cellWidthLabel = new JLabel("Cell Width: ", JLabel.RIGHT);
	final JTextField cellWidthField = new JTextField();
	cellWidthField.setEnabled(true);
	cellWidthField.setColumns(6);
	cellWidthLabel.setLabelFor(cellWidthField);
	cellWidthField.setText(getCellWidth() + "");
        cellWidthField.setToolTipText("Width of the Cells in pixels");

	JLabel cellHeightLabel = new JLabel("Cell Height: ", JLabel.RIGHT);
	final JTextField cellHeightField = new JTextField();
	cellHeightField.setEnabled(true);
	cellHeightField.setColumns(6);
	cellHeightLabel.setLabelFor(cellHeightField);
	cellHeightField.setText(getCellHeight() + "");
        cellHeightField.setToolTipText("Height of the Cells in pixels");

	JLabel environmentWidthLabel = new JLabel("Environment Width: ", JLabel.RIGHT);
	final JTextField environmentWidth = new JTextField();
	environmentWidth.setEnabled(false);
	environmentWidth.setColumns(6);
	environmentWidthLabel.setLabelFor(environmentWidth);
	environmentWidth.setText(getEnvironmentWidth() + "");
        environmentWidth.setToolTipText("Not currently functional");

	JLabel environmentHeightLabel = new JLabel("Environment Height: ", JLabel.RIGHT);
	final JTextField environmentHeight = new JTextField();
	environmentHeight.setEnabled(false);
	environmentHeight.setColumns(6);
	environmentHeightLabel.setLabelFor(environmentHeight);
	environmentHeight.setText(getEnvironmentHeight() + "");
        environmentHeight.setToolTipText("Not currently functional");

	final CommitPanel environmentPanel = new CommitPanel("Environment") {

	    @Override
	    public boolean save() {
		try
		{
		    // -----
		    setCellsWide((Integer)cellsWideSpinner.getValue());
		    setCellWidth(Integer.parseInt(cellWidthField.getText()));
		    setCellsHigh((Integer)cellsHighSpinner.getValue());
		    setCellHeight(Integer.parseInt(cellHeightField.getText()));
		    return true;
		}
		catch (NumberFormatException nfe)
		{
		    return false;
		}
	    }
	};

	final Parameters p = this;
	JButton configureWalls = new JButton("Configure Walls");
	configureWalls.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		environmentPanel.save();
		EnvironmentEditor ee = new EnvironmentEditor(environment);
		ee.setVisible(true);
		ee.setBounds(0, 0, ee.getPreferredSize().width, ee.getPreferredSize().height);
		SimZombie.addFrame(ee);
	    }
	});

	GroupLayout environmentGroupLayout = new GroupLayout(environmentPanel);
	environmentPanel.setLayout(environmentGroupLayout);

	environmentGroupLayout.setAutoCreateGaps(true);
	environmentGroupLayout.setAutoCreateContainerGaps(true);

	environmentGroupLayout.setHorizontalGroup(
	    environmentGroupLayout.createSequentialGroup()
		    .addGroup(environmentGroupLayout.createParallelGroup()
		        .addGroup(environmentGroupLayout.createSequentialGroup()
			    .addGroup(environmentGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(cellsWideLabel)
				.addComponent(cellWidthLabel)
				.addComponent(environmentWidthLabel))
			    .addGroup(environmentGroupLayout.createParallelGroup()
				.addComponent(cellsWideSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(cellWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(environmentWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))))
		    .addGroup(environmentGroupLayout.createParallelGroup()
		        .addGroup(environmentGroupLayout.createSequentialGroup()
			    .addGroup(environmentGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(cellsHighLabel)
				.addComponent(cellHeightLabel)
				.addComponent(environmentHeightLabel))
			    .addGroup(environmentGroupLayout.createParallelGroup()
				.addComponent(cellsHighSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(cellHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(environmentHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
			    .addComponent(configureWalls, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)));

	environmentGroupLayout.setVerticalGroup(
		environmentGroupLayout.createSequentialGroup()
		    .addGroup(environmentGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(cellsWideLabel)
			.addComponent(cellsWideSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(cellsHighLabel)
			.addComponent(cellsHighSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(environmentGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(cellWidthLabel)
			.addComponent(cellWidthField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(cellHeightLabel)
			.addComponent(cellHeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(environmentGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(environmentWidthLabel)
			.addComponent(environmentWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(environmentHeightLabel)
			.addComponent(environmentHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(environmentGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(configureWalls)));

	returner.add(environmentPanel);

	// ===================================================================

        final JCheckBox activeDuringDayBox = new JCheckBox();
        activeDuringDayBox.setText("Active During Day");
        activeDuringDayBox.setSelected(isMonsterActiveDuringDay());
        activeDuringDayBox.setToolTipText("Checking this box implies your monster is active during simulation steps considered 'daytime'");

        final JCheckBox activeDuringNightBox = new JCheckBox();
        activeDuringNightBox.setText("Active During Night");
        activeDuringNightBox.setSelected(isMonsterActiveDuringNight());
        activeDuringNightBox.setToolTipText("Checking this box implies your monster is active during simulation steps considered 'nighttime'");

	JLabel activeDuringLunarPhaseLabel = new JLabel("Active During Lunar Phase:");

	final JCheckBox[] lunarPhases = new JCheckBox[28];
	for (int i = 0; i < 28; i++)
	{
	    JCheckBox box = new JCheckBox();
	    box.setText("Phase " + (i+1));
	    box.setSelected(activeDuringLunarPhase(i));
            box.setToolTipText("Checking this box implies your monster is active on day " + (i+1) + " of the month - assume days 13 to 16 are the Full Moon");
	    lunarPhases[i] = box;
	}

	CommitPanel activePanel = new CommitPanel("Activity") {

	    @Override
	    public boolean save() {

		setMonsterActiveDuringDay(activeDuringDayBox.isSelected());
		setMonsterActiveDuringNight(activeDuringNightBox.isSelected());
		for (int i = 0; i < 28; i++)
		{
		    setActiveDuringLunarPhase(i, lunarPhases[i].isSelected());
		}

		return true;
	    }
	};

	GroupLayout activeGroupLayout = new GroupLayout(activePanel);
	activePanel.setLayout(activeGroupLayout);

	activeGroupLayout.setAutoCreateGaps(true);
	activeGroupLayout.setAutoCreateContainerGaps(true);

	activeGroupLayout.setHorizontalGroup(
		activeGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		    .addGroup(activeGroupLayout.createSequentialGroup()
			.addComponent(activeDuringDayBox))
	            .addGroup(activeGroupLayout.createSequentialGroup()
			.addComponent(activeDuringNightBox))
	            .addGroup(activeGroupLayout.createSequentialGroup()
			.addComponent(activeDuringLunarPhaseLabel))
		    .addGroup(activeGroupLayout.createSequentialGroup()
			.addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			    .addComponent(lunarPhases[0])
			    .addComponent(lunarPhases[4])
			    .addComponent(lunarPhases[8])
			    .addComponent(lunarPhases[12])
			    .addComponent(lunarPhases[16])
			    .addComponent(lunarPhases[20])
                            .addComponent(lunarPhases[24]))
			.addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			    .addComponent(lunarPhases[1])
			    .addComponent(lunarPhases[5])
			    .addComponent(lunarPhases[9])
			    .addComponent(lunarPhases[13])
			    .addComponent(lunarPhases[17])
			    .addComponent(lunarPhases[21])
                            .addComponent(lunarPhases[25]))
			.addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			    .addComponent(lunarPhases[2])
			    .addComponent(lunarPhases[6])
			    .addComponent(lunarPhases[10])
			    .addComponent(lunarPhases[14])
			    .addComponent(lunarPhases[18])
			    .addComponent(lunarPhases[22])
                            .addComponent(lunarPhases[26]))
			.addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			    .addComponent(lunarPhases[3])
			    .addComponent(lunarPhases[7])
			    .addComponent(lunarPhases[11])
			    .addComponent(lunarPhases[15])
			    .addComponent(lunarPhases[19])
			    .addComponent(lunarPhases[23])
                            .addComponent(lunarPhases[27])))
		    );

	activeGroupLayout.setVerticalGroup(
		activeGroupLayout.createSequentialGroup()
		    .addGroup(activeGroupLayout.createParallelGroup()
			.addComponent(activeDuringDayBox))
		    .addGroup(activeGroupLayout.createParallelGroup()
			.addComponent(activeDuringNightBox))
		    .addGroup(activeGroupLayout.createParallelGroup()
			.addComponent(activeDuringLunarPhaseLabel))
		    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lunarPhases[0])
			.addComponent(lunarPhases[1])
			.addComponent(lunarPhases[2])
			.addComponent(lunarPhases[3]))
		    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(lunarPhases[4])
			.addComponent(lunarPhases[5])
			.addComponent(lunarPhases[6])
			.addComponent(lunarPhases[7]))
                    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lunarPhases[8])
                        .addComponent(lunarPhases[9])
                        .addComponent(lunarPhases[10])
                        .addComponent(lunarPhases[11]))
                    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lunarPhases[12])
                        .addComponent(lunarPhases[13])
                        .addComponent(lunarPhases[14])
                        .addComponent(lunarPhases[15]))
                    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lunarPhases[16])
                        .addComponent(lunarPhases[17])
                        .addComponent(lunarPhases[18])
                        .addComponent(lunarPhases[19]))
                    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lunarPhases[20])
                        .addComponent(lunarPhases[21])
                        .addComponent(lunarPhases[22])
                        .addComponent(lunarPhases[23]))
                    .addGroup(activeGroupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lunarPhases[24])
                        .addComponent(lunarPhases[25])
                        .addComponent(lunarPhases[26])
                        .addComponent(lunarPhases[27]))
			);

	returner.add(activePanel);

	return returner;
    }

    public int getPopulation()
    {
        return population;
    }

    public int getCellWidth()
    {
        return environment.getCellWidth();
    }

    public int getCellHeight()
    {
        return environment.getCellHeight();
    }

    public int getCellsHigh() {
        return environment.getCellsHigh();
    }

    public int getCellsWide() {
        return environment.getCellsWide();
    }

    public int getEnvironmentWidth()
    {
        return environment.getWidth();
    }

    public int getEnvironmentHeight()
    {
        return environment.getHeight();
    }

    public boolean displayGraphics()
    {
        return displayGraphics;
    }

    public boolean displayGraph()
    {
        return displayGraph;
    }

    public boolean isTimeStepping() {
	return timeStepping;
    }

    public int getStepDelay() {
	return stepDelay;
    }

    public void setCellsHigh(int cellsHigh) {
	environment.setCellsHigh(cellsHigh);
    }

    public void setCellsWide(int cellsWide) {
	environment.setCellsWide(cellsWide);
    }

    public void setDisplayGraph(boolean displayGraph) {
	this.displayGraph = displayGraph;
    }

    public void setDisplayGraphics(boolean displayGraphics) {
	this.displayGraphics = displayGraphics;
    }

    public void setPopulation(int population) {
	this.population = population;
    }

    public void setStepDelay(int stepDelay) {
	this.stepDelay = stepDelay;
    }

    public int getAgentMaxSpeed() {
	return agentMaxSpeed;
    }

    public void setAgentMaxSpeed(int agentMaxSpeed) {
	this.agentMaxSpeed = agentMaxSpeed;
    }

    public int getAgentMinSpeed() {
	return agentMinSpeed;
    }

    public void setAgentMinSpeed(int agentMinSpeed) {
	this.agentMinSpeed = agentMinSpeed;
    }

    public boolean getLoggingEnabled()
    {
	return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled)
    {
	this.loggingEnabled = loggingEnabled;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public Environment getEnvironment()
    {
	return environment;
    }

    public void setCellWidth(int width)
    {
	environment.setCellWidth(width);
    }

    public void setCellHeight(int height)
    {
	environment.setCellHeight(height);
    }

    public void setRepeats(int repeats)
    {
        this.repeats = repeats;
    }

    public int getRepeats()
    {
        return repeats;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public boolean activeDuringLunarPhase(int i) {
        return activeDuringLunarPhase[i];
    }

    public void setActiveDuringLunarPhase(int lunarPhase, boolean active) {
        activeDuringLunarPhase[lunarPhase] = active;
    }

    public boolean isMonsterActiveDuringDay() {
        return monsterActiveDuringDay;
    }

    public void setMonsterActiveDuringDay(boolean monsterActiveDuringDay) {
        this.monsterActiveDuringDay = monsterActiveDuringDay;
    }

    public boolean isMonsterActiveDuringNight() {
        return monsterActiveDuringNight;
    }

    public void setMonsterActiveDuringNight(boolean monsterActiveDuringNight) {
        this.monsterActiveDuringNight = monsterActiveDuringNight;
    }

    public int getCurrentLunarPhase()
    {
        return currentLunarPhase;
    }

    public void setCurrentLunarPhase(int i)
    {
        currentLunarPhase = i;
    }


}
