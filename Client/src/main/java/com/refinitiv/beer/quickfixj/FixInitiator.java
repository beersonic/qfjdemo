package com.refinitiv.beer.quickfixj;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import quickfix.*;

public class FixInitiator extends ApplicationAdapter {
    final static Logger logger = LogManager.getLogger();

    @Override
    public void onCreate(SessionID sessionId) {
        logger.info("onCreate sessionId=" + sessionId.toString());
    }

    @Override
    public void onLogon(SessionID sessionId) {
        logger.info("onLogon sessionId=" + sessionId.toString());
    }

    @Override
    public void onLogout(SessionID sessionId) {
        logger.info("onLogout sessionId=" + sessionId.toString());
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
            if (s.equals("0")) {
                // logger.info(prefix + ": Heartbeat");
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

                if (s.equals("AE")) {
                    List<Group> groups = message.getGroups(552);
                    groups.forEach(group -> {
                        try {
                            if (group.isSetField(11))
                            {
                                String v11 = group.getString(11);
                                logger.info("TAG11=" + v11);
                            }
                            else
                            {
                                logger.warn("TAG11 is missing");
                            }
                        } catch (FieldNotFound e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (FieldNotFound e) {
            logger.error(e);
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
    }
}