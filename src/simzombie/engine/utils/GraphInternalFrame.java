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

package simzombie.engine.utils;

import simzombie.engine.SimZombie;
import simzombie.engine.graph.Graph;
import simzombie.engine.graph.GraphCanvas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

/**
 * A special type of JInternalFrame that comes complete with options for graphs
 *
 * This sets up the Frame menu, buttons etc. to the default configuration for graphs,
 * which includes a toggle for printer friendly graphics and to move the legend of the
 * graph from the east side to the south side
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class GraphInternalFrame extends JInternalFrame {

    private Graph graph;
    private GraphCanvas gc;

    public GraphInternalFrame(int x, int y, int width, int height)
    {
	graph = new Graph(0, 0, 0, 0);
	gc = new GraphCanvas(graph, width, height);
	
	setLayout(new BorderLayout());
	setBackground(Color.white);
	setIconifiable(true);
	setMaximizable(true);
	setClosable(true);
        setLocation(0, 0);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(width, height);
	setBounds(x, y, width, height);
        add(gc, BorderLayout.CENTER);
        setResizable(true);
	setFrameIcon(Icons.getImageIcon(Icons.graphIconLocation));

	JMenuBar graphMenuBar = new JMenuBar();
	JToolBar graphBar = new JToolBar();
	graphBar.setFloatable(false);

	final JToggleButton printerFriendlyToggle = new JToggleButton();
	final JToggleButton useHollowToggle = new JToggleButton();
	final JToggleButton legendPosition = new JToggleButton();

	legendPosition.setToolTipText("Move Legend");
	legendPosition.setIcon(Icons.getImageIcon(Icons.eastLegendIconLocation));
	legendPosition.setSelectedIcon(Icons.getImageIcon(Icons.southLegendIconLocation));
	legendPosition.setSelected(true);

	legendPosition.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		gc.toggleLegendPosition();
		gc.repaint();
	    }
	});

	printerFriendlyToggle.setToolTipText("Turn Printer Friendly On");
	printerFriendlyToggle.setIcon(Icons.getImageIcon(Icons.pfonIconLocation));
	printerFriendlyToggle.setSelectedIcon(Icons.getImageIcon(Icons.pfoffIconLocation));
	printerFriendlyToggle.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		gc.setPrinterFriendly(!gc.isPrinterFriendly());
		if (gc.isPrinterFriendly())
		{
		    printerFriendlyToggle.setToolTipText("Turn Printer Friendly Off");
		    useHollowToggle.setEnabled(true);
		}
		else
		{
		    printerFriendlyToggle.setToolTipText("Turn Printer Friendly On");
		    useHollowToggle.setEnabled(false);
		}
		gc.repaint();
	    }
	});

	useHollowToggle.setIcon(Icons.getImageIcon(Icons.usehollowIconLocation));
	useHollowToggle.setSelectedIcon(Icons.getImageIcon(Icons.dontusehollowIconLocation));
	useHollowToggle.setToolTipText("Turn Hollow Markers On");
	useHollowToggle.setEnabled(false);

	useHollowToggle.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		gc.setUseHollowNodules(!gc.useHollowNodules());
		if (gc.useHollowNodules())
		{
		    useHollowToggle.setToolTipText("Turn Hollow Markers Off");
		}
		else
		{
		    useHollowToggle.setToolTipText("Turn Hollow Markers On");
		}
		gc.repaint();
	    }

	});

	graphBar.add(printerFriendlyToggle);
	graphBar.add(useHollowToggle);
	graphBar.add(new Separator());
	graphBar.add(legendPosition);
	add(graphBar, BorderLayout.NORTH);

	JMenu graphMenu = new JMenu("File");
	JMenuItem graphSaveMenuItem = new JMenuItem("Save");
	graphSaveMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    JFileChooser inputChooser = SimZombie.ImageFileChooser;
		    int returnVal = inputChooser.showSaveDialog( ((JComponent)e.getSource()).getTopLevelAncestor() );
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			File selectedFile = inputChooser.getSelectedFile();
			if (FilterUtils.getExtension(inputChooser.getSelectedFile()) == null)
			{
			    ImageFileFilter ff = (ImageFileFilter) inputChooser.getFileFilter();
			    selectedFile = new File(inputChooser.getSelectedFile().getPath() + "." + ff.getExtension());
			}
			ImageIO.write((RenderedImage) gc.getImage(), FilterUtils.getExtension(selectedFile), selectedFile);
			SimZombie.setStatusText("Graph successfully saved to: " + selectedFile.getPath());
		    }
		    else
		    {
			SimZombie.setStatusText("Graph save cancelled by user");
		    }
		} catch (IOException ex) {
		    Logger.getLogger(GraphInternalFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});
	graphMenu.add(graphSaveMenuItem);
	graphMenuBar.add(graphMenu);
	setJMenuBar(graphMenuBar);
    }

    public Graph getGraph()
    {
	return graph;
    }

    public GraphCanvas getGraphCanvas()
    {
	return gc;
    }

    public void setGraphCanvas(GraphCanvas gc)
    {
	this.gc = gc;
    }

    @Override
    public void repaint()
    {
	super.repaint();
	if (gc != null) gc.repaint();
    }
}
