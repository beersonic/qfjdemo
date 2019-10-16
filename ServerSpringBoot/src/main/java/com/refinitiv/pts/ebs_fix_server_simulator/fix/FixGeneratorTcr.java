package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import quickfix.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Log4j2
public class FixGeneratorTcr {
    boolean m_isDone = false;
    Thread m_thread = null;
    FixAcceptor m_fixAccepter = null;
    ArrayList<SessionID> m_sessions = null;
    SessionSettings m_sessionSettings = null;
    HashMap<String, SessionSchedule> m_mapSessionToSchedule = null;

    public FixGeneratorTcr(FixAcceptor fixAccepter, ArrayList<SessionID> sessions, SessionSettings sessionSettings) {
        m_sessionSettings = sessionSettings;
        m_fixAccepter = fixAccepter;
        m_sessions = sessions;

        m_mapSessionToSchedule = new HashMap<>();
        SessionScheduleFactory sessionScheduleFactory = new DefaultSessionScheduleFactory();
        m_sessions.forEach((session) -> {
            try {
                SessionSchedule schedule = sessionScheduleFactory.create(session, m_sessionSettings);
                m_mapSessionToSchedule.put(session.toString(), schedule);
            } catch (ConfigError e) {
                e.printStackTrace();
            }
        });
    }

    public void start() {
        m_isDone = false;

        m_thread = new Thread(() -> {
            log.info("GeneratorTCR thread is started");
            generatorMain();
            log.info("GeneratorTCR thread is stopped");
        });

        m_thread.start();
    }

    public void stop() {
        try {
            m_isDone = true;
            m_thread.join(5000);
        } catch (InterruptedException e) {
            log.error("Stopping TCRGenerator: " + e.getMessage());
        }
    }

    private void generatorMain() {
        try {
            MessageBuilderTcr msgBuilderTCR = new MessageBuilderTcr();
            Date tLastSend = new Date();
            while (!m_isDone) {
                Date tNow = new Date();
                long diff = tNow.getTime() - tLastSend.getTime();
                if (diff > 10000) {
                    tLastSend = tNow;

                    //m_fixAccepter.sendMessageToClient(m_sessionId);
                    m_sessions.forEach((session) ->
                    {
                        if (false == m_mapSessionToSchedule.get(session.toString()).isSessionTime()) {
                            log.info("Outside session time");
                        } else {
                            m_fixAccepter.sendMessageToClient(session, msgBuilderTCR.createTcr());
                        }
                    });
                }
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}