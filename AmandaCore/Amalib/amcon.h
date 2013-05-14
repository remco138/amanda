/**********************************************************************
  Author : Dick Bruin
  Date   : 16/10/98
  Version: 2.01
  File   : amcon.h

  Description:
  interface functions
*************************************************************************/

#ifndef AMCON_H
#define AMCON_H

#include "bool.h"

void InitOptions(bool console, char *path);
char *GetOption(char option[]);

void CreateInterpreter(void);
void Interpret(char expr[]);
bool Load(char filename[]);

extern bool interrupted;

bool InitRemote(void);
int CreateRemote(char s[]);
void DropRemote(int handle);
void PutRemote(int handle, char s[]);
bool CallRemote(int handle, char s[]);
bool GetRemote(int handle, char s[], int size);

/* to be defined: */
void WriteString(char string[]);
void CheckIO(void);
void GraphDisplay(char string[]);

/* P4P Callback Test */
typedef void (*WriteStringCallback)(char output[]);
WriteStringCallback writeStringCallback;

char* stdoutMessages[10000];
int messageStoreIndex = 0;

void storeMessage(char out[])
{
	
	if(messageStoreIndex < 10000 & out != NULL)
	{
		//strcpy(stdoutMessages[messageStoreIndex], out);
		stdoutMessages[messageStoreIndex] = strdup(out);
		messageStoreIndex++;
		
	}
}

char** getMessages()
{
	if(messageStoreIndex > 0)
	{
		stdoutMessages[messageStoreIndex] = "\n\n\n";
		messageStoreIndex = 0;

		return stdoutMessages;
	}
	return 0;
}

bool SetOutputCallback(long pointerToWriteStringCallbackMethod)
{
	writeStringCallback = (WriteStringCallback)pointerToWriteStringCallbackMethod;

	return 1;
}

#endif
