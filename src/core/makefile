exename = ama

objfiles = amcheck.o amcon.o amerror.o ameval.o amio.o amlex.o amlib.o ammem.o ammodify.o amparse.o ampatter.o amprint.o amstack.o amsyslib.o amtable.o

compiler = gcc

.c.o:
	$(compiler) -c -g -DAMA_READLINE -O $*.c

exe: $(objfiles)
	$(compiler) -O $(objfiles) -lm -g -lreadline -o $(exename)

clean:
	rm *.o
