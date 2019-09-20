package com.refinitiv.beer.quickfixj;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.maven.model.Build;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MessageCracker;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.TradeCaptureReport;

public class FixAcceptor extends MessageCracker implements Application
{
    boolean m_isConnected = false;
    long m_tradeId = 0;
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
    public void onMessage(NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("###NewOrder Received:" + order.toString());
        System.out.println("###Symbol" + order.getSymbol().toString());
        System.out.println("###Side" + order.getSide().toString());
        System.out.println("###Type" + order.getOrdType().toString());
        System.out.println("###TransactioTime" + order.getTransactTime().toString());
    }

    private Date BuildDate(int year, int month, int day)
    {
        Calendar cldr = Calendar.getInstance();
        cldr.set(Calendar.YEAR, year);
        cldr.set(Calendar.MONTH, Calendar.JANUARY + month - 1);
        cldr.set(Calendar.DAY_OF_MONTH, day);
        return cldr.getTime();
    }

    private String ToString(Date d, String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(d);
    }

    public void sendMessageToClient(SessionID sessionID)
    {
        try 
        {
            TradeCaptureReport tcr = new TradeCaptureReport();
            {
                tcr.set(new TradeReportID("MyTradeReportId"));
                tcr.setField(new StringField(1003, Long.toString(m_tradeId++)));
                tcr.set(new ExecType(ExecType.TRADE));

                tcr.set(new Symbol("EURTHB"));
                /*
                tcr.set(new PreviouslyReported(false));
                tcr.setField(new StringField(1300, "MarketSegmentID_QS"));
                tcr.setField(new StringField(1301, "MyMarketID"));
                
                tcr.set(new Product(4));
                tcr.set(new SecurityType("FXSPOT"));
                tcr.set(new LastPx((new Random()).nextDouble()));
                tcr.setField(new StringField(15, "EUR")); // why can't set CURRENCY as native variable?
                tcr.setField(new StringField(120, "THB")); // why can't set SETTLE_CURRENCY as native variable?

                if (tcr.get(new SecurityType()).equals("FXFWD"))
                {
                    tcr.set(new LastSpotRate((new Random()).nextDouble()));
                    tcr.set(new LastForwardPoints((new Random()).nextDouble()));
                }

                //tcr.setField() (BuildDate(2019, 11, 15).toString()));
                tcr.set(new TradeDate(ToString(BuildDate(2019,11,15), "yyyymmdd")));
                tcr.set(new TransactTime());

                // Legs
                tcr.setInt(555, 1);
                {
                    Group grpLeg = new Group(555, 600);
                    {
                        grpLeg.setString(600, "EURTHB");
                        grpLeg.setInt(604, 1);
                        {
                            Group grpLegSecAltD = new Group(604, 605);
                            {
                                grpLegSecAltD.setString(605, "MyISIN");
                                grpLegSecAltD.setString(606, "This is 4");
                            }
                            grpLeg.addGroup(grpLegSecAltD);
                        }
                        grpLeg.setInt(624, 1);
                        grpLeg.setString(1788, "A");
                    }
                    tcr.addGroup(grpLeg);
                }

                // side
                tcr.setInt(552, 2);
                {
                    Group grpBuy = new Group(552, 54);
                    {
                        grpBuy.setChar(54, '1');
                        grpBuy.setInt(453, 1);
                        {
                            Group grpParty = new Group(453, 448);
                            {
                                grpParty.setString(448, "MyPartyID");
                                grpParty.setString(447, "D");
                                grpParty.setString(452, "1");
                            }
                        }
                        grpBuy.setString(1, "MyAccount");
                    }
                    tcr.addGroup(grpBuy);

                    Group grpSell = new Group(552, 54);
                    {
                        grpSell.setChar(54, '2');
                        grpSell.setInt(453, 1);
                        {
                            Group grpParty = new Group(453, 448);
                            {
                                grpParty.setString(448, "MyPartyID");
                                grpParty.setString(447, "G");
                                grpParty.setString(452, "102");
                            }
                            grpSell.setString(1, "MyAccount");
                        }
                    }
                    tcr.addGroup(grpSell);
                }
                */
            }

            System.out.println("###Sending Order Acceptance:" + tcr.toString() + "sessionID:" + sessionID.toString());
            System.out.println("TCR to XML:\n" + tcr.toXML());
            Session.sendToTarget(tcr, sessionID);
        } catch (RuntimeException e) {
            LogUtil.logThrowable(sessionID, e.getMessage(), e);
        } 
        /*
        catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } */
        catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}