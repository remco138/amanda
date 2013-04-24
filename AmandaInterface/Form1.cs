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
        // ExecuteCommand() staat in amcon.c!

        [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern IntPtr ExecuteCommand([param: MarshalAs(UnmanagedType.AnsiBStr)] String input);
        


        public Form1()
        {
            InitializeComponent();

            String amandaInput = "testfunctie a b = a + b";

            IntPtr ptr = ExecuteCommand(amandaInput);
            string testAnsi = Marshal.PtrToStringAnsi(ptr);
       
            showTextBox.Text = testAnsi;
        }
    }
}
