package com.refinitiv.pts.ebs_fix_server_simulator.fix;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.StringField;
import quickfix.field.ExecType;
import quickfix.field.Symbol;
import quickfix.field.TradeReportID;
import quickfix.fix44.Message;
import quickfix.fix44.TradeCaptureReport;

@Log4j2
@Component
public class MessageBuilderTcr {
    long m_tradeId = 0;

    ArrayList<Message> m_listSampleMsg;
    Iterator<Message> m_iterSample = null;
    DataDictionary m_dictionary = null;

    public MessageBuilderTcr() throws ConfigError {
        m_listSampleMsg = new ArrayList<>();

        m_dictionary = new DataDictionary("datadict_ebs.xml");

        loadSampleMessage();
    }

    private TradeCaptureReport setAdditionFields(TradeCaptureReport tcr, int msgIndex) {
        List<Group> g552 = tcr.getGroups(552);
        g552.forEach((grp -> {
            grp.setString(11, "MyClOrdID_" + msgIndex);
            grp.setString(198, "MySecondaryOrderID_" + msgIndex);
        }));

        tcr.setString(19, "ExecRefID_" + msgIndex);

        return tcr;
    }

    public Message fromString(String rawFIXMessage) throws InvalidMessage
    {
        Message msg = new Message();
        msg.fromString(rawFIXMessage, m_dictionary, true);
        return msg;
    }

    private void loadSampleMessage() {
        try {
            File file = new File("src/main/resources/tcr_ebs.txt");
            if (file.exists()) {
                Scanner sc = new Scanner(file);

                final String NEW_LINE = System.getProperty("line.separator");
                int i = 0;
                while (sc.hasNextLine()) {
                    ++i;

                    String line = sc.nextLine();
                    if (line.equals("break")) {
                        break;
                    }
                    Message msg = new Message();

                    line = line.replace(NEW_LINE, "");
                    msg.fromString(line, m_dictionary, true);

                    String tempStr = msg.getHeader().getString(35);
                    if (tempStr.equals("AE")) {
                        TradeCaptureReport tcr = new TradeCaptureReport();
                        tcr.fromString(line, m_dictionary, true);

                        // add missing fields
                        tcr = setAdditionFields(tcr, i);

                        // add to msg list
                        m_listSampleMsg.add(tcr);

                        log.info("read TCR log from file\n" + tcr.toXML());

                    }
                }
                m_iterSample = m_listSampleMsg.iterator();

                sc.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private Date buildDate(int year, int month, int day) {
        Calendar cldr = Calendar.getInstance();
        cldr.set(Calendar.YEAR, year);
        cldr.set(Calendar.MONTH, Calendar.JANUARY + month - 1);
        cldr.set(Calendar.DAY_OF_MONTH, day);
        return cldr.getTime();
    }

    private String ToString(Date d, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(d);
    }

    public String getRandomMarketSegmentID() {
        int i = (new Random()).nextInt(10) % 6;
        switch (i) {
            case 0:
                return "QS";
            case 1:
                return "QF";
            case 2:
                return "QR";
            case 3:
                return "OC";
            case 4:
                return "OH";
            case 5:
                return "OE";
        }
        return "QS";
    }

    private Message createTcrFromSample() {
        if (!m_iterSample.hasNext()) {
            m_iterSample = m_listSampleMsg.iterator();
        }
        return m_iterSample.next();
    }

    private Message createTcrFromScratch() {
        TradeCaptureReport tcr = new TradeCaptureReport();
        {
            tcr.set(new TradeReportID("MyTradeReportId"));
            tcr.setField(new StringField(1003, Long.toString(m_tradeId++)));
            tcr.set(new ExecType(ExecType.TRADE));

            tcr.set(new Symbol("EURTHB"));
            tcr.setString(1300, getRandomMarketSegmentID());

        }
        return (Message) tcr;
    }

    public Message createTcr() {
        if (m_listSampleMsg.isEmpty()) {
            return createTcrFromScratch();
        } else {
            return createTcrFromSample();
        }
    }
}