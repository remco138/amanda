package lib.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Jframe extends JFrame implements Jlistener, ActionListener, ItemListener, WindowListener
{
  private Jlistener listener;

  public void addlistener(Jlistener listener)
  {
    this.listener = listener;
  }

  public Jframe(int x, int y, String title)
  {
    super(title);
    listener = this;
    addWindowListener(this);
    setLayout(new BorderLayout());
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setBounds(x, y, 100, 100);
  }
  
  public void onClick(Object comp)
  {
  }

  public void onClose()
  {
  }

  public final void onClose(Window win)
  {
    onClose();
  }

  public final void actionPerformed(ActionEvent e)
  {
    listener.onClick(e.getSource());
  }

  public final void itemStateChanged(ItemEvent e)
  {
    listener.onClick(e.getItemSelectable());
  }

  public final void windowActivated(WindowEvent e)
  {
  }
  
  public final void windowClosed(WindowEvent e)
  {
  }
  
  public final void windowClosing(WindowEvent e)
  {
    listener.onClose(this);
  }
  
  public final void windowDeactivated(WindowEvent e)
  {
  }
  
  public final void windowDeiconified(WindowEvent e)
  {
  }
  
  public final void windowIconified(WindowEvent e)
  {
  }
  
  public final void windowOpened(WindowEvent e)
  {
  }
}
