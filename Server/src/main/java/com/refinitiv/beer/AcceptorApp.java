package com.refinitiv.beer;

import java.util.ArrayList;
import java.util.Scanner;

import com.refinitiv.beer.quickfixj.*;

import quickfix.*;


public class AcceptorApp 
{
    public static void main( String[] args )
    {
        SocketAcceptor socketAcceptor = null;
        try{
            FixAcceptor fixAccepter = new FixAcceptor();
            SessionSettings executorSettings = new SessionSettings("./qfj_acceptor.cfg");
            Application application = fixAccepter;
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            socketAcceptor = new SocketAcceptor(application, fileStoreFactory, executorSettings, fileLogFactory, messageFactory);
            socketAcceptor.start();

            ArrayList<SessionID> sessions = socketAcceptor.getSessions();

            FIXGeneratorTCR fixGeneratorTCR = new FIXGeneratorTCR(fixAccepter, sessions);
            fixGeneratorTCR.Start();

            promptEnterKey();

            System.out.println("Stopping FIXGeneratorTCR");
            fixGeneratorTCR.Stop();

            System.out.println("Stopping FIXSocketAcceptor");
            socketAcceptor.stop();
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public static void promptEnterKey(){
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
     }
}
