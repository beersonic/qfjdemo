package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.fix44.MessageCracker;

import java.util.HashSet;

@Component
public class FixAcceptor extends MessageCracker implements Application {
    final static Logger logger = LogManager.getLogger();
    HashSet<String> m_setConncetedSessionID = new HashSet<String>();

    public Boolean IsValidSessionID(SessionID sessionId) {
        return m_setConncetedSessionID.contains(sessionId.toString());
    }

    @Override
    public void onCreate(SessionID sessionId) {
        //super.onCreate(sessionId);
        logger.info("onCreate sessionId=" + sessionId.toString());

    }

    @Override
    public void onLogon(SessionID sessionId) {
        //super.onLogon(sessionId);
        logger.info("onLogon sessionId=" + sessionId.toString());

        m_setConncetedSessionID.add(sessionId.toString());
    }

    @Override
    public void onLogout(SessionID sessionId) {
        //super.onLogout(sessionId);
        logger.info("onLogout sessionId=" + sessionId.toString());

        m_setConncetedSessionID.remove(sessionId.toString());
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        //super.toAdmin(message, sessionId);
        handleCommonMessage("toAdmin", message, sessionId);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        //super.toApp(message, sessionId);
        handleCommonMessage("toApp", message, sessionId);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        //super.fromAdmin(message, sessionId);
        handleCommonMessage("fromAdmin", message, sessionId);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        //    super.fromApp(message, sessionId);
        //logger.info("fromApp sessionId=" + sessionId.toString() + " message=" + message.toRawString());
        //crack(message, sessionId);
        handleCommonMessage("fromApp", message, sessionId);
    }

    private void handleCommonMessage(String prefix, Message message, SessionID sessionId) {
        String s;
        try {
            s = message.getHeader().getString(35);
            if (s.equals("0")) {
                //logger.info(prefix + ": Heartbeat");
            } else if (s.equals("1")) {
                logger.info(prefix + ": TestRequest");
            } else if (s.equals("2")) {
                logger.info(prefix + ": ResendRequest");
            } else if (s.equals("4")) {
                logger.info(prefix + ": SequenceReset");
            } else if (s.equals("3")) {
                logger.info(prefix + ": SessionLevelReject");
            } else if (s.equals("5")) {
                logger.info(prefix + ": Logout");
            } else if (s.equals("A")) {
                logger.info(prefix + ": Logon, message=" + message.toString());
            } else {
                logger.info(prefix + ": sessionId=" + sessionId.toString() + " message=" + message.toString());
            }
        } catch (FieldNotFound e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(SessionID sessionID, Message message) {
        try {
            logger.info("###Sending message: sessionID [" + (IsValidSessionID(sessionID) ? "ACTIVE" : "INACTIVE") + "]:" + sessionID.toString());
            //logger.info("toXML:\n" + message.toXML());
            Session.sendToTarget(message, sessionID);
        } catch (RuntimeException e) {
            LogUtil.logThrowable(sessionID, e.getMessage(), e);
        } 
        /*
        catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } */ catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}