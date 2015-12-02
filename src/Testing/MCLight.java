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
 * class representing a light used for rendering
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.awt.Color;
import java.util.Vector;

public class MCLight
{
    // orientation (along z axis by default)
    private MCVector3 direction = new MCVector3(0, 0, 1);
    
    // intensities a(mbient), r(ed), g(reen) & b(lue)
    private static double ia;
    private double ir;
    private double ig;
    private double ib;
    
    /**
     * constructor of a MCLight object
     * @param direction light direction
     * @param ia ambient intensity
     * @param ir red intensity
     * @param ig green intensity
     * @param ib blue intensity
     */
    public MCLight(MCVector3 direction, double ia, double ir, double ig, double ib)
    {
        this.direction = direction;
        this.direction.normalize();
        this.ia = ia;
        this.ir = ir;
        this.ig = ig;
        this.ib = ib;
    }
    
    /**
     * computes the color of a face using Lambert lighting model
     * @param n normal to the face
     * @param lights lights to be taken in account
     * @param m material of the face
     * @return the color of the face
     */
    public static Color lambert(MCVector3 n, Vector lights, MCMaterial m)
    {
        double cR = m.ar*MCLight.ia;
        double cG = m.ag*MCLight.ia;
        double cB = m.ab*MCLight.ia;
        
        // sums diffuse intensities
        for (int index = 0; index < lights.size(); index++)
        {
            MCLight l = (MCLight) lights.elementAt(index);
            double k = n.dot(l.direction);
            
            cR += m.dr*l.ir*k;
            cG += m.dg*l.ig*k;
            cB += m.db*l.ib*k;
        }
        
        float fR = (float) Math.max(0.0, Math.min(cR, 1.0));
        float fG = (float) Math.max(0.0, Math.min(cG, 1.0));
        float fB = (float) Math.max(0.0, Math.min(cB, 1.0));
        
        return new Color(fR, fG, fB);
    }
    
    /**
     * computes the color of a face using Phong lighting model
     * @param n normal to the face
     * @param lights lights to be taken in account
     * @param m material of the face
     * @return the color of the face
     */
    public static Color phong(MCVector3 n, Vector lights, MCMaterial m)
    {
        double cR = m.ar*MCLight.ia;
        double cG = m.ag*MCLight.ia;
        double cB = m.ab*MCLight.ia;
        
        // sums diffuse and specular intensities
        for (int index = 0; index < lights.size(); index++)
        {
            MCLight l = (MCLight) lights.elementAt(index);
            double k = n.dot(l.direction);
            
            MCVector3 v = new MCVector3(0, 0, 1);
            MCVector3 r = new MCVector3(n);
            r.mult(2*k);
            r.add(l.direction);
            
            double p = Math.pow(v.dot(r), m.shininess);
            cR += l.ir*(m.dr*k + m.sr*p);
            cG += l.ig*(m.dg*k + m.sg*p);
            cB += l.ib*(m.db*k + m.sb*p);
        }
        
        float fR = (float) Math.max(0.0, Math.min(cR, 1.0));
        float fG = (float) Math.max(0.0, Math.min(cG, 1.0));
        float fB = (float) Math.max(0.0, Math.min(cB, 1.0));
        
        return new Color(fR, fG, fB);
    }
}