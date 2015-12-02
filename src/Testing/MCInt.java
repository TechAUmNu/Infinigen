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
 * class encapsulating an int; Integer not use for access facilities to the int value
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

public class MCInt
{
    protected int value;
    
    /**
     * contructor of a MCInt object
     * @param value the value of the encapsulated int
     */
    public MCInt(int value)
    {
        this.value = value;
    }
}