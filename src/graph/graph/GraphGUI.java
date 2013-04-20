package graph;

import java.awt.*;
import javax.swing.*;
import lib.swing.*;

public class GraphGUI implements Jlistener, Runnable
{
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

  void graphFrameClose() {}
  void graphDialogClose() {}
  void commandClick() {}
  void prevClick() {}
  void clearClick() {}
  void nextClick() {}
  void calcClick() {}
  void loadClick() {}
  void reloadClick() {}
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

  public void run()
  {
    try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception e){}
    graphFrame = new Jframe(0, 0, "Graphics");
    graphFrame.addlistener(this);
    JTabbedPane elem_1;
    graphFrame.add(elem_1 = new JTabbedPane());
    JverticalPanel elem_2;
    elem_1.add("Amanda", elem_2 = new JverticalPanel());
    elem_2.add(true, new JScrollPane(transcript = new JTextArea(40, 60)));
    JverticalPanel elem_4;
    elem_2.add(false, elem_4 = new JverticalPanel());
    elem_4.add(true, command = new JTextField("", 10));
    command.addActionListener(graphFrame);
    JhorizontalPanel elem_6;
    elem_4.add(true, elem_6 = new JhorizontalPanel());
    elem_6.add(false, prev = new JButton("<<"));
    prev.addActionListener(graphFrame);
    elem_6.add(false, clear = new JButton(""));
    clear.addActionListener(graphFrame);
    elem_6.add(false, next = new JButton(">>"));
    next.addActionListener(graphFrame);
    elem_6.add(true, calc = new JButton("calculate"));
    calc.addActionListener(graphFrame);
    elem_6.add(false, load = new JButton("load"));
    load.addActionListener(graphFrame);
    elem_6.add(false, reload = new JButton("reload"));
    reload.addActionListener(graphFrame);
    JverticalPanel elem_13;
    elem_1.add("Graph", elem_13 = new JverticalPanel());
    elem_13.add(true, graph = new JamaCanvas(400, 400));
    elem_13.add(false, input = new JtableInput("expr:min:max", 20, 3));
    input.addActionListener(graphFrame);
    JverticalPanel elem_16;
    elem_1.add("Graph2d", elem_16 = new JverticalPanel());
    elem_16.add(true, graph2d = new JamaCanvas(400, 400));
    JhorizontalPanel elem_18;
    elem_16.add(false, elem_18 = new JhorizontalPanel());
    elem_18.add(true, new Jpadding(0, 0));
    JverticalPanel elem_20;
    elem_18.add(false, elem_20 = new JverticalPanel());
    JhorizontalPanel elem_21;
    elem_20.add(false, elem_21 = new JhorizontalPanel());
    elem_21.add(false, up = new JButton("^"));
    up.addActionListener(graphFrame);
    JhorizontalPanel elem_23;
    elem_20.add(false, elem_23 = new JhorizontalPanel());
    elem_23.add(false, left = new JButton("<"));
    left.addActionListener(graphFrame);
    elem_23.add(false, right = new JButton(">"));
    right.addActionListener(graphFrame);
    JhorizontalPanel elem_26;
    elem_20.add(false, elem_26 = new JhorizontalPanel());
    elem_26.add(false, down = new JButton("v"));
    down.addActionListener(graphFrame);
    elem_18.add(true, new Jpadding(0, 0));
    JverticalPanel elem_29;
    elem_18.add(false, elem_29 = new JverticalPanel());
    elem_29.add(false, changeButton = new JButton("change"));
    changeButton.addActionListener(graphFrame);
    elem_18.add(false, new JScrollPane(figureList = new Jlist(5)));
    JverticalPanel elem_32;
    elem_1.add("AmaGraphics", elem_32 = new JverticalPanel());
    JhorizontalPanel elem_33;
    elem_32.add(false, elem_33 = new JhorizontalPanel());
    elem_33.add(false, startButton = new JButton("Start"));
    startButton.addActionListener(graphFrame);
    elem_33.add(false, program = new JcomboBox("barriere|geen3|tetris|puzzel|life|spline|curve|convex|robot|graph|graph2d"));
    program.addActionListener(graphFrame);
    JhorizontalPanel elem_36;
    elem_32.add(false, elem_36 = new JhorizontalPanel());
    elem_36.add(false, directDrawCheckBox = new JCheckBox("direct drawing"));
    directDrawCheckBox.addActionListener(graphFrame);
    graphDialog = new Jdialog(600, 100, "AmaGraphics", graphFrame);
    graphDialog.addlistener(this);
    JverticalPanel elem_39;
    graphDialog.add(elem_39 = new JverticalPanel());
    elem_39.add(true, amagraphics = new JamaCanvas(400, 400));
    JhorizontalPanel elem_41;
    elem_39.add(false, elem_41 = new JhorizontalPanel());
    JverticalPanel elem_42;
    elem_41.add(false, elem_42 = new JverticalPanel());
    elem_42.add(false, params = new JtableInput("expr:min:max", 5, 3));
    params.addActionListener(graphDialog);
    elem_42.add(false, paramsButton = new JButton("Set"));
    paramsButton.addActionListener(graphDialog);
    elem_41.add(true, new Jpadding(0, 0));
    JverticalPanel elem_46;
    elem_41.add(false, elem_46 = new JverticalPanel());
    elem_46.add(false, actions = new JcomboBox("25|30|35|40|45"));
    actions.addActionListener(graphDialog);
    elem_46.add(false, actionButton = new JButton("Do"));
    actionButton.addActionListener(graphDialog);
    graphFrame.pack();
    graphDialog.pack();
    initGUI();
  }

  public void onClose(Window win)
  {
    if(win == this.graphFrame) graphFrameClose();
    if(win == this.graphDialog) graphDialogClose();
  }

  public void onClick(Object comp)
  {
    if(comp == this.command) commandClick();
    if(comp == this.prev) prevClick();
    if(comp == this.clear) clearClick();
    if(comp == this.next) nextClick();
    if(comp == this.calc) calcClick();
    if(comp == this.load) loadClick();
    if(comp == this.reload) reloadClick();
    if(comp == this.input) inputClick();
    if(comp == this.up) upClick();
    if(comp == this.left) leftClick();
    if(comp == this.right) rightClick();
    if(comp == this.down) downClick();
    if(comp == this.changeButton) changeButtonClick();
    if(comp == this.startButton) startButtonClick();
    if(comp == this.program) programClick();
    if(comp == this.directDrawCheckBox) directDrawCheckBoxClick();
    if(comp == this.params) paramsClick();
    if(comp == this.paramsButton) paramsButtonClick();
    if(comp == this.actions) actionsClick();
    if(comp == this.actionButton) actionButtonClick();
  }
}
