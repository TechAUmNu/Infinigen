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
 * class representing a static renderer dedicated to marching cubes
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Vector;

public class MCRenderer
{    
    // constant colors
    public static final Color EDGE_COLOR = new Color(0x000000);
    public static final Color I_VERTEX_COLOR = new Color(0x888888);
    public static final Color TEXT_COLOR = new Color(0x000000);
    public static final Color BKG_COLOR = new Color(240, 230, 220);
    
    // default focal value of the camera
    public static final double DEFAULT_FOCAL = 100.0;
    
    // projection matrix
    private static MCMatrix4 prMatrix;    
    
    // available lighting models
    public final static int LI_MODL_LAMBERT = 0;
    public final static int LI_MODL_PHONG = 1;
    
    // lighting model used for rendering; protected for access facilities
    protected static int liModl = LI_MODL_LAMBERT;
    
    // lights of the scene; protected for access facilities
    protected static Vector lights = new Vector(0, 1);
    
    // material used for rendering; protected for access facilities
    protected static MCMaterial material = MCMaterial.EMERALD;            
    
    // window parameter: size, center, ratio
    private static int width;
    private static int height;
    private static int cX;
    private static int cY;
    private static double ratio;
    
    // depth buffer
    private static int[][] zBuffer;
    private static final int M = 65536;    
    
    // graphics used for rendering
    protected static Graphics g;
    
    /**
     * builds the projection matrix
     * @param r focal value
     */
    public static void buildPrMatrix(double r)
    {
        double n = 3*r;
        double f = 4*r;
        double dz = 5*r;
        
        MCRenderer.prMatrix = new MCMatrix4(-n/cX,    0, 	        0,             0,
                                                0,-n/cY,            0, 	  		   0,
                                                0, 	  0, -(f-n)/(f+n), (2*f*n)/(f+n),
                                                0, 	  0, 	       -1,    		   1);
        
        MCRenderer.prMatrix.translate(0, 0, dz);
    }

    /**
     * sets the size of the target window used when rendering
     * @param img image used for rendering
     */
    public static void setWindow(Image img)
    {
        // retrieves graphics
        MCRenderer.g = img.getGraphics();
        
        // sets size
        MCRenderer.width = img.getWidth(null);        
        MCRenderer.height = img.getHeight(null);
        
        // computes the center
        MCRenderer.cX = width >> 1;
        MCRenderer.cY = height >> 1;
        
        // specifies ratio
        MCRenderer.ratio = 1.0;
        
        // initializes depth buffer
        MCRenderer.zBuffer = new int[width][height];
    }
    
    /**
     * clears the depth buffer; should be called at the beginning of each new frame
     */
    public static void clearZBuffer()
    {
        for (int y = 0; y < zBuffer[0].length; y++)
        {
            for (int x = 0; x < zBuffer.length; x++)
            {
                MCRenderer.zBuffer[x][y] = Integer.MAX_VALUE;
            }
        }
    }
    
    /**
     * computes the color of a vertex depending on its weight
     * @param w the weight of the vertex
     * @return the color matching the specified weight
     */
    public static Color vertexColor(int w)
    {
        return (w > 0)?new Color(128 + w, 128, 128):new Color(128, 128, 128 - w);
    }
    
    /**
     * draws a string in the 3D space
     * @param g the graphics object used for rendering
     * @param p position in the 3D space
     * @param s string to display
     */
    public static void drawString(MCVector3 p, String s)
    {        
        MCVector4 p2D = MCRenderer.prMatrix.mult(new MCVector4(p));
        
        int ip = (int) (cX*p2D.x() + cX);
        int jp = (int) (cY*p2D.y()*ratio + cY);
        
        MCRenderer.g.drawString(s, ip, jp);
    }
    
    /**
     * draws a line between two points of a 3D space     
     * @param p1 first points
     * @param p2 second point     
     */    
    public static void drawLine(MCVector3 v1, MCVector3 v2)
    {       
        MCVector4 p1 = MCRenderer.prMatrix.mult(new MCVector4(v1));
        MCVector4 p2 = MCRenderer.prMatrix.mult(new MCVector4(v2));
                
        int ip1 = (int) (cX*p1.x() + cX);
        int jp1 = (int) (cY*p1.y()*ratio + cY);
        int ip2 = (int) (cX*p2.x() + cX);
        int jp2 = (int) (cY*p2.y()*ratio + cY);
     
        MCRenderer.g.drawLine(ip1, jp1, ip2, jp2);
    }
    
    /**
     * draws a circle in a 3D space     
     * @param center center of the circle
     * @param size size of the circle
     */
    public static void drawCircle(MCVector4 center, int size)
    {        
        MCVector4 pc = MCRenderer.prMatrix.mult(center);        
        
        // computes new size depending on the depth
        int s = (int) -(size*pc.z());
        
        int ip = (int) (cX*pc.x() + cX - s/2);
        int jp = (int) (cY*pc.y()*ratio + cY - s/2);
        
        MCRenderer.g.fillArc(ip, jp, s, s, 0, 360);
    }
    
    /**
     * draws a filled triangle using flat shading method     
     * @param v1 first vertex of the triangle
     * @param v2 second vertex of the triangle
     * @param v3 third triangle
     * @param n normal vector to the triangle     
     */
    public static void drawTriangleFlat(MCVector3 v1, MCVector3 v2, MCVector3 v3, MCVector3 n)
    {                               
        // computes the color depending on lights & material
        Color clr = new Color(0x00FFFFFF);
        switch (liModl)
        {
            case LI_MODL_LAMBERT:
                clr = MCLight.lambert(n, lights, MCRenderer.material);
                break;
                
            case LI_MODL_PHONG:
                clr = MCLight.phong(n, lights, MCRenderer.material);
                break;
        }
        MCRenderer.g.setColor(clr);
        
        // projection of the vertexes        
        MCVector4 q1 = MCRenderer.prMatrix.mult(new MCVector4(v1));
        MCVector4 q2 = MCRenderer.prMatrix.mult(new MCVector4(v2));
        MCVector4 q3 = MCRenderer.prMatrix.mult(new MCVector4(v3));
        
        // transformation into the viewing window
        int x1 = (int) (cX*q1.x() + cX);
        int x2 = (int) (cX*q2.x() + cX);
        int x3 = (int) (cX*q3.x() + cX);
        int y1 = (int) (cY*q1.y()* ratio + cY);
        int y2 = (int) (cY*q2.y()* ratio + cY);
        int y3 = (int) (cY*q3.y()* ratio + cY);
        
        // scaling z components to interpolate & store integers instead of doubles in the depth buffer
        int z1 = (int) (M*q1.z());
        int z2 = (int) (M*q2.z());
        int z3 = (int) (M*q3.z());
        
        // sorting by growing y to have y1 > y2 > y3 (to fill from top to bottom with horizontal lines)
        int tempX;
        int tempY;
        int tempZ;
        if (y2 < y1)
        {
            tempX = x1;
            tempY = y1;
            tempZ = z1;
            
            x1 = x2;
            y1 = y2;
            z1 = z2;
            
            x2 = tempX;
            y2 = tempY;
            z2 = tempZ;
        }
        if (y3 < y1)
        {
            tempX = x1;
            tempY = y1;
            tempZ = z1;
            
            x1 = x3;
            y1 = y3;
            z1 = z3;
            
            x3 = tempX;
            y3 = tempY;
            z3 = tempZ;
        }
        if (y3 < y2)
        {
            tempX = x2;
            tempY = y2;
            tempZ = z2;
            
            x2 = x3;
            y2 = y3;
            z2 = z3;
            
            x3 = tempX;
            y3 = tempY;
            z3 = tempZ;
        }
        
        // filling from y1 to y2
        
        // computes x and z incrents along (1,2) and (1,3) adges
        double delta12 = (x2 - x1)/(double) (y2 - y1);
        double delta13 = (x3 - x1)/(double) (y3 - y1);
        int z12 = (int) ((z2 - z1)/(double) (y2 - y1));
        int z13 = (int) ((z3 - z1)/(double) (y3 - y1));
        
        // interpolates along edges
        double _x2 = x1;
        double _x3 = x1;
        int _z2 = z1;
        int _z3 = z1;
        
        if (y1 != y2)
        {
            for (int y = y1; y <= y2; y++)
            {                
                if (y >= 0 && y < height)
                {
                    int xMin = (_x2 <= _x3)?(int) _x2:(int) _x3;
                    int xMax = (_x2 > _x3)?(int) _x2:(int) _x3;
                    
                    // interpolates z along current horizontal line
                    int cz = (_x2 <= _x3)?_z2:_z3;
                    int incZ = (int) ((_z3 - _z2)/(_x3 - _x2));
                    
                    // drawing pixels from left to right
                    for (int x = xMin; x <= xMax; x++)
                    {                        
                        if (x >= 0 && x < width)
                        {
                            // usage of the depth buffer
                            if (MCRenderer.zBuffer[x][y] >= cz)
                            {
                                MCRenderer.zBuffer[x][y] = cz;
                                MCRenderer.g.drawRect(x, y, 1, 1);
                            }
                        }
                        
                        // updates z value
                        cz += incZ;
                    }
                }
                
                // updates values along edges
                _x2 += delta12;
                _x3 += delta13;
                _z2 += z12;
                _z3 += z13;
            }
        }
        
        // filling from y2 to y3: same thing but along different edges
        
        double delta23 = (x3 - x2)/(double) (y3 - y2);
        int z23 = (int) ((z3 - z2)/(double) (y3 - y2));
        
        double x2_ = x2;
        int z2_ = z2;
        
        if (y2 != y3)
        {
            for (int y = y2; y <= y3; y++)
            {
                if (y >= 0 && y < height)
                {
                    int xMin = (x2_ <= _x3)?(int) x2_:(int) _x3;
                    int xMax = (x2_ > _x3)?(int) x2_:(int) _x3;
                    int cz = (x2_ <= _x3)?z2_:_z3;
                    int incZ = (int) ((_z3 - z2_)/(_x3 - x2_));
                    
                    for (int x = xMin; x <= xMax; x++)
                    {
                        if (x >= 0 && x < width)
                        {
                            if (MCRenderer.zBuffer[x][y] >= cz)
                            {
                                MCRenderer.zBuffer[x][y] = (int) cz;
                                MCRenderer.g.drawRect(x, y, 1, 1);
                            }
                        }
                        cz += incZ;
                    }
                }
                
                x2_ += delta23;
                _x3 += delta13;
                z2_ += z23;
                _z3 += z13;
            }
        }
    }
}