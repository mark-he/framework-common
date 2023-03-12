package com.eagletsoft.common.export.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestApp {

    public static void main(String[] args) throws Exception {

        String key = "${test}";
        String Symbol="$";
        String EXPRESSION = "[a-zA-Z0-9\\.\\#\\[\\]\\(\\)]";

        Pattern findBody=Pattern.compile("(\\" + Symbol + "\\{)(" + EXPRESSION + "+)(})");
        Pattern surplus=Pattern.compile("(\\{" + EXPRESSION + "+}|" + EXPRESSION + "+})");

        Matcher matcher = surplus.matcher(key);
        System.out.println(matcher.find());

        if (true) {
            return;
        }

        HashMap params = new HashMap();
        params.put("test", "AAAAA");

        List list = new ArrayList();
        Map item = new HashMap();
        item.put("a", "A");
        item.put("b", BigDecimal.valueOf(100).setScale(2));
        list.add(item);

        item = new HashMap();
        item.put("a", "A2");
        item.put("b", BigDecimal.valueOf(200).setScale(2));
        list.add(item);

        params.put("list", list);
        long timestamp = System.currentTimeMillis();

        XWPFDocument[] documents = new XWPFDocument[2];

        for (int i = 0; i < documents.length; i++) {
            File file = new File("/Users/markkk/Desktop/Template1.docx");
            FileInputStream is = new FileInputStream(file);

            WordDocumentBuilder builder = new WordDocumentBuilder(params).putContext("t", new T());
            documents[i] = builder.build(is);
        }

        File file2 = new File("/Users/markkk/Desktop/Template2.docx");
        MergeDocsUtils.mergeDoc(documents, new FileOutputStream(file2), "UTF-8", true);
        System.out.println((System.currentTimeMillis() - timestamp) / 1000f);
    }

    public static class T {
        public String test(Object obj) {
            return "T=" + obj;
        }
    }
}
