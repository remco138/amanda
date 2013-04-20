package lib;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import lib.swing.*;
import lib.util.*;

public class Layman implements Jlistener, DocumentListener
{
  Jframe frame;
  Jframe resultFrame;
  JTextArea text;
  JButton previewButton;
  JButton guiButton;
  JButton appButton;
  
  String filename;
  boolean changed;
  
  public static void main(final String[] args)
  {
    SwingUtilities.invokeLater(new Runnable(){public void run(){new Layman(args);}});
  }
  
  public Layman(String []args)
  {
    try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception e){}
    JPanel panel = new JPanel();
    frame = new Jframe(10, 50, "Layman");
    frame.addlistener(this);
    text = new JTextArea(20, 50);
    text.setFont(new Font("Courier", Font.PLAIN, 12));
    text.getDocument().addDocumentListener(this);
    frame.add(new JScrollPane(text), BorderLayout.CENTER);
    frame.add(panel, BorderLayout.SOUTH);
    panel.add(previewButton = new JButton("Preview"));
    previewButton.addActionListener(frame);
    panel.add(guiButton = new JButton("Generate GUI"));
    guiButton.addActionListener(frame);
    panel.add(appButton = new JButton("Generate App"));
    appButton.addActionListener(frame);

    changed = false;
    filename = args.length > 0 ? args[0] : "untitled.jlf";
    loadFromFile();
    
    resultFrame = null;
    
    frame.pack();
    frame.setVisible(true);
  }
  
  public void insertUpdate(DocumentEvent e)
  {
    changed = true;
  }
  
  public void removeUpdate(DocumentEvent e)
  {
    changed = true;
  }
  
  public void changedUpdate(DocumentEvent e)
  {
    // changed = true;
  }
  
  public void onClose(Window win)
  {
    if(win == frame)          closeFrame();
    if(win == resultFrame)    closeResultFrame();
    if(win instanceof JDialog) win.setVisible(false);
  }
  
  public void onClick(Object comp)
  {
    if(comp == guiButton)     clickGUIButton();
    if(comp == appButton)     clickAppButton();
    if(comp == previewButton) clickPreviewButton();
  }
  
  void closeFrame()
  {
    if(!changed)
      System.exit(0);
    else
      switch(Jdialog.message("save " + filename + " ?", Jdialog.messageYESNO, frame))
      {
        case 0:
          saveToFile();
          System.exit(0);
        case 1:
          System.exit(0);
      }
  }
  
  void closeResultFrame()
  {
    if(resultFrame != null)
    {
      resultFrame.setVisible(false);
      resultFrame.dispose();
      resultFrame = null;
    }
  }
  
  void clickGUIButton()
  {
    try
    {
      Seq<Insider> insiders = parse();
      if(insiders == null) return;
      Insider app = insiders.get(0);
      insiders.remove(0);
      Insider frame = insiders.get(0);
      PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(app.name + "GUI.java")));
      if(app.caption.length() > 0)
      {
        output.println("package " + app.caption + ";");
        output.println("");
      }
      output.println("import java.awt.*;");
      output.println("import javax.swing.*;");
      output.println("import lib.swing.*;");
      output.println("");
      output.println("public class " + app.name + "GUI implements Jlistener, Runnable");
      output.println("{");
      for(Insider insider : insiders)
        if(insider.isWindow())
          output.println("  " + insider.typeName() + " " + insider.name +";");
        else if(insider.anonymous() || insider.isContainer())
          ;
        else if(!insider.isMatrix())
          output.println("  " + insider.typeName() + " " + insider.name +";");
        else
          output.println("  " + insider.typeName() + " [][]" + insider.name +";");
      output.println("");
      for(Insider insider : insiders)
        if(insider.isWindow())
          output.println("  void " + insider.name + "Close() {}");
      for(Insider insider : insiders)
        if(insider.hasAction() || insider.hasItem())
          output.println("  void " + insider.name + "Click() {}");
      output.println("  void initGUI() {}");
      output.println("");
      output.println("  public void run()");
      output.println("  {");
      if(!app.isResizeable)
        output.println("    try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}");
      else
        output.println("    try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception e){}");
      String winName = frame.name;
      for(int k=0; k<insiders.size(); k++)
      {
        Insider insider = insiders.get(k);
        if(insider.isFrame())
        {
          output.println("    " + insider.name + " = new Jframe(" + insider.x + ", " + insider.y + ", \"" + insider.caption + "\");");
          output.println("    " + insider.name + ".addlistener(this);");
          winName = insider.name;
        }
        else if(insider.isDialog())
        {
          output.println("    " + insider.name + " = new Jdialog(" + insider.x + ", " + insider.y + ", \"" + insider.caption + "\", " + frame.name + ");");
          output.println("    " + insider.name + ".addlistener(this);");
          winName = insider.name;
        }
        else
        {
          Insider parent = insiders.get(Insider.parentIndex(k, insiders));
          if(insider.isMatrix())
          {
            if(!insider.anonymous())
              output.println("    " + insider.name + " = new " + insider.typeName() + "[" + insider.rows + "][" + insider.cols + "];");
            output.println("    for(int r=0; r<" + insider.rows + "; r++)");
            output.println("    for(int c=0; c<" + insider.cols + "; c++)");
            output.println("      " + parent.name + ".add(" + insider.isResizeable + ", " + insider.isResizeable2 + ", " + insider.rowstart + " + r * " + insider.rowstep  + ", " + insider.colstart + " + c * " + insider.colstep  + ", " + insider.defName(true) + ");");
          }
          else
          {
            if(insider.isContainer())
            {
              insider.name = "elem_" + k;
              output.println("    " + insider.typeName() + " " + insider.name + ";");
            }
            String def = insider.defName(false);
            if(parent.isHP())
              output.println("    " + parent.name + ".add(" + insider.isResizeable + ", " + def + ");");
            else if(parent.isVP())
              output.println("    " + parent.name + ".add(" + insider.isResizeable + ", " + def + ");");
            else if(parent.isGP())
              output.println("    " + parent.name + ".add(" + insider.isResizeable + ", " + insider.isResizeable2 + ", " + insider.rowstart + ", " + insider.colstart + ", " + def + ");");
            else if(parent.isCP())
              output.println("    " + parent.name + ".add(\"" + insider.caption + "\", " + def + ");");
            else if(insider.isMenuBar())
            {
              output.println("    " + parent.name + ".setJMenuBar(" + def + ");");
              if(!app.isResizeable)
                output.println("    " + insider.name + ".setBackground(SystemColor.control);");
            }
            else if(insider.isMenuItem() && insider.caption.equals("-"))
              output.println("    " + parent.name + ".addSeparator();");
            else
              output.println("    " + parent.name + ".add(" + def + ");");
            if(insider.hasAction())
              output.println("    " + insider.name + ".addActionListener(" + winName + ");");
            if(insider.hasItem())
              output.println("    " + insider.name + ".addItemListener(" + winName + ");");
          }
        }
      }
      for(Insider insider : insiders)
        if(insider.isWindow())
        {
          output.println("    " + insider.name + ".pack();");
          if(insider.isResizeable)
            output.println("    " + insider.name + ".setVisible(" + true + ");");
        }
      output.println("    initGUI();");
      output.println("  }");
      output.println("");
      output.println("  public void onClose(Window win)");
      output.println("  {");
      for(Insider insider : insiders)
        if(insider.isWindow())
          output.println("    if(win == this." + insider.name + ") " + insider.name + "Close();");
      output.println("  }");
      output.println("");
      output.println("  public void onClick(Object comp)");
      output.println("  {");
      for(Insider insider : insiders)
        if(insider.hasAction() || insider.hasItem())
          output.println("    if(comp == this." + insider.name + ") " + insider.name + "Click();");
      output.println("  }");
      output.println("}");
      output.close();
    }
    catch(Exception e)
    {
      Jdialog.message("couldn't create output", Jdialog.messageOK, frame);
    }
  }
  
  void clickAppButton()
  {
    try
    {
      Seq<Insider> insiders = parse();
      if(insiders == null) return;
      Insider app = insiders.get(0);
      insiders.remove(0);
      if(new File(app.name + "App.java").exists()
      && Jdialog.message("overwrite " + app.name + "App.java ?", Jdialog.messageYESNO, frame) != 0)
        return;
      PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(app.name + "App.java")));
      if(app.caption.length() > 0)
      {
        output.println("package " + app.caption + ";");
        output.println("");
      }
      output.println("import java.awt.*;");
      output.println("import javax.swing.*;");
      output.println("import lib.swing.*;");
      output.println("");
      output.println("/*");
      output.println("outlets:");
      for(Insider insider : insiders)
        if(insider.isWindow())
          output.println("  " + insider.typeName() + " " + insider.name +";");
        else if(insider.anonymous() || insider.isContainer())
          ;
        else if(!insider.isMatrix())
          output.println("  " + insider.typeName() + " " + insider.name +";");
        else
          output.println("  " + insider.typeName() + " [][]" + insider.name +";");
      output.println("actions:");
      for(Insider insider : insiders)
        if(insider.hasAction() || insider.hasItem())
          output.println("  void " + insider.name + "Click() {}");
      output.println("  void initGUI() {}");
      output.println("*/");
      output.println("");
      output.println("public class " + app.name + "App extends " + app.name + "GUI");
      output.println("{");
      output.println("  public static void main(String[] args)");
      output.println("  {");
      output.println("    SwingUtilities.invokeLater(new " + app.name + "App());");
      output.println("  }");
      for(Insider insider : insiders)
        if(insider.isWindow())
        {
          output.println("");
          output.println("  void " + insider.name + "Close()");
          output.println("  {");
          output.println("    " + insider.name + ".setVisible(false);");
          if(insider.isFrame())
            output.println("    System.exit(0);");
          output.println("  }");
        }
      output.println("}");
      output.close();
    }
    catch(Exception e)
    {
      Jdialog.message("couldn't create output", Jdialog.messageOK, frame);
    }
  }
  
  void clickPreviewButton()
  {
    closeResultFrame();
    Seq<Insider> insiders = parse();
    if(insiders == null) return;
    insiders.remove(0);
    Object []comps = new Object[insiders.size()];
    for(int k=0; k<insiders.size(); k++)
    {
      Insider insider = insiders.get(k);
      if(insider.isFrame())
      {
        Jframe frame = new Jframe(insider.x, insider.y, insider.caption );
        frame.addlistener(this);
        comps[k] = frame;
        resultFrame = frame;
      }
      else if(insider.isDialog())
      {
        Jdialog dialog = new Jdialog(insider.x, insider.y , insider.caption , resultFrame);
        dialog.addlistener(this);
        comps[k] = dialog;
      }
      else
      {
        int l = Insider.parentIndex(k, insiders);
        Insider parent = insiders.get(l);  
        Object parentComp = comps[l];      
        if(insider.isMatrix())
        {
          for(int r=0; r<insider.rows; r++)
          for(int c=0; c<insider.cols; c++)
            ((JgridPanel)parentComp).add(insider.isResizeable, insider.isResizeable2, insider.rowstart + r * insider.rowstep, insider.colstart + c * insider.colstep, (Component)insider.newComponent());
          comps[k] = null;
        }
        else
        {
          Object comp = insider.newComponent();
          if(parent.isHP())
            ((JhorizontalPanel)parentComp).add(insider.isResizeable, (Component)comp);
          else if(parent.isVP())
            ((JverticalPanel)parentComp).add(insider.isResizeable, (Component)comp);
          else if(parent.isGP())
            ((JgridPanel)parentComp).add(insider.isResizeable, insider.isResizeable2, insider.rowstart, insider.colstart, (Component)comp);
          else if(parent.isCP())
            ((JTabbedPane)parentComp).add(insider.caption, (Component)comp);
          else if(parent.isMenu() && insider.isMenuItem() && insider.caption.equals("-"))
            ((JMenu)parentComp).addSeparator();
          else if(parent.isMenuBar())
            ((JMenuBar)parentComp).add((JMenu)comp);
          else if(parent.isMenu())
            ((JMenu)parentComp).add((JMenuItem)comp);
          else if(insider.isMenuBar())
            ((JFrame)parentComp).setJMenuBar((JMenuBar)comp);
          else if(parent.isFrame())
            ((JFrame)parentComp).add((Component)comp);
          else if(parent.isDialog())
            ((JDialog)parentComp).add((Component)comp);
          comps[k] = comp;
        }
      }
    }
    for(int k=0; k<insiders.size(); k++)
    {
      Insider insider = insiders.get(k);
      if(insider.isWindow())
      {
        Window win = (Window)comps[k]; 
        win.pack();
        win.setVisible(true);
      }
    }
  }

  void loadFromFile()
  {
    text.setText("");
    try
    {
      BufferedReader input = new BufferedReader(new FileReader(filename));
      for(;;)
      {
        String s = input.readLine();
        if(s == null || s.equals("end")) break;
        if(!s.equals("")) text.append(s + "\n");
      }
      input.close();
    }
    catch(Exception e)
    {
    }
    changed = false;
  }
  
  void saveToFile()
  {
    try
    {
      PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
      for(String line : Util.tokens(text.getText(), '\n'))
        if(!line.equals("")) output.println(line);
      //output.println("end");
      output.close();
    }
    catch(Exception e)
    {
    }
    changed = false;
  }
  
  Seq<Insider> parse()
  {
    Seq<Insider> result = new Seq<Insider>();
    Seq<String> lines = Util.tokens(text.getText(), '\n');
    for(int k=0; k<lines.size(); k++)
      if(!lines.get(k).equals(""))
      {
        Insider insider = Insider.fromString(lines.get(k));
        if(insider == null || !insider.check(result))
        {
          int pos = 0;
          for(int l=0; l<k; l++) pos += lines.get(l).length() + 1;
          text.select(pos, pos + lines.get(k).length());
          text.requestFocus();
          Jdialog.message("error at line: (" + (k+1) + ")", Jdialog.messageOK, frame);
          return null;
        }
        result.add(insider);
      }
    if(result.size() < 2)
    {
      Jdialog.message("App or Frame omitted", Jdialog.messageOK, frame);
      return null;
    }
    return result;
  }
}

class Insider
{
  public int level, x, y, typeIndex, rows, cols, rowstart, colstart, rowstep, colstep;
  public boolean isResizeable, isResizeable2;
  public String caption, name;

  private final static String  []typeName   = {"App" , "frame", "dialog", "horizontalPanel", "verticalPanel", "gridPanel", "TabbedPane", "MenuBar", "Menu", "CheckBoxMenuItem", "MenuItem", "Button", "CheckBox", "comboBox", "Label", "list", "tableInput", "TextArea", "TextField", "amaCanvas", "paintBox", "padding"};
  private final static boolean []hasCaption = {true  , true   , true    , false            , false          , false      , false       , false    , true  , true              , true      , true    , true      , true      , true   , false , true        , false     , true       , false      , false     , false    };
  private final static boolean []hasAction  = {false , false  , false   , false            , false          , false      , false       , false    , false , true              , true      , true    , true      , true      , false  , false , true        , false     , true       , false      , false     , false    };
  private final static boolean []hasItem    = {false , false  , false   , false            , false          , false      , false       , false    , false , false             , false     , false   , false     , false     , false  , false , false       , false     , false      , false      , false     , false    };
  private final static boolean []hasScroll  = {false , false  , false   , false            , false          , false      , false       , false    , false , false             , false     , false   , false     , false     , false  , true  , false       , true      , false      , false      , false     , false    };
  private final static int     []params     = {0     , 2      , 2       , 0                , 0              , 0          , 0           , 0        , 0     , 0                 , 0         , 0       , 0         , 0         , 0      , 1     , 2           , 2         , 1          , 2          , 2         , 2        };
  
  private final static int indexApp        = 0;
  private final static int indexFrame      = 1;
  private final static int indexDialog     = 2;
  private final static int indexHP         = 3;
  private final static int indexVP         = 4;
  private final static int indexGP         = 5;
  private final static int indexCP         = 6;
  private final static int indexMenuBar    = 7;
  private final static int indexMenu       = 8;
  private final static int indexChMenuItem = 9;
  private final static int indexMenuItem   = 10;
  
  public Insider(int level, boolean isResizeable, boolean isResizeable2, int typeIndex)
  {
    this.level = level;
    this.isResizeable = isResizeable;
    this.isResizeable2 = isResizeable2;
    this.typeIndex = typeIndex;
    x = 0;
    y = 0;
    rows = 0;
    cols = 0;
    rowstart = -1;
    colstart = -1;
    rowstep = 1;
    colstep = 1;
    caption = "";
    name = "-";
  }
  
  public boolean isApp()
  {
    return typeIndex == indexApp;
  }
  
  public boolean isFrame()
  {
    return typeIndex == indexFrame;
  }
  
  public boolean isDialog()
  {
    return typeIndex == indexDialog;
  }
  
  public boolean isMenuBar()
  {
    return typeIndex == indexMenuBar;
  }
  
  public boolean isMenu()
  {
    return typeIndex == indexMenu;
  }
  
  public boolean isHP()
  {
    return typeIndex == indexHP;
  }
  
  public boolean isVP()
  {
    return typeIndex == indexVP;
  }
  
  public boolean isGP()
  {
    return typeIndex == indexGP;
  }
  
  public boolean isCP()
  {
    return typeIndex == indexCP;
  }
  
  public boolean isPanel()
  {
    return typeIndex >= indexHP && typeIndex <= indexCP;
  }
  
  public boolean isWindow()
  {
    return typeIndex == indexFrame  || typeIndex == indexDialog;
  }
  
  public boolean isMenuElement()
  {
    return typeIndex >= indexMenuBar && typeIndex <= indexMenuItem;
  }
  
  public boolean isMenuItem()
  {
    return typeIndex == indexMenuItem || typeIndex == indexChMenuItem;
  }
  
  public boolean isContainer()
  {
    return typeIndex <= indexMenu;
  }
  
  public boolean isComponent()
  {
    return typeIndex > indexMenuItem;
  }
  
  public String typeName()
  {
    return "J" + typeName[typeIndex];
  }
  
  public boolean hasAction()
  {
    return !anonymous() && hasAction[typeIndex] && !isMatrix();
  }
  
  public boolean hasItem()
  {
    return !anonymous() && hasItem[typeIndex] && !isMatrix();
  }
  
  public boolean isMatrix()
  {
    return rows > 0 && cols > 0;
  }
  
  public boolean isGridElement()
  {
    return rowstart >= 0 && colstart >= 0;
  }
  
  public boolean anonymous()
  {
    return name.equals("-");
  }
  
  public static int parentIndex(int index, Seq<Insider> insiders)
  {
    Insider insider = insiders.get(index);
    for(int k=index-1; k>=0; k--)
    {
      Insider parent = insiders.get(k);
      if(insider.level > parent.level) return k;
    }
    return -1;
  }
  
  public static Insider fromString(String s)
  {
    try
    {
      char []cs = s.toCharArray();
      int level = 0;
      while(level < cs.length && cs[level] == ' ') level++;
      if(level >= cs.length) return null;
      boolean isResizeable = true;
      boolean isResizeable2 = true;
      if(cs[level] == '-' || cs[level] == '+')
      {
        isResizeable2 = isResizeable = cs[level] == '+';
        level++;
        if(cs[level] == '-' || cs[level] == '+')
        {
          isResizeable2 = cs[level] == '+';
          level++;
        }
      }
      Seq<String> ws = Util.words(new String(cs, level, cs.length - level));
      int wslen = ws.size();
      int next = 0;
      if(wslen <= next) return null;
      int typeIndex = -1;
      for(int k=0; k<typeName.length; k++)
        if(typeName[k].toLowerCase().equals(ws.get(next).toLowerCase())) typeIndex = k;
      if(typeIndex < 0) return null;
      Insider result = new Insider(level, isResizeable, isResizeable2, typeIndex);
      if(wslen >= 3 && ws.get(wslen-3).equals("@"))
      {
        result.rowstart = Integer.parseInt(ws.get(wslen-2));
        result.colstart = Integer.parseInt(ws.get(wslen-1));
        wslen -= 3;
      }
      next++;
      if(result.isPanel() && next < wslen)
      {
        result.caption = ws.get(next);
        next++;
      }
      if(hasCaption[typeIndex])
      {
        if(wslen <= next) return null;
        if(result.isApp())
        {
          result.name = ws.get(next);
          int pos = result.name.lastIndexOf(".");
          if(pos > 0 && pos < result.name.length()-1)
          {
            result.caption = result.name.substring(0, pos);
            result.name    = result.name.substring(pos+1);
          }
        }
        else
          result.caption = ws.get(next);
        next++;
      }
      if(params[typeIndex] >= 1)
      {
        if(wslen <= next) return null;
        result.x = Integer.parseInt(ws.get(next));
        next++;
      }
      if(params[typeIndex] >= 2)
      {
        if(wslen <= next) return null;
        result.y = Integer.parseInt(ws.get(next));
        next++;
      }
      if(wslen > next)
      {
        result.name = ws.get(next);
        next++;
      }
      if(result.isContainer() || result.isMenuElement()) return result;
      if(wslen > next)
      {
        result.rows = Integer.parseInt(ws.get(next));
        next++;
      }
      if(wslen > next)
      {
        result.cols = Integer.parseInt(ws.get(next));
        next++;
      }
      if(wslen > next)
      {
        result.rowstep = Integer.parseInt(ws.get(next));
        next++;
      }
      if(wslen > next)
      {
        result.colstep = Integer.parseInt(ws.get(next));
        next++;
      }
      return result;
    }
    catch(Exception e)
    {
      return null;
    }
  }
  
  public String defName(boolean matrix)
  {
    StringBuffer def = new StringBuffer();
    if(!anonymous())
    {
      def.append(name);
      if(matrix)
        def.append("[r][c]");
      def.append(" = ");
    }
    def.append("new ");
    def.append(typeName());
    def.append("(");
    if(hasCaption[typeIndex])
    {
      def.append("\"");
      def.append(caption);
      def.append("\"");
      if(params[typeIndex] > 0) def.append(", ");
    }
    if(params[typeIndex] > 0) def.append(x);
    if(params[typeIndex] > 1)
    {
      def.append(", ");
      def.append(y);
    }
    def.append(")");
    if(hasScroll[typeIndex])
      return "new JScrollPane(" + def.toString() + ")";
    else
      return def.toString();
  }

  public boolean check(Seq<Insider> insiders)
  {
    int size = insiders.size();
    if(!anonymous())
      for(Insider insider : insiders)
        if(insider.name.equals(name)) return false;
    if(isApp() && anonymous()) return false;
    if(isWindow() && anonymous()) return false;
    if(size == 0 && !isApp()) return false;
    if(size == 1 && !isFrame()) return false;
    if(isApp() && size != 0) return false;
    if(isFrame() && size != 1) return false;
    if(isMenuBar() && size != 2) return false;
    if(size == 0) return true;
    Insider parent;
    for(int k=size-1;;k--)
    {
      if(k < 0) return false;
      parent = insiders.get(k);
      if(level > parent.level) break;
    }
    if(isWindow() && !parent.isApp()) return false;
    if(isMenuBar() && !parent.isFrame()) return false;
    if(isMenu() && !parent.isMenu() && !parent.isMenuBar()) return false;
    if(isMenuItem() && !parent.isMenu()) return false;
    if(isPanel() && !parent.isPanel() && !parent.isWindow()) return false;
    if(isComponent() && !parent.isPanel() && !parent.isWindow()) return false;
    if(!isPanel() && parent.isCP()) return false;
    if(caption.equals("") && parent.isCP()) return false;
    if(isMatrix() && !parent.isGP()) return false;
    if(!isGridElement() && parent.isGP()) return false;
    if(parent.isWindow())
      for(int k=size-1;;k--)
      {
        if(k < 0) return false;
        Insider peer = insiders.get(k);
        if(level > peer.level) break;
        if(!peer.isMenuElement()) return false;
      }
    return true;
  }

  public Object newComponent()
  {
    switch(typeIndex)
    {
      case 3:
        return new JhorizontalPanel();
      case 4:
        return new JverticalPanel();
      case 5:
        return new JgridPanel();
      case 6:
        return new JTabbedPane();
      case 7:
        return new JMenuBar();
      case 8:
        return new JMenu(caption);
      case 9:
        return new JCheckBoxMenuItem(caption);
      case 10:
        return new JMenuItem(caption);
      case 11:
        return new JButton(caption);
      case 12:
        return new JCheckBox(caption);
      case 13:
        return new JcomboBox(caption);
      case 14:
        return new JLabel(caption);
      case 15:
        return new JScrollPane(new Jlist(x));
      case 16:
        return new JtableInput(caption, x, y);
      case 17:
        return new JScrollPane(new JTextArea(x, y));
      case 18:
        return new JTextField(caption, x);
      case 19:
        return new JamaCanvas(x, y);
      case 20:
        return new JpaintBox(x, y);
      default:
        return new Jpadding(x, y);

    }
  }
}
