using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using FastColoredTextBoxNS;
using System.Text.RegularExpressions;
using System.Threading;

namespace AmandaInterface
{
    public partial class Form1 : Form
    {
        Amanda AmandaObj;
        AutocompleteMenu autocomplete;
        OutputCallback outputCallback;

        public Form1()
        {
            InitializeComponent();

            outputCallback = OutputCallbackMethod;
            AmandaHook.SetOutputCallback(outputCallback);
          
            AmandaObj = new Amanda();

            autocomplete = new AutocompleteMenu(LoadTextbox);
            autocomplete.MinFragmentLength = 1;
            autocomplete.Items.MaximumSize = new System.Drawing.Size(200, 300);
            autocomplete.Items.Width = 400;
            autocomplete.Items.SetAutocompleteItems(AmandaObj.GetIdentifiers());

            loadButton.Click += (sender,e) => AmandaObj.Load(LoadTextbox.Text);
            runButton.Click += (sender, e) => AmandaObj.Interpret(RunTextbox.Text);
            StatusLabel.Text = "Idle";
            ProgressBar.Value = 0;
        }

        private void OutputCallbackMethod(String output)  //Deze functie wordt bij elke WriteString() uitgevoerd //
        {
            //Console.WriteLine("Dit is niet zomaar output: " + output); // TRAAG !!
            richTextBox1.AppendText(output);
        }

        private void LoadTextbox_KeyDown(object sender, KeyEventArgs e)
        {
            string allowedChars = "([{}]);,. ";
            int currentLine = LoadTextbox.Selection.Bounds.iStartLine;
            string textTillCursor = LoadTextbox.GetLineText(currentLine).Substring(0, LoadTextbox.Selection.Bounds.iStartChar);
            //string textAfterCursor = LoadTextbox.GetLineText(currentLine);
            char charBeforeCursor = LoadTextbox.Selection.CharBeforeStart;
            char charAfterCursor = LoadTextbox.Selection.CharAfterStart;
            if (e.KeyData == (Keys.K | Keys.Control))
            {
                //forced show (MinFragmentLength will be ignored)
                autocomplete.Show(true);
                //e.Handled = true;
            }
            if (System.Char.IsLetter((char)e.KeyValue) //warning, lisp programmer at work
                && textTillCursor.Count() > 0
                && allowedChars.Contains(charBeforeCursor)
                && allowedChars.Contains(charAfterCursor))

                /*&&  cursorIsNotInsideFunctionName
                  &&  cursorIsCorrectlyIndented?? perhaps difficult with regex since parsing a language ain't easy
                 */
            {
                autocomplete.Show(true);

            }
        }

        private void LoadTextbox_AutoIndentNeeded(object sender, AutoIndentEventArgs e)
        {
            Match isIfRegex = Regex.Match(e.LineText.Trim(), ",* if");
            Match isOtherwiseRegex = Regex.Match(e.LineText.Trim(), ",* otherwise");
            Match isWhereRegex = Regex.Match(e.LineText, "where");
            int currentIndent = e.LineText.TakeWhile(q => q == ' ').Count();    //shouldn't be too inefficient

            /*
             *              All these todo's might not be neccessary, it's pretty good right now
             */
            if (isWhereRegex.Success)
            {
                e.ShiftNextLines = e.TabLength;
            }

             if (isIfRegex.Success == true)
             {
                 int at = e.LineText.IndexOf('=');
                 e.ShiftNextLines = at - currentIndent;
            }
             if (isOtherwiseRegex.Success)
                 //(CurrentFunctionContainsPatternMatching() && !previousLineIsCondition)
             {
                 //go back to the function's original indent-level, instead of matching the '='
             }

            if (e.LineText.Trim() == "" || isOtherwiseRegex.Success == true)
            {
                if (false)
                    //|| PreviousLineIsCondition
                {
                 //go back to the function's original indent-level, instead of matching the '='
                }

                e.ShiftNextLines = -e.TabLength;
                return;
            }
        }


        Style DefaultStyle = new TextStyle(Brushes.Black, null, FontStyle.Regular);
        Style KeywordStyle = new TextStyle(Brushes.Blue, null, FontStyle.Italic);
        Style CommentStyle = new TextStyle(Brushes.Green, null, FontStyle.Italic);
        Style ConstantStyle = new TextStyle(Brushes.Firebrick, null, FontStyle.Regular);

        private void LoadTextbox_TextChanged(object sender, TextChangedEventArgs e)
        {
            e.ChangedRange.ClearStyle(KeywordStyle, CommentStyle, ConstantStyle);

            e.ChangedRange.SetStyle(KeywordStyle,  @"\b(where|if|else|True|False|otherwise)\b");
            e.ChangedRange.SetStyle(CommentStyle,  @"\|\|.*");                          //comments ||...
            e.ChangedRange.SetStyle(ConstantStyle, @"(\B-)?[0-9]+\b");                  //numbers 123, -123, to be removed?
            e.ChangedRange.SetStyle(ConstantStyle, @"""[^""\\]*(?:\\.[^""\\]*)*""?");   //string "", source: stackoverflow
            e.ChangedRange.SetStyle(ConstantStyle, @"'[^'\\]*(?:\\.[^'\\]*)*'?");       //char ''
        }
    }




    //Will do simple parsing of amanda code, fetches stuff about functions and other tags
    //will also be of use for smart indenting and make life easier coding (basic)error detection
    class AmandaTagParser
    {
        public List<AmandaTag> AmandaTags;

        public AmandaTagParser()
        {
            AmandaTags = new List<AmandaTag>();
        }

        //generates the tags (function names, variables, etc)
        public void parse(string code) { }

        //Loads a database file which contains summaries for (hardcoded) amanda functions
        public void LoadDB(string file) { }

    }

    class AmandaTag
    {
        //TagType tagType; //function, weird OOP structure (::=) and possibly more?

        string Name;            //function name, like "increase"
        List<string> Arguments; //Arguments like: x y (z:xs)
        string Definition;      //code after the '='
        string Summary;         //Description of the function, this should be fetched from some DB file (minardus?)
        Point BeginLocation;    //as in line (x, y) 
        Point EndLocation;
 
    }

}
