package com.hisaige.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenyj
 * 2019/11/9 - 9:42.
 **/
public class RegexUtils {

    /**
     * 利用正则表达式提取字符串
     *
     * @param regex          正则表达式
     * @param content        待匹配内容，如果为空字符串则返回null
     * @param startCharIndex 待匹配内容跳过startIndex个字符后再开始正则匹配，不能小于0，0表示匹配第一个
     * @return 提取出来的第一个子字符串 或 null
     */
    public static String getMatcher(String regex, String content, Integer startCharIndex) {

        if (StringUtils.isEmpty(content)) {
            return null;
        }
        if (null == startCharIndex || startCharIndex < 0) {
            throw new IllegalArgumentException("startCharIndex must more than zero");
        }
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(content);

        while (matcher.find()) {
            if (--startCharIndex < 0) {
                return matcher.group();
            }
        }
        return null;
    }

    /**
     * 利用正则表达式提取字符串
     *
     * @param regex        正则表达式
     * @param content      待匹配内容
     * @param matcherIndex 需要返回第 matcherIndex 个满足的子字符串 从0开始
     * @return 返回null 或 第 matcherIndex 个满足的子字符串，如果matcherIndex是负数，则逆向返回第 -matcherIndex 个满足的子字符串
     */
    public static String getMatcherByIndex(String regex, String content, Integer matcherIndex) {

        List<String> matcherList = getMatchers(regex, content);

        int index = (null == matcherIndex) ? 0 : matcherIndex;
        if (index < 0 && index + matcherList.size() > 0) {
            //index < 0 ,逆向获取
            return matcherList.get(matcherIndex + matcherList.size());
        } else if (index < matcherList.size()) {
            // index > 0 ，正向获取
            return matcherList.get(index);
        } else {
            return null;
        }
    }

    public static List<String> getMatchers(String regex, String content) {

        if (StringUtils.isEmpty(content)) {
            return null;
        }
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(content);
        List<String> matcherList = new ArrayList<>();
        while (matcher.find()) {
            matcherList.add(matcher.group());
        }
        return matcherList;
    }

    public static void main(String[] args) {
        String regex = "\\d+$";
        String content = "13213adasd00123134";
        String matcher = getMatcher(regex, content, 0);
        System.out.println(Integer.valueOf(matcher));
        System.out.println(content.substring(0, content.lastIndexOf(matcher)));
    }

     public static boolean test(String regex, String content) {
         Pattern compile = Pattern.compile(regex);
         return compile.matcher(content).find();
     }
}
