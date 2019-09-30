package com.refinitiv.beer;

import java.util.Scanner;

import com.refinitiv.beer.quickfixj.FixInitiator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import quickfix.*;
import quickfix.field.Password;
import quickfix.field.Username;
//import quickfix.field.*;
//import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.Logon;

public class InitiatorApp 
{
    final static Logger logger = LogManager.getLogger();
    
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
            {
                String sid1 = "FIX.4.4:MyClient1->MyAcceptorService";
                String sid2 = "FIX.4.4:MyClient2->MyAcceptorService";

                SessionID sessionId = new SessionID(sid1);
                quickfix.Dictionary dict = executorSettings.get(sessionId);
                {
                    dict.setString("SenderCompID", "MyClient2");
                    dict.setString("FileStorePath", "./Client_Seq_Store2");
                    dict.setString("SocketConnectPort", "12002");
                }
                executorSettings.set(new SessionID(sid2), dict);
            }

            Application application = new FixInitiator();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            socketInitiator = new SocketInitiator(application, fileStoreFactory, executorSettings, fileLogFactory, messageFactory);
            socketInitiator.start();

            SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
            Session.lookupSession(sessionId).logon();

            promptEnterKey();
            
            socketInitiator.stop();
        }
        catch(Exception e)
        {
            logger.error("Exception: " + e.getMessage());
        }
    }
}
