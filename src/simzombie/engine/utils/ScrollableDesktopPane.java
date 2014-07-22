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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.Scrollable;

/**
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class ScrollableDesktopPane extends JDesktopPane implements Scrollable {

    Dimension scrollableSize = new Dimension(0, 0);

    public ScrollableDesktopPane()
    {
	super();
	ScrollingDesktopManager sdm = new ScrollingDesktopManager(this);
	setDesktopManager(sdm);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	resize();
    }

    public void resize()
    {
	int furthestEast = 0;
	int furthestNorth = 0;
	int furthestWest = 0;
	int furthestSouth = 0;

	for (JInternalFrame f : getAllFrames())
	{
	    if (f.isVisible() && !f.isIcon())
	    {
		Point p = f.getLocation();
		Dimension d = f.getSize();

		if (p.x < furthestWest) furthestWest = p.x;
		if (p.y < furthestNorth) furthestNorth = p.y;

		if (p.x + d.width > furthestEast) furthestEast = p.x + d.width;
		if (p.y + d.height > furthestSouth) furthestSouth = p.y + d.height;
	    }
	}

	scrollableSize = new Dimension(furthestEast - furthestWest, furthestSouth - furthestNorth);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
	return scrollableSize;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
	return 1;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	return 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
	return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
	return true;
    }
}
