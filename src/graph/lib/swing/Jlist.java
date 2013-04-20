package lib.swing;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import lib.util.*;

public class Jlist extends JList
{
  private DefaultListModel model;
  private int width;
  
  public Jlist(int rows)
  {
    model = new DefaultListModel();
    setModel(model);
    setVisibleRowCount(rows);
    removeAll();
  }
  
  public void removeAll()
  {
    model.clear();
    width = 5;
    setPrototypeCellValue(Util.repChar('X', width));
  }
  
  public int getItemCount()
  {
    return model.getSize();
  }
  
  public String getItemAt(int index)
  {
    return (String)model.get(index);
  }
  
  public boolean isSelectedAt(int index)
  {
    return isSelectedIndex(index);
  }
  
  public void add(String item, boolean selected)
  {
    model.addElement(item);
    if(selected) 
      addSelectionInterval(getItemCount()-1, getItemCount()-1);
    if(item.length() > width)
    {
      width = item.length();
      setPrototypeCellValue(Util.repChar('X', width));
    }
  }  
}

