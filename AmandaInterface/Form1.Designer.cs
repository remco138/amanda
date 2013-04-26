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
            this.RunTextbox = new System.Windows.Forms.TextBox();
            this.LoadTextbox = new System.Windows.Forms.RichTextBox();
            this.OutputTextbox = new System.Windows.Forms.RichTextBox();
            this.RunButton = new System.Windows.Forms.Button();
            this.LoadButton = new System.Windows.Forms.Button();
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
            // LoadTextbox
            // 
            this.LoadTextbox.Location = new System.Drawing.Point(39, 58);
            this.LoadTextbox.Name = "LoadTextbox";
            this.LoadTextbox.Size = new System.Drawing.Size(374, 374);
            this.LoadTextbox.TabIndex = 1;
            this.LoadTextbox.Text = "getEven = [x | x <- [0..100]; x % 2 = 0]";
            this.LoadTextbox.TextChanged += new System.EventHandler(this.LoadTextbox_TextChanged);
            // 
            // OutputTextbox
            // 
            this.OutputTextbox.Location = new System.Drawing.Point(491, 28);
            this.OutputTextbox.Name = "OutputTextbox";
            this.OutputTextbox.Size = new System.Drawing.Size(263, 404);
            this.OutputTextbox.TabIndex = 2;
            this.OutputTextbox.Text = "";
            this.OutputTextbox.TextChanged += new System.EventHandler(this.OutputTextbox_TextChanged);
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
            // Form1
            // 
            this.ClientSize = new System.Drawing.Size(813, 444);
            this.Controls.Add(this.LoadButton);
            this.Controls.Add(this.RunButton);
            this.Controls.Add(this.OutputTextbox);
            this.Controls.Add(this.LoadTextbox);
            this.Controls.Add(this.RunTextbox);
            this.Name = "Form1";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.TextBox RunTextbox;
        public System.Windows.Forms.RichTextBox LoadTextbox;
        public System.Windows.Forms.RichTextBox OutputTextbox;
        public System.Windows.Forms.Button RunButton;
        public System.Windows.Forms.Button LoadButton;

    }
}

