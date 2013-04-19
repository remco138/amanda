De delphi-code maakt gebruik van de dll. 
Het eenvoudigst voorbeeld is Amatools/Ama.dpr:

(********************************************************************************************)
program Ama;

function AmaInit(console: Integer; path: PChar; writeString, checkIO: Pointer): Boolean;
  stdcall; external 'Amadll.dll';
function AmaGetOption(option: PChar): PChar;
  stdcall; external 'Amadll.dll';
procedure AmaInterpret(expr: PChar);
  stdcall; external 'Amadll.dll';
procedure AmaSetInterrupted;
  stdcall; external 'Amadll.dll';
function AmaLoad(filename: PChar): Integer;
  stdcall; external 'Amadll.dll';

procedure AmaWriteString(str: PChar); stdcall;
begin
  write(str);
  flush(output);
end;

var
  expr: String;
begin
  if not AmaInit(1, PChar(ParamStr(0)), @AmaWriteString, Nil) then halt;
  if (ParamCount >= 1) and (AmaLoad(PChar(ParamStr(1))) = 0) then AmaWriteString(#10);
  repeat
    AmaWriteString(AmaGetOption('ConPrompt'));
    readln(expr);
    AmaInterpret(PChar(expr));
  until False;
end.
(********************************************************************************************)

Het is tamelijk rechttoe rechtaan. 
Bij AmaInit kun je de callbacks writeString en checkIO meegeven. Via writeString kun je output tonen. 
De andere callback checkIO wordt regelmatig aangeroepen. Je kan hierin bijvoorbeeld Application.ProcessMessages
aanroepen om te zien of de gebruiker de zaak wil afbreken. 
Afbreken kun je afdwingen door AmaSetInterrupted aan te roepen.
