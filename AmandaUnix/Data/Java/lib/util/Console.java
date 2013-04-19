package lib.util;

import java.io.*;

public class Console
{
  private BufferedReader in;

  public Console()
  {
    in = new BufferedReader(new InputStreamReader(System.in));
  }

  public final String readln()
  {
    try { return in.readLine(); } catch(Exception e) { return ""; }
  }

  public final int readInt()
  {
    try { return Integer.parseInt(readln()); } catch(Exception e) { return 0; }
  }

  public final void write(String s)
  {
    System.out.print(s); System.out.flush();
  }

  public final void writeln(String s)
  {
    System.out.println(s);
  }

  public final long time()
  {
    return System.currentTimeMillis();
  }
}


