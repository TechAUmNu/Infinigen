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
 * class representing a 4x4 matrix
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

class MCMatrix4
{
    // values in the matrix
    private double coef[][]= new double[4][4];
    
    /**
     * default constructor of a MCMatrix4 object
     */
    public MCMatrix4()
    {
        this(identity());
    }
    
    /**
     * contructor of a MCMatrix4 object
     * @param a value at (1, 1)
     * @param b value at (1, 2)
     * @param c value at (1, 3)
     * @param d value at (1, 4)
     * @param e value at (2, 1)
     * @param f value at (2, 2)
     * @param g value at (2, 3)
     * @param h value at (2, 4)
     * @param i value at (3, 1)
     * @param j value at (3, 2)
     * @param k value at (3, 3)
     * @param l value at (3, 4)
     * @param m value at (4, 1)
     * @param n value at (4, 2)
     * @param o value at (4, 3)
     * @param p value at (4, 4)
     */
    public MCMatrix4(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n, double o, double p)
    {
        coef[0][0]=a;
        coef[0][1]=b;
        coef[0][2]=c;
        coef[0][3]=d;
        
        coef[1][0]=e;
        coef[1][1]=f;
        coef[1][2]=g;
        coef[1][3]=h;
        
        coef[2][0]=i;
        coef[2][1]=j;
        coef[2][2]=k;
        coef[2][3]=l;
        
        coef[3][0]=m;
        coef[3][1]=n;
        coef[3][2]=o;
        coef[3][3]=p;
    }
    
    /**
     * copy constructor of a MCMatrix4 object
     * @param org original matrix to duplicate
     */
    public MCMatrix4(MCMatrix4 org)
    {
        this(org.coef[0][0],
        org.coef[0][1],
        org.coef[0][2],
        org.coef[0][3],
        
        org.coef[1][0],
        org.coef[1][1],
        org.coef[1][2],
        org.coef[1][3],
        
        org.coef[2][0],
        org.coef[2][1],
        org.coef[2][2],
        org.coef[2][3],
        
        org.coef[3][0],
        org.coef[3][1],
        org.coef[3][2],
        org.coef[3][3]);
    }
    
    /**
     * returns the identity matrix
     * @return the identity matrix
     */
    public static MCMatrix4 identity()
    {
        return new MCMatrix4(1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1);
    }
    
    /**
     * returns a value of the matrix
     * @param i row
     * @param j column
     * @return the value at (i,j)
     */
    public double coeff(int i, int j)
    {
        return coef[i][j];
    }
    
    /**
     * performs a multiplication by a vector 3D
     * @param v the vector to use for the multiplication
     * @return the result of the multiplication
     */
    public MCVector3 mult(MCVector3 v)
    {
        return new MCVector3(this.mult(new MCVector4(v)));
    }
    
    /**
     * performs a multiplication by a vector 4D
     * @param v the vector to use for the multiplication
     * @return the result of the multiplication
     */
    public MCVector4 mult(MCVector4 v)
    {
        double dummy[] = new double[4];
        
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                dummy[i] += v.component(j)*coef[i][j];
            }
        }
        
        return new MCVector4(dummy);
    }
    
    /**
     * performs a multiplication by a matrix
     * @param m the matrix to use for the multiplication
     */
    public void mult(MCMatrix4 m)
    {
        double dummy[][] = new double[4][4];
        
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                for (int k = 0; k < 4; k++)
                {
                    dummy[i][j] += this.coef[i][k]*m.coef[k][j];
                }
            }
        }
        
        this.coef = dummy;
    }
    
    /**
     * applies a translation to the matrix
     * @param x translation along the x axis
     * @param y translation along the y axis
     * @param z translation along the z axis
     */
    public void translate(double x, double y, double z)
    {
        this.mult(new MCMatrix4(1, 0, 0, x,
        0, 1, 0, y,
        0, 0, 1, z,
        0, 0, 0, 1));
    }
    
    /**
     * applies a translation to the matrix
     * @param t the translation vector
     */
    public void translate(MCVector3 t)
    {
        this.translate(t.x, t.y, t.z);
    }
    
    /**
     * applies a scaling operation to the matrix
     * @param sx scale value along x axis
     * @param sy scale value along y axis
     * @param sz scale value along z axis
     */
    public void scale(double sx, double sy, double sz)
    {
        this.mult(new MCMatrix4(sx, 0,  0,  0,
        0,  sy, 0,  0,
        0,  0,  sz, 0,
        0,  0,  0,  1));
    }
    
    /**
     * rotates the matrix along the x axis
     * @param a rotation angle
     */
    public void rotateX(double a)
    {
        this.mult(new MCMatrix4(1, 0,			  0,			0,
        0, Math.cos(a), -Math.sin(a), 0,
        0, Math.sin(a), Math.cos(a),  0,
        0, 0,			  0,			1));
    }
    
    /**
     * rotates the matrix along the y axis
     * @param a rotation angle
     */
    public void rotateY(double a)
    {
        this.mult(new MCMatrix4(Math.cos(a), 	0, Math.sin(a), 0,
        0,		   	1, 0,			0,
        -Math.sin(a), 0, Math.cos(a), 0,
        0,			0, 0,			1));
    }
    
    /**
     * rotates the matrix along the z axis
     * @param a rotation angle
     */
    public void rotateZ(double a)
    {
        this.mult(new MCMatrix4(Math.cos(a), -Math.sin(a), 0, 0,
        Math.sin(a), Math.cos(a),  0, 0,
        0,		   0,			 1, 0,
        0,		   0,			 0, 1));
    }
    
    /**
     * returns a string representation of the matrix
     * @return a string representing the matrix
     */
    public String toString()
    {
        String s = "";
        for (int i = 0; i < 4; i++)
        {
            s += "| ";
            for (int j = 0; j < 4; j++)
            {
                s +=  this.coef[i][j] +  " " ;
            }
            s += "|\n";
        }
        return s;
    }
    
    /**
     * tests the equality with another matrix
     * @param m the matrix to test equality with
     * @return true if the two matrixes are equal
     */
    public boolean equals(MCMatrix4 m)
    {
        
        boolean result = true;
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                result &= this.coef[i][j] == m.coef[i][j];
            }
        }
        
        return result;
    }
}