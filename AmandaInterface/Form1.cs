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

namespace AmandaInterface
{
    public partial class Form1 : Form
    {
        public delegate bool RunCallbackD(string expression);
        public RunCallbackD RunCallback;
        public delegate bool LoadCallbackD(string content);
        public LoadCallbackD LoadCallback;

		public void output(string t)
		{
			RunTextbox.Text = t;
			System.Console.WriteLine("worked");
		}

        public Form1()
        {
            InitializeComponent();
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


    }
}
