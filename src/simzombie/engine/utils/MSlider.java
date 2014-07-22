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

import javax.swing.JSlider;

/**
 * Extension of JSlider which allows for the slider to be set at a specific value
 *
 * Although the JSlider allows this functionality, this also allows us to
 * fire a property change event when the value of the slider is set explicitly
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class MSlider extends JSlider {

    /**
     *
     * @param min Smallest value of the slider
     * @param max Largest value of the slider
     */
    public MSlider(int min, int max)
    {
	super(min, max);
    }

    /**
     * 
     * @param n Number to set the slider to currently point to
     * @param alert If true, fires a property change after setting the value.  This is useful if your slider has behaviours on change and you wish them to happen or not happen based on this set command
     */
    public void setValue(int n, boolean alert) {
	int oldValue = getValue();
	super.setValue(n);
	if (alert)
	{
	    firePropertyChange("value", oldValue, n);
	}
    }

    /**
     * Sets the slider to a specific value
     * @param n Value to set the slider to
     */
    public void setValue(int n)
    {
	this.setValue(n, true);
    }
    
}
