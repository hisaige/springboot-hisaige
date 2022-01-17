package com.hisiage.event.supper;

import com.hisaige.core.util.CollectionUtils;
import com.hisiage.event.factory.EventRegister;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 订阅具体事件使用
 * 从所有事件中获取用户特定的事件
 *
 * @author chenyj
 * 2020/6/15
 **/
public class EventPathMatcher {

    private Iterator<String> patternSet;

    public EventPathMatcher(Iterator<String> patternSet) {
        this.patternSet = patternSet;
    }

    public Set<String> getMatcherEventPath() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        List<String> notifyEventTypes = EventRegister.getNotifyEventTypes();
        if (null == patternSet || !patternSet.hasNext()) {
            return new HashSet<>(notifyEventTypes);
        }
        HashSet<String> retEventPathSet = new HashSet<>();
        while (patternSet.hasNext()) {
            String pattern = patternSet.next();
            for (String eventPath : notifyEventTypes) {
                if (match(pattern, eventPath)) {
                    retEventPathSet.add(eventPath);
                }
            }
        }
        return retEventPathSet;
    }

    private boolean match(String pattern, String path) {
        String[] paths = path.split("\\.");
        String[] patterns = pattern.split("\\.");
        if (patterns.length < 2) {
            //最小规则必须是2个关键词，*和?不能同时存在
            return false;
        }
        boolean prefix = false;
        if ("*".equals(patterns[0])) {
            // * 在最前
            return path.endsWith(pattern.substring(2));
        }
        if ("*".equals(patterns[patterns.length - 1])) {
            // * 在最后
            return path.startsWith(pattern.substring(0, patterns.length - 2));
        }
        if (pattern.contains("*")) {
            // * 在中间
            String[] split = pattern.split("\\*");
            if (split.length != 2) {
                return false;//不需要太多*
            }
            //只匹配中甲*的
            return path.startsWith(split[0]) && path.endsWith(split[1]);
        }

        if (paths.length != patterns.length) {
            //如果没有*号，则两个数组长度必须一致
            return false;
        }

        for (int i = 0; i < paths.length; i++) {
            if ("?".equals(patterns[i])) {
                continue;
            }
            if (!patterns[i].equals(paths[i])) {
                //匹配不正确
                return false;
            }
        }
        return true;
    }
}
