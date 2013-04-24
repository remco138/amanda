using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Runtime.InteropServices;

namespace AmandaInterface
{
    static class Program
    {

        // ExecuteCommand() staat in amcon.c!

		[DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern IntPtr ExecuteCommand(char[] input);
        [DllImport("AmandaCore.dll", EntryPoint="Load", CallingConvention = CallingConvention.Cdecl)]
        public static extern bool Loada(char[] file);
       [DllImport("AmandaCore.dll")]
        public static extern void CheckIO();
       [DllImport("AmandaCore.dll")]
        public static extern void CreateInterpreter();
       [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern void Interpret(char[] expr);
 

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
			string test = Marshal.PtrToStringAnsi(ExecuteCommand("[1]".ToCharArray()));
			Console.WriteLine(test);
			Form1 window = new Form1();
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
			Application.Run(window);



			window.output(test);

		/*	
            String amandaInput = "testfunctie a b = a + b";
            CreateInterpreter();
            Interpret("[1]".ToCharArray());
           IntPtr ptr = ExecuteCommand(amandaInput.ToCharArray());
           CheckIO();
            Loada(amandaInput.ToCharArray());
            
           string testAnsi = Marshal.PtrToStringAnsi(ptr);
       */
        }
    }
}
