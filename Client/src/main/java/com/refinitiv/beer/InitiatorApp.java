package com.refinitiv.beer;

import com.refinitiv.beer.quickfixj.FixInitiator;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.NewOrderSingle;


public class InitiatorApp 
{
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
            logon.set(new quickfix.field.ResetSeqNumFlag(true));
            logon.set(new quickfix.field.EncryptMethod(0));
            try {
                Session.sendToTarget(logon, sessionId);
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }

            for(int j = 0; j < 100; j ++){
                try {
                    Thread.sleep(10000);
                    NewOrderSingle newOrderSingle = new NewOrderSingle();
                    {
                        newOrderSingle.set(new ClOrdID("456"));
                        newOrderSingle.set(new HandlInst('3'));
                        newOrderSingle.set(new Symbol("MY_SYMBOL"));
                        newOrderSingle.set(new Side(Side.BUY));
                        newOrderSingle.set(new TransactTime());
                        newOrderSingle.set(new OrdType(OrdType.MARKET));
                    }

                    System.out.println("####New Order Sent :" + newOrderSingle.toString());
                    Session.sendToTarget(newOrderSingle, sessionId);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SessionNotFound sessionNotFound) {
                    sessionNotFound.printStackTrace();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
