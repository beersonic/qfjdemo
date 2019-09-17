package com.refinitiv.beer;

import com.refinitiv.beer.quickfixj.FixInitiator;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.Logon;
import quickfix.fix42.NewOrderSingle;


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
                    NewOrderSingle newOrderSingle = new NewOrderSingle(
                            new ClOrdID("456"),
                            new HandlInst('3'),
                            new Symbol("AJCB"),
                            new Side(Side.BUY),
                            new TransactTime(),
                            new OrdType(OrdType.MARKET)
                    );
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
