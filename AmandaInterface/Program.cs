using System;
using System.IO;
using System.Reflection;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Threading;
using System.IO.Pipes;
using System.Text;
using FastColoredTextBoxNS;

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
                        "x = [x | x <- [0..900]]");
            

            Thread gui = new Thread(StartInterface);
            gui.Start();
            Thread ama = new Thread(StartAmanda);
            ama.Start();

        }


        private static void StartAmanda()
        {
            StringBuilder output = new StringBuilder("Welcome to amanda!", 10000);
            for (; ; )
            {
                Thread.Sleep(200);
                foreach (string t in amanda.GetOutput())
                {
                    output.Append(t);
                }
                if (output.Length > 0)
                {
                    ////window.OutputTextbox.Invoke(new Action(() => window.OutputTextbox.Text += output)); //hack, C# likes to babysit other threads
                    output.Clear();
                }
            }
        }

        private static void StartInterface()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            
            window = new Form1();
            window.RunCallback = new Form1.RunCallbackD(amanda.Interpret);
            window.LoadCallback = new Form1.LoadCallbackD(amanda.Load);
            window.AddAutocompleteEntries(amanda.GetIdentifiers());

			Application.Run(window);

        }

    }
}
