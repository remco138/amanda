Amanda voor Unix

Te doen:
uitvoeringsrechten toekennen (chmod +x) aan:
amaApp
Data/Amanda/ama
Data/Java/buildall
Data/Java/clean

Uitvoeren:
amaApp is een batch-file dat een in java geschreven grafische schil om amanda opent.
Data/Amanda/ama is de console versie van amanda.

Toelichting bij de broncode:
Data/Amanda/Amalib is de broncode van amanda (compileren via bijgeleverd make file).
EN NIET VERGETEN: ama kopieren naar de bovenliggende map (Data/Amanda).

Onder Ubuntu is een c-compiler en libreadline nodig. 
Op te halen via:
sudo apt-get install build-essential
sudo apt-get install libreadline-dev

Toelichting bij de java spulletjes:
Data/Java/buildall is een batch-file om alles te compileren. (het is gemaakt in Java 6).
Er is nogal veel java-code. Het bestaat voornamelijk uit een generator voor GUI-applicaties genaamd Layman tezamen
met GUI-helper classes.

Data/Java/lib/util/AmaObj.java gebruikt een java-process dat de console-versie van amanda uitvoert en ermee communiceert.
Het protocol is aan onderstaande fragmenten uit Data/Java/graph/GraphApp.java te zien:

  AmaObj amandaObj      = null;
  String prog           = "../Amanda/ama -proc";
  
    amandaObj = new AmaObj(prog);
    if(!amandaObj.isOk()) 
    {
      Jdialog.message("couldn't run program: " + prog, Jdialog.messageOK, graphFrame);
      graphFrameClose();
    }
    
  void commandClick() 
  {
    String cs = command.getText();
    transcript.append("> " + cs);
    transcript.append("\n");
    amandaObj.execute(cs);
    history.add(cs);
    historyNr = history.size() - 1;
    for(;;)
    {
      String s = amandaObj.getResult();
      if(s == null) break;
      transcript.append(s);
      transcript.append("\n");
    }
    transcript.setCaretPosition(transcript.getText().length());
  }
  
