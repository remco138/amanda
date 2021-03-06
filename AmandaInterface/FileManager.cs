﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FastColoredTextBoxNS;
using System.Windows.Forms;
using System.Drawing;
using System.IO;
using System.Text.RegularExpressions;
using System.Drawing.Drawing2D;
using System.Runtime.InteropServices;
using System.Drawing.Imaging;

namespace AmandaInterface
{
    [System.ComponentModel.DesignerCategory("Code")]
    class FileManagerTabControl : TabControl
    {
        OpenFileDialog openDialog = new OpenFileDialog();

        public FileManagerTabControl()
        {
            this.ImageList = new ImageList();
            this.ImageList.Images.Add(Properties.Resources.tab_close);
            this.ImageList.Images.Add(Properties.Resources.tab_close___Copy);
           
            this.SetStyle(ControlStyles.UserPaint | ControlStyles.AllPaintingInWmPaint | ControlStyles.DoubleBuffer, true);
            this.MouseMove += _MouseMove;
            this.MouseLeave += _MouseLeave;
            
            this.AllowDrop = true;
            this.DragEnter += _DragEnter;
            this.DragDrop += _DragDrop;

            openDialog.Filter = "Amanda File|*.ama";
            
            this.MouseClick += _MouseClick;

            AddNewFile();   
        }


        #region events

        void _MouseClick(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Middle)
            {
                // Find out which tab has been clicked & close that file.
                //
                for (int i = 0; i < TabCount; i++)
                {
                    if (GetTabRect(i).Contains(e.X, e.Y))
                    {
                        CloseFile((FileEditorTab)TabPages[i]);
                    }
                }
            }

            if (e.Button == MouseButtons.Left)
            {
                // Left click already sets the target tab to selected so no need to do the stuff we do for the middle mouse button (looping through all the tabs) 
                //
                int tabIndex = TabPages.IndexOf(SelectedTab);
                Rectangle r = ConvertRectangleBounds_CloseButton(GetTabRect(tabIndex));

                if (r.Contains(e.Location))
                {
                    CloseFile(this.SelectedFileTab);
                    SelectedIndex = tabIndex;
                }
            }      
        }

        void _MouseMove(object sender, MouseEventArgs e)
        {
            Rectangle r;

            for (int i = 0; i < TabCount; i++)
            {
                r = ConvertRectangleBounds_CloseButton(GetTabRect(i));

                if (r.Contains(e.Location))
                {
                    // Cursor is on the close button
                    TabPages[i].ImageIndex = 1;                  
                }
                else
                {
                    // Cursor is NOT on the close button
                    TabPages[i].ImageIndex = 0;
                }
            }
        }

        void _MouseLeave(object sender, EventArgs e)
        {
            for (int i = 0; i < TabCount; i++)
            {
                TabPages[i].ImageIndex = 0;
            }
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

        /// <summary>
        /// Converts a Tab Rectangle to the bounding rectangle of the close button
        /// </summary>
        Rectangle ConvertRectangleBounds_CloseButton(Rectangle r)
        {
            return r = new Rectangle(r.X + r.Width - 13, r.Y + 7, 7, 9);
        }
        Rectangle ConvertRectangleBounds_CloseButton(int tabIndex)
        {
            return this.ConvertRectangleBounds_CloseButton(GetTabRect(tabIndex));
        }

        #endregion


        public void UpdateAutocompleteIdentifiers(List<string> StockIdentifiers)
        {
            foreach(FileEditorTab tab in TabPages)
            {
                 tab.UpdateAutocompleteIdentifiers();
            }
        }

        protected override void OnPaintBackground(PaintEventArgs pevent)
        {
            // Draw the whole Tab control
            //
            Graphics g = pevent.Graphics;
            Pen borderGray = new Pen(Color.FromArgb(172, 172, 172));

            // Draw the Tab Control background stuff
            //
            g.FillRectangle(Brushes.White, 0, 0, this.Size.Width, this.Size.Height);
            g.FillRectangle(SystemBrushes.Control, 0, 0, this.Size.Width, 25);
            g.DrawLine(borderGray, new Point(0, 0), new Point(0, this.Height));
            g.DrawLine(borderGray, new Point(5, 25), new Point(this.Width, 25));

            // Loop through all the tabs & paint them
            //
            TabPage currTab;
            Rectangle TabBoundary, origTabBoundary;
            RectangleF TabTextBoundary;
            StringFormat format = new StringFormat();
            Bitmap image;

            for (int i = 0; i < TabPages.Count; i++)
            {
                currTab = TabPages[i];
                TabBoundary = this.GetTabRect(i);
                origTabBoundary = TabBoundary;
                TabTextBoundary = new RectangleF(TabBoundary.X + 12, TabBoundary.Y + 3, TabBoundary.Width, TabBoundary.Height);

                if (currTab == SelectedTab)
                {
                    // Fill Background
                    TabBoundary = new Rectangle(TabBoundary.X - 2, TabBoundary.Y - 2, TabBoundary.Width + 2, TabBoundary.Height + 4);
                    g.FillRectangle(new SolidBrush(Color.White), TabBoundary);

                    // Draw Border
                    g.DrawLine(borderGray, TabBoundary.Location, new Point(TabBoundary.X, TabBoundary.Height));
                    g.DrawLine(borderGray, TabBoundary.Location, new Point(TabBoundary.Right, TabBoundary.Y));
                    g.DrawLine(borderGray, new Point(TabBoundary.Right, TabBoundary.Y), new Point(TabBoundary.Right, TabBoundary.Bottom));
                }
                else
                {
                    // Fill Background
                    TabBoundary = new Rectangle(TabBoundary.X + 2, TabBoundary.Y + 2, TabBoundary.Width, TabBoundary.Height);
                    g.FillRectangle(new LinearGradientBrush(TabBoundary, SystemColors.Control, SystemColors.ControlLight, 90.0f), TabBoundary);

                    // Draw Border
                    TabBoundary = new Rectangle(TabBoundary.X - 1, TabBoundary.Y - 1, TabBoundary.Width - 1, TabBoundary.Height);
                    g.DrawRectangle(borderGray, TabBoundary);
                }

                // Draw Tab Title
                //
                Font f = new System.Drawing.Font("Segoe UI", 10.0f);
                g.DrawString(currTab.Text, f, new SolidBrush(Color.Black), TabTextBoundary, format);

                // Draw Tab Close Button 
                //
                if (ImageList.Images.Count > 0)
                {
                    if (currTab.ImageIndex == -1) currTab.ImageIndex = 0;// Sometimes ImageIndex is not set yet
                    // Zet de kleur wit om naar transparency
                    //
                    image = (Bitmap)ImageList.Images[currTab.ImageIndex];
                    image.MakeTransparent();
                    g.DrawImage(image, origTabBoundary.Right - 15, origTabBoundary.Top + 5, image.Width, image.Height);
                }

            }
        }

        public void AddNewFile()
        {
            // Add a new FileEditorTab (which has a FastColoredTextbox inside it)
            FileEditorTab newPage = new FileEditorTab();     
            newPage.Padding = new System.Windows.Forms.Padding(3);
            
            this.TabPages.Add(newPage);
            SelectedIndex = TabCount - 1;
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
            foreach (FileEditorTab page in TabPages)
            {
                if (page.FileLocation == pathToFile)
                    return;
            }


            // Read file & Open a new Tab
            //
            try
            {
                String txt = File.ReadAllText(pathToFile);

                FileEditorTab newPage = new FileEditorTab();
                newPage.FileLocation = pathToFile;
                newPage.Content = txt;

                this.TabPages.Add(newPage);
                SelectedIndex = TabCount - 1;
            }
            catch (IOException ex)
            {
                Console.WriteLine("Unable to open file: {0}", ex.Message);
            }
        }
        public void CloseSelectedTab()
        {
            var index = SelectedIndex;
            CloseFile(SelectedFileTab);

            if (index >= TabCount - 1)
                SelectedIndex = TabCount - 1;
            else
                SelectedIndex = index;
        }
        public void CloseFile(FileEditorTab fileTab)
        {
            // TODO: Make AskToSaveFile so that it also has a CANCEL button
            //
            fileTab.AskToSaveFile();                
            TabPages.Remove(fileTab);

            if (TabCount == 0)
            {
                this.AddNewFile();
            }
        }

        /// <summary>
        /// The original SelectedTab returns a TabPage. This one returns a FileEditorPage
        /// </summary>
        public FileEditorTab SelectedFileTab
        {
            get { return ((FileEditorTab)this.SelectedTab); }
        }
        public FastColoredTextBox SelectedTabTextBox
        {
            get { return ((FileEditorTab)this.SelectedFileTab).textBox; }
        }


    }


    class FileEditorTab : TabPage
    {
        private string fileLocation = "";
        public string FileLocation
        {
            get { return fileLocation; }
            set
            {
                this.Text = Path.GetFileName(value);
                fileLocation = value;
            }
        }
        private bool isEdited;
        public bool IsEdited
        {
            get { return isEdited; }
            set
            {
                if (value)
                    Text += "*";
                else
                    Text.Replace("*", String.Empty);

                this.isEdited = value;
            }
        }

        public FastColoredTextBox textBox;
        AutocompleteMenu autocomplete;
        static AmandaTagParser amandaTagParser;
        public string Content
        {
            get { return textBox.Text; }
            set { textBox.Text = value; }
        }
        public void Copy() { textBox.Copy(); }
        public void Paste() { textBox.Paste(); }

        static Style DefaultStyle = new TextStyle(Brushes.Black, null, FontStyle.Regular);
        static Style KeywordStyle = new TextStyle(Brushes.Blue, null, FontStyle.Italic);
        static Style CommentStyle = new TextStyle(Brushes.Green, null, FontStyle.Italic);
        static Style ConstantStyle = new TextStyle(Brushes.Firebrick, null, FontStyle.Regular);
        static Style FunctionStyle =  new TextStyle(Brushes.DarkCyan, null, FontStyle.Regular);

        static SaveFileDialog saveDialog = new SaveFileDialog();

        public FileEditorTab() : this("") { }
        public FileEditorTab(string content)
        {
            Text = "Untitled";
           
            UseVisualStyleBackColor = true;
            textBox = new FastColoredTextBox();
            textBox.AllowDrop = true;
            textBox.KeyDown += _KeyDown;
            textBox.TextChanged += _TextChanged;
            textBox.AutoIndentNeeded += _AutoIndentNeeded;
            textBox.Dock = DockStyle.Fill;
            textBox.Text = content;

            autocomplete = new AutocompleteMenu(textBox);
            autocomplete.MinFragmentLength = 1;
            autocomplete.Items.MaximumSize = new System.Drawing.Size(200, 300);
            autocomplete.Items.Width = 400;

            amandaTagParser = new AmandaTagParser();

            saveDialog.Filter = "Amanda File|*.ama";

            Controls.Add(textBox); 
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
            UpdateAutocompleteIdentifiers();
        }


        private void _TextChanged(object sender, TextChangedEventArgs e)
        {
            AmandaTagParser parser = new AmandaTagParser();
            parser.Parse(textBox.Text);

            // Set isEdited to true so the we can ask the user to save the file when he closes the program.
            //
            if (!IsEdited) IsEdited = true;

            e.ChangedRange.ClearStyle(KeywordStyle, CommentStyle, ConstantStyle, FunctionStyle);

            e.ChangedRange.SetStyle(KeywordStyle, @"\b(where|if|else|True|False|otherwise)\b");
            e.ChangedRange.SetStyle(CommentStyle, @"\|\|.*");                          //comments ||...
            e.ChangedRange.SetStyle(ConstantStyle, @"\b(\B-)?[0-9]+\b");                  //numbers 123, -123, to be removed?
            e.ChangedRange.SetStyle(ConstantStyle, @"""[^""\\]*(?:\\.[^""\\]*)*""?");   //string "", source: stackoverflow
            e.ChangedRange.SetStyle(ConstantStyle, @"'[^'\\]*(?:\\.[^'\\]*)*'?");       //char ''

            foreach(string functionName in parser.AmandaTags.Select(q => q.Name))
            {
                e.ChangedRange.SetStyle(FunctionStyle, @"\b" + functionName + @"\b" );
            }
        }

        private void _AutoIndentNeeded(object sender, AutoIndentEventArgs e)
        {
            Match isOtherwiseRegex = Regex.Match(e.LineText.Trim(), ",* otherwise");
            int currentIndent = e.LineText.TakeWhile(q => q == ' ').Count();    //shouldn't be too inefficient
            int at = e.LineText.IndexOf('=');

            AmandaTagParser tagParser = new AmandaTagParser();
            tagParser.Parse(textBox.Text);

            /*
             *              All these todo's might not be neccessary, it's pretty good right now
             */
            //If the line contains a where, we want the next lines to be indented by a tab
            if (Regex.IsMatch(e.LineText, "where"))
            {
                e.ShiftNextLines = e.TabLength;
            }

            //if the line is a condition, we want to find the '=' and indent to there, that's how youre supposed to do conditions in amanda
            else if (Regex.IsMatch(e.LineText.Trim(), ",* if"))
            {
                e.ShiftNextLines = at - currentIndent;
            }
                
            //If line is empty | prev line is empty | contains otherwise (which indicates the end of an if/else statement)
            else if (e.LineText.Trim() == "" || e.PrevLineText.Trim().Count() == 0 || Regex.IsMatch(e.LineText.Trim(), ",* otherwise") == true)
            {
                //Get the tag based on our current line, if we are at line 3, and there is a big function going from line 1-5, it will return that.
                AmandaTag tag = tagParser.GetTag(e.iLine);
                if (tag != null)
                {
                    e.ShiftNextLines = tag.BeginLocation.X - at;
                }

                //No function found? No problem, resort to a tab
                else
                {
                    e.ShiftNextLines = -e.TabLength;
                }
            }
        }


        public void UpdateAutocompleteIdentifiers()
        {
            //of toch wel? :D, word per keydown opnieuw geparsed, zou niet langzaam moeten zijn omdat string compare snel is/kan zijn

            Amanda amandaObj = Amanda.GetInstance();
            amandaTagParser.Parse(textBox.Text);

            ICollection<string> tags = new List<string>();
            amandaTagParser.AmandaTags.ForEach(q => tags.Add(q.Name));

            ICollection<string> items = tags.Concat(amandaObj.GetIdentifiers()).ToList();//amandaTagParser.Concat(Identifiers).ToList();

            autocomplete.Items.SetAutocompleteItems(items);
        }

        public void Save()
        {
            if (!IsEdited) return;

            if (FileLocation == String.Empty)
            {
                SaveAs();
            }
            else
            {
                File.WriteAllText(FileLocation, textBox.Text);
                IsEdited = false;
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

                FileLocation = saveDialog.FileName;
                IsEdited = false;
            }
        }

        public void AskToSaveFile()
        {
            if (!IsEdited) return;

            String file = (FileLocation == "") ? "Untitled" : FileLocation;

            DialogResult result = MessageBox.Show("Save File " + file + "?",
                                                    "Save File",
                                                    MessageBoxButtons.YesNo);

            if (result == DialogResult.Yes)
            {
                Save();
            }
        }
    }
}
