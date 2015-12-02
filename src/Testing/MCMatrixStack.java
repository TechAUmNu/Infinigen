/*
* Marching Cubes Tutorial Applet 
* Copyright (C) 2002 - GERVAISE Raphael & RICHARD Karen
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

/**
 * class representing a stack of matrixes
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.util.Stack;

public class MCMatrixStack
{
    // the stack containing the matrixes
    private Stack stack;
    
    /**
     * default contructor of a MCMatrixStack object
     */
    public MCMatrixStack()
    {
        this.stack = new Stack();
        this.stack.push(MCMatrix4.identity());
    }
    
    /**
     * duplicates the top of the stack
     */
    public void push()
    {
        this.stack.push(new MCMatrix4((MCMatrix4) this.stack.peek()));
    }
    
    /**
     * removes the top of the stack
     */
    public void pop()
    {
        this.stack.pop();
    }
    
    /**
     * returns the top of the stack without removing it
     * @return the top of the stack
     */
    public MCMatrix4 peek()
    {
        return (MCMatrix4) this.stack.peek();
    }
    
    /**
     * loads a matrix at the top of the stack
     * @param m the matrix to load
     */
    public void load(MCMatrix4 m)
    {
        this.stack.pop();
        this.stack.push(m);
    }
    
    /**
     * multiply the top of the stack by the specified matrix
     * @param m matrix used to multiply the top of the stack
     */
    public void mult(MCMatrix4 m)
    {
        ((MCMatrix4) this.stack.peek()).mult(m);
    }     
}