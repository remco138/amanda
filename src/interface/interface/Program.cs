using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Runtime.InteropServices;

namespace @interface
{

    class test
    {

        [DllImport("amanda.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern Int32 Load(char[] filename); //bool is redifined as int in the C code
    }

    static class Program
    {


        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            test.Load("test.ama".ToCharArray());
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Form1());
        }



    }
}
