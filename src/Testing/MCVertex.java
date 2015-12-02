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
 * class representing a vertex (position & wiehgt) used for marching cubes
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.awt.Graphics;

public class MCVertex
{
    public static int DEFAULT_WEIGHT = 0;
    
    // size of the vertex on screen
    public static int SIZE = 24;
    
    // an objet is used instead of an int to keep "pointer" relations betweeen vertexes
    protected MCInt weight;
    
    protected MCVector3 position;    
    
    /**
     * contructor of a MCVertex object
     * @param position position of the vertex in the 3D space
     */
    public MCVertex(MCVector3 position)
    {
        this(position, DEFAULT_WEIGHT);
    }
    
    /**
     * contructor of MCVertex object
     * @param position position of the vertex in the 3D space
     * @param weight weight of the vertex (value of the function at this point)
     */
    public MCVertex(MCVector3 position, int weight)
    {        
        this.position = position;
        this.weight = new MCInt(weight);        
    }    
    
    /**
     * returns the weight of the vertex
     * @return the weight of the vertex
     */
    public int weight()
    {
        return this.weight.value;
    }
    
    /**
     * draw the vertex into a specified graphics
     * @param g the graphics in which the vertex is to be drawn
     */
    public void display()
    {        
        MCRenderer.drawCircle(new MCVector4(this.position), SIZE);        
    }
}