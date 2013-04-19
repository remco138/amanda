package lib.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import lib.util.*;

public class JtableInput extends JgridPanel implements ActionListener
{
  public static boolean checkBoxEnabled = false;
  public static String trueValue  = "true";
  public static String falseValue = "false";
  
  private ActionListener listener;
  private Seq<Component> inputs;

  // caption example: prompt1|value21|value22:prompt2:prompt3|value3
  // items are separated by :
  // values are separated by |
  public JtableInput(String caption, int textWidth, int rows)
  {
    listener = null;
    inputs = new Seq<Component>();
    Seq<String> prompts = Util.tokens(caption, ':');
    for(int k=0; k<prompts.size(); k++)
    {
      Seq<String> ts = Util.tokens(prompts.get(k), '|');
      JLabel label = new JLabel(ts.get(0));
      Component comp;
      switch(ts.size())
      {
        case 1:
          comp = new JTextField(textWidth);
          break;
        case 2:
          comp = new JTextField(ts.get(1), textWidth);
          break;
        default:
          if(checkBoxEnabled && ts.size() == 3 
          && ((ts.get(1).equals(trueValue) && ts.get(2).equals(falseValue))
             || (ts.get(2).equals(trueValue) && ts.get(1).equals(falseValue))))
          {
            JCheckBox cb = new JCheckBox();
            cb.setSelected(ts.get(1).equals(trueValue));
            comp = cb;
          }
          else
          {
            JComboBox cb = new JComboBox();
            for(int l=1; l<ts.size(); l++)
              cb.addItem(ts.get(l));
            comp = cb;
          }
          break;
      }
      add(false, false, k % rows, 0 + 2 * (k / rows), label);
      add(false, true , k % rows, 1 + 2 * (k / rows), comp);
      inputs.add(comp);
    }
  }
  
  public void addActionListener(ActionListener listener)
  {
    this.listener = listener;
    for(Component comp : inputs)
      if(comp instanceof JTextField)
        ((JTextField)comp).addActionListener(this);
  }
  
  public void actionPerformed(ActionEvent e)
  {
    if(listener != null)
      listener.actionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand()));
  }
  
  public int getItemCount()
  {
    return inputs.size();
  }
  
  public String getItemAt(int k)
  {
    if(k < 0 || k >= inputs.size())
      return null;
    if(inputs.get(k) instanceof JComboBox)
      return (String)(((JComboBox)inputs.get(k)).getSelectedItem());
    else if(inputs.get(k) instanceof JCheckBox)
      return ((JCheckBox)inputs.get(k)).isSelected() ? trueValue : falseValue;
    else
      return ((JTextField)inputs.get(k)).getText();
  }

  public void setItemAt(String s, int k)
  {
    if(k < 0 || k >= inputs.size())
      ;
    else if(inputs.get(k) instanceof JComboBox)
    {
      JComboBox cb = (JComboBox)inputs.get(k);
      for(int l=0; l<cb.getItemCount(); l++)
      if(s.equals(cb.getItemAt(l)))
      {
        cb.setSelectedIndex(l);
        break;
      }
    }
    else if(inputs.get(k) instanceof JCheckBox)
      ((JCheckBox)inputs.get(k)).setSelected(s.equals(trueValue));
    else
      ((JTextField)inputs.get(k)).setText(s);
  }
}
