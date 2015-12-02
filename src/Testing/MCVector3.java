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
 * class representing a three component vector
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

public class MCVector3
{
    // protected fields for access facilities
    protected double x;
    protected double y;
    protected double z;
    
    /**
     * default contructor of a MCVector3 object
     */
    public MCVector3()
    {
        this(0.0, 0.0, 0.0);
    }
    
    /**
     * contructor of a MCVector3 object
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public MCVector3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * copy contructor of a MCVector3 object
     * @param v the vector to be duplicated
     */
    public MCVector3(MCVector3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    /**
     * contructor of a MCVector3 object from a four dimensional vector
     * @param v a four dimensional vector
     */
    public MCVector3(MCVector4 v)
    {
        this(v.x(), v.y(), v.z());
    }
    
    /**
     * returns the length of the vector
     * @return the length of the vector
     */
    public double length()
    {
        return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }
    
    /**
     * makes the vectors length equal to 1
     */
    public MCVector3 normalize()
    {
        double l = this.length();
        if (l != 0)
        {
            this.x /= l;
            this.y /= l;
            this.z /= l;
        }
        
        return this;
    }
    
    /**
     * performs an addition between two vectors
     * @param v the vector to add
     * @return the result of the addition
     */
    public MCVector3 add(MCVector3 v)
    {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        
        return this;
    }
    
    /**
     * adds values to the vectors components
     * @param x value to add to the x component
     * @param x value to add to the x component
     * @param x value to add to the x component
     * @return the result of the addtion
     */
    public MCVector3 add(double x, double y, double z)
    {        
        return this.add(new MCVector3(x, y, z));
    }
    
    /**
     * performs a substraction between two vectors
     * @param v the vector to substract
     * @return the result of the substraction
     */
    public MCVector3 sub(MCVector3 v)
    {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        
        return this;
    }
    
    /**
     * substracts values to the vectors components
     * @param x value to substract to the x component
     * @param x value to substract to the x component
     * @param x value to substract to the x component
     * @return the result of the substraction
     */
    public MCVector3 sub(double x, double y, double z)
    {        
        return this.sub(new MCVector3(x, y, z));
    }
    
    /**
     * performs a multiplication by a scalar
     * @param k the scalar value to multiply by
     * @return the result of the multiplication
     */
    public MCVector3 mult(double k)
    {
        this.x *= k;
        this.y *= k;
        this.z *= k;
        
        return this;
    }
    
    /**
     * performs a division by a scalar
     * @param k the scalar value to divide by
     * @return the result of the division
     */
    public MCVector3 div(double k)
    {
        this.x /= k;
        this.y /= k;
        this.z /= k;
        
        return this;
    }
    
    /**
     * compute dot product
     * @param v the vector used for computation
     * @return the dot product value
     */
    public double dot(MCVector3 v)
    {
        return this.x*v.x + this.y*v.y + this.z*v.z;
    }
    
    /**
     * compute cross product
     * @param v the vector used for computation
     * @return the cross product vector
     */
    public MCVector3 cross(MCVector3 v)
    {
        double a = this.y*v.z - this.z*v.y;
        double b = this.z*v.x - this.x*v.z;
        double c = this.x*v.y - this.y*v.x;
        
        return new MCVector3(a, b, c);
    }   
    
     /**
     * returns a string representation of the vector
     * @return a string representing the vector
     */
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}