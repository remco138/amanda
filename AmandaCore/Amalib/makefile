exename = ama

objfiles = amcheck.o amcon.o amerror.o ameval.o amio.o amlex.o amlib.o ammem.o ammodify.o amparse.o ampatter.o amprint.o amstack.o amsyslib.o amtable.o

compiler = gcc

.c.o:
	$(compiler) -c -g -O0 -Wl,-output-def,testdll.def $*.c

exe: $(objfiles)
	$(compiler) -O0 -shared $(objfiles) -g -lm  -Wl,--output-def,testdll.def -o $(exename)

clean:
	rm *.o
