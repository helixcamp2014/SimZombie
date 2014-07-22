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
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.swing.JFrame;

/**
 * An alternative startup option to SimZombie which runs the application headless.
 * 
 * Currently only supports default parameters unless explicitly coded,
 * could be extended later to open a saved Parameters file, or display the parameters
 * window before commencing simulation execution
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class CommandLineRunner {

    // could be a command line arg too
    private static final boolean graphicsOn = false;

    public static void main(String [] args)
    {
        ZombieParameters zp = new ZombieParameters();

        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(new File("/zombie.param")))));
            Object o = ois.readObject();
            if (o instanceof ZombieParameters)
            {
                zp = (ZombieParameters) o;
            }
        } catch (IOException ex) {
            System.err.println("WARNING: Zombie Parameters File not found - using defaults");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CommandLineRunner.class.getName()).log(Level.SEVERE, null, ex);
        }


        // code here to assign command line variables to zp, eg
        // zp.setChanceInfectionTransmits(chance);

        ZombieSimulation zs = new ZombieSimulation(zp);

        // this initialises the simulation
        zs.configureEnvironment(zp.getEnvironment());


        JFrame frame = new JFrame("Simulation");
        SimCanvas ep = new SimCanvas(zp, zs.getAgentTypes());
        if (graphicsOn)
        {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // this sizing isn't very clean, sorry!
            frame.setSize(zp.getEnvironmentWidth() + 20, zp.getEnvironmentHeight() + 40);
            frame.setLayout(new BorderLayout());
            frame.add(ep, BorderLayout.NORTH);
            frame.setVisible(true);
        }

        StringBuffer countB = new StringBuffer();
        int count = 0;
        countB.append("Count = [ ");
        Map<AgentType, StringBuffer> outputs = new HashMap<AgentType, StringBuffer>();

        for (AgentType at : zs.getAgentTypes())
        {
            StringBuffer sb = new StringBuffer();
            sb.append(at.getName() + " = [ ");
            outputs.put(at, sb);
        }

        boolean terminated = false;
        while (!terminated)
        {
            count++;
            countB.append(count + " ");
            terminated = zs.updateEnvironment(zp.getEnvironment());
            if (graphicsOn)
            {
                ep.repaint();
            }
            for (AgentType at : zs.getAgentTypes())
            {
                List<Agent> l = zp.getEnvironment().getAgentsOfType(at);
                outputs.get(at).append(l.size() + " ");
            }
        }

        countB.append("];\n");
        System.out.println(countB);

        for (AgentType at : zs.getAgentTypes())
        {
            StringBuffer sb = outputs.get(at);
            sb.append("];\n");
            System.out.println(sb);
        }

        System.out.println("hold on");
        System.out.println("plot(Count, Susceptible, 'g')");
        System.out.println("plot(Count, Monsterified, 'r')");
        System.out.println("plot(Count, Infected, 'y')");
        System.out.println("hold off");
    }

}
