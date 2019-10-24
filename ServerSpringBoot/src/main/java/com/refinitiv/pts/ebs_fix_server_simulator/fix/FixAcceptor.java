package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import java.util.ArrayList;
import java.util.HashSet;

import com.refinitiv.pts.ebs_fix_server_simulator.util.CommonUtils;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogUtil;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.field.Password;
import quickfix.field.SessionStatus;
import quickfix.field.Username;
import quickfix.fix44.MessageCracker;

@Log4j2
@Component
public class FixAcceptor extends MessageCracker implements Application {
    //final static Logger logger = LogManager.getLogger();
    HashSet<String> m_setConncetedSessionID = new HashSet<String>();

    public Boolean IsValidSessionID(SessionID sessionId) {
        return m_setConncetedSessionID.contains(sessionId.toString());
    }

    @Override
    public void onCreate(SessionID sessionId) {
        //super.onCreate(sessionId);
        log.info("onCreate sessionId=" + sessionId.toString());

    }

    @Override
    public void onLogon(SessionID sessionId) {
        //super.onLogon(sessionId);
        log.info("onLogon sessionId=" + sessionId.toString());

        m_setConncetedSessionID.add(sessionId.toString());
    }

    @Override
    public void onLogout(SessionID sessionId) {
        //super.onLogout(sessionId);
        log.info("onLogout sessionId=" + sessionId.toString());

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

    private void PerformUsernamePasswordAuthenChecking(Message message) throws RejectLogon, FieldNotFound
    {
        if (message.isSetField(Username.FIELD) && message.isSetField(Password.FIELD))
        {
            boolean goodAuthen = false;

            String username = message.getString(Username.FIELD);
            String password = message.getString(Password.FIELD);

            ArrayList<String> userTokens = new ArrayList<String>();
            ArrayList<String> passwordTokens = new ArrayList<String>();
            if (CommonUtils.RegexMatch(".+(\\d+)", username, userTokens)
                && CommonUtils.RegexMatch(".+(\\d+)", password, passwordTokens))
            {
                int x1 = Integer.parseInt(userTokens.get(0));
                int x2 = Integer.parseInt(passwordTokens.get(0));

                if (x1 == x2)
                {
                    goodAuthen = (x1 == x2);
                }
            }

            if (goodAuthen)
            {
                log.info("Sucessfully authenticate for user=" + username);
            }
            else
            {
                log.info("Failed to authenticate for user=" + username);
                throw new RejectLogon("Invalid username/password", true, SessionStatus.INVALID_USERNAME_OR_PASSWORD);
            }
        }
        else
        {
            log.info("No username/password set in LOGON message");
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon 
    {        
        if (message.getHeader().getString(MsgType.FIELD).equals("A"))
        {
            log.info("Authentication: " + message.toRawString());
            PerformUsernamePasswordAuthenChecking(message);
        }     
        handleCommonMessage("fromAdmin", message, sessionId);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        //    super.fromApp(message, sessionId);
        //log.info("fromApp sessionId=" + sessionId.toString() + " message=" + message.toRawString());
        //crack(message, sessionId);
        handleCommonMessage("fromApp", message, sessionId);
    }

    private void handleCommonMessage(String prefix, Message message, SessionID sessionId) {
        String s;
        try {
            s = message.getHeader().getString(35);
            if (s.equals("0")) {
                //log.info(prefix + ": Heartbeat");
            } else if (s.equals("1")) {
                log.info(prefix + ": TestRequest");
            } else if (s.equals("2")) {
                log.info(prefix + ": ResendRequest");
            } else if (s.equals("4")) {
                log.info(prefix + ": SequenceReset");
            } else if (s.equals("3")) {
                log.info(prefix + ": SessionLevelReject");
            } else if (s.equals("5")) {
                log.info(prefix + ": Logout");
            } else if (s.equals("A")) {
                log.info(prefix + ": Logon, message=" + message.toString());
            } else {
                log.info(prefix + ": sessionId=" + sessionId.toString() + " message=" + message.toString());
            }
        } catch (FieldNotFound e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(SessionID sessionID, Message message) {
        try {
            log.info("###Sending message: sessionID [" + (IsValidSessionID(sessionID) ? "ACTIVE" : "INACTIVE") + "]:" + sessionID.toString());
            //log.info("toXML:\n" + message.toXML());
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