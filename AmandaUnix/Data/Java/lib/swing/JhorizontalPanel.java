package lib.swing;

import java.awt.*;
import javax.swing.*;

public class JhorizontalPanel extends JgridPanel
{
  private int count;
  
  public JhorizontalPanel()
  {
    count = 0;
  }
  
  public void add(boolean isResizeable, Component comp)
  {
    add(true, isResizeable, 0, count++, comp);
  }
}

