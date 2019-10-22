package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.InvalidMessage;
import quickfix.MessageFactory;
import quickfix.RuntimeError;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

@Log4j2
@Component
public class AcceptorApp {
    @Autowired
    private FixAcceptor fixAcceptor;

    @Autowired
    private FixGeneratorTcr m_fixGeneratorTcr;

    private SocketAcceptor m_socketAcceptor = null;
    private boolean m_isStartedServer = false;

    public void start() {
        try {
            log.info("Starting FIX acceptor");
            SessionSettings executorSettings = new SessionSettings("qfj_acceptor.cfg");
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            log.info("Starting SocketAcceptor");
            m_socketAcceptor = new SocketAcceptor(this.fixAcceptor, fileStoreFactory, executorSettings, fileLogFactory,
                    messageFactory);
            m_socketAcceptor.start();

            ArrayList<SessionID> sessions = m_socketAcceptor.getSessions();
            log.info("NumberOfSession = " + sessions.size());

            log.info("Starting FIXGeneratorTCR");
            //m_fixGeneratorTcr = new FixGeneratorTcr();
            m_fixGeneratorTcr.Init(fixAcceptor, sessions, executorSettings);

            m_isStartedServer = true;
            log.info("Server is started");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void ToggleStartStopServer() throws RuntimeError, ConfigError {
        if (m_isStartedServer) {
            m_socketAcceptor.stop();
        } else {
            m_socketAcceptor.start();
        }
    }

    public void StartGenerateTCRFromFile() {
        m_fixGeneratorTcr.start();
    }

    public void StopGenerateTCRFromFile() {
        m_fixGeneratorTcr.stop();
    }

    public void setMessageRate(double nMsgPerSecond) throws Exception {
        m_fixGeneratorTcr.setMessageRate(nMsgPerSecond);
    }

    public void sendFIXMessageTCR(String rawFIXMessage) throws ConfigError, InvalidMessage
    {
        m_fixGeneratorTcr.sendFIXTCRRaw(rawFIXMessage);
    }
}
