using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FastColoredTextBoxNS;
using System.Windows.Forms;
using System.Drawing;
using System.IO;
using System.Text.RegularExpressions;

namespace AmandaInterface
{
    class FileManagerTabControl : TabControl
    {
        OpenFileDialog openDialog = new OpenFileDialog();

        public FileManagerTabControl()
        {
            this.AllowDrop = true;
            this.DragEnter += _DragEnter;
            this.DragDrop += _DragDrop;

            openDialog.Filter = "Amanda File|*.ama";

            AddNewFile();
        }

        
        public void AddNewFile()
        {
            // Add a new TabPage and place a FastColoredTextBox inside it.

            FileEditorPage newPage = new FileEditorPage();     
            newPage.Padding = new System.Windows.Forms.Padding(3);
            newPage.Text = "tabPage1";

            this.TabPages.Add(newPage);
        }

        public void OpenFile()
        {
            if (openDialog.ShowDialog() == DialogResult.OK)
            {
                OpenExistingFile(openDialog.FileName);
            }
        }

        private void OpenExistingFile(string pathToFile)
        {
            // Is this file already open?
            //
            foreach (FileEditorPage page in TabPages)
            {
                if (page.fileLocation == pathToFile)
                    return;
            }

            // Read file & Open a new Tab
            //
            try
            {
                String txt = File.ReadAllText(pathToFile);

                FileEditorPage newPage = new FileEditorPage();
                newPage.fileLocation = pathToFile;
                newPage.Text = pathToFile; // TODO: Only show the file name and not the whole path

                this.TabPages.Add(newPage);
            }
            catch (IOException ex)
            {
                Console.WriteLine("Unable to open file: {0}", ex.Message);
            }
        }

        public void CloseFile()
        {
            // TODO: Ask to save file
            TabPages.Remove(SelectedFileTab);
        }

        /// <summary>
        /// The original SelectedTab returns a TabPage. This one returns a FileEditorPage (our own)
        /// </summary>
        public FileEditorPage SelectedFileTab
        {
            get { return ((FileEditorPage)this.SelectedTab); }
        }
        public FastColoredTextBox SelectedTabTextBox
        {
            get { return ((FileEditorPage)this.SelectedFileTab).textBox; }
        }


        private void _DragDrop(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop)) // if the dropped file is a text file
            {
                string[] filePaths = (string[])e.Data.GetData(DataFormats.FileDrop);

                foreach (string file in filePaths)
                {
                    OpenExistingFile(file);
                }
            }
        }

        private void _DragEnter(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop)) e.Effect = DragDropEffects.Copy;
        }
    }


    class FileEditorPage : TabPage
    {
        private string _fileLocation;
        public string fileLocation
        {
            get { return _fileLocation; }
            set { _fileLocation = value; }
        }
        bool isEdited;
        public FastColoredTextBox textBox;
        AutocompleteMenu autocomplete;
        public string Content
        {
            get { return textBox.Text; }
        }
        public void Copy() { textBox.Copy(); }
        public void Paste() { textBox.Paste(); }

        Style DefaultStyle = new TextStyle(Brushes.Black, null, FontStyle.Regular);
        Style KeywordStyle = new TextStyle(Brushes.Blue, null, FontStyle.Italic);
        Style CommentStyle = new TextStyle(Brushes.Green, null, FontStyle.Italic);
        Style ConstantStyle = new TextStyle(Brushes.Firebrick, null, FontStyle.Regular);

        SaveFileDialog saveDialog = new SaveFileDialog();

        public FileEditorPage()
        { 
            UseVisualStyleBackColor = true;

            textBox = new FastColoredTextBox();
            textBox.AllowDrop = true;
            textBox.KeyDown += _KeyDown;
            textBox.TextChanged += _TextChanged;
            textBox.Dock = DockStyle.Fill;

            autocomplete = new AutocompleteMenu(textBox);
            autocomplete.MinFragmentLength = 1;
            autocomplete.Items.MaximumSize = new System.Drawing.Size(200, 300);
            autocomplete.Items.Width = 400;
            // autocomplete.Items.SetAutocompleteItems(AmandaObj.GetIdentifiers()); // TODO: FIX K

            saveDialog.Filter = "Amanda File|*.ama";


            Controls.Add(textBox); 
        }

        public void Save()
        {
            if (!isEdited) return;

            if (fileLocation == String.Empty)
            {
                SaveAs();
            }
            else
            {
                File.WriteAllText(fileLocation, textBox.Text);
                isEdited = false;
            }
        }

        public void SaveAs()
        {
            DialogResult result = saveDialog.ShowDialog();

            if (result == DialogResult.OK)
            {
                try
                {
                    File.WriteAllText(saveDialog.FileName, textBox.Text);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Unable to write to file: {0}", ex.Message);
                }

                fileLocation = saveDialog.FileName;
                isEdited = false;
            }
        }

        public void AskToSaveFile()
        {
            if (!isEdited) return;

            String file = (fileLocation == String.Empty) ? "Untitled" : fileLocation;

            DialogResult result = MessageBox.Show("Save File " + file + "?",
                                                    "Save File",
                                                    MessageBoxButtons.YesNo);

            if (result == DialogResult.Yes)
            {
                Save();
            }
        }


        private void _KeyDown(object sender, KeyEventArgs e)
        {
            string allowedChars = "([{}]);,. ";
            int currentLine = textBox.Selection.Bounds.iStartLine;
            string textTillCursor = textBox.GetLineText(currentLine).Substring(0, textBox.Selection.Bounds.iStartChar);
            //string textAfterCursor = tbEditor.GetLineText(currentLine);
            char charBeforeCursor = textBox.Selection.CharBeforeStart;
            char charAfterCursor = textBox.Selection.CharAfterStart;
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

        private void _TextChanged(object sender, TextChangedEventArgs e)
        {
            // Set isEdited to true so the we can ask the user to save the file when he closes the program.
            //
            if (!isEdited) isEdited = true;

            e.ChangedRange.ClearStyle(KeywordStyle, CommentStyle, ConstantStyle);

            e.ChangedRange.SetStyle(KeywordStyle, @"\b(where|if|else|True|False|otherwise)\b");
            e.ChangedRange.SetStyle(CommentStyle, @"\|\|.*");                          //comments ||...
            e.ChangedRange.SetStyle(ConstantStyle, @"(\B-)?[0-9]+\b");                  //numbers 123, -123, to be removed?
            e.ChangedRange.SetStyle(ConstantStyle, @"""[^""\\]*(?:\\.[^""\\]*)*""?");   //string "", source: stackoverflow
            e.ChangedRange.SetStyle(ConstantStyle, @"'[^'\\]*(?:\\.[^'\\]*)*'?");       //char ''
        }

        private void tbEditor_AutoIndentNeeded(object sender, AutoIndentEventArgs e)
        {
            Match isIfRegex = Regex.Match(e.LineText.Trim(), ",* if");
            Match isOtherwiseRegex = Regex.Match(e.LineText.Trim(), ",* otherwise");
            Match isWhereRegex = Regex.Match(e.LineText, "where");
            int currentIndent = e.LineText.TakeWhile(q => q == ' ').Count();    //shouldn't be too inefficient
            int at = e.LineText.IndexOf('=');

            AmandaTagParser tagParser = new AmandaTagParser();
            tagParser.parse(textBox.Text);

            /*
             *              All these todo's might not be neccessary, it's pretty good right now
             */
            if (isWhereRegex.Success)
            {
                e.ShiftNextLines = e.TabLength;
            }

            else if (isIfRegex.Success == true)
            {
                e.ShiftNextLines = at - currentIndent;
            }

            else if (e.LineText.Trim() == "" || e.PrevLineText.Trim().Count() == 0 || isOtherwiseRegex.Success == true)
            {
                AmandaTag tag = tagParser.GetTag(e.iLine);
                if (tag != null)
                {
                    e.ShiftNextLines = tag.BeginLocation.X - at;
                }
                else
                {
                    e.ShiftNextLines = -e.TabLength;
                }
                return;
            }
        }
    }
}
