package com.refinitiv.beer.quickfixj;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import quickfix.DataDictionary;
import quickfix.Group;
import quickfix.StringField;
import quickfix.field.*;
import quickfix.fix44.Message;
import quickfix.fix44.TradeCaptureReport;

public class MessageBuilderTCR {
    final static Logger logger = LogManager.getLogger();
    long m_tradeId = 0;

    ArrayList<Message> m_listSampleMsg = null;
    Iterator<Message> m_iterSample = null;

    public MessageBuilderTCR() {
        m_listSampleMsg = new ArrayList<Message>();

        LoadSampleMessage();
    }

    private TradeCaptureReport SetAdditionFields(TradeCaptureReport tcr, int msgIndex)
    {
        List<Group> g552 = tcr.getGroups(552);
        g552.forEach((grp ->
        {
            grp.setString(11, "MyClOrdID_" + msgIndex);
            grp.setString(198, "MySecondaryOrderID_" + msgIndex);
        }));

        tcr.setString(19, "ExecRefID_" + msgIndex);

        return tcr;
    }

    private void LoadSampleMessage() {
        try {
            DataDictionary dd = new DataDictionary("./datadict_ebs.xml");

            File file = new File("C:\\DriveD\\CodePTS\\QuickFixJ\\qfjdemo\\Server\\src\\main\\resources\\tcr_ebs.txt");
            if (file.exists())
            {
                Scanner sc = new Scanner(file);

                final String NEW_LINE = System.getProperty("line.separator");
                int i = 0;
                while (sc.hasNextLine()) {
                    ++i;

                    String line = sc.nextLine();
                    if (line.equals("break"))
                    {
                        break;
                    }
                    Message msg = new Message();

                    line = line.replace(NEW_LINE, "");
                    msg.fromString(line, dd, true);

                    String tempStr = msg.getHeader().getString(35);
                    if (tempStr.equals("AE"))
                    {
                        TradeCaptureReport tcr = new TradeCaptureReport();
                        tcr.fromString(line, dd, true);
                        
                        // add missing fields
                        tcr = SetAdditionFields(tcr, i);

                        // add to msg list
                        m_listSampleMsg.add(tcr);

                        logger.info("read TCR log from file\n" + tcr.toXML());

                    }
                }
                m_iterSample = m_listSampleMsg.iterator();

                sc.close();
            }
        }
        catch(Exception e)
        {
            logger.error(e);
        }
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

    public String GetRandomMarketSegmentID()
    {
        int i = (new Random()).nextInt(10) % 6;
        switch(i){
        case 0: return "QS";
        case 1: return "QF";
        case 2: return "QR";
        case 3: return "OC";
        case 4: return "OH";
        case 5: return "OE";
        }
        return "QS";
    }

    private Message CreateTCRFromSample()
    {
        if (!m_iterSample.hasNext())
        {
            m_iterSample = m_listSampleMsg.iterator();
        }
        return m_iterSample.next();
    }

    private Message CreateTCRFromScratch()
    {
        TradeCaptureReport tcr = new TradeCaptureReport();
        {
            tcr.set(new TradeReportID("MyTradeReportId"));
            tcr.setField(new StringField(1003, Long.toString(m_tradeId++)));
            tcr.set(new ExecType(ExecType.TRADE));

            tcr.set(new Symbol("EURTHB"));
            tcr.setString(1300, GetRandomMarketSegmentID());
            
        }
        return (Message)tcr;
    }

    public Message CreateTCR()
    {
        if (m_listSampleMsg.isEmpty())
        {
            return CreateTCRFromScratch();
        }
        else
        {
            return CreateTCRFromSample();
        }
    }
}