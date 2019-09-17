package com.refinitiv.beer.quickfixj;

import quickfix.*;

public class FixInitiator extends ApplicationAdapter
{
@Override
public void onCreate(SessionID sessionId) {
    // TODO Auto-generated method stub
    //super.onCreate(sessionId);
    System.out.println("onCreate sessionId=" + sessionId.toString());    
}

@Override
public void onLogon(SessionID sessionId) {
    // TODO Auto-generated method stub
    //super.onLogon(sessionId);
    System.out.println("onLogon sessionId=" + sessionId.toString());    
}

@Override
public void onLogout(SessionID sessionId) {
    // TODO Auto-generated method stub
    //super.onLogout(sessionId);
    System.out.println("onLogout sessionId=" + sessionId.toString());
}

@Override
public void toAdmin(Message message, SessionID sessionId) {
    // TODO Auto-generated method stub
    //super.toAdmin(message, sessionId);
    System.out.println("toAdmin sessionId=" + sessionId.toString() + " message=" + message.toRawString());
}

@Override
public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    // TODO Auto-generated method stub
    //super.toApp(message, sessionId);
    System.out.println("toApp sessionId=" + sessionId.toString() + " message=" + message.toRawString());
}

@Override
public void fromAdmin(Message message, SessionID sessionId)
        throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    // TODO Auto-generated method stub
    //super.fromAdmin(message, sessionId);
    System.out.println("fromAdmin sessionId=" + sessionId.toString() + " message=" + message.toRawString());
}

@Override
public void fromApp(Message message, SessionID sessionId)
        throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    // TODO Auto-generated method stub
//    super.fromApp(message, sessionId);
    System.out.println("fromApp sessionId=" + sessionId.toString() + " message=\n" + message.toXML());
}
}