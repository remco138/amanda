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
		/*
       [DllImport("AmandaCore.dll")]
        public static extern IntPtr ExecuteCommand(char[] input);
        [DllImport("AmandaCore.dll", EntryPoint="Load", CallingConvention = CallingConvention.Cdecl)]
        public static extern bool Loada(char[] file);
       [DllImport("AmandaCore.dll")]
        public static extern void CheckIO();
       [DllImport("AmandaCore.dll")]
        public static extern void CreateInterpreter();
       [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern void Interpret(char[] expr);
 


        public Form1()
        {
            InitializeComponent();

            String amandaInput = "testfunctie a b = a + b";
            CreateInterpreter();
            Interpret("[1]".ToCharArray());
           IntPtr ptr = ExecuteCommand(amandaInput.ToCharArray());
           CheckIO();
            Loada(amandaInput.ToCharArray());
            
           string testAnsi = Marshal.PtrToStringAnsi(ptr);
       
            showTextBox.Text = testAnsi;
        }
		*/
    }
}
