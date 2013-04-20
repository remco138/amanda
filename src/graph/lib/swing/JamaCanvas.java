/*
  Dick Bruin, 21/05/2000, 08/06/2012

  AmaCancas is subclass of Canvas
  which has a AmaObj as delegate
  it handles repainting and mousedragging
  it calls the following methods of the AmaObject which should return a list of graphics:
  Repaint, MouseDown, MouseDrag

  For drawing it uses the following graphics protocol in Amanda:
  graphics ::= GraphText num (num, num) [char]
             | GraphPolyLine num [(num, num)]
             | GraphPolygon num [(num, num)]
             | GraphRectangle num (num, num) (num, num)
             | GraphEllipse num (num, num) (num, num)
             | GraphDisc num (num, num) (num, num)

  graphicsout:: [graphics] -> [[char]]

  graphicsout = concat.map graphicsToString

  graphicsToString (GraphText color (x, y) text) = ["GraphText", itoa color] ++ coordsToString (x, y) ++ [text]
  graphicsToString (GraphPolyLine color ps) = ["GraphPolyLine", itoa color] ++ concat[coordsToString (x, y) | (x, y) <- ps] ++ ["End"]
  graphicsToString (GraphPolygon color ps) = ["GraphPolygon", itoa color] ++ concat[coordsToString (x, y) | (x, y) <- ps] ++ ["End"]
  graphicsToString (GraphRectangle color (x1, y1) (x2, y2)) = ["GraphRectangle", itoa color] ++ coordsToString (x1, y1) ++ coordsToString (x2, y2)
  graphicsToString (GraphEllipse color (x1, y1) (x2, y2)) = ["GraphEllipse", itoa color] ++ coordsToString (x1, y1) ++ coordsToString (x2, y2)
  graphicsToString (GraphDisc color (x1, y1) (x2, y2)) = ["GraphDisc", itoa color] ++ coordsToString (x1, y1) ++ coordsToString (x2, y2)

  coordsToString (x, y) = map ftoa [x, y]

*/

package lib.swing;

import java.awt.*;
import java.util.*;
import lib.util.*;

public class JamaCanvas extends JpaintBox implements JpaintBoxListener
{
  private AmaObj obj;
  private Color[] colorTable;
  private boolean directDrawing;
  
  public JamaCanvas(int w, int h)
  {
    super(w, h);
    obj = null;
    directDrawing = false;
    colorTable = new Color[16];
    colorTable[ 0] = Color.black;
    colorTable[ 1] = Color.blue.darker();
    colorTable[ 2] = Color.green.darker();
    colorTable[ 3] = Color.cyan.darker();
    colorTable[ 4] = Color.red.darker();
    colorTable[ 5] = Color.pink.darker();
    colorTable[ 6] = Color.magenta;
    colorTable[ 7] = Color.lightGray;
    colorTable[ 8] = Color.darkGray;
    colorTable[ 9] = Color.blue;
    colorTable[10] = Color.green;
    colorTable[11] = Color.cyan;
    colorTable[12] = Color.red;
    colorTable[13] = Color.pink;
    colorTable[14] = Color.yellow;
    colorTable[15] = Color.white;
    addPaintBoxListener(this);
  }
  
  public void setAmaObj(AmaObj obj)
  {
    this.obj = obj;
  }
  
  public void setDirectDrawing(boolean directDrawing)
  {
    this.directDrawing = directDrawing;
  }
  
  public void exec(String command)
  {
    if(obj == null) return;
    obj.call(command);
    paintResult();
  }
  
  private Color toColor(String s)
  {
    int n = toInt(s);
    if(n < 0 || n > 15) n = 0;
    return colorTable[n];
  }
  
  private int toInt(String s)
  {
    try
    {
      return Integer.parseInt(s);
    }
    catch(Exception e)
    {
      return 0;
    }
  }

  private double toDouble(String s)
  {
    try
    {
      return new Double(s).doubleValue();
    }
    catch(Exception e)
    {
      return 0.0;
    }
  }
  
  private void paintResult()
  {
    if(graphics == null && !directDrawing)
    {
      while(obj.getResult() != null)
        ;
      repaint();
      return;
    }
    Seq<Graphic> gs = new Seq<Graphic>();
    for(;;)
    {
      String command = obj.getResult();
      if(command == null)
        break;
      else if(command.equals("GraphText"))
      {
        Color c  = toColor(obj.getResult());
        double x = toDouble(obj.getResult());
        double y = toDouble(obj.getResult());
        String text = obj.getResult();
        gs.add(Graphic.text(c, new Coord(x, y), text));
      }
      else if(command.equals("GraphPolyLine"))
      {
        Color c = toColor(obj.getResult());
        Seq<Coord> coords = new Seq<Coord>();
        for(;;)
        {
          String s = obj.getResult();
          if(s.equals("End")) break;
          double x = toDouble(s);
          double y = toDouble(obj.getResult());
          coords.add(new Coord(x, y));
        }
        gs.add(Graphic.polyline(c, coords));
      }
      else if(command.equals("GraphPolygon"))
      {
        Color c = toColor(obj.getResult());
        Seq<Coord> coords = new Seq<Coord>();
        for(;;)
        {
          String s = obj.getResult();
          if(s.equals("End")) break;
          double x = toDouble(s);
          double y = toDouble(obj.getResult());
          coords.add(new Coord(x, y));
        }
        gs.add(Graphic.polygon(c, coords));
      }
      else if(command.equals("GraphRectangle"))
      {
        Color c   = toColor(obj.getResult());
        double x1 = toDouble(obj.getResult());
        double y1 = toDouble(obj.getResult());
        double x2 = toDouble(obj.getResult());
        double y2 = toDouble(obj.getResult());
        gs.add(Graphic.rectangle(c, new Coord(x1, y1), new Coord(x2, y2)));
      }
      else if(command.equals("GraphEllipse"))
      {
        Color c   = toColor(obj.getResult());
        double x1 = toDouble(obj.getResult());
        double y1 = toDouble(obj.getResult());
        double x2 = toDouble(obj.getResult());
        double y2 = toDouble(obj.getResult());
        gs.add(Graphic.ellipse(c, new Coord(x1, y1), new Coord(x2, y2)));
      }
      else if(command.equals("GraphDisc"))
      {
        Color c   = toColor(obj.getResult());
        double x1 = toDouble(obj.getResult());
        double y1 = toDouble(obj.getResult());
        double x2 = toDouble(obj.getResult());
        double y2 = toDouble(obj.getResult());
        gs.add(Graphic.disc(c, new Coord(x1, y1), new Coord(x2, y2)));
      }
    }
    boolean newGraphics = graphics == null;
    if(newGraphics) graphics = getGraphics();
    display(gs);
    if(newGraphics) graphics = null;
  }

  public void onPaint(JpaintBox p)
  {
    exec("Repaint");
  }
  
  public void onKeyPressed(JpaintBox p, char key)
  {
    exec("Key " + key);
  }
  
  public void onMouseDown(JpaintBox p, double x1, double y1, double x2, double y2)
  {
    exec("MouseDown " + x1 + " " + y1 + " " + x2 + " " + y2);
  }
  
  public void onMouseDrag(JpaintBox p, double x1, double y1, double x2, double y2)
  {
    exec("MouseDrag " + x1 + " " + y1 + " " + x2 + " " + y2);
  }
}
