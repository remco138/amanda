package lib.swing;

import java.awt.*;
import javax.swing.*;

public class JverticalPanel extends JgridPanel
{
  private int count;
  
  public JverticalPanel()
  {
    count = 0;
  }
  
  public void add(boolean isResizeable, Component comp)
  {
    add(isResizeable, true, count++, 0, comp);
  }
}

