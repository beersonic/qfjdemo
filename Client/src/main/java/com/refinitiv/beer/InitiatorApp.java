package com.refinitiv.beer;

import java.util.Scanner;

import com.refinitiv.beer.quickfixj.FixInitiator;

import quickfix.*;
import quickfix.field.Password;
import quickfix.field.Username;
//import quickfix.field.*;
//import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.Logon;

public class InitiatorApp 
{
    public static void promptEnterKey(){
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
     }
     
    public static void main( String[] args )
    {
        SocketInitiator socketInitiator = null;
        try{
            SessionSettings executorSettings = new SessionSettings("./qfj_initiator.cfg");
            Application application = new FixInitiator();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            socketInitiator = new SocketInitiator(application, fileStoreFactory, executorSettings, fileLogFactory, messageFactory);
            socketInitiator.start();

            SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
            Session.lookupSession(sessionId).logon();

            Logon logon = new Logon();
            logon.set(new quickfix.field.HeartBtInt(30));
            //logon.set(new quickfix.field.ResetSeqNumFlag(false));
            logon.setBoolean(141, false);
            logon.set(new Username("MyUserName"));
            logon.set(new Password("MyPassword"));
            logon.set(new quickfix.field.EncryptMethod(0));

            System.out.println("Logon: " + logon.toString());
            try {
                Session.sendToTarget(logon, sessionId);
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }

            promptEnterKey();
            
            socketInitiator.stop();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
