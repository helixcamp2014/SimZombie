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

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Utility class that stores the package location of all relevant icons
 *
 * Also provides a way to retrieve an icon from file, and caches icons
 * to improve on time spent reading from the disk
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class Icons {

    // this class needs reorganising into an enum at some point!
    
    public static final String pauseIconLocation = "simzombie/engine/utils/icons/pause.png";
    public static final String playIconLocation = "simzombie/engine/utils/icons/play.png";
    public static final String rewindIconLocation = "simzombie/engine/utils/icons/rewind.png";
    public static final String forwardIconLocation = "simzombie/engine/utils/icons/fastforward.png";
    public static final String graphIconLocation = "simzombie/engine/utils/icons/graph.png";
    public static final String graphNAIconLocation = "simzombie/engine/utils/icons/graphna.png";
    public static final String tabularIconLocation = "simzombie/engine/utils/icons/tabular.png";
    public static final String graphicsIconLocation = "simzombie/engine/utils/icons/graphics.png";
    public static final String pfonIconLocation = "simzombie/engine/utils/icons/pfon.png";
    public static final String pfoffIconLocation = "simzombie/engine/utils/icons/pfoff.png";
    public static final String usehollowIconLocation = "simzombie/engine/utils/icons/usehollow.png";
    public static final String dontusehollowIconLocation = "simzombie/engine/utils/icons/dontusehollow.png";
    public static final String saveIconLocation = "simzombie/engine/utils/icons/save.png";
    public static final String regressionIconLocation = "simzombie/engine/utils/icons/regression.png";
    public static final String noregressionIconLocation = "simzombie/engine/utils/icons/noregression.png";
    public static final String graphicsexportIconLocation = "simzombie/engine/utils/icons/graphicsexport.png";
    public static final String eastLegendIconLocation = "simzombie/engine/utils/icons/legendeast.png";
    public static final String southLegendIconLocation = "simzombie/engine/utils/icons/legendsouth.png";
    public static final String simzLogoIconLocation = "simzombie/engine/utils/icons/sz.png";
    public static final String simWindowIconLocation = "simzombie/engine/utils/icons/sim.png";
    public static final String paramIconLocation = "simzombie/engine/utils/icons/param.png";

    public static final String bellIconLocation = "simzombie/engine/utils/icons/bell.png";
    public static final String sunIconLocation = "simzombie/engine/utils/icons/sunt.png";
    public static final String[] lunarPhaseLocations =
    {
	"simzombie/engine/utils/icons/phase0.png",
	"simzombie/engine/utils/icons/phase1.png",
	"simzombie/engine/utils/icons/phase2.png",
	"simzombie/engine/utils/icons/phase3.png",
	"simzombie/engine/utils/icons/phase4.png",
	"simzombie/engine/utils/icons/phase5.png",
	"simzombie/engine/utils/icons/phase6.png",
	"simzombie/engine/utils/icons/phase7.png",
	"simzombie/engine/utils/icons/phase8.png",
	"simzombie/engine/utils/icons/phase9.png",
	"simzombie/engine/utils/icons/phase10.png",
	"simzombie/engine/utils/icons/phase11.png",
	"simzombie/engine/utils/icons/phase12.png",
	"simzombie/engine/utils/icons/phase13.png",
	"simzombie/engine/utils/icons/phase14.png",
	"simzombie/engine/utils/icons/phase15.png",
	"simzombie/engine/utils/icons/phase16.png",
	"simzombie/engine/utils/icons/phase17.png",
	"simzombie/engine/utils/icons/phase18.png",
	"simzombie/engine/utils/icons/phase19.png",
	"simzombie/engine/utils/icons/phase20.png",
	"simzombie/engine/utils/icons/phase21.png",
	"simzombie/engine/utils/icons/phase22.png",
	"simzombie/engine/utils/icons/phase23.png",
	"simzombie/engine/utils/icons/phase24.png",
	"simzombie/engine/utils/icons/phase25.png",
	"simzombie/engine/utils/icons/phase26.png",
	"simzombie/engine/utils/icons/phase27.png",
	"simzombie/engine/utils/icons/phase28.png",
    };

    private static Map<String, ImageIcon> savedIcons = new HashMap<String, ImageIcon> ();
    public static ImageIcon getImageIcon(String location)
    {
	if (savedIcons.containsKey(location))
	{
	    return savedIcons.get(location);
	}
	else
	{
	    ImageIcon icon = new ImageIcon(Icons.class.getClassLoader().getResource(location));
	    savedIcons.put(location, icon);
	    return icon;
	}
    }

    private static Map<String, Image> savedImages = new HashMap<String, Image>();
    public static Image getImage(String location)
    {
	if (savedImages.containsKey(location))
	{
	    return savedImages.get(location);
	}
	else
	{
	    try {
		Image i = ImageIO.read(Icons.class.getClassLoader().getResource(location));
		savedImages.put(location, i);
		return i;
	    } catch (IOException ex) {
		Logger.getLogger(Icons.class.getName()).log(Level.SEVERE, null, ex);
		return null;
	    }
	}
    }

}
