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

import java.io.Serializable;

/**
 * A storage class that holds information about the typings of agents
 * 
 * AgentTypes are assigned unique identifiers when created, so each simulation will have
 * their own unique collection of AgentTypes.  This makes them quick to compare,
 * and also includes a 'friendly' String version for output purposes
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class AgentType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int uniqueIdentifierCount = 0;

    private int uniqueIdentifier;
    private String name;

    public AgentType(String name)
    {
	uniqueIdentifier = ++uniqueIdentifierCount;
	this.name = name;
    }

    /**
     * Compares based on unique identifiers only
     * @param o
     * @return True if the unique identifiers match, false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
	if (o instanceof AgentType)
	{
	    AgentType at = (AgentType) o;
	    if (at.uniqueIdentifier == uniqueIdentifier)
	    {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns the unique identifier for this AgentType
     * @return Unique identifier of this AgentType
     */
    public int getUniqueIdentifier()
    {
	return uniqueIdentifier;
    }

    /**
     * Returns the output friendly name for this AgentType
     * @return Output name of this AgentType
     */
    public String getName()
    {
	return name;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 83 * hash + this.uniqueIdentifier;
	return hash;
    }
}
