package lib.swing;

import java.awt.*;
import javax.swing.*;

public class JgridPanel extends JPanel
{
  public static int padx = 1;
  public static int pady = 1;

  private GridBagLayout gridBag;
  
  public JgridPanel()
  {
    gridBag = new GridBagLayout();
    setLayout(gridBag);
  }
  
  public void add(boolean verResizeable, boolean horResizeable, int row, int col, Component comp)
  {
    add(comp);
    GridBagConstraints cs = new GridBagConstraints();
    cs.gridx = col;
    cs.gridy = row;
    cs.fill = GridBagConstraints.BOTH;
    cs.weightx = horResizeable ? 1.0 : 0.0;
    cs.weighty = verResizeable ? 1.0 : 0.0;
    if(this instanceof JtableInput)
      cs.insets = new Insets(1, 1, 1, 1);
    else if(comp instanceof JtableInput)
      cs.insets = new Insets(pady, padx, pady, padx);
    else if(comp instanceof JgridPanel)    
      cs.insets = new Insets(0, 0, 0, 0);
    else
      cs.insets = new Insets(pady, padx, pady, padx);
    gridBag.setConstraints(comp, cs);
  }
}
