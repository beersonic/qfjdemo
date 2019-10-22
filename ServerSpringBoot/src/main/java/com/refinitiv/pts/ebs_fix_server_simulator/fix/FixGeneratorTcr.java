package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import lombok.extern.log4j.Log4j2;
import quickfix.ConfigError;
import quickfix.DefaultSessionScheduleFactory;
import quickfix.SessionID;
import quickfix.SessionSchedule;
import quickfix.SessionScheduleFactory;
import quickfix.SessionSettings;

@Log4j2
public class FixGeneratorTcr {
    FixAcceptor m_fixAcceptor;

    boolean m_isDone = true;
    Thread m_thread = null;
    ArrayList<SessionID> m_sessions = null;
    SessionSettings m_sessionSettings = null;
    HashMap<String, SessionSchedule> m_mapSessionToSchedule = null;
    double m_messageRatePerSec = 1.0;

    public void Init(FixAcceptor fixAcceptor, ArrayList<SessionID> sessions, SessionSettings sessionSettings) {
        m_fixAcceptor = fixAcceptor;
        m_sessionSettings = sessionSettings;
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

    public void setMessageRate(double msgPerSecond) throws Exception
    {
        if (msgPerSecond > 0)
        {
            m_messageRatePerSec = msgPerSecond;
            log.info("set FIXGeneratorTCR message rate to " + msgPerSecond + " msg/sec");
        }
        else
        {
            throw new Exception("message rate must be positive number, inputRate=" + msgPerSecond);
        }
    }

    public void start() {
        if (m_thread == null || !m_thread.isAlive())
        {
            m_isDone = false;

            m_thread = new Thread(() -> {
                log.info("GeneratorTCR thread is started");
                generatorMain();
                log.info("GeneratorTCR thread is stopped");
            });

            m_thread.start();
        }
        else{
            log.warn("unexpected start, GeneratorTCR thread is already running");
        }
    }

    public void stop() {
        if (m_thread.isAlive())
        {
            try {
                m_isDone = true;
                m_thread.join(5000);
            } catch (InterruptedException e) {
                log.error("Stopping TCRGenerator: " + e.getMessage());
            }
        }
        else
        {
            log.warn("unexpected stop, GeneratorTCR isn't running");
        }
    }

    private void generatorMain() {
        try {
            MessageBuilderTcr msgBuilderTCR = new MessageBuilderTcr();
            while (!m_isDone) {
                m_sessions.forEach((session) ->
                {
                    if (false == m_mapSessionToSchedule.get(session.toString()).isSessionTime()) {
                        log.info("Outside session time");
                    } else {
                        m_fixAcceptor.sendMessageToClient(session, msgBuilderTCR.createTcr());
                    }
                });
                
                // message rate control
                {
                    double interval = 1000 / m_messageRatePerSec;
                    Thread.sleep((long)interval);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}