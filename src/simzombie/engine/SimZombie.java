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

import simzombie.engine.simulations.zombies.ZombieParameters;
import simzombie.engine.simulations.zombies.ZombieSimulation;
import simzombie.engine.utils.CommitPanel;
import simzombie.engine.utils.FilterUtils;
import simzombie.engine.utils.GIFFilter;
import simzombie.engine.utils.Icons;
import simzombie.engine.utils.ImageFileFilter;
import simzombie.engine.utils.JPGFilter;
import simzombie.engine.utils.PARAMFilter;
import simzombie.engine.utils.PNGFilter;
import simzombie.engine.utils.SIMFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author Matthew
 */
public class SimZombie {

    private static JLabel statusText = new JLabel("");
    private static JDesktopPane desktop = new JDesktopPane();

    // initialising these this early gives them the Java default LnF, oh no! Initialise after LnF change instead
    public static JFileChooser ImageFileChooser; // = new JFileChooser();
    public static JFileChooser GifFileChooser; // = new JFileChooser();
    public static JFileChooser SimFileChooser; // = new JFileChooser();
    public static JFileChooser ParametersFileChooser; // = new JFileChooser();

    private static final List<SimulationRunner> activeSimulations = Collections.synchronizedList(new ArrayList<SimulationRunner> ());
    private static final List<SimulationRunner> inactiveSimulations = Collections.synchronizedList(new ArrayList<SimulationRunner> ());

    public static void addFrame(JInternalFrame frame)
    {
	boolean foundAFrame;
	do
	{
	    foundAFrame = false;
	    for (JInternalFrame eFrame : desktop.getAllFrames())
	    {
		if (Math.abs(frame.getLocation().x - eFrame.getLocation().x) < 25
			&& Math.abs(frame.getLocation().y - eFrame.getLocation().y) < 25)
		{
		    frame.setLocation(frame.getLocation().x + 25, frame.getLocation().y + 25);
		    foundAFrame = true;
		}
	    }
	} while (foundAFrame);
	desktop.add(frame);
	frame.toFront();
    }

    public static void setStatusText(String text)
    {
	statusText.setText(text);
    }

    private static Map<JInternalFrame, Parameters> frameToParams = new HashMap<JInternalFrame, Parameters> ();
    private static int paramFrameCount = 0;
    public static JInternalFrame createParametersFrame()
    {
//	setStatusText("createParametersFrame entered");
	paramFrameCount++;
	final JInternalFrame parametersFrame = new JInternalFrame("Parameters");
	parametersFrame.setFrameIcon(Icons.getImageIcon(Icons.paramIconLocation));
	parametersFrame.setResizable(true);

	ZombieParameters parameters = ZombieParameters.getDefaultParameters();

	frameToParams.put(parametersFrame, parameters);

	parameters.setName("Simulation " + paramFrameCount);

	parametersFrame.setSize(400, 400);
	parametersFrame.setVisible(true);
	parametersFrame.setLayout(new BorderLayout());

	final JTabbedPane parameterTabs = new JTabbedPane();
	parametersFrame.add(parameterTabs, BorderLayout.CENTER);

	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem saveParameters = new JMenuItem("Save Configuration");

	saveParameters.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    for (Component c : parameterTabs.getComponents())
		    {
			if (c instanceof CommitPanel)
			{
			    ((CommitPanel)c).save();
			}
		    }

		    JFileChooser inputChooser = SimZombie.ParametersFileChooser;
		    int returnVal = inputChooser.showSaveDialog(parametersFrame);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			File selectedFile = inputChooser.getSelectedFile();
			if (FilterUtils.getExtension(selectedFile) == null)
			{
			    ImageFileFilter ff = (ImageFileFilter) inputChooser.getFileFilter();
			    selectedFile = new File(inputChooser.getSelectedFile().getPath() + "." + ff.getExtension());
			}

			// Note: These files are actually so small (~66bytes) that zipping them INCREASES their size...
			// However, it also hides the contents, which is a huge plus point in my opinion
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(selectedFile))));
			oos.writeObject(frameToParams.get(parametersFrame));
			oos.flush();
			oos.close();

			SimZombie.setStatusText("Parameters for \"" + frameToParams.get(parametersFrame).getName() + "\" successfully saved to: " + selectedFile.getPath());
		    }
		    else
		    {
			SimZombie.setStatusText("Parameter save cancelled by user");
		    }
		} catch (IOException ex) {
		    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});

	JMenuItem loadParameters = new JMenuItem("Load Configuration");

	loadParameters.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    JFileChooser inputChooser = SimZombie.ParametersFileChooser;
		    int returnVal = inputChooser.showOpenDialog(parametersFrame);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			File selectedFile = inputChooser.getSelectedFile();

			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(selectedFile))));

			Object o = ois.readObject();
			if (o instanceof Parameters)
			{
			    Parameters p = (Parameters) o;
			    parameterTabs.removeAll();

			    for (CommitPanel cp : p.getPanels())
			    {
				parameterTabs.add(cp);
			    }

			    frameToParams.remove(parametersFrame);
			    frameToParams.put(parametersFrame, p);

			    SimZombie.setStatusText(p.getName() + " parameters successfully loaded");
			}
			else
			{
			    SimZombie.setStatusText(selectedFile.getPath() + " does not contain simulation parameters");
			}

			ois.close();
		    }
		    else
		    {
			SimZombie.setStatusText("Parameter load cancelled by user");
		    }
		} catch (InvalidClassException ex) {
		    SimZombie.setStatusText("Parameter version is incompatible");
		} catch (IOException ex) {
		    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
		    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});

	fileMenu.add(saveParameters);
	fileMenu.add(loadParameters);
	menuBar.add(fileMenu);

	parametersFrame.setJMenuBar(menuBar);

	JPanel panel = new JPanel(new BorderLayout());
	JPanel panel2 = new JPanel(new GridLayout(0, 2));

	for (CommitPanel cp : parameters.getPanels())
	{
	    parameterTabs.add(cp, cp.getName());
	}

	final JButton submit = new JButton("Begin");
	submit.addActionListener(new ActionListener(){
            @Override
	    public void actionPerformed(ActionEvent e) {
		boolean allSavedOkay = true;
		for (Component c : parameterTabs.getComponents())
		{
		    if (c instanceof CommitPanel)
		    {
			boolean saveOkay = ((CommitPanel)c).save();
			if (!saveOkay) allSavedOkay = false;
		    }
		}

		if (allSavedOkay)
		{
		    Parameters p = frameToParams.get(parametersFrame);
		    parametersFrame.dispose();
		    final SimulationRunner s = new SimulationRunner(p.getName(), new ZombieSimulation((ZombieParameters)p), p);
		    addFrame(s.getFrame());

		    s.getFrame().addInternalFrameListener(new InternalFrameListener(){

			@Override
			public void internalFrameOpened(InternalFrameEvent e) {}

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {}

			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
			    synchronized(activeSimulations)
			    {
				activeSimulations.remove(s);
			    }
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

		    synchronized(activeSimulations)
		    {
			activeSimulations.add(s);
		    }
		}
		else
		{
		    setStatusText("Something went wrong! Check parameters are all valid");
		}
	    }
	});

	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener(){

	    public void actionPerformed(ActionEvent e) {
		paramFrameCount --;
		parametersFrame.dispose();
	    }
	});
	panel2.add(submit);
	panel2.add(cancel);
	panel.add(panel2, BorderLayout.EAST);
	parametersFrame.add(panel, BorderLayout.SOUTH);

//	setStatusText("Point I");

	return parametersFrame;
    }
    
    private static void loadSimulation()
    {
	try {
	    JFileChooser inputChooser = SimZombie.SimFileChooser;
	    int returnVal = inputChooser.showOpenDialog(frame);
	    if (returnVal == JFileChooser.APPROVE_OPTION)
	    {
		File selectedFile = inputChooser.getSelectedFile();

		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(selectedFile))));

		Object o = ois.readObject();
		if (o instanceof SavedSimulation)
		{
		    SavedSimulation ss = (SavedSimulation) o;
		    SimulationRunner sr = new SimulationRunner(ss, new ZombieSimulation((ZombieParameters)ss.getParameters()));
		    addFrame(sr.getFrame());
		    activeSimulations.add(sr);
		}
		else
		{
		    SimZombie.setStatusText(selectedFile.getPath() + " does not contain a saved simulation");
		}

		ois.close();
//		SavedSimulation savedSim = new SavedSimulation(parameters, simulationHistory);

		SimZombie.setStatusText("Simulation \"" + " name " + "\" successfully loaded from: " + selectedFile.getPath());
	    }
	    else
	    {
		SimZombie.setStatusText("Simulation load cancelled by user");
	    }
	} catch (InvalidClassException ex) {
	    SimZombie.setStatusText("Simulation version is incompatible");
	} catch (IOException ex) {
	    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	} catch (ClassNotFoundException ex) {
	    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private static JFrame frame = new JFrame("SimZombie");
    private static void createWindow()
    {
	int width = 1280;
	int height = 940;
//	int width = 800;
//	int height = 600;
	frame.setVisible(true);
	frame.setSize(width, height);
	frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (width/2),
			Toolkit.getDefaultToolkit().getScreenSize().height / 2 - (height/ 2));
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setIconImage(Icons.getImageIcon(Icons.simzLogoIconLocation).getImage());
	frame.setLayout(new BorderLayout());
	JPanel bottomPanel = new JPanel(new BorderLayout());
	bottomPanel.setBorder(new EmptyBorder(1, 4, 1, 1));
	frame.add(bottomPanel, BorderLayout.SOUTH);
	bottomPanel.add(statusText, BorderLayout.WEST);
	desktop.setSize(width, height);
	desktop.setBackground(Color.lightGray);
	desktop.setVisible(true);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	frame.add(desktop, BorderLayout.CENTER);

	JMenuBar menubar = new JMenuBar();
	JMenu menu = new JMenu("Simulations");

	final JMenuItem newSimulationMenuItem = new JMenuItem("New Simulation");
	newSimulationMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
//		setStatusText("Menu Item Clicked Before createParametersFrame()");
		JInternalFrame frame = createParametersFrame();
//		setStatusText("Menu Item Clicked Before addFrame()");
		addFrame(frame);
//		setStatusText("Menu Item Clicked Before setSelected()");
		try {
		    frame.setSelected(true);
		} catch (PropertyVetoException ex) {
		    Logger.getLogger(SimZombie.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});

	final JMenuItem loadSimulationMenuItem = new JMenuItem("Open Simulation");
	loadSimulationMenuItem.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		loadSimulation();
	    }
	});

	menu.add(newSimulationMenuItem);
	menu.add(loadSimulationMenuItem);
	menubar.add(menu);
	frame.setJMenuBar(menubar);

	setStatusText("Welcome to SimZombie");
    }

    static int count = 0;
    public static void runSimulations()
    {
	List<SimulationRunner> completeSims = new ArrayList<SimulationRunner>();
	synchronized(activeSimulations)
	{
	    for (SimulationRunner sim : activeSimulations)
	    {
		sim.update();
	    }
	}

	if (!completeSims.isEmpty())
	{
	    String status;
	    if (completeSims.size() > 1)
	    {
		status = "Simulations ";
	    }
	    else
	    {
		status = "Simulation ";
	    }

	    int count = 0;
	    for (SimulationRunner sim : completeSims)
	    {
		count++;
		status += "\"" + sim.getName() + "\"";
		if (count < completeSims.size() - 1)
		{
		    status += ", ";
		}
		else if (count == completeSims.size() - 1)
		{
		    status += " and ";
		}
		synchronized(activeSimulations)
		{
		    activeSimulations.remove(sim);
		}
		synchronized(inactiveSimulations)
		{
		    inactiveSimulations.add(sim);
		}
	    }
	    if (completeSims.size() > 1) status += " have completed.";
	    else status += " has completed.";

	    if (completeSims.size() >= 1) SimZombie.setStatusText(status);
	    completeSims.clear();
	}
    }

    public static void main(String [] args)
    {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException ex) {
	    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	} catch (InstantiationException ex) {
	    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	} catch (UnsupportedLookAndFeelException ex) {
	    Logger.getLogger(SimulationRunner.class.getName()).log(Level.SEVERE, null, ex);
	}

	ImageFileChooser = new JFileChooser();
	SimFileChooser = new JFileChooser();
	ParametersFileChooser = new JFileChooser();
	GifFileChooser = new JFileChooser();

	createWindow();

	ImageFileChooser.setAcceptAllFileFilterUsed(false);
	ImageFileChooser.addChoosableFileFilter(new GIFFilter());
	ImageFileChooser.addChoosableFileFilter(new JPGFilter());
	ImageFileChooser.addChoosableFileFilter(new PNGFilter());

	SimFileChooser.setAcceptAllFileFilterUsed(false);
	SimFileChooser.addChoosableFileFilter(new SIMFilter());

	ParametersFileChooser.setAcceptAllFileFilterUsed(false);
	ParametersFileChooser.addChoosableFileFilter(new PARAMFilter());

	GifFileChooser.setAcceptAllFileFilterUsed(false);
	GifFileChooser.addChoosableFileFilter(new GIFFilter());

	while(true)
	{
	    runSimulations();
	}
    }
}
