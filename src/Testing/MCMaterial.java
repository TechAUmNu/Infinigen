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
 * class representing a material used for rendering
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.awt.Color;
import java.util.Vector;

public class MCMaterial
{
    // public fiels for access facilities
    
    // ambient
    public double ar;
    public double ag;
    public double ab;
    
    // diffuse
    public double dr;
    public double dg;
    public double db;
    
    // specular
    public double sr;
    public double sg;
    public double sb;
    
    // shininess
    public double shininess;
    
    // identifier
    public String name;
    
    // predefined materials
    public static MCMaterial EMERALD = new MCMaterial(0.022, 0.175, 0.022, 0.076, 0.614, 0.076, 0.633, 0.728, 0.633, 0.6, "Emerald");
    public static MCMaterial JADE = new MCMaterial(0.135, 0.222, 0.158, 0.540, 0.890, 0.630, 0.316, 0.316, 0.316, 0.1, "Jade");
    public static MCMaterial RUBY = new MCMaterial(0.175, 0.012, 0.012, 0.614, 0.041, 0.041, 0.728, 0.627, 0.627, 0.6, "Ruby");
    public static MCMaterial BRONZE = new MCMaterial(0.212, 0.128, 0.054, 0.714, 0.428, 0.181, 0.393, 0.271, 0.167, 0.2, "Bronze");
    public static MCMaterial CHROME = new MCMaterial(0.250, 0.250, 0.250, 0.4, 0.4, 0.4, 0.775, 0.775, 0.775, 0.6, "Chrome");
    public static MCMaterial GOLD = new MCMaterial(0.247, 0.199, 0.075, 0.752, 0.606, 0.226, 0.628, 0.556, 0.366, 0.4, "Gold");
    public static MCMaterial SILVER = new MCMaterial(0.192, 0.192, 0.192, 0.507, 0.507, 0.507, 0.508, 0.508, 0.508, 0.4, "Silver");
    
    public static Vector materials;        
    
    /**
     * constructor of a MCMaterial object
     * @param ar ambient red
     * @param ag ambient green
     * @param ab ambient blue
     * @param dr diffuse red
     * @param dg diffuse green
     * @param db diffuse blue
     * @param sr specular red
     * @param sg specular green
     * @param sb specular blue
     * @param shininess specular attenuation
     */
    public 	MCMaterial(double ar, double ag, double ab, double dr, double dg, double db, double sr, double sg, double sb, double shininess, String name)
    {
        this.ar = ar;
        this.ag = ag;
        this.ab = ab;
        
        this.dr = dr;
        this.dg = dg;
        this.db = db;
        
        this.sr = sr;
        this.sg = sg;
        this.sb = sb;
        
        this.shininess = shininess;
        
        this.name = name;
    }    
    
    /**
     * initialise un vecteur avec des matériau par défaut
     */
    public static void fillMaterials()
    {
        materials = new Vector(0, 1);
        materials.add(EMERALD);
        materials.add(JADE);
        materials.add(RUBY);
        materials.add(BRONZE);
        materials.add(CHROME);
        materials.add(GOLD);
        materials.add(SILVER);
    }        
    
    /**
     * returns a string representation of the material (its name)
     * @return a string representing the material
     */
    public String toString()
    {
        return this.name;
    }
}