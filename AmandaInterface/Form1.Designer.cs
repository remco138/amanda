namespace AmandaInterface
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.RunTextbox = new System.Windows.Forms.TextBox();
            this.RunButton = new System.Windows.Forms.Button();
            this.LoadButton = new System.Windows.Forms.Button();
            this.LoadTextbox = new FastColoredTextBoxNS.FastColoredTextBox();
            this.OutputTextbox = new FastColoredTextBoxNS.FastColoredTextBox();
            ((System.ComponentModel.ISupportInitialize)(this.LoadTextbox)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.OutputTextbox)).BeginInit();
            this.SuspendLayout();
            // 
            // RunTextbox
            // 
            this.RunTextbox.Location = new System.Drawing.Point(39, 32);
            this.RunTextbox.Name = "RunTextbox";
            this.RunTextbox.Size = new System.Drawing.Size(400, 20);
            this.RunTextbox.TabIndex = 0;
            this.RunTextbox.Text = "getEven";
            // 
            // RunButton
            // 
            this.RunButton.Location = new System.Drawing.Point(445, 26);
            this.RunButton.Name = "RunButton";
            this.RunButton.Size = new System.Drawing.Size(40, 23);
            this.RunButton.TabIndex = 3;
            this.RunButton.Text = "Run";
            this.RunButton.UseVisualStyleBackColor = true;
            this.RunButton.Click += new System.EventHandler(this.RunButton_Click);
            // 
            // LoadButton
            // 
            this.LoadButton.Location = new System.Drawing.Point(419, 238);
            this.LoadButton.Name = "LoadButton";
            this.LoadButton.Size = new System.Drawing.Size(66, 54);
            this.LoadButton.TabIndex = 4;
            this.LoadButton.Text = "Load";
            this.LoadButton.UseVisualStyleBackColor = true;
            this.LoadButton.Click += new System.EventHandler(this.LoadButton_Click);
            // 
            // LoadTextbox
            // 
            this.LoadTextbox.AutoScrollMinSize = new System.Drawing.Size(347, 14);
            this.LoadTextbox.BackBrush = null;
            this.LoadTextbox.CharHeight = 14;
            this.LoadTextbox.CharWidth = 8;
            this.LoadTextbox.Cursor = System.Windows.Forms.Cursors.IBeam;
            this.LoadTextbox.DisabledColor = System.Drawing.Color.FromArgb(((int)(((byte)(100)))), ((int)(((byte)(180)))), ((int)(((byte)(180)))), ((int)(((byte)(180)))));
            this.LoadTextbox.IsReplaceMode = false;
            this.LoadTextbox.Location = new System.Drawing.Point(39, 58);
            this.LoadTextbox.Name = "LoadTextbox";
            this.LoadTextbox.Paddings = new System.Windows.Forms.Padding(0);
            this.LoadTextbox.SelectionColor = System.Drawing.Color.FromArgb(((int)(((byte)(60)))), ((int)(((byte)(0)))), ((int)(((byte)(0)))), ((int)(((byte)(255)))));
            this.LoadTextbox.Size = new System.Drawing.Size(374, 374);
            this.LoadTextbox.TabIndex = 5;
            this.LoadTextbox.Text = "getEven = [x | x <- [0..100]; x % 2 = 0]";
            this.LoadTextbox.Zoom = 100;
            this.LoadTextbox.TextChanged += new System.EventHandler<FastColoredTextBoxNS.TextChangedEventArgs>(this.LoadTextbox_TextChanged);
            this.LoadTextbox.AutoIndentNeeded += new System.EventHandler<FastColoredTextBoxNS.AutoIndentEventArgs>(this.LoadTextbox_AutoIndentNeeded);
            this.LoadTextbox.Load += new System.EventHandler(this.LoadTextbox_Load);
            this.LoadTextbox.KeyDown += new System.Windows.Forms.KeyEventHandler(this.LoadTextbox_KeyDown);
            // 
            // OutputTextbox
            // 
            this.OutputTextbox.AutoScrollMinSize = new System.Drawing.Size(51, 14);
            this.OutputTextbox.BackBrush = null;
            this.OutputTextbox.CharHeight = 14;
            this.OutputTextbox.CharWidth = 8;
            this.OutputTextbox.Cursor = System.Windows.Forms.Cursors.IBeam;
            this.OutputTextbox.DisabledColor = System.Drawing.Color.FromArgb(((int)(((byte)(100)))), ((int)(((byte)(180)))), ((int)(((byte)(180)))), ((int)(((byte)(180)))));
            this.OutputTextbox.IsReplaceMode = false;
            this.OutputTextbox.Location = new System.Drawing.Point(489, 58);
            this.OutputTextbox.Name = "OutputTextbox";
            this.OutputTextbox.Paddings = new System.Windows.Forms.Padding(0);
            this.OutputTextbox.SelectionColor = System.Drawing.Color.FromArgb(((int)(((byte)(60)))), ((int)(((byte)(0)))), ((int)(((byte)(0)))), ((int)(((byte)(255)))));
            this.OutputTextbox.Size = new System.Drawing.Size(312, 374);
            this.OutputTextbox.TabIndex = 6;
            this.OutputTextbox.Text = "Yo!";
            this.OutputTextbox.Zoom = 100;
            this.OutputTextbox.Load += new System.EventHandler(this.OutputTextbox_Load);
            // 
            // Form1
            // 
            this.ClientSize = new System.Drawing.Size(813, 444);
            this.Controls.Add(this.OutputTextbox);
            this.Controls.Add(this.LoadTextbox);
            this.Controls.Add(this.LoadButton);
            this.Controls.Add(this.RunButton);
            this.Controls.Add(this.RunTextbox);
            this.Name = "Form1";
            ((System.ComponentModel.ISupportInitialize)(this.LoadTextbox)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.OutputTextbox)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.TextBox RunTextbox;
        public System.Windows.Forms.Button RunButton;
        public System.Windows.Forms.Button LoadButton;
        public FastColoredTextBoxNS.FastColoredTextBox LoadTextbox;
        public FastColoredTextBoxNS.FastColoredTextBox OutputTextbox;

    }
}

