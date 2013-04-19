package lib.swing;

import java.awt.*;
import java.awt.event.*;

public interface JpaintBoxListener
{
  public void onPaint(JpaintBox p);
  public void onKeyPressed(JpaintBox p, char key);
  public void onMouseDown(JpaintBox p, double x1, double y1, double x2, double y2);
  public void onMouseDrag(JpaintBox p, double x1, double y1, double x2, double y2);
  // coordinates: (-1, -1) .. (1, 1)
  // (x1, y1) is the coordinate where dragging started
  // (x2, y2) is the current coordinate
  // MouseDown indicates the end of a dragging sequence
  // every MouseDrag is called twice to facilitate temporary drawing in XOR mode
}

