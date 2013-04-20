package lib.swing;

import java.awt.*;
import java.util.*;
import lib.util.*;

public class Graphic
{
  public static enum Tag { text, polyline, polygon, rectangle, ellipse, disc }

  public Tag tag;
  public Color color;
  public String text;
  public Seq<Coord> coords;
  
  public static Graphic text(Color c, Coord p, String text)
  {
    Graphic g = new Graphic();
    g.tag = Tag.text;
    g.color = c;
    g.coords = new Seq<Coord>();
    g.coords.add(p);
    g.text = text;
    return g;
  }
  
  public static Graphic polyline(Color c, Seq<Coord> coords)
  {
    Graphic g = new Graphic();
    g.tag = Tag.polyline;
    g.color = c;
    g.coords = coords;
    return g;
  }
  
  public static Graphic polygon(Color c, Seq<Coord> coords)
  {
    Graphic g = new Graphic();
    g.tag = Tag.polygon;
    g.color = c;
    g.coords = coords;
    return g;
  }
  
  public static Graphic rectangle(Color c, Coord p1, Coord p2)
  {
    Graphic g = new Graphic();
    g.tag = Tag.rectangle;
    g.color = c;
    g.coords = new Seq<Coord>();
    g.coords.add(p1);
    g.coords.add(p2);
    return g;
  }
  
  public static Graphic ellipse(Color c, Coord p1, Coord p2)
  {
    Graphic g = new Graphic();
    g.tag = Tag.ellipse;
    g.color = c;
    g.coords = new Seq<Coord>();
    g.coords.add(p1);
    g.coords.add(p2);
    return g;
  }
  
  public static Graphic disc(Color c, Coord p1, Coord p2)
  {
    Graphic g = new Graphic();
    g.tag = Tag.disc;
    g.color = c;
    g.coords = new Seq<Coord>();
    g.coords.add(p1);
    g.coords.add(p2);
    return g;
  }
  
  public String toString()
  {
    return "Graphic." + tag + "(" + color + ", " + coords + (tag == Tag.text ? ", \"" + text + "\"": "") + ")";
  }
}
