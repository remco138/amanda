amanda
==============

Amanda is a functional programming language; very haskell-like.

dependencies
======
gcc
mono
Probably mono-devel

How-to Build & run (linux):
======

1) cd to the root
2) make
3) cd Debug
4) mono AmandaInterface.exe

Please note that autocomplete somehow isn't functional with the mono runtime (compiler & OS irrelevant).
Its not sure whether this is caused by a mono bug or code in the FastColoredTextBox library.

Windows
======

1) open the solution (.sln) file with Visual Studio 2012 (or 2010) 
2) hit "run" (F5)
