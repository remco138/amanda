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

        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            //stderr should be stdout... whatever, doesnt work
            //MemoryStream stderr= new MemoryStream();
            //StreamWriter stderrHook = new StreamWriter(stderr);
            //StreamReader stderrReader = new StreamReader(stderr);
            //Console.SetOut(stderrHook);


            Amanda amanda = new Amanda("test.ama");//Works
            amanda.Interpret("[x | x < [0..999]]");//Works
            amanda.Interpret("[x | x asa< [0..999]]");
            amanda.Interpret("WAT");
            
            List<string> functions = amanda.GetIdentifiers(); //Works
            List<string> zip = amanda.GetIdentifiers("zip"); //Works

            zip.ForEach(q => Console.WriteLine(q));

            amanda.Load("amanda = \"leuk\"\n" + //Works
                        "ik = [0..999]\n" +
                        "x = [x | x <- [0..1000]]");


            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
			Form1 window = new Form1();
			Application.Run(window);

        }
    }
}
