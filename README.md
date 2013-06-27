amanda
==============

Amanda is a functional programming language; very haskell-like.

dependencies
======
- gcc
- mono
- Probably mono-devel

How-to Build & run (linux):
======

- cd to the root
- make
- cd Debug
- mono AmandaInterface.exe

Please note that auto-complete isn't functional with the mono runtime (even on windows, when run with mono.exe).
I'm not sure whether this is caused by a mono bug or code in the FastColoredTextBox library.

Windows
======

- open the solution (.sln) file with Visual Studio 2012 (or 2010) 
- hit "run" (F5)
