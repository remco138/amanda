package lib.util;

import java.util.*;
import java.io.*;

public class AmaObj
{
  private BufferedReader in;
  private PrintWriter out;
  private Process proc = null;
  private boolean ok;

  public AmaObj(String command, String objectName)
  {
    try
    {
      ok = false;
      proc = Runtime.getRuntime().exec(command);
      in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      out = new PrintWriter(proc.getOutputStream(), true);
      out.println("object " + objectName);
      ok = check();
    }
    catch(IOException e)
    {
      System.out.println("error: " + e.getMessage());
    }
  }

  public AmaObj(String command)
  {
    try
    {
      ok = false;
      proc = Runtime.getRuntime().exec(command);
      in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      out = new PrintWriter(proc.getOutputStream(), true);
      ok = true;
    }
    catch(IOException e)
    {
      System.out.println("error: " + e.getMessage());
    }
  }

  public boolean isOk()
  {
    return ok;
  }

  public void close()
  {
    if(proc != null) proc.destroy();
  }

  public boolean check()
  {
    out.println("call __check__");
    for(;;)
    {
      String s = readIn();
      if(s.length() > 1 && !s.startsWith(">") && !s.startsWith("Amanda")) return false;
      if(s == null || s.equals(">")) return true;
    }
  }

  public void call(String command)
  {
    out.println("call " + command);
    for(;;)
    {
      String s = readIn();
      if(s == null || s.equals("<")) return;
    }
  }

  public void execute(String command)
  {
    out.println(command);
    for(;;)
    {
      String s = readIn();
      if(s == null || s.equals("<")) return;
    }
  }

  public String getResult()
  {
    String s = readIn();
    if(s == null || s.equals(">")) return null;
    return s;
  }

  public Seq<String> exec(String command)
  {
    call(command);
    Seq<String> v = new Seq<String>();
    for(;;)
    {
      String s = getResult();
      if(s == null) break;
      v.add(s);
    }
    return v;
  }

  private String readIn()
  {
    try
    {
      return in.readLine();
    }
    catch(IOException e)
    {
      System.out.println("error: " + e.getMessage());
    }
    return null;
  }
}

