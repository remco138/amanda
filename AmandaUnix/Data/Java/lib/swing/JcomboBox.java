package lib.swing;

import java.awt.*;
import javax.swing.*;
import lib.util.*;

public class JcomboBox extends JComboBox
{
  public JcomboBox(String s)
  {
    int maxlen = 2;
    for(String t : Util.tokens(s, '|'))
    {
      if(t.length() > 0) addItem(t);
      if(t.length() > maxlen) maxlen = t.length();
    }
    setPrototypeDisplayValue(Util.repChar('X', maxlen));
  }
  
  public void select(String s)
  {
    for(int l=0; l<getItemCount(); l++)
    if(s.equals(getItemAt(l)))
    {
      setSelectedIndex(l);
      break;
    }
  }
}
