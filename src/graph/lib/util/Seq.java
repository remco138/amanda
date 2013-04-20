package lib.util;

import java.util.*;

public class Seq<T> extends ArrayList<T>
{
  public Seq()
  {
  }
  
  public Seq(T ... ts)
  {
    for(T t : ts)
      add(t);
  }
  
  public String toString()
  {
    return "Seq" + super.toString();
  }
}


