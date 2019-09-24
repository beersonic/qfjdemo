package com.refinitiv.beer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.refinitiv.beer.quickfixj.FixAcceptor;
import com.refinitiv.beer.quickfixj.MessageBuilderTCR;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import quickfix.ConfigError;
import quickfix.DefaultSessionScheduleFactory;
import quickfix.SessionID;
import quickfix.SessionSchedule;
import quickfix.SessionScheduleFactory;
import quickfix.SessionSettings;

public class FIXGeneratorTCR {
    boolean m_isDone = false;
    Thread m_thread = null;
    FixAcceptor m_fixAccepter = null;
    ArrayList<SessionID> m_sessions = null;
    SessionSettings m_sessionSettings = null;
    HashMap<String, SessionSchedule> m_mapSessionToSchedule = null;

    final static Logger logger = LogManager.getLogger();
    
    public FIXGeneratorTCR(FixAcceptor fixAccepter, ArrayList<SessionID> sessions, SessionSettings sessionSettings) {
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

    public void Start() {
        m_isDone = false;

        m_thread = new Thread(() -> {
            logger.info("GeneratorTCR thread is started");
            GeneratorMain();
            logger.info("GeneratorTCR thread is stopped");
        });

        m_thread.start();
    }

    public void Stop() {
        try {
            m_isDone = true;
            m_thread.join(5000);
        } catch (InterruptedException e) {
            logger.error("Stopping TCRGenerator: " + e.getMessage());
        }
    }    
    
    private void GeneratorMain()
    {
        try
        {
            MessageBuilderTCR msgBuilderTCR = new MessageBuilderTCR();
            Date tLastSend = new Date();
            while(!m_isDone)
            {
                Date tNow = new Date();
                long diff = tNow.getTime() - tLastSend.getTime();
                if (diff > 10000)
                {
                    tLastSend = tNow;

                    //m_fixAccepter.sendMessageToClient(m_sessionId);
                    m_sessions.forEach((session) -> 
                    {
                        if (false == m_mapSessionToSchedule.get(session.toString()).isSessionTime())
                        {
                            logger.info("Outside session time");
                        }
                        else{
                            m_fixAccepter.sendMessageToClient(session, msgBuilderTCR.CreateTCR());
                        }
                    });
                }
                Thread.sleep(200);
            } 
        }
        catch (InterruptedException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}