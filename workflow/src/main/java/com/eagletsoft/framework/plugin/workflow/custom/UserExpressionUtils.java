package com.eagletsoft.framework.plugin.workflow.custom;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UserExpressionUtils {
    public static void applyUsers(List<String> users) {
        for (int i = 0; i < users.size(); i++) {
            String user = users.get(i);
            user = "#{activiti.users(execution, '" + user + "')}";
            users.set(i, user);
        }
    }

    public static void applyGroups(List<String> groups) {
        for (int i = 0; i < groups.size(); i++) {
            String group = groups.get(i);
            group = "#{activiti.groupUsers(execution, '" + group + "')}";
            groups.set(i, group);
        }
    }
}
