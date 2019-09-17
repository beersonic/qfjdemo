package com.refinitiv.beer;

import com.refinitiv.beer.quickfixj.*;

import quickfix.*;


public class AcceptorApp 
{
    public static void main( String[] args )
    {
        SocketAcceptor socketAcceptor = null;
        try{
            SessionSettings executorSettings = new SessionSettings("./qfj_acceptor.cfg");
            Application application = new FixAcceptor();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            socketAcceptor = new SocketAcceptor(application, fileStoreFactory, executorSettings, fileLogFactory, messageFactory);
            socketAcceptor.start();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
