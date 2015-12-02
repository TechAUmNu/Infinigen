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
 * class representing a canvas whose goal is to display marching cubes
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class MCCanvas extends Canvas implements MouseListener, MouseMotionListener
{
    // lisibility constant for transformation mode
    public static final int TF_MODE_ROTATION = 0;
    public static final int TF_MODE_TRANSLATION = 1;
    
    // current transformation mode
    protected int tfMode = TF_MODE_ROTATION;

    // image & graphics used for double buffering (to avoid flickering)
    public Image img;
    public Graphics dbg;                  
    
    // last mouse position (used to detect movements)
    private Point prevMouse;
    
    // size & center of the window
    protected int width;
    protected int height;
    private int cX;
    private int cY;
    
    // the mesh to display    
    protected MCMesh mesh;
    
    // matrix stack used for hierarchccal transformation
    protected static MCMatrixStack matrixStack;
    
    /**
     * constructor of a MCCanvas object
     * @param width width of the window
     * @param height height of the window
     */
    public MCCanvas(int width, int height)
    {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        this.width = width;
        this.height = height;
        
        // computes center
        this.cX = width >> 1;
        this.cY = height >> 1;        
        
        this.setSize(width, height);
        
        // adds a default white colored light (along z axis) with little ambient
        MCRenderer.lights.add(new MCLight(new MCVector3(0.0, 0.0, 1.0), 0.2, 1.0, 1.0, 1.0));
        
        this.mesh = new MCMesh();
        
        this.matrixStack = new MCMatrixStack();
    }
    
    /**
     * modifies the size of the window
     * @param width width of the window
     * @param height height of the window
     */
    public void changeSize(int width, int height)
    {
        this.setSize(width, height);
        this.width = width;
        this.height = height;
        this.cX = width >> 1;
        this.cY = height >> 1;
        
        this.repaint();
    }
    
    /**
     * draws components in the window
     * @param g graphics to use for drawing
     */
    public void paint(Graphics g)
    {
        if (MCApplet.started)
        {
            MCRenderer.clearZBuffer();
            
            // clears the backgound
            this.dbg.setColor(MCRenderer.BKG_COLOR);
            this.dbg.fillRect(0, 0, this.width, this.height);
            
            // display the mesh into the back buffer
            matrixStack.load(MCMatrix4.identity());
            this.mesh.display();
            
            // copy the back buffer into the frame buffer
            g.drawImage(this.img, 0, 0, this);
        }
    }
    
    /**
     * repaints components
     */
    public void repaint()
    {
        this.paint(this.getGraphics());
    }
    
    // mouse events
    
    public void mouseEntered(MouseEvent me)
    {
    }
    
    public void mouseExited(MouseEvent me)
    {
    }
    
    public void mousePressed(MouseEvent me)
    {
        this.prevMouse = me.getPoint();
    }
    
    public void mouseReleased(MouseEvent me)
    {
    }
    
    public void mouseClicked(MouseEvent me)
    {
    }
    
    public void mouseMoved(MouseEvent me)
    {
    }
    
    /**
     * Manages mouse movement
     * @param me mouse event
     */
    public void mouseDragged(MouseEvent me)
    {
        Point newMouse = me.getPoint();
        
        // compute the angle of rotation in relation with the size of the canvas
        float thetaX = (float) ((newMouse.getX() - prevMouse.getX())*(Math.PI/this.getSize().width));
        float thetaY = (float) ((newMouse.getY() - prevMouse.getY())*(Math.PI/this.getSize().height));
        
        // transformation along z axis
        if (MCApplet.shift)
        {
            // computes the transformation
            MCMatrix4 dummy = new MCMatrix4();
            if (this.tfMode == TF_MODE_ROTATION)
                dummy.rotateZ(thetaX);
            if (this.tfMode == TF_MODE_TRANSLATION)
                dummy.translate(0, 0, 50*thetaX);
            dummy.mult(this.mesh.modelView);
            
            // modifies the model view of the mesh
            this.mesh.modelView = dummy;
        }
        // transformation along x & y axis
        else
        {
            // computes the transformation
            MCMatrix4 dummy = new MCMatrix4();
            if (this.tfMode == TF_MODE_ROTATION)
            {
                dummy.rotateY(-thetaX);
                dummy.rotateX(thetaY);
            }
            if (this.tfMode == TF_MODE_TRANSLATION)
                dummy.translate(50*thetaX, 50*thetaY, 0);
            dummy.mult(this.mesh.modelView);
            
            // modifies the model view of the mesh
            this.mesh.modelView = dummy;
        }
        
        prevMouse = newMouse;
        
        this.repaint();
    }
}