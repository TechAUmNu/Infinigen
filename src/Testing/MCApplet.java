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
 * applet designed to view and experiment with marching cubes algorithm
 * @author GERVAISE Raphael & RICHARD Karen
 */

package marchingcubes;

// not much comments: interface work... better have a glance at MCCube and the others...

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import java.util.Vector;

public class MCApplet extends JApplet implements KeyListener
{
    // range value of the slider bars
    public static final int DEFAULT_RANGE = 100;
    
    // background color of the interface
    private Color bgColor = new Color(230, 210, 185);    
    
    // true when the applet starts
    protected static boolean started = false;
    
    // visual components
    
    private MCCanvas canvas;
    private JPanel pControlW;
    private JPanel pControlE;
    private JPanel pControlWW;
    private JPanel pControlWE;
    
    private JPanel pOperation;
    private ButtonGroup transfo;
    private JRadioButton trans;
    private JRadioButton rot;
    
    private JPanel pRender;
    private JComboBox cbMat;
    private ButtonGroup rendu;
    private JRadioButton lambert;
    private JRadioButton phong;
    private ButtonGroup visu;
    protected static JRadioButton wCube;
    private JRadioButton woCube;
    
    private JPanel pModel;
    private JLabel reference;
    private JTextField value;
    private JLabel lCase;
    private JTextField tfCase;
    private JCheckBox cbVisible;    
    private ActionListener cbVisibleListener;
    private CaretListener tfCaseListener;
    private ButtonGroup faceBox;
    private JRadioButton face1;
    private JRadioButton face2;
    private JRadioButton face3;
    private JRadioButton face4;
    private JRadioButton face5;
    private JRadioButton face6;
    private JRadioButton face7;
    private JButton addB;
    private JButton supB;
    
    protected static JComboBox cbCube;
    private JButton reset;
    
    private JSlider[] sVertex;
    private ChangeListener sVertexListener;
    private ChangeListener sVertexListenerTfCase;
    private JButton[] mButton;    
    private static JButton bComplementary;
    private static ActionListener bComplementaryListener;
    protected static JCheckBox cbAmbigous;
    private ActionListener cbAmbigousListener;
    
    protected static boolean shift;
    
    /**
     * automatically called when applet initializes
     */
    public void init()
    {                                
        this.addKeyListener(this);
        
        MCMaterial.fillMaterials();
        
        int aWidth;
        int aHeight;
        try
        {
            // gets size specified in HTML document
            aWidth = Integer.parseInt(this.getParameter("width"));            
            aHeight = Integer.parseInt(this.getParameter("height"));
        }catch (NumberFormatException nfe)
        {
            this.showStatus("Applet could not be initialized");
            return;
        }
        
        this.getContentPane().setLayout(new BorderLayout());
                
        this.pControlW=new JPanel();
        this.pControlW.setBackground(bgColor);
        this.pControlW.setLayout(new BorderLayout());
        this.getContentPane().add(this.pControlW, "East");
                
        this.pControlWE=new JPanel();
        this.pControlWE.setBackground(bgColor);
        this.pControlWE.setLayout(new GridLayout(8, 1));
        this.pControlW.add(this.pControlWE, "East");
                
        this.pControlWW=new JPanel();
        this.pControlWW.setBackground(bgColor);
        this.pControlWW.setLayout(new GridLayout(8, 1));
        this.pControlW.add(this.pControlWW, "West");
                
        this.pControlE=new JPanel();
        this.pControlE.setBackground(bgColor);
        this.pControlE.setLayout(new BoxLayout(this.pControlE, BoxLayout.Y_AXIS));
        this.getContentPane().add(this.pControlE, "West");
                
        this.pOperation=new JPanel();
        this.pOperation.setBackground(bgColor);
        this.pOperation.setLayout(new GridLayout(2,1));
        this.pOperation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Transformation", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        
        this.transfo=new ButtonGroup();
        this.trans=new JRadioButton("Translate", false);
        this.trans.setBackground(bgColor);
        this.trans.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                transAction(ae);
            }
        }
        );
        this.rot=new JRadioButton("Rotate", true);
        this.rot.setBackground(bgColor);
        this.rot.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                rotAction(ae);
            }
        }
        );
        this.transfo.add(this.trans);
        this.transfo.add(this.trans);
        this.transfo.add(this.rot);
        this.pOperation.add(this.trans);
        this.pOperation.add(this.rot);
        this.pControlE.add(this.pOperation);
     
        pRender = new JPanel();
        this.pRender.setBackground(bgColor);
        this.pRender.setLayout(new GridLayout(7,1));
        pRender.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Rendering", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        this.cbMat = new JComboBox();
        this.cbMat.setBackground(bgColor);
        for (int index = 0; index < MCMaterial.materials.size(); index++)
        {
            this.cbMat.addItem(MCMaterial.materials.elementAt(index));
        }
        this.cbMat.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                cbMatAction(ae);
            }
        });
        this.pRender.add(this.cbMat);        
        
        JPanel saut = new JPanel();
        saut.setBackground(bgColor);
        this.pRender.add(saut);
        
        this.rendu=new ButtonGroup();
        this.lambert=new JRadioButton("Lambert", true);
        this.lambert.setBackground(bgColor);
        this.lambert.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                MCRenderer.liModl = MCRenderer.LI_MODL_LAMBERT;
                if (MCApplet.this.started)
                    canvas.repaint();
            }
        }
        );
        this.phong=new JRadioButton("Phong", false);
        this.phong.setBackground(bgColor);
        this.phong.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                MCRenderer.liModl = MCRenderer.LI_MODL_PHONG;
                if (MCApplet.this.started)
                    canvas.repaint();
            }
        }
        );
        this.rendu.add(this.lambert);
        this.rendu.add(this.phong);
        this.pRender.add(this.lambert);
        this.pRender.add(this.phong);
                
        saut = new JPanel();
        saut.setBackground(bgColor);
        this.pRender.add(saut);
                
        this.visu = new ButtonGroup();
        this.wCube = new JRadioButton("With cube", true);
        this.wCube.setBackground(bgColor);
        this.wCube.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if (MCApplet.this.started)
                    canvas.repaint();
            }
        }
        );
        this.woCube = new JRadioButton("Without cube", false);
        this.woCube.setBackground(bgColor);
        this.woCube.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if (MCApplet.this.started)
                    canvas.repaint();
            }
        });
        
        this.visu.add(this.wCube);
        this.pRender.add(this.wCube);
        this.visu.add(this.woCube);
        this.pRender.add(this.woCube);
        this.pControlE.add(pRender);
        
        pModel = new JPanel();
        this.pModel.setBackground(bgColor);
        this.pModel.setLayout(new GridLayout(15,1));
        pModel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Modeling", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
                
        this.reference = new JLabel("Isovalue", SwingConstants.CENTER);
        this.reference.setBackground(bgColor);
        this.pModel.add(this.reference);
        
        this.value = new JTextField("0.00");
        this.value.setBackground(bgColor);
        this.value.addCaretListener(new CaretListener()
        {
            public void caretUpdate(CaretEvent ce)
            {
                valueAction(ce);
            }
        }
        );
        this.pModel.add(this.value);
        
        this.lCase = new JLabel("Case number", SwingConstants.CENTER);
        this.lCase.setBackground(bgColor);
        this.pModel.add(this.lCase);
        
        this.tfCase = new JTextField("0");
        this.tfCase.setBackground(bgColor);
        this.tfCaseListener = new CaretListener()
        {
            public void caretUpdate(CaretEvent ce)
            {
                tfCaseAction(ce);
            }
        };
        this.tfCase.addCaretListener(this.tfCaseListener);
        this.pModel.add(this.tfCase);        
        
        this.cbVisible = new JCheckBox("Hidden");
        this.cbVisibleListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                MCApplet.this.cbVisibleAction(ae);
            }
        };
        this.cbVisible.addActionListener(this.cbVisibleListener);
        this.cbVisible.setBackground(bgColor);
        this.pModel.add(this.cbVisible);
                
        saut = new JPanel();
        saut.setBackground(bgColor);
        this.pModel.add(saut);
        
        this.faceBox = new ButtonGroup();        
        this.face1 = new JRadioButton("face 0,1,2,3", false);
        this.face1.setBackground(bgColor);
        this.face2 = new JRadioButton("face 4,5,6,7", false);
        this.face2.setBackground(bgColor);
        this.face3 = new JRadioButton("face 1,2,5,6", false);
        this.face3.setBackground(bgColor);
        this.face4 = new JRadioButton("face 0,3,4,7", false);
        this.face4.setBackground(bgColor);
        this.face5 = new JRadioButton("face 0,1,4,5", false);
        this.face5.setBackground(bgColor);
        this.face6 = new JRadioButton("face 2,3,6,7", false);
        this.face6.setBackground(bgColor);
        this.face7 = new JRadioButton("", true);
        this.faceBox.add(this.face1);
        this.faceBox.add(this.face2);
        this.faceBox.add(this.face3);
        this.faceBox.add(this.face4);
        this.faceBox.add(this.face5);
        this.faceBox.add(this.face6);
        this.faceBox.add(this.face7);
        this.pModel.add(this.face1);
        this.pModel.add(this.face2);
        this.pModel.add(this.face3);
        this.pModel.add(this.face4);
        this.pModel.add(this.face5);
        this.pModel.add(this.face6);        
                
        this.addB = new JButton("Add a cube");
        this.addB.setBackground(bgColor);
        this.addB.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addSupAction(e);
            }
        }
        );
        this.pModel.add(this.addB);
        this.supB = new JButton("Del the cube");
        this.supB.setBackground(bgColor);
        this.supB.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                addSupAction(e);
            }
        }
        );
        this.pModel.add(this.supB);
        this.pControlE.add(this.pModel);        
        
        this.reset = new JButton("Reset");
        this.reset.setBackground(bgColor);
        this.reset.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                resetAction(ae);
            }
        });
        this.pModel.add(this.reset);
                
        this.cbCube = new JComboBox();
        this.cbCube.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Current cube", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        this.cbCube.setBackground(bgColor);
        this.pControlW.add(this.cbCube, "North");
        this.cbCube.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                cbCubeChange(ae);
            }
        });
                
        this.sVertexListener = new ChangeListener()
        {
            public void stateChanged(ChangeEvent ce)
            {
                MCApplet.this.sVertexAction(ce);
            }
        };
        this.sVertexListenerTfCase = new ChangeListener()
        {
            public void stateChanged(ChangeEvent ce)
            {
                MCApplet.this.sVertexActionTfCase(ce);
            }
        };
        
        this.sVertex = new JSlider[8];
        this.mButton = new JButton[16];
        for (int index = 0; index < this.sVertex.length; index++)
        {
            this.sVertex[index] = new JSlider(-DEFAULT_RANGE, DEFAULT_RANGE, 0);
            this.sVertex[index].setBackground(bgColor);
            this.sVertex[index].setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Vertex " + index + " (" + (int) Math.pow(2, index) + ")", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
            this.sVertex[index].setMajorTickSpacing(DEFAULT_RANGE/2);
            this.sVertex[index].setMinorTickSpacing(1);
            this.sVertex[index].setSnapToTicks(true);
            this.sVertex[index].setPaintTicks(false);
            this.sVertex[index].setPaintLabels(true);
            this.sVertex[index].setPaintTrack(true);
            this.sVertex[index].addChangeListener(this.sVertexListener);
            this.sVertex[index].addChangeListener(this.sVertexListenerTfCase);
            this.pControlWW.add(this.sVertex[index]);
            
            JPanel pButton=new JPanel();
            pButton.setBackground(bgColor);
            pButton.setLayout(new GridLayout(1, 2));
            this.mButton[2*index] = new JButton("Min");
            this.mButton[2*index].setBackground(bgColor);
            this.mButton[2*index].addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ba)
                {
                    mButtonAction(ba);
                }
            });
            this.mButton[2*index+1] = new JButton("Max");
            this.mButton[2*index+1].setBackground(bgColor);
            this.mButton[2*index+1].addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ba)
                {
                    mButtonAction(ba);
                }
            });
            pButton.setBackground(bgColor);
            pButton.add(this.mButton[2*index]);
            pButton.add(this.mButton[2*index+1]);
            this.pControlWE.add(pButton);
        }
        
        JPanel pDummy = new JPanel();
        pDummy.setLayout(new GridLayout(2, 1));
        this.pControlW.add(pDummy, "South");
                
        this.bComplementary = new JButton("Switch to complementary case");
        this.bComplementary.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                bComplementaryAction(ae);
            }
        });
        
        this.bComplementary.setBackground(bgColor);
        pDummy.add(this.bComplementary);
        
        this.cbAmbigous = new JCheckBox("Use ambigous cases resolution");
        this.cbAmbigous.setEnabled(true);
        this.cbAmbigous.setBackground(bgColor);
        this.cbAmbigousListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                cbAmbigousAction(ae);
            }
        };
        this.cbAmbigous.addActionListener(this.cbAmbigousListener);
        pDummy.add(this.cbAmbigous);        
                
        int w = aWidth - this.pControlW.getSize().width;
        int h = aHeight;
        this.canvas = new MCCanvas(w, h);
        this.canvas.addKeyListener(this);
        this.canvas.img = this.createImage(w, h);
        this.canvas.dbg = this.canvas.img.getGraphics();
        this.getContentPane().add(this.canvas, "Center");
        
        this.matchCbCube();        
    }        
    
    /**
     * automatically called when applet starts
     */
    public void start()
    {                
        // updates the size of the canvas depending on the control panels
        this.canvas.changeSize(this.getSize().width - this.pControlW.getSize().width - this.pControlE.getSize().width, this.pControlW.getSize().height);
        
        this.canvas.img = this.createImage(this.canvas.getSize().width, this.canvas.getSize().height);
        this.canvas.dbg = this.canvas.img.getGraphics();
        
        MCRenderer.setWindow(this.canvas.img);
        MCRenderer.buildPrMatrix(MCRenderer.DEFAULT_FOCAL);
        
        this.started = true;
        this.resetAction(null);
        this.canvas.repaint();
    }
    
    /**
     * automatically called when applet stops
     */
    public void stop()
    {
    }
    
    /**
     * automatically called when applet get destroyed
     */
    public void destroy()
    {       
    }
    
    /**
     * updates the combo box that allow the user to choose the selected cube
     */
    private void matchCbCube()
    {
        this.cbCube.removeAllItems();
        Vector cubes = this.canvas.mesh.getCubes();
        for (int index = 0; index < cubes.size(); index++)
        {
            this.cbCube.addItem(cubes.elementAt(index));
        }
        this.cbCube.setSelectedIndex(0);
    }
    
    /**
     * manages action on the slider bars
     * @param ce event from the modified slider bar
     */    
    private void sVertexAction(ChangeEvent ce)
    {
        MCCube cCube = (MCCube) this.cbCube.getSelectedItem();
        
        for (int index = 0; index < this.sVertex.length; index++)
        {
            if (ce.getSource() == this.sVertex[index])
            {
                int value = this.sVertex[index].getValue();                
                
                cCube.v[index].weight.value = value;
                cCube.computeEdges();
                for (int n = 0; n < cCube.neighbours.size(); n++)
                {
                    MCCube nc = ((MCNeighbour) cCube.neighbours.elementAt(n)).cube;
                    nc.computeEdges();
                }                                
            }
        }
        
        //this.cbAmbigous.setEnabled(MCCube.isAmbigous(cCube.caseNumber()));        
        
        if (MCApplet.this.started)
            this.canvas.repaint();
    }
    
    /**
     * updates the textfield that displays the case number
     * @param ce event from the modified slider bar
     */
    private void sVertexActionTfCase(ChangeEvent ce)
    {
        MCCube cCube = (MCCube) this.cbCube.getSelectedItem();        
        this.tfCase.removeCaretListener(this.tfCaseListener);
        this.tfCase.setText((new Integer(cCube.caseNumber())).toString());         
        this.tfCase.addCaretListener(this.tfCaseListener);      
    }
    
    /**
     * manages add/delete cube actions
     * @param e event from the add or delete button
     */
    private void addSupAction(ActionEvent e)
    {
        if(e.getSource()==this.addB)
        {
            int position = 0;
            if (this.face1.isSelected())
                position = MCNeighbour.SOUTH;
            if (this.face2.isSelected())
                position = MCNeighbour.NORTH;
            if (this.face3.isSelected())
                position = MCNeighbour.EAST;
            if (this.face4.isSelected())
                position = MCNeighbour.WEST;
            if (this.face5.isSelected())
                position = MCNeighbour.DOWN;
            if (this.face6.isSelected())
                position = MCNeighbour.UP;
            
            if (position != 0)
            {
                String input = JOptionPane.showInputDialog(this, "Please enter a name for the new cube:", "Cube identifier", JOptionPane.INFORMATION_MESSAGE);
                if (input != null)
                    this.canvas.mesh.addCube((MCCube) (cbCube.getSelectedItem()), position, input);
            }
        }
        if(e.getSource()==this.supB)
        {
            if (this.canvas.mesh.getCubes().size() > 1)
                this.canvas.mesh.deleteCube((MCCube) (cbCube.getSelectedItem()));
        }
        this.matchCbCube();
        this.matchDisabledFaces();
        if (MCApplet.this.started)
            this.canvas.repaint();
    }
    
    /**
     * updates radio button allowing the user to choose a face when adding a new cube, depending on the neighbourhood of the current cube
     */
    private void matchDisabledFaces()
    {
        MCCube cube = (MCCube)(cbCube.getSelectedItem());
        
        boolean n=true;
        boolean s=true;
        boolean e=true;
        boolean w=true;
        boolean u=true;
        boolean d=true;
        
        for(int i=0; i<cube.neighbours.size(); i++)
        {
            MCNeighbour voisin =(MCNeighbour) cube.neighbours.elementAt(i);
            switch(voisin.position)
            {
                case MCNeighbour.NORTH:
                    n &=false;
                    break;
                case MCNeighbour.SOUTH:
                    s &=false;
                    break;
                case MCNeighbour.EAST:
                    e &=false;
                    break;
                case MCNeighbour.WEST:
                    w &=false;
                    break;
                case MCNeighbour.UP:
                    u &=false;
                    break;
                case MCNeighbour.DOWN:
                    d &=false;
                    break;
            }
        }
        
        face2.setEnabled(n);
        face1.setEnabled(s);
        face3.setEnabled(e);
        face4.setEnabled(w);
        face5.setEnabled(d);
        face6.setEnabled(u);
        
        face1.setSelected(false);
        face2.setSelected(false);
        face3.setSelected(false);
        face4.setSelected(false);
        face5.setSelected(false);
        face6.setSelected(false);
        face7.setSelected(true);
    }
    
    /**
     * manages cube selection from the dedicated combo box
     * @param ae event from the cube selection combo box
     */
    private void cbCubeChange(ActionEvent ae)
    {
        MCCube cube = (MCCube)(cbCube.getSelectedItem());
        if (cube != null)
        {
            for (int index = 0; index < this.sVertex.length; index++)
            {
                int p = cube.v[index].weight.value;               
                this.sVertex[index].setValue(p);
            }
            
            this.matchDisabledFaces();
            
            this.cbVisible.removeActionListener(this.cbVisibleListener);
            this.cbVisible.setSelected(cube.hidden);
            this.cbVisible.addActionListener(this.cbVisibleListener);
        }
        if (MCApplet.this.started)
            this.canvas.repaint();
    }
    
    /**
     * manages reference value modification
     * @param ce event from the reference value textfield
     */
    private void valueAction(CaretEvent ce)
    {
        try
        {
            double v = Double.parseDouble(this.value.getText());
            MCCube.seekValue = v;
            
            Vector theCubes = this.canvas.mesh.getCubes();
            for(int i=0; i<theCubes.size(); i++)
            {
                ((MCCube)theCubes.elementAt(i)).computeEdges();
            }
            
            if (MCApplet.this.started)
                this.canvas.repaint();
        } catch(NumberFormatException ne)
        {
        }
    }
    
    /**
     * manages material selection
     * @param ae event from the material selection combo box
     */
    private void cbMatAction(ActionEvent ae)
    {
        MCRenderer.material = (MCMaterial) cbMat.getSelectedItem();
        if (MCApplet.this.started)
            this.canvas.repaint();
    }
    
    /**
     * resets both view and mesh
     * @param ae event from the reset button
     */
    private void resetAction(ActionEvent ae)
    {
        this.canvas.mesh = new MCMesh();
        this.matchCbCube();
        if (MCApplet.this.started)
        {
            this.repaint();
            this.canvas.repaint();
        }
    }
    
    /**
     * switch to translation mode
     * @param ae event from the translaion radio button
     */
    private void transAction(ActionEvent ae)
    {
        this.canvas.tfMode = MCCanvas.TF_MODE_TRANSLATION;
    }
    
    /**
     * switch to rotation mode
     * @param ae event from the rotation radio button
     */
    private void rotAction(ActionEvent ae)
    {
        this.canvas.tfMode = MCCanvas.TF_MODE_ROTATION;
    }
    
    /**
     * manages complementary case action
     * @param ae event from the dedicated checkbox
     */
    private void bComplementaryAction(ActionEvent ae)
    {        
        MCCube cCube = (MCCube) this.cbCube.getSelectedItem();
        this.tfCase.setText((new Integer(255 - cCube.caseNumber())).toString());
        
        if (MCApplet.this.started)
            this.canvas.repaint();        
    }
    
    /**
     * manages min/max actions
     * @param be event from a min/max button
     */
    private void mButtonAction(ActionEvent ba)
    {                    
        JButton b = (JButton)ba.getSource();
        for (int i=0; i<this.mButton.length; i++)
        {
            if(mButton[i]==b)
            {
                if(i%2 == 0)
                {
                    this.sVertex[i/2].setValue((-1)*DEFAULT_RANGE);
                }else
                {
                    this.sVertex[i/2].setValue(DEFAULT_RANGE);
                }
            }
        }
    }
    
    /**
     * manages explicit case number specification
     * @param ce event from the case number textfield
     */
    private void tfCaseAction(CaretEvent ce)
    {
        try
        {
            int caseNumber = Integer.parseInt(this.tfCase.getText());
            if (caseNumber < 0 || caseNumber > 255)
                throw new NumberFormatException();
            
            for (int p = 7; p >= 0; p--)
            {
                this.sVertex[p].removeChangeListener(this.sVertexListenerTfCase);
                int n = (int) Math.pow(2, p);
                if (caseNumber >= n)
                {
                    this.sVertex[p].setValue(DEFAULT_RANGE);
                    caseNumber -= n;
                }
                else
                {
                    this.sVertex[p].setValue(-DEFAULT_RANGE);
                }
                this.sVertex[p].addChangeListener(this.sVertexListenerTfCase);
            }
        }
        catch (NumberFormatException nfe)
        {
        }
    }
    
    // keyboard events
    
    public void keyPressed(KeyEvent ke)
    {
        switch (ke.getKeyCode())
        {
            // enables transformations along z axis
            case KeyEvent.VK_SHIFT:
                this.shift = true;
                break;
        }
    }
    
    public void keyReleased(KeyEvent ke)
    {
        switch (ke.getKeyCode())
        {
            // enables transformations along xy axis
            case KeyEvent.VK_SHIFT:
                this.shift = false;
                break;
        }
    }
    
    public void keyTyped(KeyEvent ke)
    {
        switch (ke.getKeyChar())
        {
            // cycle through all the cubes
            case ' ':                
                int index = this.cbCube.getSelectedIndex() + 1;
                if (index >= this.cbCube.getItemCount())
                {
                    index = 0;
                }
                this.cbCube.setSelectedIndex(index);                
                break;
        }                    
    }
    
    /**
     * toggles selected cube visibility
     * @param ae event from the dedicated checkbox
     */
    private void cbVisibleAction(ActionEvent ae)
    {
        MCCube cCube = (MCCube) this.cbCube.getSelectedItem();
        cCube.hidden = !cCube.hidden;
        if (this.started)
            this.canvas.repaint();
    }
    
    /**
     * toggles ambigous case resolution
     * @ ae event from the dedicated checkbox
     */
    private void cbAmbigousAction(ActionEvent ae)
    {
        MCCube cCube = (MCCube) this.cbCube.getSelectedItem();
        cCube.amb = !cCube.amb;
        if (this.started)
            this.canvas.repaint();
    }
}