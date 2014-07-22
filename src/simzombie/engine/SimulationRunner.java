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
import simzombie.engine.graph.Plot;
import simzombie.engine.simulations.Simulation;
import simzombie.engine.simulations.zombies.ZombieParameters;
import simzombie.engine.simulations.zombies.ZombieSimulation;
import simzombie.engine.utils.AnimatedGIFWriter;
import simzombie.engine.utils.CSVFilter;
import simzombie.engine.utils.FilterUtils;
import simzombie.engine.utils.GraphInternalFrame;
import simzombie.engine.utils.Icons;
import simzombie.engine.utils.ImageFileFilter;
import simzombie.engine.utils.MSlider;
import simzombie.engine.utils.ScrollableDesktopPane;
import simzombie.engine.utils.UneditableDefaultTableModel;
import simzombie.engine.utils.XMLFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Matthew
 */
public class SimulationRunner {

    private JInternalFrame outerFrame = new JInternalFrame("");
    private JDesktopPane desktop = new ScrollableDesktopPane();
    private Simulation simulation;
    private Parameters parameters; // = new Parameters();
    private final ArrayList<List<SavedStep>> collectionOfHistories = new ArrayList<List<SavedStep>>();
    private final List<SavedStep> simulationHistory = new Vector<SavedStep>();
    private ListIterator<SavedStep> simulationHistoryIterator = simulationHistory.listIterator();
    private int updates = 0;
    private long lastUpdate = System.currentTimeMillis();
    private long lastPauseTimer = System.currentTimeMillis();
    private SimCanvas sc; // = new SimCanvas();
    private GraphInternalFrame graphWindow;
    private GraphInternalFrame eulerGraphWindow;   
    private JInternalFrame tabularWindow = new JInternalFrame("Tabular Data");
    private boolean simComplete = false;
    private boolean paused = false;
    private String name;
    private JLabel frame = new JLabel();
    private MSlider rewindSlider = new MSlider(0, 0);
    private final JButton pauseButton = new JButton("Pause");
    private final JButton rewindButton = new JButton("Rewind");
    private final JButton forwardButton = new JButton("Forward");
    private Map<AgentType, Color> typeToColour = Collections.synchronizedMap(new HashMap<AgentType, Color> ());
    private final List<Color> potentialColors = new ArrayList<Color> ();

    private void resetSimulation() 
    {
        while(simulationHistoryIterator.hasNext())
        {
            simulationHistoryIterator.next();
            simulationHistoryIterator.remove();
        }
        while(simulationHistoryIterator.hasPrevious())
        {
            simulationHistoryIterator.previous();
            simulationHistoryIterator.remove();
        }
        simulation.reset();
        graphWindow.getGraph().reset();
        updates = 0;
    }
    
    public Map<AgentType, Integer> getAgentCount()
    {
	Map<AgentType, Integer> agentCount = new HashMap<AgentType, Integer>();
	for (AgentType a : simulation.getAgentTypes())
	{
	    agentCount.put(a, 0);
	}
	for (Agent a : parameters.getEnvironment().getAgents())
	{
	    agentCount.put(a.getType(), agentCount.get(a.getType()) + 1);
	}
	return agentCount;
    }

    private void forcePausedTo(boolean force)
    {
	if (force)
	{
	    paused = true;
	    pauseButton.setIcon(Icons.getImageIcon(Icons.playIconLocation));

	    if (parameters.getLoggingEnabled())
	    {
		rewindSlider.setEnabled(true);
		rewindButton.setEnabled(true);
		forwardButton.setEnabled(true);
	    }
	}
	else
	{
	    pauseButton.setIcon(Icons.getImageIcon(Icons.pauseIconLocation));
	    paused = false;
	    rewindSlider.setEnabled(false);
	    rewindButton.setEnabled(false);
	    forwardButton.setEnabled(false);
	}
    }

    private void togglePause()
    {
	forcePausedTo(!paused);
    }

    private boolean updateSimulation()
    {
        if (Math.abs(lastUpdate - System.currentTimeMillis()) > parameters.getStepDelay())
        {
	    if (simulationHistoryIterator.hasNext())
	    {
		if (Math.abs(lastPauseTimer - System.currentTimeMillis()) > 0)
		{
		    SavedStep ss = simulationHistoryIterator.next();
		    forceSimulationToUpdateToStep(ss);
		    rewindSlider.setValue(ss.getStepNumber(), false);
		    graphWindow.getGraphCanvas().setPointer(ss.getStepNumber());
		    graphWindow.getGraphCanvas().setDisplayPointer(true);
		    sc.repaint();
		    graphWindow.repaint();
		    lastPauseTimer = System.currentTimeMillis();
		}
		return false;
	    }
	    
	    if (simComplete) return true;

	    graphWindow.getGraphCanvas().setDisplayPointer(false);

	    boolean completesThisRun = simulation.updateEnvironment(parameters.getEnvironment());

	    if (parameters.getLoggingEnabled())
	    {
		ArrayList<Agent> history = new ArrayList<Agent>();
		synchronized(parameters.getEnvironment().getAgents())
		{
		    for (Agent a : parameters.getEnvironment().getAgents())
		    {
			history.add(a.createCopy());
		    }
		}

                ZombieParameters zp = (ZombieParameters) parameters;
		SavedStep ss = new SavedStep(updates, history, zp.isAwarenessRaised());
		simulationHistoryIterator.add(ss);
	    }
	    
	    frame.setText(updates + "");
	    rewindSlider.setMaximum(updates);
	    rewindSlider.setValue(updates, false);

	    Map<AgentType, Integer> agentCount = getAgentCount();

	    updateGraph(agentCount);
	    updateTable(agentCount);

            sc.repaint();
	    updates++;

            lastUpdate = System.currentTimeMillis();
            return (completesThisRun);
        }
        return false;
    }

    public void updateTable(Map<AgentType, Integer> agentCount)
    {
	Vector<Object> data = new Vector<Object> ();
	data.add(updates);
	for (AgentType at : simulation.getAgentTypes())
	{
	    data.add(agentCount.get(at));
	}
	data.add(parameters.getEnvironment().getAgents().size());
	tableModel.addRow(data);
    }

    public void updateGraph(Map<AgentType, Integer> agentCount)
    {
	for (AgentType t : simulation.getAgentTypes())
	{
	    if (t == ZombieSimulation.SUSCEPTIBLE || t == ZombieSimulation.ZOMBIFIED || t == ZombieSimulation.REMOVED || t == ZombieSimulation.INFECTED)
	    graphWindow.getGraph().addPlot(new Plot(t.getName(), updates, agentCount.get(t), typeToColour.get(t)));
	}

	graphWindow.getGraph().addPlot(new Plot("Total", updates, parameters.getEnvironment().getAgents().size(), Color.darkGray));
	graphWindow.repaint();
    }

    public void setPaused(boolean set)
    {
        paused = set;
    }

    public String getName()
    {
	return name;
    }

    private boolean eulerComplete = false;
    
    public boolean update()
    {
        if (!simComplete || !eulerComplete || simulationHistoryIterator.hasNext())
        {
	    if (!eulerComplete)
	    {
		eulerComplete = simulation.updateNumericalAnalysis(eulerGraphWindow.getGraph());
		eulerGraphWindow.repaint();
	    }

            if (!paused)
            {
		if (!simComplete)
		{
		    boolean oldSimComplete = simComplete;
		    simComplete = updateSimulation();
                    if (simComplete)
                    {
                        List<SavedStep> copy = new ArrayList<SavedStep>();
                        for (SavedStep ss : simulationHistory)
                        {
                            copy.add(ss);
                        }
                        collectionOfHistories.add(copy);
                        if (collectionOfHistories.size() < parameters.getRepeats())
                        {
                            // We've saved this simuation, so we want to RESET EVERYTHING
                            // AND START OVER. Yikes.

                            simComplete = false;
                            resetSimulation();
                        }
                        else if (parameters.getRepeats() > 1)
                        {
                            // we're complete now, and we're averaging!

                            Map<Integer, List<Agent>> agents = new HashMap<Integer, List<Agent>>();
                            Map<Integer, Integer> divisors = new HashMap<Integer, Integer>();
                            for (List<SavedStep> list : collectionOfHistories)
                            {
                                for (SavedStep ss : list)
                                {
                                    if (!agents.containsKey(ss.getStepNumber()))
                                    {
                                        agents.put(ss.getStepNumber(), new ArrayList<Agent>());
                                    }
                                    for (Agent a : ss.getAgents())
                                    {
                                        agents.get(ss.getStepNumber()).add(a);
                                    }
                                    
                                    if (!divisors.containsKey(ss.getStepNumber()))
                                    {
                                        divisors.put(ss.getStepNumber(), 0);
                                    }
                                    divisors.put(ss.getStepNumber(), divisors.get(ss.getStepNumber()) + 1);
                                }
                            }

                            Map<Integer, Map<AgentType, Integer>> map = new HashMap<Integer, Map<AgentType, Integer>>();
                            
                            for (Integer i : agents.keySet())
                            {
                                map.put(i, new HashMap<AgentType, Integer>());
                                for (AgentType at : simulation.getAgentTypes())
                                {
                                    map.get(i).put(at, 0);
                                    for (Agent a : agents.get(i))
                                    {
                                        if (a.isOfType(at))
                                        {
                                            map.get(i).put(at, map.get(i).get(at) + 1);
                                        }
                                    }
                                }
                            }
                            
                            System.out.print("T = [");
                            for (int i = 0; i < agents.keySet().size(); i++)
                            {
                                System.out.print(i + " ");
                            }
                            System.out.println("];");
                            for (AgentType at : simulation.getAgentTypes())
                            {
                                System.out.print(at.getName().charAt(0) + " = [");
                                for (int i = 0; i < agents.keySet().size(); i++)
                                {
                                    System.out.print(map.get(i).get(at)/(float)divisors.get(i) + " ");
                                }
                                System.out.println("];");
                            }
                            System.out.println("hold on");
                            System.out.println("plot(T, S, 'g')");
                            System.out.println("plot(T, R, 'k')");
                            System.out.println("plot(T, Z, 'r')");
                            System.out.println("legend('Susceptibles', 'Removed', 'Zombified')");
                            System.out.println("hold off");
                        }
                    }
		    if (!oldSimComplete && simComplete)
		    {
			forcePausedTo(true);
		    }
		    if (parameters.isTimeStepping())
		    {
			paused = true;
		    }
		}
		else
		{
		    forceSimulationToUpdateToStep(simulationHistoryIterator.next());
		    sc.repaint();
		}
            }
        }
        
	return simComplete;
    }

    public SimulationRunner(String name, Simulation simulation, Parameters parameters)
    {
	this.simulation = simulation;
	this.parameters = parameters;
	this.name = name;
	sc = new SimCanvas(parameters, simulation.getAgentTypes());
	init(false);
    }

    public SimulationRunner(SavedSimulation ss, Simulation s)
    {
	simulation = s;
	this.parameters = ss.getParameters();
	this.name = ss.getName();
	sc = new SimCanvas(parameters, s.getAgentTypes());
	init(true);

	synchronized(simulationHistory)
	{
	    while (simulationHistoryIterator.hasNext())
	    {
		simulationHistoryIterator.next();
		simulationHistoryIterator.remove();
	    }
	    while (simulationHistoryIterator.hasPrevious())
	    {
		simulationHistoryIterator.previous();
		simulationHistoryIterator.remove();
	    }

	    updates = 0;

	    for (SavedStep sstep : ss.getSavedSteps())
	    {
		simulationHistoryIterator.add(sstep);
		forceSimulationToUpdateToStep(sstep);
		Map<AgentType, Integer> agentCount = getAgentCount();

		for (AgentType a : s.getAgentTypes())
		{
		    System.out.println(a.getName() + " " + agentCount.get(a));
		}

		updateGraph(agentCount);
		updateTable(agentCount);
		updates++;
	    }
	    rewindSlider.setMaximum(simulationHistory.size());
	    rewindSlider.setValue(simulationHistory.size(), false);
	    simComplete = true;
	    forcePausedTo(true);
	}
    }

    private void locateStepNumber(int i)
    {
	if (i > simulationHistory.size())
	{
	    System.out.println("This i " + (i) + " is greater than expected");
	    while (simulationHistoryIterator.hasNext())
	    {
		simulationHistoryIterator.next();
	    }
	    return;
	}

	if (i == 0)
	{
	    while (simulationHistoryIterator.hasPrevious())
	    {
		simulationHistoryIterator.previous();
	    }
	    return;
	}

	if (i == simulationHistoryIterator.nextIndex())
	{
	    return;
	}
	else if (i > simulationHistoryIterator.nextIndex())
	{
	    while (i != simulationHistoryIterator.nextIndex())
	    {
		simulationHistoryIterator.next();
	    }
	}
	else
	{
	    while (i != simulationHistoryIterator.nextIndex())
	    {
		simulationHistoryIterator.previous();
	    }
	}
    }

    public void saveSimulation()
    {
	if (parameters.getLoggingEnabled())
	{
	    try {

		JFileChooser inputChooser = SimZombie.SimFileChooser;
		int returnVal = inputChooser.showSaveDialog(outerFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
		    File selectedFile = inputChooser.getSelectedFile();
		    if (FilterUtils.getExtension(inputChooser.getSelectedFile()) == null)
		    {
			ImageFileFilter ff = (ImageFileFilter) inputChooser.getFileFilter();
			selectedFile = new File(inputChooser.getSelectedFile().getPath() + "." + ff.getExtension());
		    }

		    SavedSimulation savedSim = new SavedSimulation(name, parameters, simulationHistory, parameters.getEnvironment().getCellMap());
		    ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(selectedFile))));
		    oos.writeObject(savedSim);
		    oos.flush();
		    oos.close();

		    SimZombie.setStatusText("Simulation \"" + name + "\" successfully saved to: " + selectedFile.getPath());
		}
		else
		{
		    SimZombie.setStatusText("Simulation save cancelled by user");
		}
	    } catch (IOException ex) {
		Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	else
	{
	    SimZombie.setStatusText("Cannot save this simulation: Logging is Disabled");
	}
    }

    private void forceSimulationToUpdateToStep(SavedStep ss)
    {
	synchronized(parameters.getEnvironment().getAgents())
	{
	    parameters.getEnvironment().removeAllAgents();
	    frame.setText(ss.getStepNumber() + "");
	    rewindSlider.setValue(ss.getStepNumber(), false);
	    List<Agent> clonedAgents = new ArrayList<Agent>();
	    for (Agent a : ss.getAgents())
	    {
		clonedAgents.add(a.createCopy());
	    }
	    parameters.getEnvironment().addAgents(clonedAgents);
            	int stepsPerHalfDay = 2;
            int dayOrNight = ss.getStepNumber() / stepsPerHalfDay % 2;
            if (dayOrNight == 0) parameters.setTimeOfDay(Parameters.TimeOfDay.DAY);
            else if (dayOrNight == 1) parameters.setTimeOfDay(Parameters.TimeOfDay.NIGHT);

            int lunarPhase = (ss.getStepNumber()/(stepsPerHalfDay*2)) % 28;
            parameters.setCurrentLunarPhase(lunarPhase);

            ZombieParameters zp = (ZombieParameters) parameters;
            zp.setAwarenessRaised(ss.isAwarenessRaised());

	    graphWindow.getGraphCanvas().setPointer(ss.getStepNumber());
	}
    }
    
    
    private final JInternalFrame simWindow = new JInternalFrame("Graphics");

    private final JToggleButton toggleGraph = new JToggleButton("Simulation Graph");
    private final JToggleButton toggleNAGraph = new JToggleButton("Numerical Analysis Graph");
    private final JToggleButton toggleGraphics = new JToggleButton("Graphics");
    private final JToggleButton toggleTabular = new JToggleButton("Tabular");

    private DefaultTableModel tableModel;

    private JToggleButton setupWindowToggleButton(final JToggleButton button, final JInternalFrame frame)
    {
	button.setSelected(frame.isVisible());
	button.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		JToggleButton button = (JToggleButton)e.getSource();
		if (!frame.isVisible())
		{
		    frame.setVisible(true);
		    button.setSelected(true);
		}
		else
		{
		    frame.setVisible(false);
		    button.setSelected(false);
		}
	    }
	});

	return button;
    }

    protected void assignColours()
    {
	potentialColors.add(Color.GREEN);
	potentialColors.add(Color.ORANGE);
	potentialColors.add(Color.RED);
	potentialColors.add(Color.LIGHT_GRAY);
	ListIterator<Color> colors = potentialColors.listIterator(0);

	for (AgentType t : simulation.getAgentTypes())
	{
	    typeToColour.put(t, colors.next());
	}
    }

    // clear major variables
    public void cleanup()
    {
	paused = true;
	sc = null;
	simulationHistory.clear();
	parameters = null;
    }

    public void init(boolean loading)
    {
	assignColours();

	outerFrame.setLayout(new BorderLayout());

	outerFrame.setTitle(name);
	outerFrame.setResizable(true);
	outerFrame.setMaximizable(true);
	outerFrame.setIconifiable(true);
	outerFrame.setClosable(true);
	outerFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

	outerFrame.addInternalFrameListener(new InternalFrameListener() {

	    @Override
	    public void internalFrameOpened(InternalFrameEvent e) {}

	    @Override
	    public void internalFrameClosing(InternalFrameEvent e) {}

	    @Override
	    public void internalFrameClosed(InternalFrameEvent e) {
		cleanup();
	    }

	    @Override
	    public void internalFrameIconified(InternalFrameEvent e) {}

	    @Override
	    public void internalFrameDeiconified(InternalFrameEvent e) {}

	    @Override
	    public void internalFrameActivated(InternalFrameEvent e) {}

	    @Override
	    public void internalFrameDeactivated(InternalFrameEvent e) {}
	});

	final JScrollPane scroller = new JScrollPane(desktop);

	scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	outerFrame.add(scroller, BorderLayout.CENTER);

	JToolBar toolbar = new JToolBar();

	toolbar.setFloatable(false);

	JButton saveButton = new JButton();
	saveButton.setIcon(Icons.getImageIcon(Icons.saveIconLocation));
	saveButton.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e) {
		saveSimulation();
	    }
	});
	toolbar.add(saveButton);
	toolbar.add(new Separator());

	JButton gifButton = new JButton();
	gifButton.setIcon(Icons.getImageIcon(Icons.graphicsexportIconLocation));
	gifButton.setToolTipText("Save as Animated Gif");
	gifButton.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		int option = SimZombie.GifFileChooser.showSaveDialog(frame);

		if (option == JFileChooser.APPROVE_OPTION)
		{
		    Environment tempEnvironment = new Environment(parameters.getEnvironment().getCellMap());
		    tempEnvironment.setCellsHigh(parameters.getCellsHigh());
		    tempEnvironment.setCellsWide(parameters.getCellsWide());
		    tempEnvironment.setCellWidth(parameters.getCellWidth());
		    tempEnvironment.setCellHeight(parameters.getCellHeight());

                    Parameters p = new ZombieParameters();
                    p.setEnvironment(tempEnvironment);

		    SimCanvas tempCanvas = new SimCanvas(p, simulation.getAgentTypes());

		    List<Image> images = new ArrayList<Image>();
		    List<String> delayTimes = new ArrayList<String>();

		    for (SavedStep ss : simulationHistory)
		    {
			Image i = new BufferedImage(tempCanvas.getSize().width, tempCanvas.getSize().height, BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = i.getGraphics();

			tempCanvas.setBackBuffer(i);
			tempEnvironment.removeAllAgents();
			List<Agent> clonedAgents = new ArrayList<Agent>();
			for (Agent a : ss.getAgents())
			{
			    clonedAgents.add(a.createCopy());
			}
			tempEnvironment.addAgents(clonedAgents);

			ZombieParameters zp = (ZombieParameters) parameters;
			
			int stepsPerHalfDay = 2;
			int dayOrNight = ss.getStepNumber() / stepsPerHalfDay % 2;
			if (dayOrNight == 0) parameters.setTimeOfDay(Parameters.TimeOfDay.DAY);
			else if (dayOrNight == 1) parameters.setTimeOfDay(Parameters.TimeOfDay.NIGHT);

			int lunarPhase = (ss.getStepNumber()/(stepsPerHalfDay*2)) % 28;
			parameters.setCurrentLunarPhase(lunarPhase);
			zp.setAwarenessRaised(ss.isAwarenessRaised());

			tempCanvas.paint(g);
			images.add(i);
			delayTimes.add("5");
		    }

		    try {
			AnimatedGIFWriter.saveAnimate(SimZombie.GifFileChooser.getSelectedFile(), images.toArray(new BufferedImage[0]), delayTimes.toArray(new String[0]));
			SimZombie.setStatusText("Animated GIF Saved Successfully to: " + SimZombie.GifFileChooser.getSelectedFile().getCanonicalPath());
		    } catch (Exception ex) {
			Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
			SimZombie.setStatusText("Something went wrong saving an Animated GIF");
		    }
		}
		else
		{
		    SimZombie.setStatusText("Animated GIF Save Cancelled by User");
		}
	    }
	    
	});

	toolbar.add(gifButton);
	toolbar.add(new Separator());

	rewindButton.setIcon(Icons.getImageIcon(Icons.rewindIconLocation));
	rewindButton.setText("");
	toolbar.add(rewindButton);
	if (paused)
	{
	    pauseButton.setIcon(Icons.getImageIcon(Icons.playIconLocation));
	}
	else
	{
	    pauseButton.setIcon(Icons.getImageIcon(Icons.pauseIconLocation));
	}
	pauseButton.setText("");
	toolbar.add(rewindSlider);
	forwardButton.setIcon(Icons.getImageIcon(Icons.forwardIconLocation));
	forwardButton.setText("");
	toolbar.add(forwardButton);
	toolbar.add(pauseButton);

	toolbar.add(new Separator());

	rewindSlider.setMaximumSize(new Dimension(200, rewindSlider.getPreferredSize().height));
	outerFrame.setFrameIcon(Icons.getImageIcon(Icons.simWindowIconLocation));
	outerFrame.add(toolbar, BorderLayout.NORTH);

	desktop.setBackground(Color.LIGHT_GRAY);

	graphWindow = new GraphInternalFrame(0, 0, 700, 400);
//	graphWindow = new GraphInternalFrame(0, 0, 400, 400);
	graphWindow.getGraph().setTitle("Simulation Results");
	graphWindow.setTitle("Stochastic Method Graph");
	graphWindow.setVisible(parameters.displayGraph());
	desktop.add(graphWindow);
	toolbar.add(setupWindowToggleButton(toggleGraph, graphWindow));
	toggleGraph.setIcon(Icons.getImageIcon(Icons.graphIconLocation));
	toggleGraph.setText("");

	eulerGraphWindow = new GraphInternalFrame(0, 400, 400, 400);
	eulerGraphWindow.getGraph().setTitle("Numerical Analysis Results");
	eulerGraphWindow.setTitle("Numerical Method Graph");
	eulerGraphWindow.setVisible(false);

	toolbar.add(setupWindowToggleButton(toggleNAGraph, eulerGraphWindow));
	toggleNAGraph.setIcon(Icons.getImageIcon(Icons.graphNAIconLocation));
	toggleNAGraph.setText("");

	desktop.add(eulerGraphWindow);

	tabularWindow.setVisible(true);
	toolbar.add(setupWindowToggleButton(toggleTabular, tabularWindow));
	toggleTabular.setIcon(Icons.getImageIcon(Icons.tabularIconLocation));
	toggleTabular.setText("");

	simWindow.setLayout(new BorderLayout());

	JPanel simPanel = new JPanel();
	simWindow.add(simPanel, BorderLayout.CENTER);
	
	GroupLayout simWindowLayout = new GroupLayout(simPanel);
	simPanel.setLayout(simWindowLayout);

	simWindow.setLocation(graphWindow.getWidth(), 0);
	simWindow.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
	simWindow.setClosable(true);
	simWindow.setResizable(false);
	simWindow.setMaximizable(false);
	simWindow.setIconifiable(true);
	simWindow.setVisible(parameters.displayGraphics());

	simWindow.setFrameIcon(Icons.getImageIcon(Icons.graphicsIconLocation));

	toolbar.add(setupWindowToggleButton(toggleGraphics, simWindow));
	toggleGraphics.setIcon(Icons.getImageIcon(Icons.graphicsIconLocation));
	toggleGraphics.setText("");

	pauseButton.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
		togglePause();
	    }
	});

	rewindButton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		if (simulationHistoryIterator.hasPrevious())
		{
//		    locateStepNumber(0);
//		    forceSimulationToUpdateToStep(simulationHistoryIterator.next());
		    SavedStep ss = simulationHistoryIterator.previous();
    		    forceSimulationToUpdateToStep(ss);
		    graphWindow.getGraphCanvas().setPointer(ss.getStepNumber());
		    graphWindow.getGraphCanvas().setDisplayPointer(true);
		    sc.repaint();
		    graphWindow.repaint();
		}
	    }	
	});
	rewindButton.setEnabled(false);
	
	forwardButton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		if (!simulationHistoryIterator.hasNext())
		{
		    updateSimulation();
		}
		else
    		{
		    SavedStep ss = simulationHistoryIterator.next();
//		    System.out.println("Forward to: " + ss.getStepNumber());
		    forceSimulationToUpdateToStep(ss);
		    graphWindow.getGraphCanvas().setPointer(ss.getStepNumber());
		    graphWindow.getGraphCanvas().setDisplayPointer(true);
		    sc.repaint();
		    graphWindow.getGraphCanvas().repaint();
		}
	    }
	});
	forwardButton.setEnabled(false);

	rewindSlider.addPropertyChangeListener("value", new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt) {
		Integer i = (Integer) evt.getNewValue();
		locateStepNumber(i);
		forceSimulationToUpdateToStep(simulationHistoryIterator.next());
		frame.setText(i + "");
		graphWindow.getGraphCanvas().setPointer(i);
		graphWindow.getGraphCanvas().setDisplayPointer(true);
		sc.repaint();
		graphWindow.repaint();
	    }
	});
	rewindSlider.setEnabled(false);

	JLabel frameLabel = new JLabel("Frame: ");
	frameLabel.setLabelFor(frame);
	frameLabel.setText("Step: ");
	frame.setText("0");

	simWindowLayout.setAutoCreateContainerGaps(true);
	simWindowLayout.setAutoCreateGaps(true);

	simWindowLayout.setHorizontalGroup(
		simWindowLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
		    .addGroup(simWindowLayout.createSequentialGroup()
			.addComponent(sc, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(simWindowLayout.createSequentialGroup()
			.addComponent(frameLabel)
			.addComponent(frame)));
	
	simWindowLayout.setVerticalGroup(
		simWindowLayout.createSequentialGroup()
		    .addGroup(simWindowLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(sc, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		    .addGroup(simWindowLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(frameLabel)
			.addComponent(frame)));

        simWindow.setPreferredSize(new Dimension(530, 550));
//        simWindow.setPreferredSize(new Dimension(350, 450));
	simWindow.setSize(simWindow.getPreferredSize().width, simWindow.getPreferredSize().height);
	
//	SimZombie.addFrame(simWindow);
	desktop.add(simWindow);

	tabularWindow.setLayout(new BorderLayout());
        
	final JTable tabularData = new JTable();
	tabularData.setModel(new UneditableDefaultTableModel());

	JScrollPane scrollPane = new JScrollPane(tabularData);
	tabularWindow.add(scrollPane, BorderLayout.CENTER);
	tabularWindow.setBounds(simWindow.getX(), simWindow.getY() + simWindow.getHeight(), simWindow.getWidth(), 175);
	tabularWindow.setResizable(true);
        tabularWindow.setVisible(false);
//	tabularData.setAutoCreateRowSorter(true);
	tabularData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	// this needs to be better to be honest!
	tabularData.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if (paused)
		{
		    int row = tabularData.getSelectedRow();
		    int column = tableModel.findColumn("Step");
		    Integer step = ((Integer)tableModel.getValueAt(row, column));
		    locateStepNumber(step);
		    forceSimulationToUpdateToStep(simulationHistoryIterator.next());
		    sc.repaint();
		}
		else
		{
		    SimZombie.setStatusText("Please wait until simulation finishes, or press the pause button, before rewinding a simulation");
		}
	    }
	    
	});

	tableModel = (DefaultTableModel) tabularData.getModel();
	tableModel.addColumn("Step");
	for (AgentType at : simulation.getAgentTypes())
	{
	    tableModel.addColumn(at.getName());
	}
	tableModel.addColumn("Total");

	JMenuBar tabMenuBar = new JMenuBar();
	JMenuItem tabMenu = new JMenuItem("Export");

	final JFileChooser exporty = new JFileChooser();
	exporty.setAcceptAllFileFilterUsed(false);
	exporty.addChoosableFileFilter(new XMLFilter());
	exporty.addChoosableFileFilter(new CSVFilter());
	tabMenu.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		int returned = exporty.showSaveDialog(tabularWindow);
		if (returned == JFileChooser.APPROVE_OPTION)
		{
		    String outputString = "";
		    File f = exporty.getSelectedFile();

		    if (FilterUtils.getExtension(f).equals("xml"))
		    {
			outputString += "<SIMULATION>\n";

			for (SavedStep ss : simulationHistory)
			{
			    HashMap<AgentType, Integer> agentCount = new HashMap<AgentType, Integer>();
			    for (AgentType at : simulation.getAgentTypes())
			    {
				agentCount.put(at, 0);
			    }
			    
			    for (Agent a : ss.getAgents())
			    {
				agentCount.put(a.getType(), agentCount.get(a.getType()) + 1);
			    }

			    outputString += "\t<STEP>\n";
			    outputString += "\t\t<NUMBER>";
			    outputString += ss.getStepNumber();
			    outputString += "<\\NUMBER>\n";

			    outputString += "\t\t<AGENTS>\n";
			    for (AgentType at : simulation.getAgentTypes())
			    {
				outputString += "\t\t\t<" + at.getName().toUpperCase() + ">";
				outputString += agentCount.get(at);
				outputString += "<\\" + at.getName().toUpperCase() + ">\n";
			    }
			    outputString += "\t\t<\\AGENTS>\n";

			    outputString += "\t<\\STEP>\n";
			}

			outputString += "<\\SIMULATION>\n";
		    }
		    else if (FilterUtils.getExtension(f).equals("csv"))
		    {
			char delimiter = ',';
			outputString += "Number" + delimiter;
			for (AgentType at : simulation.getAgentTypes())
			{
			    outputString += at.getName() + delimiter;
			}
			outputString = outputString.substring(0, outputString.length() - 1) + "\n";
			
			for (SavedStep ss : simulationHistory)
			{
			    HashMap<AgentType, Integer> agentCount = new HashMap<AgentType, Integer>();
			    for (AgentType at : simulation.getAgentTypes())
			    {
				agentCount.put(at, 0);
			    }

			    for (Agent a : ss.getAgents())
			    {
				agentCount.put(a.getType(), agentCount.get(a.getType()) + 1);
			    }

			    outputString += "" + ss.getStepNumber() + delimiter;
			    for (AgentType at : simulation.getAgentTypes())
			    {
				outputString += "" + agentCount.get(at) + delimiter;
			    }
			    outputString = outputString.substring(0, outputString.length() - 1) + "\n";
			}
		    }
		    try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(exporty.getSelectedFile())));
			pw.println(outputString);
			pw.flush();
			pw.close();
		    } catch (IOException ex) {
			Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
		else
		{
		    SimZombie.setStatusText("Export cancelled by user");
		}
	    }
	});

	tabularWindow.setJMenuBar(tabMenuBar);
	tabMenuBar.add(tabMenu);

	desktop.add(tabularWindow);

	outerFrame.setSize(graphWindow.getSize().width + simWindow.getSize().width + 18,
			70 + Math.max(graphWindow.getSize().height, simWindow.getSize().height));
	outerFrame.setVisible(true);

	SimZombie.setStatusText("Initialisation complete,  Simulation \"" + name + "\" running...");

	long startTime = System.currentTimeMillis();
	simulation.configureEnvironment(parameters.getEnvironment());

	if (!loading)
	{
	    ArrayList<Agent> history = new ArrayList<Agent>();
	    synchronized(parameters.getEnvironment().getAgents())
	    {
		for (Agent a : parameters.getEnvironment().getAgents())
		{
		    history.add(a.createCopy());
		}
	    }

            ZombieParameters zp = (ZombieParameters) parameters;
	    SavedStep ss = new SavedStep(updates, history, zp.isAwarenessRaised());
	    simulationHistoryIterator.add(ss);

	    Map<AgentType, Integer> agentCount = getAgentCount();
	    updateGraph(agentCount);
	    updateTable(agentCount);
	}
	
	simulation.updateNumericalAnalysis(eulerGraphWindow.getGraph());
	updates++;
	frame.setText(0 + "");

	startTime = System.currentTimeMillis();
    }

    public JInternalFrame getFrame()
    {
	return outerFrame;
    }
}
