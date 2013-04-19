package lib.util;

import java.io.*;
import java.util.*;

public class Util
{
  public static String justify(String s, int n)
  {
    while(s.length() < n) s = s + " ";
    return s + " ";
  }

  public static Seq<String> words(String str)
  {
    final int stNone   = 0;
    final int stWord   = 1;
    final int stString = 2;
    Seq<String> ws = new Seq<String>();
    int state = stNone;
    String s = "";
    for(char c : str.toCharArray())
      if(state == stNone && c == ' ')
        ;
      else if(state == stNone && c == '\"')
        state = stString;
      else if(state == stNone)
      {
        state = stWord;
        s = s + c;
      }
      else if(state == stWord && c == ' ')
      {
        state = stNone;
        ws.add(s);
        s = "";
      }
      else if(state == stWord && c == '\"')
      {
        state = stString;
        ws.add(s);
        s = "";
      }
      else if(state == stString && c == '\"')
      {
        state = stNone;
        ws.add(s);
        s = "";
      }
      else
        s = s + c;
    if(state == stWord) ws.add(s);
    return ws;
  }

  public static String unwords(Seq<String> ws)
  {
    StringBuffer result = new StringBuffer();
    for(String w : ws)
      result.append("\"" + w + "\"");
    return result.toString();
  }
  
  public static Seq<String> tokens(String str, char sep)
  {
    Seq<String> ws = new Seq<String>();
    String s = "";
    for(char c : str.toCharArray())
      if(c == sep)
      {
        ws.add(s);
        s = "";
      }
      else
        s = s + c;
    if(s.length() > 0) ws.add(s);
    return ws;
  }
  
  public static Seq<String> recsplit(String str, String[] lparens, String[] rparens, String[] seps)
  {
    Seq<String> ws = new Seq<String>();
    int level = 0;
    String w = "";
    for(int k=0; k<str.length(); k++)
    {
      boolean found = false;
      for(String s : lparens)
      if(str.startsWith(s, k))
      {
        level++;
        w += s;
        k += s.length() - 1;
        found = true;
        break;
      }
      if(found) continue;
      for(String s : rparens)
      if(str.startsWith(s, k))
      {
        level--;
        w += s;
        k += s.length() - 1;
        found = true;
        break;
      }
      if(found) continue;
      if(level == 0)
      for(String s : seps)
      if(str.startsWith(s, k))
      {
        ws.add(w.trim());
        ws.add(s);
        w = "";
        k += s.length() - 1;
        found = true;
        break;
      }
      if(found) continue;
      w += str.charAt(k);
    }
    ws.add(w.trim());
    return ws;
  }
  
  public static Seq<String> readLines(String filename)
  {
    try
    {
      Seq<String> lines = new Seq<String>();
      lines.clear();
      BufferedReader input = new BufferedReader(new FileReader(filename));
      for(;;)
      {
        String line = input.readLine();
        if(line == null) break;
        lines.add(line);
      }
      input.close();
      return lines;
    }
    catch(Exception e)
    {
      return null;
    }
  }
  
  public static boolean writeLines(Seq<String> lines, String filename)
  {
    try
    {
      PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
      for(String line : lines)
        output.println(line);
      output.close();
      return true;
    }
    catch(Exception e)
    {
      return false;
    }      
  }
  
  public static int count(String s, char c)
  {
    int n = 0;
    for(int k=0; k<s.length(); k++)
    if(s.charAt(k) == c)
      n++;
    return n;
  }
  
  public static String repChar(char c, int n)
  {
    StringBuffer sb = new StringBuffer();
    for(int k=0; k<n; k++) sb.append(c);
    return sb.toString();
  }
}
