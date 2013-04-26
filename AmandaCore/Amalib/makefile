exename = ama

objfiles = amcheck.o amcon.o amerror.o ameval.o amio.o amlex.o amlib.o ammem.o ammodify.o amparse.o ampatter.o amprint.o amstack.o amsyslib.o amtable.o

compiler = gcc

.c.o:
	$(compiler) -c -g -O0 -fPIC $*.c

exe: $(objfiles)
	$(compiler) -O0 $(objfiles) -g -lm -fPIC -o $(exename)

clean:
	rm *.o
