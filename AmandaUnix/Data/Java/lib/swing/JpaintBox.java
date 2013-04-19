package lib.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import lib.util.*;

public class JpaintBox extends JPanel implements KeyListener, MouseListener, MouseMotionListener
{
  private int mouseX1, mouseY1, mouseX2, mouseY2;
  private JpaintBoxListener listener;
  private Rectangle bounds;
  
  public Graphics graphics;
    
  public JpaintBox(int w, int h)
  {
    setPreferredSize(new Dimension(w, h));
    graphics = null;
    listener = null;
    setFocusable(true);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  public void addPaintBoxListener(JpaintBoxListener listener)
  {
    this.listener = listener;
  }
  
  public void display(Seq<Graphic> gs)
  {
    if(graphics == null) return;
    bounds = getBounds();
    for(Graphic g : gs)
    {
      graphics.setColor(g.color);
      switch(g.tag)
      {
        case text:
        {
          Coord p = g.coords.get(0);
          int x = toX(p.x);
          int y = toY(p.y);
          graphics.drawString(g.text, x, y);
          break;
        }
        case polyline:
        case polygon:
        {
          int count = g.coords.size();
          int[] xs = new int[count];
          int[] ys = new int[count];
          for(int k=0; k<count; k++)
          {
            Coord p = g.coords.get(k);
            xs[k] = toX(p.x);
            ys[k] = toY(p.y);
          }
          switch(g.tag)
          {
            case polyline: graphics.drawPolyline(xs, ys, count); break;
            case polygon:  graphics.fillPolygon(xs, ys, count);  break;
          }
          break;
        }
        case rectangle:
        case ellipse:
        case disc:
        {
          Coord p1 = g.coords.get(0);
          int x1 = toX(p1.x);
          int y1 = toY(p1.y);
          Coord p2 = g.coords.get(1);
          int x2 = toX(p2.x);
          int y2 = toY(p2.y);
          if(x1 > x2) { int h = x1; x1 = x2; x2 = h; }
          if(y1 > y2) { int h = y1; y1 = y2; y2 = h; }
          switch(g.tag)
          {
            case rectangle: graphics.fillRect(x1, y1, x2-x1, y2-y1); break;
            case ellipse:   graphics.drawOval(x1, y1, x2-x1, y2-y1); break;
            case disc:      graphics.fillOval(x1, y1, x2-x1, y2-y1); break;
          }
          break;
        }
        default:
          break;
      }
    }
  }
  
  private int toX(double d)
  {
    return (int)((double)bounds.width * 0.5 * (1.0 + d));
  }

  private int toY(double d)
  {
    return (int)((double)bounds.height * 0.5 * (1.0 - d));
  }

  private double fromX(int x)
  {
    return (double)(2 * x) / (double)bounds.width - 1.0;
  }

  private double fromY(int y)
  {
    return 1.0 - (double)(2 * y) / (double)bounds.height;
  }
  
  public final void keyTyped(KeyEvent e)
  {
    if(listener != null) listener.onKeyPressed(this, e.getKeyChar());
  }
  
  public final void keyPressed(KeyEvent e)
  {
  }
  
  public final void keyReleased(KeyEvent e)
  {
  }
  
  public final void paint(Graphics g)
  {
    graphics = g;
    if(listener != null) listener.onPaint(this);
    graphics = null;
  }
  
  public final void mouseDragged(MouseEvent e)
  {
    doMouseDrag();
    mouseX2 = e.getX();
    mouseY2 = e.getY();
    doMouseDrag();
  }
  
  public final void mouseMoved(MouseEvent e)
  {
  }
  
  public final void mouseClicked(MouseEvent e)
  {
    requestFocusInWindow();
  }
  
  public final void mouseEntered(MouseEvent e)
  {
  }
  
  public final void mouseExited(MouseEvent e)
  {
  }
  
  public final void mousePressed(MouseEvent e)
  {
    mouseX1 = e.getX();
    mouseY1 = e.getY();
    mouseX2 = mouseX1;
    mouseY2 = mouseY1;
    doMouseDrag();
  }
  
  public final void mouseReleased(MouseEvent e)
  {
    doMouseDrag();
    doMouseDown();
  }

  private void doMouseDown()
  {
    if(listener == null) return;
    bounds = getBounds();
    double x1 = fromX(mouseX1);
    double y1 = fromY(mouseY1);
    double x2 = fromX(mouseX2);
    double y2 = fromY(mouseY2);
    graphics = getGraphics();
    listener.onMouseDown(this, x1, y1, x2, y2);
    graphics = null;
  }
  
  private void doMouseDrag()
  {
    if(listener == null) return;
    bounds = getBounds();
    double x1 = fromX(mouseX1);
    double y1 = fromY(mouseY1);
    double x2 = fromX(mouseX2);
    double y2 = fromY(mouseY2);
    graphics = getGraphics();
    graphics.setXORMode(Color.black);
    listener.onMouseDrag(this, x1, y1, x2, y2);
    graphics.setPaintMode();
    graphics = null;
  }
}
