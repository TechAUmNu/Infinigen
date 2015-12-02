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
 * class representing a gathering of marching cubes
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

import java.util.Vector;

public class MCMesh
{
    // position and orientation of the mesh
    protected MCMatrix4 modelView;
    
    // cubes of the mesh
    private Vector cubes;
    
    /**
     * default constructor of a MCMesh object
     */
    public MCMesh()
    {
        this.modelView = new MCMatrix4();
        this.cubes = new Vector(0, 1);
        
        // at start, the mesh contains an initial cube
        MCCube c1 = new MCCube("Cube 0");
        this.cubes.add(c1);
    }
    
    /**
     * adds a new cube to the mesh
     * @param _cube the cube of the mesh next to which a new cube should be added
     * @param position the position of the new cube in comparison with _cube
     * @param name identifier of the new cube
     */
    public void addCube(MCCube _cube, int position, String name)
    {
        // if unnamed cube, automatic naming
        if (name.equals(""))
            name = "Cube " + this.cubes.size();
        
        // creates a new cube
        MCCube cube = new MCCube(name);
        
        // translates the cube to the rught position
        cube.modelView = new MCMatrix4(_cube.modelView);
        cube.modelView.translate(MCCube.translate(position));
        
        // computes the neighbourhood of the freshly created cube and merge vertexes that are connected to it
        for (int dir = -3; dir <= 3; dir++)
        {
            if (dir != 0)
            {
                MCMatrix4 dummy = new MCMatrix4(cube.modelView);
                dummy.translate(MCCube.translate(dir));
                
                for (int index = 0; index < this.cubes.size(); index++)
                {
                    MCCube cube_ = (MCCube) this.cubes.elementAt(index);
                    if (dummy.equals(cube_.modelView))
                    {                        
                        switch (dir)
                        {
                            case MCNeighbour.SOUTH:
                                cube.v[0].weight = cube_.v[4].weight;
                                cube.v[1].weight = cube_.v[5].weight;
                                cube.v[2].weight = cube_.v[6].weight;
                                cube.v[3].weight = cube_.v[7].weight;
                                break;
                                
                            case MCNeighbour.NORTH:
                                cube.v[4].weight = cube_.v[0].weight;
                                cube.v[5].weight = cube_.v[1].weight;
                                cube.v[6].weight = cube_.v[2].weight;
                                cube.v[7].weight = cube_.v[3].weight;
                                break;
                                
                            case MCNeighbour.WEST:
                                cube.v[0].weight = cube_.v[1].weight;
                                cube.v[4].weight = cube_.v[5].weight;
                                cube.v[7].weight = cube_.v[6].weight;
                                cube.v[3].weight = cube_.v[2].weight;
                                break;
                                
                            case MCNeighbour.EAST:
                                cube.v[1].weight = cube_.v[0].weight;
                                cube.v[5].weight = cube_.v[4].weight;
                                cube.v[6].weight = cube_.v[7].weight;
                                cube.v[2].weight = cube_.v[3].weight;
                                break;
                                
                            case MCNeighbour.DOWN:
                                cube.v[1].weight = cube_.v[2].weight;
                                cube.v[5].weight = cube_.v[6].weight;
                                cube.v[4].weight = cube_.v[7].weight;
                                cube.v[0].weight = cube_.v[3].weight;
                                break;
                                
                            case MCNeighbour.UP:
                                cube.v[2].weight = cube_.v[1].weight;
                                cube.v[6].weight = cube_.v[5].weight;
                                cube.v[7].weight = cube_.v[4].weight;
                                cube.v[3].weight = cube_.v[0].weight;
                                break;
                        }
                        
                        cube.neighbours.add(new MCNeighbour(cube_, dir));
                        cube_.neighbours.add(new MCNeighbour(cube, -dir));
                    }
                }
            }            
        }
        
        // adds the cube to the mesh
        this.cubes.add(cube);        
        
        // computes interpolated values along edges
        cube.computeEdges();               
        
        // centers the mesh
        this.modelView = new MCMatrix4();
        this.modelView.translate((new MCVector3()).sub(this.computeCenter()));        
    }
    
    /**
     * deletes a cube from the mesh
     * @param cube the cube of the mesh to delete
     */    
    public void deleteCube(MCCube cube)
    {
        // removes the cube from the mesh
        this.cubes.remove(cube);
        
        // removes the cube from the neighbourhood of each other cube connected to the removed one
        for (int index = 0; index < cube.neighbours.size(); index++)
        {            
            MCCube nc = ((MCNeighbour) cube.neighbours.elementAt(index)).cube;
            for (int n = 0; n < nc.neighbours.size(); n++)
            {
                MCCube ncnc = ((MCNeighbour) nc.neighbours.elementAt(n)).cube;
                if (ncnc == cube)
                {
                    nc.neighbours.removeElementAt(n);
                    n--;
                }
            }            
        }
        
        // centers the mesh
        this.modelView = new MCMatrix4();
        this.modelView.translate((new MCVector3()).sub(this.computeCenter()));        
    }
    
    /**
     * computes the center of the mesh
     * @return the center of the mesh
     */    
    public MCVector3 computeCenter()
    {
        MCVector3 center = new MCVector3();
        
        // sums the center of all cubes
        for (int index = 0; index < this.cubes.size(); index++)
        {
            center.add(new MCVector3(((MCCube) this.cubes.elementAt(index)).modelView.mult(new MCVector4())));            
        }        
        
        // divides by the number of cubes
        return center.div(this.cubes.size());                
    }
    
    /**
     * returns the cubes of the mesh
     * @return the vector containing all the cubes
     */
    public Vector getCubes()
    {
        return this.cubes;
    }
    
    /**
     * draws the mesh
     */
    public void display()
    {
        MCCanvas.matrixStack.push();
        MCCanvas.matrixStack.mult(this.modelView);
        
        for (int index = 0; index < this.cubes.size(); index++)
        {
            MCCube cCube = (MCCube) this.cubes.elementAt(index);
            if (!cCube.hidden)
                cCube.display();
        }
        
        MCCanvas.matrixStack.pop();
    }
}