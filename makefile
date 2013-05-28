exename = libAmandaCore.dll

VPATH = AmandaCore/Amalib/ AmandaInterface/

objfiles = amcheck.o amcon.o amerror.o ameval.o amio.o amlex.o amlib.o ammem.o ammodify.o amparse.o ampatter.o amprint.o amstack.o amsyslib.o amtable.o

compiler = gcc

.c.o:
	$(compiler) -c -shared -fPIC -g -O0 $?

all: $(objfiles)
	$(compiler) -O0 -shared -fPIC $(objfiles) -g -lm  -o Debug/$(exename)
	xbuild AmandaInterface/AmandaInterface.csproj

clean:
	rm *.o ama.* ama
