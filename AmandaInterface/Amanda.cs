using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Runtime.InteropServices;

namespace AmandaInterface
{
    class AmandaHook    //C-style chars MOETEN gemarchald worden, anders krijg je rare chars na de string (charArray is niet \0 terminated?)
    {
		[DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern IntPtr ExecuteCommand(char[] input);

        [DllImport("AmandaCore.dll",CharSet = CharSet.Ansi, CallingConvention = CallingConvention.Cdecl)]
        public static extern bool Load([MarshalAs(UnmanagedType.LPStr)] string file);

       [DllImport("AmandaCore.dll")]
        public static extern void CheckIO();

       [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern void InitOptions(bool console, [MarshalAs(UnmanagedType.LPStr)] string path);

       [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern void CreateInterpreter();

       [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern void Interpret(char[] expr);

       [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern IntPtr[] gethashtable([MarshalAs(UnmanagedType.LPStr)] string search);
    }


    class Amanda
    {
        public Amanda(string autorun = null)
        {
            AmandaHook.InitOptions(false, ""); //empty char will result in amanda loading up amanda.ini
            AmandaHook.CreateInterpreter();
            if (autorun != null) AmandaHook.Load(autorun);
        }

        public bool Interpret(string expression)
        {
            AmandaHook.Interpret(expression.ToCharArray());
            return true;
        }

        public List<string> GetIdentifiers(string search = "")
        {
            List<string> functionList = new List<string>();
            IntPtr[] funcList = AmandaHook.gethashtable(search);

            foreach (IntPtr t in funcList)
            {
                functionList.Add(Marshal.PtrToStringAnsi(t));
            }
            return functionList;
        }

    }
}
