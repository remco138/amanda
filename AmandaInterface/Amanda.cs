﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Runtime.InteropServices;
using System.IO;
using System.ComponentModel;

namespace AmandaInterface
{
    [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
    public delegate void OutputCallback(string output);

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
        public static extern void Interpret([MarshalAs(UnmanagedType.LPStr)] String expr);

        [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern IntPtr gethashtable([MarshalAs(UnmanagedType.LPStr)] string search); //char**

        [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern IntPtr getMessages();                                                //char**

        [DllImport("AmandaCore.dll", CallingConvention = CallingConvention.Cdecl)]
        public static extern bool SetOutputCallback([MarshalAs(UnmanagedType.FunctionPtr)] OutputCallback callbackPointer);
    }


    class Amanda
    {
        public Amanda(string autorun = null)
        {
            AmandaHook.InitOptions(true, ""); //empty char will result in amanda loading up amanda.ini
            AmandaHook.CreateInterpreter();
            if (autorun != null) AmandaHook.Load(autorun);
        }

        public List<String> GetOutput()
        {
            return CStringPointerToList(AmandaHook.getMessages());
        }

        //Run a block of code (eg: definitions)
        public bool Load(string code)
        {
            File.WriteAllText(".temp.ama", code);
            bool success = AmandaHook.Load(".temp.ama");
            File.Delete(".temp.ama");

            return success;
        }

        //Run a single expression
        public bool Interpret(string expression)
        {
            AmandaHook.Interpret(expression);
            return true;
        }

        //C char** to C# string is nasty
        public List<string> GetIdentifiers(string search = "")
        {
            return CStringPointerToList(AmandaHook.gethashtable(search));
        }


        private List<string> CStringPointerToList(IntPtr CStringPtr)
        {
            List<string> functionList = new List<string>();
            IntPtr ptr = CStringPtr;
            if ((int)CStringPtr == 0) return new List<string>();

            for (int i = 0;; i++)
            {
                IntPtr strPtr = (IntPtr)Marshal.PtrToStructure(ptr, typeof(IntPtr));
                functionList.Add(Marshal.PtrToStringAnsi(strPtr));
                ptr = new IntPtr(ptr.ToInt64() + IntPtr.Size);
                if (functionList[i] == "\n\n\n") //pretty lame but who cares
                {
                    functionList.RemoveAt(i);
                    break;
                }
            }

            return functionList;
        }
    }
}
