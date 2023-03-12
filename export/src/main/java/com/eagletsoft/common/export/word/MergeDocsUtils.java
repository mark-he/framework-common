package com.eagletsoft.common.export.word;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import java.io.OutputStream;
import java.util.List;

public class MergeDocsUtils {

    public static void mergeDoc(XWPFDocument[] documents, OutputStream dest, String encoding, boolean pageBreak) {
        StringBuffer parts = new StringBuffer();
        try {
            XWPFDocument src1Document = documents[0];
            if (pageBreak && documents.length > 1) {
                setPageBreak(src1Document);
            }
            CTBody src1Body = src1Document.getDocument().getBody();

            for (int i = 1; i < documents.length; i++) {
                XWPFDocument src2Document = documents[i];
                if (pageBreak && i != documents.length - 1) {
                    setPageBreak(src2Document);
                }
                CTBody src2Body = src2Document.getDocument().getBody();
                parts.append(getPart(src2Body, encoding));
            }

            appendBody(src1Body, parts, encoding);
            src1Document.write(dest);
            dest.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dest);
        }
    }

    private static void setPageBreak(XWPFDocument document) {
        List<XWPFRun> runs = document.getLastParagraph().getRuns();
        runs.get(runs.size() - 1).addBreak(BreakType.PAGE);
    }

    private static String getPart(CTBody append, String encoding) {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setCharacterEncoding(encoding);
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String addPart = appendString.substring(appendString.indexOf(">") + 1,
                appendString.lastIndexOf("<"));

        return addPart;
    }

    private static void appendBody(CTBody src, StringBuffer parts, String encoding) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setCharacterEncoding(encoding);
        optionsOuter.setSaveOuter();
        String srcString = src.xmlText();

        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1,
                srcString.lastIndexOf("<"));
        String suffix = srcString.substring(srcString.lastIndexOf("<"));

        StringBuffer sb = new StringBuffer();
        sb.append(prefix).append(mainPart).append(parts).append(suffix);

        CTBody makeBody = CTBody.Factory.parse(sb.toString());
        src.set(makeBody);
    }
}
