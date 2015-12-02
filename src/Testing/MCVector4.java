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
 * class representing a four component homogeneous vector
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

class MCVector4
{
    // components of the vector
    private double components[]= new double[4];
    
    /**
     * default contructor of a MCVector4 object
     */
    public MCVector4()
    {
        this(0, 0, 0);
    }
    
    /**
     * contructor of a MCVector4 object
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public MCVector4(double x, double y, double z)
    {
        this(x,y,z,1);
    }
    
    /**
     * contructor of a MCVector4 object
     * @param x x component
     * @param y y component
     * @param z z component
     * @param w homogeneous component
     */
    public MCVector4(double x, double y, double z, double h)
    {
        this.components[0] = x;
        this.components[1] = y;
        this.components[2] = z;
        this.components[3] = h;
    }
    
    /**
     * contructor of a MCVector4 object from an array
     * @param tab array containing the four components
     */
    public MCVector4(double[] components)
    {
        this.components = components;
    }
    
    /**
     * copy contructor of a MCVector4 object
     * @param v the vector to be duplicated
     */
    public MCVector4(MCVector4 v)
    {
        this(v.components[0], v.components[1], v.components[2], v.components[3]);
    }
    
    /**
     * contructor of a MCVector4 object from a three dimensional vector
     * @param v a three dimensional vector
     */
    public MCVector4(MCVector3 v)
    {
        this(v.x, v.y, v.z);
    }
    
    /**
     * returns the x component
     * @return x component
     */
    public double x()
    {
        return (this.components[0]/this.components[3]);
    }
    
    /**
     * returns the y component
     * @return y component
     */
    public double y()
    {
        return (this.components[1]/this.components[3]);
    }
    
    /**
     * returns the z component
     * @return z component
     */
    public double z()
    {
        return (this.components[2]/this.components[3]);
    }
    
    /**
     * returns a specified component
     * @param index index of the component
     * @return the wanted component
     */
    public double component(int index)
    {
        return this.components[index];
    }
    
    /**
     * returns a string representation of the vector
     * @return a string representing the vector
     */
    public String toString()
    {
        String s = "( ";
        for (int index = 0; index < 4; index++)
        {
            s += components[index] + " ";
        }
        s += ")";
        
        return s;
    }
}