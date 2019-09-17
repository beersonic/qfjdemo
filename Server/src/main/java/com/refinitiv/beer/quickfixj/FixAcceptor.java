package com.refinitiv.beer.quickfixj;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.MessageCracker;
import quickfix.fix42.NewOrderSingle;

public class FixAcceptor extends MessageCracker implements Application
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
        //System.out.println("fromApp sessionId=" + sessionId.toString() + " message=" + message.toRawString());
        crack(message, sessionId);
    }

    @Override
    public void onMessage(quickfix.fix42.NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("###NewOrder Received:" + order.toString());
        System.out.println("###Symbol" + order.getSymbol().toString());
        System.out.println("###Side" + order.getSide().toString());
        System.out.println("###Type" + order.getOrdType().toString());
        System.out.println("###TransactioTime" + order.getTransactTime().toString());

        sendMessageToClient(order, sessionID);
    }

    private void sendMessageToClient(NewOrderSingle order, SessionID sessionID) {
        try 
        {
            quickfix.fix42.ExecutionReport accept = new ExecutionReport();
            {
                accept.set(new ExecID("789"));
                accept.set(new OrdStatus(OrdStatus.NEW));
                accept.set(new ExecTransType(ExecTransType.NEW));
                accept.set(new OrderQty(56.0));
            }


            accept.set(order.getClOrdID());
            System.out.println("###Sending Order Acceptance:" + accept.toString() + "sessionID:" + sessionID.toString());
            Session.sendToTarget(accept, sessionID);
        } catch (RuntimeException e) {
            LogUtil.logThrowable(sessionID, e.getMessage(), e);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}