package com.refinitiv.beer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.refinitiv.beer.quickfixj.FixInitiator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FieldConvertError;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.MessageFactory;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

public class InitiatorApp {
    final static Logger logger = LogManager.getLogger();
    static HashMap<Integer, SocketInitiator> c_dictIdAndClient = null;
    static boolean c_okToRun = false;

    public static void promptEnterKey() {
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
    }

    public static boolean RegexMatch(String pattern, String input, ArrayList<String> matchedTokens)
    {
        return RegexMatch(pattern, input, matchedTokens, true);
    }
    public static boolean RegexMatch(String pattern, String input, ArrayList<String> matchedTokens, Boolean caseSensitive)
    {
        Pattern rx = (caseSensitive)
                    ? Pattern.compile(pattern)
                    : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE) ;
        Matcher matcher = rx.matcher(input);

        boolean isMatch = false;
        if (matchedTokens != null) // require catpured string
        {
            while(matcher.find())
            {
                isMatch = true;
                
                matchedTokens.add(matcher.group(1));
            }
        }
        else
        {
            isMatch = matcher.find();
        }

        return isMatch;
    }
    
    public static void ReceiveCommand() throws RuntimeError, ConfigError {
        System.out.print("Enter command: ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        scanner.close();

        ArrayList<String> regexMatchTokens = new ArrayList<String>();
        if (RegexMatch("exit", line, null, false))
        {
            c_okToRun = false;
            logger.info("Exit program");
        }     
        else if (RegexMatch("start\\s*(\\d+)", line, regexMatchTokens))
        {
            int id = Integer.parseInt(regexMatchTokens.get(0));
            StartInitiator(id);
        }   
    }

    public static void main(String[] args) {
        try {
            c_okToRun = true;
            c_dictIdAndClient = new HashMap<Integer, SocketInitiator>();
            while(c_okToRun)
            {
                ReceiveCommand();
            }
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
        }
    }

    private static void StartInitiator(int id) throws RuntimeError, ConfigError
    {
        SocketInitiator socketInitiator = CreateInitiator("FIX.4.4", "MyClient" + id, "MyAcceptorService");
        socketInitiator.start();

        c_dictIdAndClient.put(id, socketInitiator);
    }

    private static void StopInitiator(int id)
    {
        if (c_dictIdAndClient.containsKey(id))
        {
            c_dictIdAndClient.get(id).stop();
        }
        else
        {
            logger.warn("Ask stop with invalid id, " + id);
        }
    }
    static SessionID ExtractSessionIDFromDictionary(quickfix.Dictionary dict) throws ConfigError, FieldConvertError
    {
        String s = dict.getString("BeginString") + ":" + dict.getString("SenderCompID") + "->" + dict.getString("TargetCompID");
        return new SessionID(s);
    }

    static SessionSettings InitializeSessionSettings() throws ConfigError, FieldConvertError {
     
        SessionSettings sessionSettings = new SessionSettings();
        {
            quickfix.Dictionary dict = new quickfix.Dictionary();
            {
                dict.setString("ConnectionType", "initiator");
                dict.setString("LogonTimeout", "30");
                dict.setString("ReconnectInterval", "5");
                dict.setString("ResetOnLogon", "N");
                dict.setString("FileLogPath", "./Client_Logs");
                dict.setString("ValidateIncomingMessage", "N");
                dict.setString("FileStorePath", "./Client_Seq_Store1");

                // session id
                dict.setString("BeginString", "FIX.4.4");
                dict.setString("SenderCompID", "MyClient1");
                dict.setString("TargetCompID", "MyAcceptorService");
                
                // operating time
                dict.setString("StartDay", "sunday");
                dict.setString("EndDay", "friday");
                dict.setString("StartTime", "06:00:00");
                dict.setString("EndTime", "17:00:00");

                // connection
                dict.setString("CheckLatency", "N");
                dict.setString("HeartBtInt", "10");
                dict.setString("SocketConnectPort", "12001");
                dict.setString("SocketConnectHost", "127.0.0.1");

                // dictionary
                dict.setString("UseDataDictionary", "Y");
                dict.setString("DataDictionary", "./FIX44.xml");
            }

            SessionID sid = ExtractSessionIDFromDictionary(dict);
            sessionSettings.set(sid, dict);
        }
        return sessionSettings;
    }

    public static SocketInitiator CreateInitiator(String fixVersionAsBeginString, String senderCompID, String targetCompID)
    {
        SocketInitiator socketInitiator = null;
        try {
            SessionSettings executorSettings = InitializeSessionSettings();
            
            Application application = new FixInitiator();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);

            socketInitiator = new SocketInitiator(application, fileStoreFactory, executorSettings, fileLogFactory, messageFactory);
        }
        catch(Exception e)
        {
            logger.error("Exception: " + e.getMessage());
        }
        return socketInitiator;
    }
}
