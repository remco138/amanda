using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;


namespace AmandaInterface
{
    //Will do simple parsing of amanda code, fetches stuff about functions and other tags
    //will also be of use for smart indenting and make life easier coding (basic)error detection
    class AmandaTagParser
    {
        public List<AmandaTag> AmandaTags;
        //To prevent unnecessary parsing
        private string LastCodeParsed;



        public AmandaTagParser()
        {
            AmandaTags = new List<AmandaTag>();
        }

        //Returns the location of bracket mismatch
        //Rewrite this to make use of better suited container for brackets
        public int GetBracketMismatch(string code)
        {
            Dictionary<char, char> bracketMap = new Dictionary<char, char>() 
            { {'{', '}'}, {'(', ')'}, {'[', ']'}, {'<', '>'} };//, {'\'', '\''} }; //{} () [] <> "" ''
            Stack<char> bracketStack = new Stack<char>();
            int counter = 0;

            foreach (char c in code)
            {
                if("(){}<>".Contains(c))
                {
                    if (!")}>".Contains(c))
                    {
                        bracketStack.Push(c);
                    }
                    else if (bracketMap.Any(q => q.Key == bracketMap[bracketStack.Last()]))
                    {
                        bracketStack.Pop();
                    }
                    else
                    {
                        return counter;
                    }
                }
                counter++;
            }

            return -1;
        }

        //gets the 
        public AmandaTag GetTag(int line)
        {
            return AmandaTags.Find(q => q.BeginLocation.Y <= line && q.EndLocation.Y >= line);
        }

        //generates the tags (function names, variables, etc)
        public void parse(string code)
        {
            if (code != LastCodeParsed)
            {
                int counter = 0;
                AmandaTag tag = null;
                LastCodeParsed = code;

                foreach(string line in code.Split('\n'))
                {
                    string functionName = new string(line.TrimStart().TakeWhile(q => q != ' ').ToArray());
                    if (line.Trim().Count() == 0)
                    {
                        continue;
                    }
                    else if (tag != null && line.TrimStart()[0] == '=')
                    {
                        tag.EndLocation = new Point(0, counter);
                    }
                    else if(line.Contains('=') && line.TrimStart()[0] != '=' /*&& GetBracketMismatch(line) < 1*/)
                    {
                        if (tag != null)
                        {
                             AmandaTags.Add(tag);
                        }
                        List<string> functionArguments = line.TrimStart().Replace(functionName, "").TakeWhile(q => q != '=').ToString().Split(' ').ToList(); //This is getting awkward
                        //Unimplemented: Definition, Summary & endlocation
                        tag = new AmandaTag(functionName, functionArguments, "", "", new Point(line.IndexOf(functionName), counter), new Point(0, 0));
                    }
                    counter++;
                }
                if (tag != null)
                {
                    AmandaTags.Add(tag);
                }
            }
        }

        //Loads a database file which contains summaries for (hardcoded) amanda functions
        public void LoadDB(string file) { }

    }

    class AmandaTag
    {
        //TagType tagType; //function, weird OOP structure (::=) and possibly more?

        public string Name;            //function name, like "increase"
        public List<string> Arguments; //Arguments like: x y (z:xs)
        public string Definition;      //code after the '='
        public string Summary;         //Description of the function, this should be fetched from some DB file (minardus?)
        public Point BeginLocation;    //as in line (x, y) 
        public Point EndLocation;

        public AmandaTag(string name, List<string> arguments, string definition, string summary, Point beginLocation, Point endLocation)
        {
            Name = name;
            Arguments = arguments;
            Definition = definition;
            Summary = summary;
            BeginLocation = beginLocation;
            EndLocation = endLocation;
        }
 
    }

}
