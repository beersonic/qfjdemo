package com.refinitiv.beer;

import java.util.ArrayList;
import java.util.Date;

import com.refinitiv.beer.quickfixj.FixAcceptor;

import quickfix.SessionID;

public class FIXGeneratorTCR 
{
    boolean m_isDone = false;
    Thread m_thread = null;
    FixAcceptor m_fixAccepter = null;
    ArrayList<SessionID> m_sessions = null;
    public FIXGeneratorTCR(FixAcceptor fixAccepter, ArrayList<SessionID> sessions)
    {
        m_fixAccepter = fixAccepter;
        m_sessions = sessions;
    }

    public void Start() {
        m_isDone = false;

        m_thread = new Thread(() -> {
            System.out.println("GeneratorTCR thread is started");
            GeneratorMain();
            System.out.println("GeneratorTCR thread is stopped");
        });

        m_thread.start();
    }

    public void Stop() {
        try {
            m_isDone = true;
            m_thread.join(5000);
        } catch (InterruptedException e) {
            System.out.println("Stopping TCRGenerator: " + e.getMessage());
        }
    }

    private void GeneratorMain()
    {
        try
        {
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
                        m_fixAccepter.sendMessageToClient(session);
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