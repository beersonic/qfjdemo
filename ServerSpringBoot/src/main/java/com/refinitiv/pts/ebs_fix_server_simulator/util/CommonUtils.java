package com.refinitiv.pts.ebs_fix_server_simulator.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils
{
    public static boolean RegexMatch(String pattern, String input, ArrayList<String> matchedTokens) {
        return RegexMatch(pattern, input, matchedTokens, true);
    }

    public static boolean RegexMatch(String pattern, String input, ArrayList<String> matchedTokens,
            Boolean caseSensitive) {
        Pattern rx = (caseSensitive) ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = rx.matcher(input);

        boolean isMatch = false;
        if (matchedTokens != null) // require catpured string
        {
            while (matcher.find()) {
                isMatch = true;

                matchedTokens.add(matcher.group(1));
            }
        } else {
            isMatch = matcher.find();
        }

        return isMatch;
    }
}