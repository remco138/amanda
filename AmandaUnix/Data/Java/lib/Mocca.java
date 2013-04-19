package lib;

import java.util.*;
import lib.util.*;

public class Mocca
{
  public static void main(String[] args)
  {
    new Mocca(args);
  }
  
  public Mocca(String[] args)
  {
    error = new Error();
    for(String inputfilename : args)
    {
      error.setFilename(inputfilename);
      if(error.check(inputfilename.endsWith(".mocca"), "wrong file extension"))
      {
        Seq<String> inputlines = Util.readLines(inputfilename);
        if(!error.check(inputlines != null, "could not open file")) continue;
        processMacros(inputlines);
        Seq<String> outputlines = new Seq<String>();
        while(process(inputlines, outputlines))
        {
          inputlines = outputlines;
          outputlines = new Seq<String>();
        }
        String outputfilename = inputfilename.substring(0, inputfilename.length()-6) + ".java";
        error.check(Util.writeLines(outputlines, outputfilename), "could not write output");
      }
    }
  }
  
  class Error
  {
    private String filename;
    private int linenr;
    
    public Error()
    {
      filename = null;
      linenr = -1;
    }
    
    public void setFilename(String filename)
    {
      this.filename = filename;
      this.linenr = -1;
    }
    
    public void setLinenr(int linenr)
    {
      this.linenr = linenr + 1;
    }
    
    public boolean check(boolean test, String message)
    {
      if(!test)
      {
        if(filename != null) System.out.println("Error in file: " + filename);
        if(linenr >= 0)      System.out.println("Error on line: " + linenr);
        System.out.println("  " + message);
        System.out.println();
      }
      return test;
    }
  } 
  
  Error error; 
  
  boolean process(Seq<String> input, Seq<String> output)
  {
    boolean result = false;
    for(int k=0; k<input.size(); k++)
    {
      Seq<String> selection = new Seq<String>();
      String line = input.get(k);
      error.setLinenr(k);
      Seq<String> idents = new Seq<String>();
      identifiers(line, idents, "=/", typeAlfabet);      
      if((idents.size() >= 2 && idents.get(0).equals("struct")) || (idents.size() >= 3 && idents.get(1).equals("struct")))
      {
        result = true;
        selection.add(line);
        comment(selection, output);
        processStruct(selection, output);
      }
      else if((idents.size() >= 2 && idents.get(0).equals("union")) || (idents.size() >= 3 && idents.get(1).equals("union")))
      {
        result = true;
        do 
        {
          selection.add(line = input.get(k++));
        } 
        while(k<input.size() && !line.contains("}"));
        k--;
        comment(selection, output);
        processUnion(selection, output);
      }
      else if(idents.size() >= 5 && idents.get(0).equals("var") && idents.get(2).equals("=") && idents.get(3).equals("new"))
      {
        result = true;
        selection.add(line);
        comment(selection, output);
        processVar(selection, output);
      }
      else if(idents.size() >= 4 && idents.get(0).equals("var") && idents.get(2).equals("=") && idents.get(3).endsWith(">")
           || idents.size() >= 3 && idents.get(1).equals("=") && idents.get(2).endsWith(">")
           || idents.size() >= 2 && idents.get(0).equals("return") && idents.get(1).endsWith(">") )
      {
        result = true;
        int level = 0;
        do
        {
          selection.add(line = input.get(k++));
          level += Util.count(line, '[') - Util.count(line, ']');
        }
        while(k<input.size() && !(level<=0 && line.contains("]")));
        k--;
        comment(selection, output);
        processComprehension(selection, output);
      }
      else if(idents.size() >= 2 && idents.get(0).equals("call"))
      {
        result = true;
        selection.add(line);
        comment(selection, output);
        processCall(selection, output);
      }
      else if(idents.size() >= 3 && idents.get(0).equals("proc"))
      {
        result = true;
        if(line.contains(";"))
        {
          selection.add(line);
          comment(selection, output);
          processProcdef(selection, output);
        }
        else
        {
          Seq<String> block1 = new Seq<String>();
          int level = 0;
          do
          {
            block1.add(line = input.get(k++));
            level += Util.count(line, '{') - Util.count(line, '}');
          }
          while(k<input.size() && !(level<=0 && line.contains("}")));
          int l = k;
          if(k<input.size())
          do
          {
            line = input.get(k++);
            level += Util.count(line, '{') - Util.count(line, '}');
          }
          while(k<input.size() && !line.contains("where") && !line.contains(";") && !line.contains("(") && !line.contains(")") && !line.contains("{") && !line.contains("}"));
          Seq<String> block2 = new Seq<String>();
          if(!line.contains("where"))
            k = l;
          else
          {
            block2.add(line);
            do
            {
              block2.add(line = input.get(k++));
              level += Util.count(line, '{') - Util.count(line, '}');
            }
            while(k<input.size() && !(level<=0 && line.contains("}")));
          }
          k--;
          processProc(block1, block2, output);
        }
      }
      else
        output.add(line);
    }
    return result;
  }
  
  void comment(Seq<String> input, Seq<String> output)
  {
    for(String line : input)
      output.add("// mocca: " + line);
  }
  
  String fieldAlfabet = "abcdefghijklmnopqrstuvwxyz0123456789_<>[]";
  String typeAlfabet  = "abcdefghijklmnopqrstuvwxyz0123456789_<>";
  String wordAlfabet  = "abcdefghijklmnopqrstuvwxyz0123456789_";
  
  int identifiers(String s, Seq<String> idents, String seps, String alfabet)
  {
    idents.clear();
    char[] cs = s.toLowerCase().toCharArray();
    int result = 0;
    for(int k=0; k<cs.length; k++)
      if(seps.contains(String.valueOf(cs[k])))
      {
        if(idents.size() == 0) result = k;
        idents.add(s.substring(k, k+1));
      }
      else if(alfabet.contains(String.valueOf(cs[k])))
      {
        if(idents.size() == 0) result = k;
        int l = k+1;
        while(l<cs.length && alfabet.contains(String.valueOf(cs[l]))) l++;
        idents.add(s.substring(k, l));
        k = l-1;
      }
    return result;
  }
  
  class Struct
  {
    public String structname;
    public Seq<String> typenames;
    public Seq<String> fieldnames;
    public int fieldcount;
    
    public Struct(Seq<String> idents)
    {
      structname = idents.get(0);
      typenames  = new Seq<String>();
      fieldnames = new Seq<String>();
      for(int k=1; k<idents.size(); k+=2)
      {
        typenames.add(idents.get(k));
        fieldnames.add(idents.get(k+1));
      }
      fieldcount = fieldnames.size();
    }
    
    public String paramlist()
    {
      String result = "";
      for(int k=0; k<fieldcount; k++)
      {
        if(k > 0) result += ", ";
        result += typenames.get(k) + " " + fieldnames.get(k);
      }
      return result;
    }
        
    public String display()
    {
      String result = "";
      for(int k=0; k<fieldcount; k++)
      {
        if(k > 0) result += "\", \" + ";
        result += fieldnames.get(k) + " + ";
      }
      return result;
    }

    public String equality()
    {
      String result = "";
      for(int k=0; k<fieldcount; k++)
      {
        String typename  = typenames.get(k); 
        String fieldname = fieldnames.get(k); 
        if(k > 0) result += " && ";
        if(typename.endsWith("[]"))
          result += "Arrays.equals(other." + fieldname + ", " + fieldname + ")";
        else if(isBasicType(typename))
          result += "other." + fieldname + " == " + fieldname;
        else
          result += "other." + fieldname + ".equals(" + fieldname + ")";
      }
      if(result.length() == 0) result = "true";
      return result;
    }
  }
  
  void processStruct(Seq<String> input, Seq<String> output)
  {
    String definition = input.get(0);
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(definition, idents, "", fieldAlfabet);
    String prefix = definition.substring(0, offset);
    String heading = "class ";
    
    if(!idents.get(0).equals("struct"))
    {  
      heading = idents.get(0) + " " + heading;
      idents.remove(0);
    }
    idents.remove(0);
    
    if(!error.check(idents.size() % 2 == 1, "wrong number of parameters")) return;
    
    Struct struct = new Struct(idents);
    
    output.add(prefix + heading + struct.structname);
    output.add(prefix + "{");
    for(int k=0; k<struct.fieldcount; k++)
      output.add(prefix + "  public " + struct.typenames.get(k) + " " + struct.fieldnames.get(k) + ";");
    output.add(prefix);
    output.add(prefix + "  public " + struct.structname + "(" + struct.paramlist() + ")");
    output.add(prefix + "  {");
    for(int k=0; k<struct.fieldcount; k++)
      output.add(prefix + "    this." + struct.fieldnames.get(k) + " = " + struct.fieldnames.get(k) + ";");
    output.add(prefix + "  }");
    output.add(prefix);
    output.add(prefix + "  public boolean equals(Object obj)");
    output.add(prefix + "  {");
    output.add(prefix + "    if(!(obj instanceof " + struct.structname + ")) return false;");
    output.add(prefix + "    " + struct.structname + " other = (" + struct.structname + ")obj;");
    output.add(prefix + "    return " + struct.equality() + ";");
    output.add(prefix + "  }");
    output.add(prefix);
    output.add(prefix + "  public String toString()");
    output.add(prefix + "  {");
    output.add(prefix + "    return \"" + struct.structname + "(\" + " + struct.display() + "\")\";");
    output.add(prefix + "  }");
    output.add(prefix + "}");
  }
  
  void processUnion(Seq<String> input, Seq<String> output)
  {
    String definition = input.get(0);
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(definition, idents, "", fieldAlfabet);
    String prefix = definition.substring(0, offset);
    String heading = "class ";
    
    if(!idents.get(0).equals("union"))
    {  
      heading = idents.get(0) + " " + heading;
      idents.remove(0);
    }
    
    if(!error.check(idents.size() == 2, "wrong number of parameters")) return;
    
    String unionname = idents.get(1);
    
    Seq<Struct> structs = new Seq<Struct>();
    for(int k=1; k<input.size(); k++)
    {
      Seq<String> ids = new Seq<String>();
      identifiers(input.get(k), ids, "", fieldAlfabet);
      if(ids.size() % 2 == 1) 
        structs.add(new Struct(ids));
      else 
        error.check(ids.size() == 0, "skipped line");
    }
    Seq<String> types = new Seq<String>();
    Seq<String> names = new Seq<String>();
    for(Struct struct : structs)
    for(int k=0; k<struct.fieldcount; k++)
    {
      String typename  = struct.typenames.get(k); 
      String fieldname = struct.fieldnames.get(k); 
      int index = names.indexOf(fieldname);
      if(index < 0)
      {
        types.add(typename);
        names.add(fieldname);
      }
      else 
        error.check(types.get(index).equals(typename), "type conflict for field " + fieldname);
    }
    String taglist = "";
    for(int k=0; k<structs.size(); k++)
    {
      if(k > 0) taglist += ", ";
      taglist += structs.get(k).structname;
    }

    output.add(prefix + heading + unionname);
    output.add(prefix + "{");
    output.add(prefix + "  public static enum Tag { " + taglist + " }");
    output.add(prefix);
    output.add(prefix + "  public Tag tag;");
    for(int k=0; k<names.size(); k++)
      output.add(prefix + "  public " + types.get(k) + " " + names.get(k) + ";");
    for(Struct struct : structs)
    {
      output.add(prefix);
      output.add(prefix + "  public static " + unionname + " " + struct.structname + "(" + struct.paramlist() + ")");
      output.add(prefix + "  {");
      output.add(prefix + "    " + unionname + " result = new " + unionname + "();");
      output.add(prefix + "    result.tag = Tag." + struct.structname + ";");
      for(int k=0; k<struct.fieldcount; k++)
        output.add(prefix + "    result." + struct.fieldnames.get(k) + " = " + struct.fieldnames.get(k) + ";");
      output.add(prefix + "    return result;");
      output.add(prefix + "  }");
    }
    output.add(prefix);
    output.add(prefix + "  public boolean equals(Object obj)");
    output.add(prefix + "  {");
    output.add(prefix + "    if(!(obj instanceof " + unionname + ")) return false;");
    output.add(prefix + "    " + unionname + " other = (" + unionname + ")obj;");
    output.add(prefix + "    if(other.tag != tag) return false;");
    output.add(prefix + "    switch(tag)");
    output.add(prefix + "    {");
    for(Struct struct : structs)
    {
      output.add(prefix + "      case " + struct.structname + ":");
      output.add(prefix + "        return " + struct.equality() + ";");
    }
    output.add(prefix + "    }");
    output.add(prefix + "    return super.equals(obj);");
    output.add(prefix + "  }");
    output.add(prefix);
    output.add(prefix + "  public String toString()");
    output.add(prefix + "  {");
    output.add(prefix + "    switch(tag)");
    output.add(prefix + "    {");
    for(Struct struct : structs)
    {
      output.add(prefix + "      case " + struct.structname + ":");
      output.add(prefix + "        return \"" + unionname + "." + struct.structname + "(\" + " + struct.display() + "\")\";");
    }
    output.add(prefix + "    }");
    output.add(prefix + "    return super.toString();");
    output.add(prefix + "  }");
    output.add(prefix + "}");
  }
  
  void processVar(Seq<String> input, Seq<String> output)
  {
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(input.get(0), idents, "", typeAlfabet);
    String prefix = input.get(0).substring(0, offset);
    
    if(!error.check(idents.size() >= 4, "wrong number of parameters")) return;
    
    output.add(prefix + idents.get(3) + input.get(0).substring(offset+3));
  }
  
  String insideParentheses(char lpar, char rpar, String s)
  {
    int lpos = s.indexOf(lpar);
    int rpos = s.lastIndexOf(rpar);
    if(lpos >= 0 && rpos >= 0 && lpos < rpos) return s.substring(lpos+1, rpos);
    return null;
  }
  
  Seq<String> splitList(String s)
  {
    Seq<String> items = Util.recsplit(s, new String[]{"(", "{", "["}, new String[]{")", "}", "]"}, new String[]{","}); 
    Seq<String> result = new Seq<String>();
    for(int k=0; k<items.size(); k+=2)
    if(items.get(k).length() > 0)
      result.add(items.get(k));   
    return result;
  }
  
  boolean isBasicType(String typename)
  {
         if(typename.equals("int"))      return true;
    else if(typename.equals("float"))    return true;
    else if(typename.equals("double"))   return true;
    else if(typename.equals("boolean"))  return true;
    else if(typename.equals("char"))     return true;
    else                                 return false;
  }
  
  boolean isAggregateName(String aggname)
  {
         if(aggname.equals("sum"))       return true;
    else if(aggname.equals("count"))     return true;
    else if(aggname.equals("min"))       return true;
    else if(aggname.equals("max"))       return true;
    else if(aggname.equals("or"))        return true;
    else if(aggname.equals("and"))       return true;
    else                                 return false;
  }
  
  String newAggregate(String aggname, String typename)
  {
         if(aggname.equals("sum"))       return "0";
    else if(aggname.equals("count"))     return "0";
    else if(aggname.equals("min"))       return "1000000";
    else if(aggname.equals("max"))       return "-1000000";
    else if(aggname.equals("or"))        return "false";
    else if(aggname.equals("and"))       return "true";
    else                                 return "new " + typename + "()";
  }
  
  String updateAggregate(String aggname, String typename, String varname, String item)
  {
         if(aggname.equals("sum"))       return varname + " += " + item + ";";
    else if(aggname.equals("count"))     return varname + "++;";
    else if(aggname.equals("min"))       return "{ " + typename + " _val_" + lcCount + " = " + item
                                              + "; if(_val_" + lcCount + " < " + varname + ") " 
                                              + varname + " = _val_" + lcCount + "; }";
    else if(aggname.equals("max"))       return "{ " + typename + " _val_" + lcCount + " = " + item 
                                              + "; if(_val_" + lcCount + " > " + varname + ") " 
                                              + varname + " = _val_" + lcCount + "; }";
    else if(aggname.equals("or"))        return "if(" + item + ") { " + varname + " = true; break _block_" + lcCount + "; }";
    else if(aggname.equals("and"))       return "if(!(" + item + ")) { " + varname + " = false; break _block_" + lcCount + "; }";
    else                                 return varname + ".add(" + item + ");";
  }
  
  int lcCount = 0;
  
  void processComprehension(Seq<String> input, Seq<String> output)
  {
    lcCount++;
    String line = "";
    for(String s : input) line += s;
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(line, idents, "", typeAlfabet);
    String body = insideParentheses('[', ']', line);

    if(!error.check(idents.size() >= 2, "missing type name")) return;
    if(!error.check(body != null, "wrong comprehension body")) return;
    
    boolean isReturn = idents.get(0).equals("return");
    boolean isVar    = idents.get(0).equals("var");
    boolean isFold   = false;
    String varname   = idents.get(isVar ? 1 : 0);
    String typename  = idents.get(isVar ? 2 : 1);
    String aggname   = "";
    
    identifiers(typename, idents, "", wordAlfabet);
    String typebody = insideParentheses('<', '>', typename); 

    if(!error.check(idents.size() >= 1, "missing type name")) return;
    if(!error.check(typebody != null, "wrong type body")) return;
    
    if(isAggregateName(idents.get(0)) && isBasicType(typebody))
    {
      aggname  = idents.get(0);
      typename = typebody;
    }
    else if(idents.get(0).equals("fold"))
    {
      isFold = true;
      typename = typebody;
    }
    
    String prefix = line.substring(0, offset);
    String command = isReturn ? "return" : line.substring(offset + (isVar ? 4 : 0), line.indexOf('=')+1);
    String oldvarname = varname;
    if(!isVar) varname = "_result_" + lcCount;
    
    Seq<String> items = Util.recsplit(body, new String[]{"(", "{", "["}, new String[]{")", "}", "]"}, new String[]{",", "for", "if", "let", "until"});  
    Seq<String> updates    = new Seq<String>();  
    Seq<String> generators = new Seq<String>();  
    Seq<String> details    = new Seq<String>(); 
    String prev = ","; 
    for(int k=0; k<items.size(); k++)
    {
      String item = items.get(k);
      if(k % 2 != 0)
      {
        if(!item.equals(","))
          generators.add(prev = item);
        else 
          if(!error.check(prev.equals(",") , "wrongly placed ,")) return;
      }
      else if(!prev.equals(",")) 
      {
        item = insideParentheses('(', ')', item);
        if(!error.check(item != null, "wrong generator body")) return;
        details.add(item);
      }
      else if(item.length() > 0) 
        updates.add(item);
    }
    
    if(isFold)
    {
      if(!error.check(updates.size() >= 2 && updates.get(0).contains("="), "wrong fold initialisation")) return;
      varname = updates.get(0).substring(0, updates.get(0).indexOf('='));
    }
    
    int braces = 0;
    String oldprefix = prefix;
    if(!isFold)
      output.add(prefix + (isVar ? "" : "{ ") + typename + " " + varname + " = " + newAggregate(aggname, typename) + ";");
    else
    {
      if(isVar) output.add(prefix + typename + " " + oldvarname + ";");
      output.add(prefix + "{ " + typename + " " + updates.get(0) + ";");
      if(updates.size() > 2) output.add(prefix + "  int _count_" + lcCount + " = 0;");
    }
    if(!isVar || isFold) 
    {
      prefix += "  ";
      braces++;
    }
    
    boolean hasuntil = false;
    for(String generator : generators)
    if(generator.equals("until"))
      hasuntil = true;
    if(hasuntil || aggname.equals("or") || aggname.equals("and"))
    {
      output.add(prefix + "{ _block_" + lcCount + ": {");
      prefix += "  ";
      braces += 2;
    }
    
    for(int k=0; k<generators.size(); k++)
    {
      String generator = generators.get(k);
      String detail = details.get(k);
      if(generator.equals("for"))
        output.add(prefix + "for(" + detail + ")");
      else if(generator.equals("if"))
        output.add(prefix + "if(" + detail + ")");
      else if(generator.equals("let"))
      {
        if(k > 0 && (generators.get(k-1).equals("for") || generators.get(k-1).equals("if")))
        {
          output.add(prefix + "{");
          braces++;
          prefix += "  ";
        }
        output.add(prefix + detail + ";");
      }
      else if(generator.equals("until"))
      {
        if(k > 0 && (generators.get(k-1).equals("for") || generators.get(k-1).equals("if")))
        {
          output.add(prefix + "{");
          braces++;
          prefix += "  ";
        }
        output.add(prefix + "if(" + detail + ") break _block_" + lcCount + ";");
      }
    }
    
    if(!isFold)
    {
      if(updates.size() == 0)
        output.add(prefix + "  ;");
      else if(updates.size() > 1)
      {
        output.add(prefix + "{");
        braces++;
      }
      for(String update : updates)
        output.add(prefix + "  " + updateAggregate(aggname, typename, varname, update));
    }
    else if(updates.size() == 2)
      output.add(prefix + "  " + updates.get(1) + ";");
    else
    {
      output.add(prefix + "  switch(++_count_" + lcCount + ") {");
      for(int k=1; k<updates.size()-1; k++)
        output.add(prefix + "  case " + k + ": " + updates.get(k) + "; break;");
      output.add(prefix + "  default: " + updates.get(updates.size()-1) + "; break;");
      braces++;
    }
      
    prefix = oldprefix;
    if(!isVar || isFold)
    {
      if(braces > 1) output.add(prefix + "  " + Util.repChar('}', braces-1));
      output.add(prefix + "  " + command + " " + varname + ";");
      output.add(prefix + "}");
    }
    else
      if(braces > 0) output.add(prefix + Util.repChar('}', braces));
  }
  
  Seq<String> stripBlock(Seq<String> block)
  {
    int k = 0;
    while(k < block.size() && !block.get(k).contains("{")) k++;
    int l = block.size() - 1;
    while(l >= 0 && !block.get(l).contains("}")) l--;
    Seq<String> result = new Seq<String>();
    for(int m=k+1; m<l; m++)
      result.add(block.get(m));
    return result;
  }
  
  String paramlist(int argcount, Seq<String> typenames, Seq<String> argnames)
  {
    String result = "";
    for(int k=0; k<argcount; k++)
    {
      if(k > 0) result += ", ";
      result += typenames.get(k) + " " + argnames.get(k);
    }
    return result;
  }
  
  String arglist(int argcount, Seq<String> argnames, Seq<String> argvals)
  {
    String result = "";
    for(int k=0; k<argcount; k++)
    {
      if(k > 0) result += ", ";
      result += argnames.get(k);
    }
    if(argvals != null)
    for(int k=argcount; k<argvals.size(); k++)
    {
      if(k > 0) result += ", ";
      result += argvals.get(k);
    }
    return result;
  }

  void processCall(Seq<String> input, Seq<String> output)
  {
    String definition = input.get(0);
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(definition, idents, "", wordAlfabet);
    String prefix = definition.substring(0, offset);
    String parameters = insideParentheses('(', ')', definition);
    
    if(!error.check(idents.size() >= 2, "missing function name")) return;
    if(!error.check(parameters != null, "wrong parameterlist")) return;
    
    String funname = idents.get(1);
    Seq<String> params = splitList(parameters);
    
    output.add(prefix + "{");
    output.add(prefix + "  " + funname + " _proc_ = new " + funname + "();");
    String paramlist = "";
    for(int k=0; k<params.size(); k++)
    {
      if(k > 0) paramlist += ", ";
      String param = params.get(k);
      if(param.startsWith("var ")) param = param.substring(4);
      paramlist += param;
    }
    output.add(prefix + "  _proc_.call(" + paramlist + ");");
    for(int k=0; k<params.size(); k++)
    if(params.get(k).startsWith("var "))
      output.add(prefix + "  " + params.get(k).substring(4) + " = _proc_.getArg" + k + "();");
    output.add(prefix + "}");
  }
  
  void processProcdef(Seq<String> input, Seq<String> output)
  {
    String definition = input.get(0);
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(definition, idents, "", fieldAlfabet);
    String prefix = definition.substring(0, offset);
    String parameters = insideParentheses('(', ')', definition);
    
    if(!error.check(idents.size() >= 3, "missing function name")) return;
    if(!error.check(parameters != null, "wrong parameterlist")) return;
    
    String resulttype = idents.get(1);
    String funname    = idents.get(2);
    Seq<String> args  = splitList(parameters);
    Seq<String> typenames = new Seq<String>();
    Seq<String> argnames  = new Seq<String>();
    int argcount = args.size();
    for(int k=0; k<argcount; k++)
    {
      String arg = args.get(k);
      if(arg.startsWith("var ")) arg = arg.substring(4);
      typenames.add(arg);
      argnames.add("_arg_" + k);
    }
    
    output.add(prefix + "public interface " + funname);
    output.add(prefix + "{");
    int varcount = 0;
    for(int k=0; k<argcount; k++)
    if(args.get(k).startsWith("var "))
    {
      output.add(prefix + "  " + typenames.get(k) + " getArg" + k + "();");
      varcount++;
    }
    if(varcount > 0) output.add(prefix);
    output.add(prefix + "  " + resulttype + " call(" + paramlist(argcount, typenames, argnames) + ");");
    output.add(prefix + "}");
  }
    
  void processProc(Seq<String> block1, Seq<String> block2, Seq<String> output)
  {
    String definition = block1.get(0);
    block1 = stripBlock(block1);
    block2 = stripBlock(block2);
    Seq<String> idents = new Seq<String>();
    int offset = identifiers(definition, idents, "", fieldAlfabet);
    String prefix = definition.substring(0, offset);
    String parameters = insideParentheses('(', ')', definition);
    
    if(!error.check(idents.size() >= 3, "missing function name")) return;
    if(!error.check(parameters != null, "wrong parameterlist")) return;
    
    Seq<String> args = splitList(parameters);
    String extra = definition.substring(definition.lastIndexOf(')')+1);
    if(extra.contains("{")) extra = extra.substring(0, extra.indexOf('{'));
    
    String resulttype = idents.get(1);
    String funname    = idents.get(2);
    Seq<String> typenames  = new Seq<String>();
    Seq<String> fieldnames = new Seq<String>();
    Seq<String> argnames   = new Seq<String>();
    Seq<String> argvals    = new Seq<String>();
    int argcount = args.size();
    for(int k=0; k<argcount; k++)
    {
      String arg = args.get(k);
      if(arg.startsWith("var ")) arg = arg.substring(4);
      if(arg.contains("="))
      {
        int pos = arg.indexOf("=");
        argvals.add(arg.substring(pos+1).trim());
        arg = arg.substring(0, pos);
      }
      else
        argvals.add("");
      Seq<String> ids = new Seq<String>();
      identifiers(arg, ids, "", fieldAlfabet);
      if(!error.check(ids.size() >= 2, "missing parameter name")) return;
      typenames.add(ids.get(0));
      fieldnames.add(ids.get(1));
      argnames.add("_arg_" + k);
    }
  
    for(int k=argcount;; k--)
    {
      output.add(prefix + "// mocca");
      output.add(prefix + "public " + resulttype + " " + funname + "(" + paramlist(k, typenames, fieldnames) + ")");
      output.add(prefix + "{");
      output.add(prefix + "  " + (resulttype.equals("void") ? "" : "return ") +  "new " + funname + "().call(" + arglist(k, fieldnames, null) + ");");
      output.add(prefix + "}");
      output.add(prefix);
      if(k == 0 || argvals.get(k-1).length() == 0) break;
    }
    output.add(prefix + "// mocca");
    output.add(prefix + "public class " + funname + " " + extra);
    output.add(prefix + "{");
    for(int k=0; k<argcount; k++)
      output.add(prefix + "  " + typenames.get(k) + " " + fieldnames.get(k) + ";");
    output.add(prefix);
    int varcount = 0;
    for(int k=0; k<argcount; k++)
    if(args.get(k).startsWith("var "))
    {
      output.add(prefix + "  public " + typenames.get(k) + " getArg" + k + "() { return " + fieldnames.get(k) + "; }");
      varcount++;
    }
    if(varcount > 0) output.add(prefix);
    output.add(prefix + "  public " + resulttype + " call(" + paramlist(argcount, typenames, argnames) + ")");
    output.add(prefix + "  {");
    for(int k=0; k<argcount; k++)
      output.add(prefix + "    " + fieldnames.get(k) + " = " + argnames.get(k) + ";");
    for(String s : block1)
      output.add("  " + s);
    output.add(prefix + "  }");
    output.add(prefix);
    for(int k=argcount-1; k >= 0 && argvals.get(k).length() > 0; k--)
    {
      output.add(prefix + "  public " + resulttype + " call(" + paramlist(k, typenames, argnames) + ")");
      output.add(prefix + "  {");
      output.add(prefix + "    " + (resulttype.equals("void") ? "" : "return ") + "call(" + arglist(k, argnames, argvals) + ");");
      output.add(prefix + "  }");
      output.add(prefix);
    }
    for(String s : block2)
      output.add(s);
    output.add(prefix + "}");
  }
  
  boolean hasWordAt(String string, String word, int i)
  {
    if(!string.startsWith(word, i)) return false;
    if(wordAlfabet.contains(String.valueOf(word.charAt(0))) && i > 0 && wordAlfabet.contains(String.valueOf(string.charAt(i-1)))) return false;
    int len = word.length();
    i += len;
    if(wordAlfabet.contains(String.valueOf(word.charAt(len-1))) && i < string.length() && wordAlfabet.contains(String.valueOf(string.charAt(i)))) return false;
    return true;
  }
  
  class Macro
  {
    private String name;
    private Seq<Integer> subst;
    private Seq<String> text;
    private int argcount;
    private int substcount;
    
    public Macro(Seq<String> head, String body)
    {
      name = head.get(0);
      argcount = head.size()-1;
      subst = new Seq<Integer>();
      text = new Seq<String>();
      int pos0 = 0;
      for(int pos1=0; pos1<body.length(); pos1++)
      for(int k=0; k<argcount; k++)
      if(hasWordAt(body, head.get(k+1), pos1))
      {
        text.add(body.substring(pos0, pos1));
        subst.add(k);
        pos0 = pos1 + head.get(k+1).length();
        pos1 = pos0 - 1;
        break;
      }
      text.add(body.substring(pos0, body.length()));
      substcount = subst.size();
    }
    
    public String getName()
    {
      return name;
    }
    
    public int getArgcount()
    {
      return argcount;
    }
    
    public String substitute(String parameters)
    {
      Seq<String> params = splitList(parameters);
      if(!error.check(params.size() == argcount, "wrong number of parameters for macro: " + name)) return "";
      StringBuffer sb = new StringBuffer();
      for(int k=0; k<substcount; k++)
      {
        sb.append(text.get(k));
        sb.append(params.get(subst.get(k)));
      }
      sb.append(text.get(substcount));
      return sb.toString();
    } 
  }
  
  void processMacros(Seq<String> lines)
  {
    lines.add("let Seq<int>       := Seq<Integer>");
    lines.add("let Seq<float>     := Seq<Float>");
    lines.add("let Seq<double>    := Seq<Double>");
    lines.add("let Seq<boolean>   := Seq<Boolean>");
    lines.add("let Seq<char>      := Seq<Character>");
    lines.add("let range(v, l, h) := for(int v=l; v<h; v++)");
    
    Seq<Macro> macros = new Seq<Macro>();
    for(int k=0; k<lines.size(); k++)
    {
      error.setLinenr(k);
      String line = lines.get(k).trim();
      if(!line.startsWith("let ") || !line.contains(":=")) continue;
      lines.set(k, "// mocca " + line);
      String head = line.substring(4, line.indexOf(":=")).trim();
      String body = line.substring(line.indexOf(":=")+2).trim();
      Seq<String> idents = new Seq<String>();
      identifiers(head, idents, "", typeAlfabet);  
          
      if(!error.check(idents.size() >= 1, "missing macro name")) continue;
      
      boolean found = false;
      for(Macro macro : macros)
      if(idents.get(0).equals(macro.getName()))
        found = true;
      if(!error.check(!found, "duplicate macro: " + idents.get(0))) continue;
      
      macros.add(new Macro(idents, body));
    }
    
    for(int k=0; k<lines.size(); k++)
    {
      error.setLinenr(k);
      String line = lines.get(k);
      if(line.trim().startsWith("//")) continue;
      int len = line.length();
      int substitutions = 0;
      for(boolean found=true; found && substitutions<10; )
      {
        found = false;
        loop:
        for(int pos1=0; pos1<len; pos1++)
        for(Macro macro : macros)
        if(hasWordAt(line, macro.getName(), pos1))
        {
          int pos2 = pos1 + macro.getName().length();
          int pos3 = pos2;
          int pos4 = pos2;
          int pos5 = pos2;
          if(macro.getArgcount() > 0)
          {
            while(pos5 < len && line.charAt(pos5) == ' ') pos5++;
            if(pos5 < len && line.charAt(pos5) == '(')
            {
              int level = 1;
              int pos6 = pos5+1;
              while(pos6 < len && level > 0)
              {
                if(line.charAt(pos6) == '(') level++;
                if(line.charAt(pos6) == ')') level--;
                pos6++;
              }
              if(level == 0)
              {
                pos2 = pos5+1;
                pos3 = pos6-1;
                pos4 = pos6;
              }
            }
          }
          line = line.substring(0, pos1) + macro.substitute(line.substring(pos2, pos3)) + line.substring(pos4);
          len = line.length();
          found = true;
          substitutions++;
          break loop;
        }
      }
      lines.set(k, line);
    }
  }
}
