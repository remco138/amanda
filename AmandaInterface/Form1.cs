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

        private void LoadTextbox_AutoIndentNeeded(object sender, AutoIndentEventArgs e)
        {
            if (e.LineText.Trim() == "where")
            {
                e.ShiftNextLines = e.TabLength;
            }

            else if (e.LineText.Trim() == "")
            {
                e.ShiftNextLines = -e.TabLength;
            }
        }


        Style DefaultStyle = new TextStyle(Brushes.Black, null, FontStyle.Regular);
        Style GreenStyle = new TextStyle(Brushes.Green, null, FontStyle.Italic);

        private void LoadTextbox_TextChanged(object sender, TextChangedEventArgs e)
        {
            e.ChangedRange.ClearStyle(GreenStyle);
            e.ChangedRange.SetStyle(GreenStyle, "(where|if|else|True|False|otherwise)");
            
            //foreach (Range found in LoadTextbox.GetRanges(@"where"))
              //  LoadTextbox.Range.SetStyle(GreenStyle, found.Text);
        }


    }
}
