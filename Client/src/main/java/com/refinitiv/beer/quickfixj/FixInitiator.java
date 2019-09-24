package com.refinitiv.beer.quickfixj;

import java.util.logging.LogManager;

import org.apache.logging.log4j.Logger;

import quickfix.*;

public class FixInitiator extends ApplicationAdapter {
    //final static Logger logger = LogManager.getLogger();
    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("onCreate sessionId=" + sessionId.toString());
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("onLogon sessionId=" + sessionId.toString());
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("onLogout sessionId=" + sessionId.toString());
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        handleCommonMessage("toAdmin", message, sessionId);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        
        handleCommonMessage("toApp", message, sessionId);
    }

    private void handleCommonMessage(String prefix, Message message, SessionID sessionId) {
        String s;
        try {
            s = message.getHeader().getString(35);
            if (s.equals("0"))
            {
                //System.out.println(prefix + ": Heartbeat");
            }
            else if (s.equals("1"))
            {
                System.out.println(prefix + ": TestRequest");
            }
            else if (s.equals("2"))
            {
                System.out.println(prefix + ": ResendRequest");
            }
            else if (s.equals("4"))
            {
                System.out.println(prefix + ": SequenceReset");
            }
            else if (s.equals("3"))
            {
                System.out.println(prefix + ": SessionLevelReject");
            }
            else if (s.equals("5"))
            {
                System.out.println(prefix + ": Logout");
            }
            else if (s.equals("A"))
            {
                System.out.println(prefix + ": Logon, message=" + message.toString());
            }
            else
            {
                System.out.println(prefix + ": sessionId=" + sessionId.toString() + " message=" + message.toString());
            }
        } catch (FieldNotFound e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        handleCommonMessage("fromAdmin", message, sessionId);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        handleCommonMessage("fromApp", message, sessionId);
        //System.out.println("fromApp sessionId=" + sessionId.toString() + " message=\n" + message.toXML());
    }
}