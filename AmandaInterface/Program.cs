using System;
using System.IO;
using System.Reflection;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Threading;
using System.IO.Pipes;


namespace AmandaInterface
{
    static class Program
    {

        // ExecuteCommand() staat in amcon.c!




        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            //stderr should be stdout... whatever
            MemoryStream stderr= new MemoryStream();
            StreamWriter stderrHook = new StreamWriter(stderr);
            StreamReader stderrReader = new StreamReader(stderr);
            Console.SetOut(stderrHook);


            Amanda amanda = new Amanda("test.ama");
            amanda.Interpret("[x | x < [0..999]]");
            amanda.Interpret("[x | x asa< [0..999]]");
            amanda.Interpret("WAT");

            string errors = stderrReader.ReadToEnd();


            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
			Form1 window = new Form1();
			Application.Run(window);

        }
    }
}
