package graph;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.filechooser.*;
import lib.swing.*;
import lib.util.*;

/*
outlets:
  Jframe graphFrame;
  JTextArea transcript;
  JTextField command;
  JButton prev;
  JButton clear;
  JButton next;
  JButton calc;
  JButton load;
  JButton reload;
  JamaCanvas graph;
  JtableInput input;
  JamaCanvas graph2d;
  JButton up;
  JButton left;
  JButton right;
  JButton down;
  JButton changeButton;
  Jlist figureList;
  JButton startButton;
  JcomboBox program;
  JCheckBox directDrawCheckBox;
  Jdialog graphDialog;
  JamaCanvas amagraphics;
  JtableInput params;
  JButton paramsButton;
  JcomboBox actions;
  JButton actionButton;
actions:
  void commandClick() {}
  void prevClick() {}
  void clearClick() {}
  void nextClick() {}
  void calcClick() {}
  void loadClick() {}
  void reloadClick() {}
  void graphFrameClose() {}
  void graphDialogClose() {}
  void inputClick() {}
  void upClick() {}
  void leftClick() {}
  void rightClick() {}
  void downClick() {}
  void changeButtonClick() {}
  void startButtonClick() {}
  void programClick() {}
  void directDrawCheckBoxClick() {}
  void paramsClick() {}
  void paramsButtonClick() {}
  void actionsClick() {}
  void actionButtonClick() {}
  void initGUI() {}
*/

public class GraphApp extends GraphGUI implements ActionListener
{
  AmaObj amandaObj      = null;
  AmaObj graphObj       = null;
  AmaObj graph2dObj     = null;
  AmaObj amagraphicsObj = null;
  String path           = "../Amanda/ama -obj ../Amanda/Amagr/";
  String prog           = "../Amanda/ama -proc";
  String dir            = "../Amanda";
  Timer amagraphicsTimer;
  JFileChooser fileChooser;
  Seq<String> history = new Seq<String>("");
  int historyNr = 0; 

  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new GraphApp());
  }

  void initGUI()
  {
    amandaObj = new AmaObj(prog);
    if(!amandaObj.isOk()) 
    {
      Jdialog.message("couldn't run program: " + prog, Jdialog.messageOK, graphFrame);
      graphFrameClose();
    }
    Font font = new Font("Courier", Font.PLAIN, 14);
    command.setFont(font);
    transcript.setFont(font);
    transcript.setEditable(false);
    transcript.setLineWrap(true);
    transcript.append(amandaObj.getResult());
    transcript.append("\n");
    transcript.append("\n");
    prog = path + "graph.ama";
    graphObj = new AmaObj(prog, "AmaGraphics");
    if(!graphObj.isOk()) 
    {
      Jdialog.message("couldn't run program: " + prog, Jdialog.messageOK, graphFrame);
      graphFrameClose();
    }
    graph.setAmaObj(graphObj);
    prog = path + "graph2d.ama";
    graph2dObj = new AmaObj(prog, "AmaGraphics");
    if(!graph2dObj.isOk())
    {
      Jdialog.message("couldn't run program: " + prog, Jdialog.messageOK, graphFrame);
      graphFrameClose();
    }
    graph2d.setAmaObj(graph2dObj);
    Seq<String> paramList = graph2dObj.exec("DefParameters");
    for(int k=0; k<paramList.size(); k+=2)
      figureList.add(paramList.get(k), paramList.get(k+1).startsWith("True"));
    graphFrame.setVisible(true);
    amagraphicsTimer = new Timer(30, this);
    JtableInput.checkBoxEnabled = true;
    JtableInput.trueValue = "True";
    JtableInput.falseValue = "False";
    fileChooser = new JFileChooser(dir);
  }

  public void actionPerformed(ActionEvent evt)
  {
    if(evt.getSource() == amagraphicsTimer)
    {
      amagraphicsTimer.stop();
      amagraphics.exec("Timer");
      amagraphicsTimer.start();
    }
  }

  void graphFrameClose()
  {
    if(graphObj != null) graphObj.close();
    if(graph2dObj != null) graph2dObj.close();
    graphFrame.setVisible(false);
    System.exit(0);
  }

  void graphDialogClose() 
  {
    if(amagraphicsObj != null) amagraphicsObj.close();
    amagraphicsObj = null;
    amagraphicsTimer.stop();
    graphDialog.setVisible(false);
  }
  
  void commandClick() 
  {
    String cs = command.getText();
    transcript.append("> " + cs);
    transcript.append("\n");
    amandaObj.execute(cs);
    history.add(cs);
    historyNr = history.size() - 1;
    for(;;)
    {
      String s = amandaObj.getResult();
      if(s == null) break;
      transcript.append(s);
      transcript.append("\n");
    }
    transcript.setCaretPosition(transcript.getText().length());
  }
  
  void prevClick()
  {
    if(historyNr > 0) historyNr--;
    command.setText(history.get(historyNr));
  }
  
  void clearClick()
  {
    command.setText("");
  }
  
  void nextClick()
  {
    if(historyNr < history.size()-1) historyNr++;
    command.setText(history.get(historyNr));
  }
  
  void calcClick()
  {
    commandClick();
  }
  
  void loadClick()
  {
    int returnVal = fileChooser.showOpenDialog(graphFrame);
    if(returnVal == JFileChooser.APPROVE_OPTION) 
    {
      File file = fileChooser.getSelectedFile();
      command.setText("load " + file.getPath());
      commandClick();
    }
  }
  
  void reloadClick()
  {
    command.setText("reload");
    commandClick();
  }
    
  void inputClick()
  {
    graph.exec("Parameters \"" + input.getItemAt(0) + "\" "  + input.getItemAt(1) + " " + input.getItemAt(2));
  }

  void changeButtonClick()
  {
    String command = "Parameters";
    for(int k=0; k<figureList.getItemCount(); k++)
      command = command + " " + (figureList.isSelectedAt(k) ? "True" : "False");
    graph2d.exec(command);
  }
  
  void upClick() 
  {
    graph2d.exec("Key w");
  }
  
  void leftClick()
  {
    graph2d.exec("Key a");
  }
  
  void rightClick()
  {
    graph2d.exec("Key s");
  }
  
  void downClick()
  {
    graph2d.exec("Key z");
  }

  void startButtonClick() 
  {
    graphDialogClose();
    String prog = path + program.getSelectedItem() + ".ama";
    amagraphicsObj = new AmaObj(prog, "AmaGraphics");
    if(!amagraphicsObj.isOk())
    {
      amagraphicsObj.close();
      amagraphicsObj = null;      
      Jdialog.message("couldn't run program: " + prog, Jdialog.messageOK, graphFrame);
      return;
    }
    amagraphics.setDirectDrawing(directDrawCheckBox.isSelected());
    boolean startTimer = false;
    Seq<String> systemList = amagraphicsObj.exec("DefSystem");
    for(int k=0; k<systemList.size(); k+=2)
    if(systemList.get(k).equals("timer"))
    {
      int delay = Integer.parseInt(systemList.get(k+1));
      amagraphicsTimer.setDelay(delay);
      amagraphicsTimer.setInitialDelay(delay);
      startTimer = true;
    }
    Seq<String> paramList = amagraphicsObj.exec("DefParameters");
    String tableDef = "";
    for(int k=0; k<paramList.size(); k+=2)
    {
      if(k > 0) tableDef = tableDef + ":";
      tableDef = tableDef + paramList.get(k) + "|" + paramList.get(k+1);
    }
    Seq<String> actionList = amagraphicsObj.exec("DefActions");
    String actionDef = "";
    for(int k=0; k<actionList.size(); k++)
    {
      if(k > 0) actionDef = actionDef + "|";
      actionDef = actionDef + actionList.get(k);
    }  
    JgridPanel parent = (JgridPanel)params.getParent();
    parent.remove(params);    
    params = new JtableInput(tableDef, 5, 5);
    parent.add(false, false, 0, 0, params);    
    parent.setVisible(params.getItemCount() > 0);
    parent = (JgridPanel)actions.getParent();
    parent.remove(actions);    
    actions = new JcomboBox(actionDef);
    parent.add(false, false, 0, 0, actions); 
    parent.setVisible(actions.getItemCount() > 0);
    amagraphics.setAmaObj(amagraphicsObj);
    graphDialog.pack();
    graphDialog.setVisible(true);
    if(startTimer) amagraphicsTimer.start();
  }
  
  void paramsButtonClick() 
  {
    String command = "Parameters";
    for(int k=0; k<params.getItemCount(); k++)
      command = command + " \"" + params.getItemAt(k) + "\"";
    amagraphics.exec(command);
  }

  void actionButtonClick()
  {
    amagraphics.exec("Action " + actions.getSelectedItem());
  }
  
}

