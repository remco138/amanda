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
        private static Form1 window;
        private static Amanda amanda;

        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            amanda = new Amanda("test.ama");//Works
            amanda.Interpret("[x | x < [0..999]]");//Works
            amanda.Interpret("[x | x asa< [0..999]]");
            amanda.Interpret("WAT");
            
            List<string> functions = amanda.GetIdentifiers(); //Works
            List<string> zip = amanda.GetIdentifiers("zip"); //Works

            zip.ForEach(q => Console.WriteLine(q));

            amanda.Load("amanda = \"leuk\"\n" + //Works
                        "ik = [0..999]\n" +
                        "x = [x | x <- [0..900000]]");
            amanda.GetOutput();
            
            /*
            Thread MessageCollecter = new Thread(
                (output) =>
                {
                    string temp = "";
                    amanda.GetOutput();
                    Console.Write(temp);
                    Thread.Sleep(200);
                });
             */

            //amanda.Interpret("[x | x <- [0..100000]; False]");

            Thread gui = new Thread(StartInterface);
            gui.Start();

            for (; ; )
            {
                foreach (string t in amanda.GetOutput())
                {
                    window.OutputTextbox.Invoke(new Action(() => window.OutputTextbox.Text += t)); //hack, C# likes to babysit other threads
                }
                Thread.Sleep(50);
            }
        }

        private static void StartInterface()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            
            window = new Form1();
            window.RunCallback = new Form1.RunCallbackD(amanda.Interpret);
            window.LoadCallback = new Form1.LoadCallbackD(amanda.Load);


			Application.Run(window);

        }

    }
}
