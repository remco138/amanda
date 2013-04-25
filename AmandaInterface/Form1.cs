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

		public void output(string t)
		{
			showTextBox.Text = t;
			System.Console.WriteLine("worked");
		}

        public Form1()
        {
            InitializeComponent();

            //
            //
        }

    }
}
