package lib.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Jdialog extends JDialog implements Jlistener, ActionListener, ItemListener, WindowListener
{
  private Jlistener listener;

  public void addlistener(Jlistener listener)
  {
    this.listener = listener;
  }

  public Jdialog(int x, int y, String title, Jframe parent)
  {
    super(parent, title);
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
  
  public static String []messageOK = {"Ok"};
  public static String []messageYESNO = {"Yes", "No"};
  
  public static int message(String s, String []buttonLabels, Jframe parent)
  {
    JmessageDialog m = new JmessageDialog(s, buttonLabels, parent);
    return m.nr;
  }

  // caption example: prompt1|value21|value22:prompt2:prompt3|value3
  // items are separated by :
  // values are separated by |
  public static String [] form(String title, String caption, Jframe parent)
  {
    JformDialog f = new JformDialog(title, caption, parent);
    return f.result;
  }

  public static String form(String title, String prompt, String defaultValue, Jframe parent)
  {
    String []result = form(title, prompt + "|" + defaultValue, parent);
    return result == null ? null : result[0];
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

class JmessageDialog extends Jdialog
{
  public int nr;
  private JButton []buttons;
  
  public JmessageDialog(String s, String []buttonLabels, Jframe parent)
  {
    super(300, 200, parent.getTitle() + " message", parent);
    setModal(true);
    Panel panel1 = new Panel();
    Panel panel2 = new Panel();
    add(panel1, BorderLayout.NORTH);
    add(panel2, BorderLayout.SOUTH);
    panel1.add(new JLabel(s));
    buttons = new JButton[buttonLabels.length];
    for(int k=0; k<buttonLabels.length; k++)
    {
      buttons[k] = new JButton(buttonLabels[k]);
      buttons[k].addActionListener(this);
      panel2.add(buttons[k]);
    }
    getToolkit().beep();
    pack();
    setVisible(true);
  }
  
  public void onClick(Object comp)
  {
    nr = -1;
    for(int k=0; k<buttons.length; k++)
      if(buttons[k] == comp)
        nr = k;
    setVisible(false);
    dispose();
  }

  public void onClose()
  {
    nr = -1;
    setVisible(false);
    dispose();
  }
}

class JformDialog extends Jdialog
{
  public String []result;
  private JtableInput table;
  
  public JformDialog(String title, String caption, Jframe parent)
  {
    super(300, 200, title, parent);
    setModal(true);
    table = new JtableInput(caption, 20, 10);
    Panel panel = new Panel();
    add(table, BorderLayout.NORTH);
    add(panel, BorderLayout.SOUTH);
    JButton okButton = new JButton("Ok");
    panel.add(okButton);
    okButton.addActionListener(this);
    pack();
    setVisible(true);
  }
  
  public void onClick(Object comp)
  {
    int count = table.getItemCount();
    result = new String[count];
    for(int k=0; k<count; k++)
      result[k] = table.getItemAt(k);
    setVisible(false);
    dispose();
  }

  public void onClose()
  {
    result = null;
    setVisible(false);
    dispose();
  }
}
