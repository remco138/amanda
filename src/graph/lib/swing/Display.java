package lib.swing;

import java.awt.*;
import javax.swing.*;
import lib.util.*;

public class Display implements Jlistener, JpaintBoxListener, Runnable
{
  Jframe mainwindow;
  JpaintBox view;
  JLabel status;
  int width;
  int height;
  String title;
  Seq<Graphic> content;
  
  public static void display(int width, int height, String title, Seq<Graphic> content)
  {
    SwingUtilities.invokeLater(new Display(width, height, title, content));
  }
  
  private Display(int width, int height, String title, Seq<Graphic> content)
  {
    this.width   = width;
    this.height  = height;
    this.title   = title;
    this.content = content;
  }
  
  boolean isGraph = false;
  double x1, x2, y1, y2, dx, dy;    
  double epsilon = 0.00001;  
  double gx1 = -0.85;
  double gx2 = 0.65;
  double gy1 = -0.8;
  double gy2 = 0.85;
  
  public static void graph(int height, String title, Seq<Seq<Coord>> graphs)
  {
    SwingUtilities.invokeLater(new Display(height, title, graphs));
  }
  
  private Display(int height, String title, Seq<Seq<Coord>> graphs)
  {
    this.width   = (int)(height * (gy2 - gy1) / (gx2 - gx1));
    this.height  = height;
    this.title   = title;
    this.content = new Seq<Graphic>();
    
    x1 = 100000;
    x2 = -100000;
    y1 = 100000;
    y2 = -100000;
    int count = 0;
    for(Seq<Coord> ps : graphs) 
    for(Coord p : ps)
    {
      count++;
      if(p.x < x1) x1 = p.x;
      if(p.x > x2) x2 = p.x;
      if(p.y < y1) y1 = p.y;
      if(p.y > y2) y2 = p.y;
    }
    if(count == 0) return;
    
    isGraph = true;
    
    if(x2-x1 < epsilon) x2 = x1 + epsilon;
    dx = (gx2-gx1) / (x2-x1);
    if(y2-y1 < epsilon) y2 = y1 + epsilon;
    dy = (gy2-gy1) / (y2-y1);
    
    Color lineColor = Color.red;
    Color textColor = Color.black;
    Color gridColor = Color.lightGray;
    
    this.content.add(Graphic.rectangle(Color.white, new Coord(-1, -1), new Coord(1, 1)));
    this.content.add(Graphic.text(textColor, new Coord(gx1-0.05, gy1-0.1), String.valueOf(x1)));
    this.content.add(Graphic.text(textColor, new Coord(gx2-0.05, gy1-0.1), String.valueOf(x2)));
    this.content.add(Graphic.text(textColor, new Coord(gx2+0.05, gy1    ), String.valueOf(y1)));
    this.content.add(Graphic.text(textColor, new Coord(gx2+0.05, gy2    ), String.valueOf(y2)));
    for(int k=0; k<10; k++)
    {
      this.content.add(line(gridColor, gx1 + (gx2-gx1) * k / 9, gy1, gx1 + (gx2-gx1) * k / 9, gy2));
      this.content.add(line(gridColor, gx1, gy1 + (gy2-gy1) * k / 9, gx2, gy1 + (gy2-gy1) * k / 9));
    }
    for(Seq<Coord> ps : graphs)
    {
      Seq<Coord> scaled = new Seq<Coord>();
      for(Coord p : ps) scaled.add(scale(p));
      this.content.add(Graphic.polyline(lineColor, scaled));
    }
  }

  public void run()
  {
    mainwindow = new Jframe(0, 0, title);
    mainwindow.addlistener(this);
    JverticalPanel elem_1;
    mainwindow.add(elem_1 = new JverticalPanel());
    elem_1.add(true, view = new JpaintBox(width, height));
    if(isGraph) elem_1.add(false, status = new JLabel("No selection"));
    mainwindow.pack();
    mainwindow.setVisible(true);
    view.addPaintBoxListener(this);
  }

  Coord scale(Coord p)
  {
    return new Coord(gx1 + (p.x-x1) * dx, gy1 + (p.y-y1) * dy);
  }
    
  Coord unscale(Coord p)
  {
    return new Coord(x1 + (p.x-gx1) / dx, y1 + (p.y-gy1) / dy);
  }
    
  Graphic line(Color c, double x1, double y1, double x2, double y2)
  {
    return Graphic.polyline(c, new Seq<Coord>(new Coord(x1, y1), new Coord(x2, y2)));
  }
  
  public void onPaint(JpaintBox p)
  {
    p.display(content);
  }
  
  public void onKeyPressed(JpaintBox p, char key)
  {
  }
  
  public void onMouseDown(JpaintBox p, double x1, double y1, double x2, double y2)
  {
  }
  
  public void onMouseDrag(JpaintBox p, double x1, double y1, double x2, double y2)
  {
    if(!isGraph) return;
    Color color = Color.magenta.brighter().brighter();
    p.display(new Seq<Graphic>(line(color, -1, y2, 1, y2), line(color, x2, -1, x2, 1)));
    status.setText("" + unscale(new Coord(x2, y2)));
  }
  
  public void onClose(Window win)
  {
    mainwindow.setVisible(false);
    mainwindow.dispose();
  }

  public void onClick(Object comp)
  {
  }
}
