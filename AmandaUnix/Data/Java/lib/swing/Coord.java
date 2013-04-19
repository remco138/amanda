package lib.swing;

import java.awt.*;
import java.util.*;

public class Coord
{
  public double x;
  public double y;
  
  public Coord(double x, double y)
  {
    this.x = x;
    this.y = y;
  }
  
  public String toString()
  {
    return "Coord(" + x + ", " + y + ")";
  }
}
