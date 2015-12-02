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
 * class representing a neighbour for a marching cube: another cube and a direction
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

public class MCNeighbour
{
    // lisibility constants designing directions/positions
    public static final int NORTH = 1;
    public static final int SOUTH = -1;
    public static final int EAST = 2;
    public static final int WEST = -2;
    public static final int UP = 3;
    public static final int DOWN = -3;
    
    // the neighbour cube
    protected MCCube cube;
    // the position where the neigbour is
    protected int position;
    
    /**
     * contructor of a MCNeighbour object
     * @param cube the neighbour cube
     * @param the position of the neighbour
     */
    public MCNeighbour(MCCube cube, int position)
    {
        this.cube = cube;
        this.position = position;
    }   
}