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
using System.IO;

namespace AmandaInterface
{
    public partial class mainForm : Form
    {
        Amanda AmandaObj;
        AutocompleteMenu autocomplete;
        
        OutputCallback outputCallback;
        string tempOutput = "";
        System.Windows.Forms.Timer runTimer = new System.Windows.Forms.Timer();
        System.Timers.Timer runTTimer = new System.Timers.Timer();
        bool isRunning = false;

        SaveFileDialog saveDialog = new SaveFileDialog();
        OpenFileDialog openDialog = new OpenFileDialog();
        string currentFileLocation = String.Empty;
        bool isEdited = false;

        System.Diagnostics.Stopwatch stopWatch = new System.Diagnostics.Stopwatch();
        BackgroundWorker rcBw = new BackgroundWorker();

        public mainForm()
        {
            InitializeComponent();

            outputCallback = OutputCallbackMethod;
            AmandaHook.SetOutputCallback(outputCallback);
          
            AmandaObj = new Amanda();
            tbConsole.AppendText(tempOutput);
            
            tempOutput = "";

            autocomplete = new AutocompleteMenu(tbEditor);
            autocomplete.MinFragmentLength = 1;
            autocomplete.Items.MaximumSize = new System.Drawing.Size(200, 300);
            autocomplete.Items.Width = 400;
            autocomplete.Items.SetAutocompleteItems(AmandaObj.GetIdentifiers());

            runButton.Click += (sender, e) => RunCode();
            loadButton.Click += (sender,e) => AmandaObj.Load(tbEditor.Text);

            runTimer.Tick += new EventHandler(runTimer_Tick);
            runTimer.Interval = 10;
            
            rcBw.WorkerSupportsCancellation = true;
            rcBw.WorkerReportsProgress = false;
            rcBw.DoWork += new DoWorkEventHandler(rcBw_doWork);
            rcBw.RunWorkerCompleted += new RunWorkerCompletedEventHandler(rcBw_runWorkerCompleted);

            saveDialog.Filter = "Amanda File|*.ama";
            openDialog.Filter = "Amanda File|*.ama";

            isEdited = false;
        }

        private void RunCode()
        {
            // show expression
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.SelectionBackColor = Color.Yellow;
            string expr = "> " + tbRun.Text + "\r\n";
            tbConsole.AppendText(expr);

            // show output
            isRunning = true;
            runButton.Enabled = false;
            loadButton.Enabled = false;
            clearButton.Enabled = false;

            runTimer.Start(); // doet hetzelfde als runTimer.Enabled = true;

            if (rcBw.IsBusy != true)
            {
                rcBw.RunWorkerAsync();
            }
        }

        private void runTimer_Tick(object sender, EventArgs e)
        {           
            // Append text & Scroll to the bottom
            tbConsole.AppendText(tempOutput);
            tempOutput = "";
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.ScrollToCaret();
        }

        private void rcBw_doWork(object sender, DoWorkEventArgs e)
        {
            statusBar.BackColor = Color.Orange;
            lblStatus.Text = "Running...";
            tempOutput = "";
            stopWatch.Start();
            AmandaObj.Interpret(tbRun.Text);        
        }

        private void rcBw_runWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            tbConsole.AppendText(tempOutput); // Voeg het laatste stukje text ook toe.
            tempOutput = "";
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.ScrollToCaret();

            runButton.Enabled = true;
            loadButton.Enabled = true;
            clearButton.Enabled = true;
            isRunning = false;
            statusBar.BackColor = Color.LightSkyBlue;

            stopWatch.Stop();
            runTimer.Stop();

            lblStatus.Text = "Ready (Completed in: " + stopWatch.ElapsedMilliseconds + " Milliseconds) ";

            stopWatch.Reset();
        }

        private void OutputCallbackMethod(String output)  //Deze functie wordt bij elke WriteString() uitgevoerd //
        {
            //Console.WriteLine("Dit is niet zomaar output: " + output); // TRAAG !!
            tempOutput += output;
        }

        private void tbEditor_KeyDown(object sender, KeyEventArgs e)
        {
            string allowedChars = "([{}]);,. ";
            int currentLine = tbEditor.Selection.Bounds.iStartLine;
            string textTillCursor = tbEditor.GetLineText(currentLine).Substring(0, tbEditor.Selection.Bounds.iStartChar);
            //string textAfterCursor = tbEditor.GetLineText(currentLine);
            char charBeforeCursor = tbEditor.Selection.CharBeforeStart;
            char charAfterCursor = tbEditor.Selection.CharAfterStart;
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

        private void tbEditor_AutoIndentNeeded(object sender, AutoIndentEventArgs e)
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

        private void tbEditor_TextChanged(object sender, TextChangedEventArgs e)
        {
            // Set isEdited to true so the we can ask the user to save the file when he closes the program.
            //
            if (!isEdited) isEdited = true;

            e.ChangedRange.ClearStyle(KeywordStyle, CommentStyle, ConstantStyle);

            e.ChangedRange.SetStyle(KeywordStyle,  @"\b(where|if|else|True|False|otherwise)\b");
            e.ChangedRange.SetStyle(CommentStyle,  @"\|\|.*");                          //comments ||...
            e.ChangedRange.SetStyle(ConstantStyle, @"(\B-)?[0-9]+\b");                  //numbers 123, -123, to be removed?
            e.ChangedRange.SetStyle(ConstantStyle, @"""[^""\\]*(?:\\.[^""\\]*)*""?");   //string "", source: stackoverflow
            e.ChangedRange.SetStyle(ConstantStyle, @"'[^'\\]*(?:\\.[^'\\]*)*'?");       //char ''
        }

        private void tbRun_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == (char)13 && !isRunning)
            {
                RunCode();
                e.Handled = true;
            }
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            tbConsole.Clear();
        }

        private void undoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.Undo();
        }
        private void redoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.Redo();
        }
        private void cutToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            tbEditor.Cut();
        }
        private void copyToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            tbEditor.Copy();
        }
        private void pasteToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            tbEditor.Paste();
        }
        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            int SelectionIndex = tbEditor.SelectionStart;
            int SelectionCount = tbEditor.SelectionLength;
            tbEditor.Text = tbEditor.Text.Remove(SelectionIndex, SelectionCount);
            tbEditor.SelectionStart = SelectionIndex;
        }
        private void selectAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.SelectAll();
        }

        private void timeDateToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.InsertText(String.Format("{0:HH:mm dd-MM-yyyy}", DateTime.Now));
        }

        private void findToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.ShowFindDialog();
        }

        private void findNextToolStripMenuItem_Click(object sender, EventArgs e)
        {
            var findForm = tbEditor.findForm;
            findForm.FindNext(findForm.tbFind.Text);
        }

        private void findAndReplaceToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.ShowReplaceDialog();
        }

        private void gotoLineToolStripMenuItem_Click(object sender, EventArgs e)
        {
            tbEditor.ShowGoToDialog();
        }

        private void Save()
        {
            if (!isEdited) return;

            if (currentFileLocation == String.Empty)
            {
                SaveAs();
            }
            else
            {
                File.WriteAllText(currentFileLocation, tbEditor.Text);
                isEdited = false;
            }
        }

        private void SaveAs()
        {
            DialogResult result = saveDialog.ShowDialog();

            if (result == DialogResult.OK)
            {
                try
                {
                    File.WriteAllText(saveDialog.FileName, tbEditor.Text);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Unable to write to file: {0}", ex.Message);
                }

                currentFileLocation = saveDialog.FileName;
                isEdited = false;
            }
        }

        private void AskToSaveFile()
        {
            String file = (currentFileLocation == String.Empty) ? "Untitled" : currentFileLocation;

            DialogResult result = MessageBox.Show("Save File " + file + "?",
                                                    "Save File",
                                                    MessageBoxButtons.YesNo);

            if (result == DialogResult.Yes)
            {
                Save();
            }
        }

        private void saveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveAs();
        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Save();
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (isEdited)
            {
                AskToSaveFile();
            }

            DialogResult result = openDialog.ShowDialog();

            if (result == DialogResult.OK)
            {
                try
                {
                    tbEditor.Text = File.ReadAllText(openDialog.FileName);
                }
                catch (IOException ex)
                {
                    Console.WriteLine("Unable to open file: {0}", ex.Message);
                }
                currentFileLocation = openDialog.FileName;
                isEdited = false;
            }
        }

        private void newFileToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            if (isEdited) 
                AskToSaveFile();

            tbEditor.Text = "";
        }

        private void helpToolStripMenuItem_Click(object sender, EventArgs e)
        {

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
