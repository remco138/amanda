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
using System.Drawing.Printing;

namespace AmandaInterface
{
    public partial class mainForm : Form
    {
        public Amanda AmandaObj;
               
        OutputCallback outputCallback;
        StringBuilder tempOutput = new StringBuilder();
        
        bool isRunning = false;
        bool conBwStopped = false;

        System.Diagnostics.Stopwatch stopWatch = new System.Diagnostics.Stopwatch();
        BackgroundWorker bwInterpret = new BackgroundWorker();
        BackgroundWorker bwTextToConsole = new BackgroundWorker();

        public mainForm()
        {
            InitializeComponent();

            outputCallback = OutputCallbackMethod;
            AmandaHook.SetOutputCallback(outputCallback);

            AmandaObj = Amanda.GetInstance();
            tbConsole.AppendText(tempOutput.ToString());


            runButton.Click += new EventHandler(RunCodeHandler);
            loadButton.Click += (sender, e) =>
                {
                    if (fileManager.SelectedTabTextBox.Text == "") return;

                    if (AmandaObj.Load(fileManager.SelectedTabTextBox.Text) == true)
                    {
                        MessageBox.Show("File Loaded");
                        fileManager.UpdateAutocompleteIdentifiers(AmandaObj.GetIdentifiers());
                    }

                    // Print if error
                    //
                    tbConsole.AppendText("\n\n");
                    runTimer_Tick(null, null);
                    
                };

            //Hacky but meh
            fileManager.UpdateAutocompleteIdentifiers(AmandaObj.GetIdentifiers());
            
            bwInterpret.WorkerSupportsCancellation = true;
            bwInterpret.WorkerReportsProgress = false;
            bwInterpret.DoWork += new DoWorkEventHandler(rcBw_doWork);
            bwInterpret.RunWorkerCompleted += new RunWorkerCompletedEventHandler(rcBw_runWorkerCompleted);

            bwTextToConsole.WorkerSupportsCancellation = true;
            bwTextToConsole.WorkerReportsProgress = false;
            bwTextToConsole.DoWork += new DoWorkEventHandler(conBw_doWork);
            bwTextToConsole.RunWorkerCompleted += new RunWorkerCompletedEventHandler(conBw_runWorkerCompleted);
        }

        private void RunCode()
        {
            conBwStopped = false;
            // show expression
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.SelectionBackColor = Color.Yellow;
            string expr = "> " + tbRun.Text + "\r\n";
            tbConsole.AppendText(expr);
            tbConsole.SelectionBackColor = Color.Transparent;
            // show output
            isRunning = true;
            // change runButton to stopButton
            runButton.Click -= new EventHandler(RunCodeHandler);
            runButton.Click += new EventHandler(StopCodeHandler);
            runButton.Image = Properties.Resources.stop;
            // disable the rest of the buttons
            loadButton.Enabled = false;

            if (bwInterpret.IsBusy != true && bwTextToConsole.IsBusy != true)
            {
                bwTextToConsole.RunWorkerAsync();
                bwInterpret.RunWorkerAsync();
            }
        }

        private void RunCodeHandler(object sender, EventArgs e) {
            RunCode();
        }

        private void StopCodeHandler(object sender, EventArgs e)
        {
            AmandaObj.SetInterrupt(true);
        }

        private void runTimer_Tick(object sender, EventArgs e)
        {
            // blabla
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
            conBwStopped = true;
            stopWatch.Stop();

            // change stopButton to runButton
            runButton.Image = Properties.Resources.run;
            runButton.Click -= new EventHandler(StopCodeHandler);
            runButton.Click += new EventHandler(RunCodeHandler);
            loadButton.Enabled = true;
                  
            statusBar.BackColor = Color.LightSkyBlue;
            lblStatus.Text = "Ready (Completed in: " + stopWatch.ElapsedMilliseconds + " Milliseconds) ";

            stopWatch.Reset();
            isRunning = false;
        }

        private void conBw_doWork(object sender, DoWorkEventArgs e)
        {
            for (; ; )
            {
                Thread.Sleep(2);
                if (conBwStopped)
                    break;

                // StringBuilder isn't threadsafe, so we use a lock here to prevent Exceptions
                //
                lock (tempOutput)
                {
                    tbConsole.Invoke((MethodInvoker)delegate
                    {
                        if (tbConsole.TextLength > 50000)
                        {
                            Console.WriteLine("Resetting Console Content");
                            tbConsole.Text = tbConsole.Text.Substring(45000);
                        }
                        tbConsole.AppendText(tempOutput.ToString());
                        tempOutput.Clear();
                        tbConsole.SelectionStart = tbConsole.TextLength;
                        tbConsole.ScrollToCaret();
                    });
                }
            }
        }

        private void conBw_runWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            tbConsole.AppendText(tempOutput.ToString()); // Voeg het laatste stukje text ook toe.
            tempOutput.Clear();
            tbConsole.SelectionStart = tbConsole.TextLength;
            tbConsole.ScrollToCaret();
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

        private void printToolStripMenuItem_Click(object sender, EventArgs e)
        {
            PrintDialogSettings pds = new PrintDialogSettings();
            pds.ShowPrintDialog = true;
            pds.ShowPrintPreviewDialog = false;
            pds.IncludeLineNumbers = true;
            fileManager.SelectedFileTab.textBox.Margin = new Padding(10);
            fileManager.SelectedFileTab.textBox.Print(pds);
        }

        private void exitToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void StopButton_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {

        }

        private void closeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fileManager.CloseSelectedTab();
        }

        private void amandaDocsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                System.Diagnostics.Process.Start(Application.StartupPath + @"\documentation\index.html");
            }
            catch (Win32Exception ex)
            {
                MessageBox.Show("Kan de documentatie bestanden niet vinden! Vraag je leraar om hulp.");
            }
        }

        private void aboutAmandaToolStripMenuItem_Click(object sender, EventArgs e)
        {
            About aboutForm = new About();
            aboutForm.StartPosition = FormStartPosition.Manual;
            aboutForm.Location = new Point(this.Location.X + (this.Width - aboutForm.Width) / 2, this.Location.Y + (this.Height - aboutForm.Height) / 2);
            aboutForm.Show(this);
        }
    }

}
