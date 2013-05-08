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

namespace AmandaInterface
{
    public partial class Form1 : Form
    {
        public delegate bool RunCallbackD(string expression);
        public RunCallbackD RunCallback;
        public delegate bool LoadCallbackD(string content);
        public LoadCallbackD LoadCallback;
        public AutocompleteMenu autocomplete;

		public void output(string t)
		{
			RunTextbox.Text = t;
			System.Console.WriteLine("worked");
		}

        public void AddAutocompleteEntries(List<string> entries)
        {
            autocomplete.Items.SetAutocompleteItems(entries);
        }


        public Form1()
        {
            InitializeComponent();

            autocomplete = new AutocompleteMenu(LoadTextbox);
            autocomplete.MinFragmentLength = 1;
            autocomplete.Items.MaximumSize = new System.Drawing.Size(200, 300);
            autocomplete.Items.Width = 400;
        }

        private void OutputTextbox_TextChanged(object sender, EventArgs e)
        {

        }

        private void RunButton_Click(object sender, EventArgs e)
        {
            RunCallback(RunTextbox.Text);
        }

        private void LoadButton_Click(object sender, EventArgs e)
        {
            LoadCallback(LoadTextbox.Text);
        }

        private void LoadTextbox_TextChanged(object sender, EventArgs e)
        {

        }

        private void OutputTextbox_Load(object sender, EventArgs e)
        {

        }

        private void LoadTextbox_Load(object sender, EventArgs e)
        {

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


        private Stack<int> WhereIndents = new Stack<int>();
        private Stack<int> ConditionIndents = new Stack<int>();

        private int indentSize = 0;

        static bool isInCondition = false;
        private void LoadTextbox_AutoIndentNeeded(object sender, AutoIndentEventArgs e)
        {
            Match isIfRegex = Regex.Match(e.LineText.Trim(), ",* if");
            Match isOtherwiseRegex = Regex.Match(e.LineText.Trim(), ",* otherwise");
            Match isWhereRegex = Regex.Match(e.LineText, "where");

            if (isWhereRegex.Success)
            {
                e.ShiftNextLines = e.TabLength;
                indentSize += e.TabLength;
            }
            if (isIfRegex.Success == true)
             {
                try
                {
                    if(e.LineText.Trim()[0] != '=')
                    {
                        int at = e.LineText.IndexOf('=');
                        //e.ShiftNextLines = at - ((e.iLine != 0) ? e.iLine : 0);
                        e.ShiftNextLines = e.TabLength; //FAIL
                        //LoadTextbox.DoAutoIndent(at);
                        isInCondition = true;
                        //else e.ShiftNextLines -= e.TabLength;
                    }
                }
                catch(Exception wat)
                {
                    //fucking exceptions
                }
            }


           // if (e.LineText.Trim().Length > 0 && )
            //{
                //int at = e.LineText.IndexOf('=');
                //e.ShiftNextLines = e.Shift = at;
            //}
            if (e.LineText.Trim() == "" || isOtherwiseRegex.Success == true)
            {
                e.ShiftNextLines = -e.TabLength;
                indentSize -= e.TabLength;
                isInCondition = false;
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
            e.ChangedRange.SetStyle(CommentStyle,  @"\|\|.*");
            e.ChangedRange.SetStyle(ConstantStyle, @"-?[0-9]+\b");
        }


    }
}
