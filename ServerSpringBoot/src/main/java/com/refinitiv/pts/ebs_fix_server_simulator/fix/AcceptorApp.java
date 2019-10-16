package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.*;

import java.util.ArrayList;

@Log4j2
@Component
public class AcceptorApp {
    @Autowired
    private FixAcceptor fixAcceptor;

    public void init() {
        try {
            SessionSettings executorSettings = new SessionSettings("qfj_acceptor.cfg");
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            SocketAcceptor socketAcceptor = new SocketAcceptor(this.fixAcceptor, fileStoreFactory, executorSettings, fileLogFactory, messageFactory);
            socketAcceptor.start();

            ArrayList<SessionID> sessions = socketAcceptor.getSessions();

            FixGeneratorTcr fixGeneratorTcr = new FixGeneratorTcr(this.fixAcceptor, sessions, executorSettings);
            fixGeneratorTcr.start();

            Thread.currentThread().join();

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
