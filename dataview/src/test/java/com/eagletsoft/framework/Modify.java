package com.eagletsoft.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Modify {

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/markkk/Desktop/fallback-xx.txt");

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split( " ", -1);


            String organization_id = null;
            String account_id = null;
            String id = null;
            String status = null;
            for (String s : split) {
                if (null == organization_id && s.contains("`organization_id`")) {
                    organization_id = s;
                } else if (null == account_id && s.contains("`account_id`")) {
                    account_id = s;
                } else if (null == id && s.contains("`id`")) {
                    id = s;
                } else if (null == status && s.contains("`status`")) {
                    status = s;
                }
            }
            organization_id = removeComm(organization_id);
            account_id = removeComm(account_id);
            status = removeComm(status);
            id = removeComm(id);
            if (null != id) {

                String fallback = "UPDATE revenue SET %s, %s, %s WHERE %s;";
                System.out.println(String.format(fallback, organization_id, account_id, status, id));
            }
        }
    }

    private static String removeComm(String s) {

        if (null != s && s.endsWith(",")) {
            return s.substring(0, s.length() - 1);
        }
        else {
            System.out.println("# Above is null");
        }
        return s;
    }
}
