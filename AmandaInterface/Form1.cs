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
               
        OutputCallback outputCallback;
        StringBuilder tempOutput = new StringBuilder();
        
        System.Windows.Forms.Timer runTimer = new System.Windows.Forms.Timer();
        System.Timers.Timer runTTimer = new System.Timers.Timer();
        bool isRunning = false;

        System.Diagnostics.Stopwatch stopWatch = new System.Diagnostics.Stopwatch();
        BackgroundWorker rcBw = new BackgroundWorker();

        public mainForm()
        {
            InitializeComponent();

            outputCallback = OutputCallbackMethod;
            AmandaHook.SetOutputCallback(outputCallback);
          
            AmandaObj = new Amanda();
            tbConsole.AppendText(tempOutput.ToString());


            runButton.Click += (sender, e) => RunCode();
            loadButton.Click += (sender, e) =>
                {
                    if (fileManager.SelectedTabTextBox.Text == "") return;

                    if (AmandaObj.Load(fileManager.SelectedTabTextBox.Text) == true)
                    {
                        MessageBox.Show("File Loaded");
                    }
                };

            runTimer.Tick += new EventHandler(runTimer_Tick);
            runTimer.Interval = 20;
            
            rcBw.WorkerSupportsCancellation = true;
            rcBw.WorkerReportsProgress = false;
            rcBw.DoWork += new DoWorkEventHandler(rcBw_doWork);
            rcBw.RunWorkerCompleted += new RunWorkerCompletedEventHandler(rcBw_runWorkerCompleted);
        }

        private void RunCode()
        {
            // show expression
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.SelectionBackColor = Color.Yellow;
            string expr = "> " + tbRun.Text + "\r\n";
            tbConsole.AppendText(expr);
            tbConsole.SelectionBackColor = Color.Transparent;
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
            // Make sure the console isn't getting too full
            //
            if (tbConsole.TextLength > 40000)
            {
                Console.WriteLine("Resetting Console Content");
                tbConsole.Text = tbConsole.Text.Substring(35000);
            }

            // StringBuilder isn't threadsafe, so we use a lock here to prevent Exceptions
            //
            lock (tempOutput)
            {
                tbConsole.AppendText(tempOutput.ToString());
                tempOutput.Clear();
            }

            // Scroll to the end of the textbox
            //
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.ScrollToCaret();
        }

        private void rcBw_doWork(object sender, DoWorkEventArgs e)
        {
            // Start the Amanda Interpereter & active the statusbar
            //
            statusBar.BackColor = Color.Orange;
            lblStatus.Text = "Running...";
            tempOutput.Clear();
            stopWatch.Start();
            AmandaObj.Interpret(tbRun.Text);        
        }

        private void rcBw_runWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            stopWatch.Stop();
            runTimer.Stop();

            runButton.Enabled = true;
            loadButton.Enabled = true;
            clearButton.Enabled = true;      
            statusBar.BackColor = Color.LightSkyBlue;
            lblStatus.Text = "Ready (Completed in: " + stopWatch.ElapsedMilliseconds + " Milliseconds) ";

            tbConsole.AppendText(tempOutput.ToString()); // Voeg het laatste stukje text ook toe.
            tempOutput.Clear();
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.ScrollToCaret();

            stopWatch.Reset();
            isRunning = false;
        }

        private void OutputCallbackMethod(String output)  //Deze functie wordt bij elke WriteString() (in de amanda DLL) uitgevoerd.
        {
            lock (tempOutput)
            {
                tempOutput.Append(output);
            }
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
            if (ActiveControl.Name == "tbRun")
                tbRun.Undo();
            else
                fileManager.SelectedTabTextBox.Undo();
        }
        private void redoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // tbRun has no builtin redo function;
            fileManager.SelectedTabTextBox.Redo();
        }
        private void cutToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            if (ActiveControl.Name == "tbRun")
                tbRun.Cut();
            else
                fileManager.SelectedTabTextBox.Cut();
        }
        private void copyToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            if (ActiveControl.Name == "tbRun")
                tbRun.Copy();
            else if (splitContainer1.ActiveControl.Name == "tbConsole")
                tbConsole.Copy();
            else
                fileManager.SelectedTabTextBox.Copy();
        }
        private void pasteToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            if (ActiveControl.Name == "tbRun")
                tbRun.Paste();
            else
                fileManager.SelectedTabTextBox.Paste();
        }
        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveControl.Name == "tbRun")
            {
                int SelectionIndex = tbRun.SelectionStart;
                int SelectionCount = tbRun.SelectionLength;
                tbRun.Text = tbRun.Text.Remove(SelectionIndex, SelectionCount);
                tbRun.SelectionStart = SelectionIndex;
            }
            else
            {
                int SelectionIndex = fileManager.SelectedTabTextBox.SelectionStart;
                int SelectionCount = fileManager.SelectedTabTextBox.SelectionLength;
                fileManager.SelectedTabTextBox.Text = fileManager.SelectedTabTextBox.Text.Remove(SelectionIndex, SelectionCount);
                fileManager.SelectedTabTextBox.SelectionStart = SelectionIndex;
            }
        }
        private void selectAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (ActiveControl.Name == "tbRun")
                tbRun.SelectAll();
            else
                fileManager.SelectedTabTextBox.SelectAll();
        }

        private void timeDateToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedTabTextBox.InsertText(String.Format("{0:HH:mm dd-MM-yyyy}", DateTime.Now));
        }

        private void findToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedTabTextBox.ShowFindDialog();
        }

        private void findNextToolStripMenuItem_Click(object sender, EventArgs e)
        {
            var findForm = fileManager.SelectedTabTextBox.findForm;
            findForm.FindNext(findForm.tbFind.Text);
        }

        private void findAndReplaceToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedTabTextBox.ShowReplaceDialog();
        }

        private void gotoLineToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedTabTextBox.ShowGoToDialog();
        }

        private void saveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedFileTab.SaveAs();
        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedFileTab.Save();
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.SelectedFileTab.AskToSaveFile();
            fileManager.OpenFile();
        }

        private void newFileToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            fileManager.AddNewFile();
        }

        private void helpToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }

        private void tabPage2_Click(object sender, EventArgs e)
        {

        }
    }

}
