package com.eagletsoft.common.export.word;

import ognl.Ognl;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordDocumentBuilder {

    private static final String Symbol="$";

    private static final String EXPRESSION = "[a-zA-Z0-9\\.\\#\\[\\]\\(\\)]";
    /**
     * 验证主体
     */
    private static final Pattern findBody=Pattern.compile("(\\" + Symbol + "\\{)(" + EXPRESSION + "+)(})");
    /**
     * 验证附属主体-后半段
     */
    private static final Pattern surplus= Pattern.compile("(\\{" + EXPRESSION + "+}|" + EXPRESSION + "+})");
    /**
     * 文档主体-占位符确定后的截取行为依赖
     */
    private static final Pattern keyInfo=Pattern.compile(EXPRESSION + "+");


    private Map<String, Object> context = new HashMap<>();
    private Object root;
    private String empty = "";

    public WordDocumentBuilder(Object root) {
        this.root = root;
    }

    public WordDocumentBuilder putContext(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public XWPFDocument build(InputStream is) throws Exception {
        //对单行中存在多个的进行再次捕捉，表格自己优化，暂时没有贴别的解决方案
        XWPFDocument document = new XWPFDocument(is);
        paragraphSearchAndReplace(document.getParagraphs());
        tableSearchAndReplace(document.getTables());
        return document;
    }

    private Object getValue(String key, Object root) {
        try {
            return Ognl.getValue(key, context, root);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getListKey(String key) {
        if (key.contains("[]")) {
            int lastLeft = key.lastIndexOf('(');
            if (lastLeft > 0) {
                int lastRight = key.indexOf(')', lastLeft);
                key = key.substring(lastLeft + 1, lastRight);
            }

            int idx = key.indexOf("[]");
            if (idx > 0) {
                key = key.substring(0, idx);
                return key;
            }
        }
        return null;
    }

    private String formatKey(String key, int idx) {
        String newKey = key.replaceAll("\\[\\]", "\\[" + idx+ "\\]");
        return newKey;
    }

    private void replaceTable(XWPFTable table, XWPFTableRow templateRow, int templateIdx, List list) {
        int colNum = templateRow.getTableCells().size();
        for (int i = 0; i < list.size(); i++) {
           XWPFTableRow row = table.insertNewTableRow(templateIdx + i + 1);
           row.setHeight(templateRow.getHeight());
           row.setCantSplitRow(templateRow.isCantSplitRow());
           row.setRepeatHeader(templateRow.isRepeatHeader());

           for (int k = 0; k < colNum; k++) {
               XWPFTableCell cell = row.createCell();
               XWPFTableCell tmpCell = templateRow.getCell(k);
               setCellText(tmpCell, cell, tmpCell.getText());

               tableParagraphSearchAndReplace(cell.getParagraphs(), i);
           }
        }
        table.removeRow(templateIdx);
    }

    private void tableSearchAndReplace(XWPFTable table) {
        int count = table.getNumberOfRows();
        for (int i = 0; i < count; i++) {
            XWPFTableRow row = table.getRow(i);
            List<XWPFTableCell> cells = row.getTableCells();

            for (XWPFTableCell cell : cells) {
                List<XWPFParagraph> Paragraph= cell.getParagraphs();
                if(Paragraph.size()>1) {
                    paragraphSearchAndReplace(Paragraph);
                }else {
                    String nowText = cell.getText();
                    // 第一次查询
                    Matcher divFind = findBody.matcher(nowText);
                    while(divFind.find()){
                        String key = new StringBuffer(nowText).substring(divFind.start(2),divFind.end(2)).toString();

                        String listKey = getListKey(key);
                        if (null != listKey) {
                            List list = (List)getValue(listKey, root);
                            replaceTable(table, row, i, (List)list);
                            tableSearchAndReplace(table);
                            return;
                        }

                        //获取数据
                        Object _value = getValue(key, root);

                        String value = _value !=null ?_value.toString():empty;
                        //直接替换占位符开始的部位
                        nowText=new StringBuffer(nowText).replace(divFind.start(), divFind.end(), value).toString();

                        if(!divFind.find()) {
                            XWPFRun tempRun = cell.getParagraphs().get(0).getRuns().get(0);
                            //删除表格内容
                            //新建段落
                            XWPFParagraph pIO = cell.addParagraph();
                            //新建字体开始
                            XWPFRun rIO = pIO.createRun();
                            rIO.setBold(tempRun.isBold());
                            rIO.setItalic(tempRun.isItalic());
                            rIO.setFontFamily(tempRun.getFontFamily());
                            rIO.setFontSize(tempRun.getFontSize());
                            rIO.setTextPosition(tempRun.getTextPosition());
                            rIO.setText(nowText);

                            copyParagraph(pIO, rIO,  cell.getParagraphs().get(0));
                            cell.removeParagraph(0);
                        }else {
                            divFind = findBody.matcher(nowText);
                        }

                    }
                }
            }
        }
    }

    private void tableSearchAndReplace(List<XWPFTable> itTable) {
        for (XWPFTable table : itTable) {
            tableSearchAndReplace(table);
        }
    }

    private void replaceRun(List<XWPFRun> run, int idx) {
        for (int runIndex = 0; runIndex < run.size(); runIndex++) {
            // 获取到占位符文本深度(即文本从左到右的非可用长度),避免处理过多的文本
            int runDepth=0;
            //记录总共深度(即在for里边用了几个runIndex)
            int j=0;
            //最终确认的文本位置
            int findIndex=runIndex;
            //数据最终的key
            String key = null;
            //获取文本节点的第一个
            String nowRunText = run.get(runIndex).getText(run.get(runIndex).getTextPosition());
            if(nowRunText==null){
                continue;
            }
            //全文本节点
            String allRunText = nowRunText;
            //查找到符号位置
            if(nowRunText.contains(Symbol)){
                //第一次查找
                Matcher divFind = findBody.matcher(nowRunText);

                j=runIndex;
                //一直循环知道处理完成,直到所有文本全部找到
                while(!divFind.find()){
                    //继续深度处理,记录处理深度
                    runDepth++;
                    j++;
                    //当前文本
                    String newRunText=run.get(j).getText(run.get(j).getTextPosition());
                    //存在文本
                    if(newRunText!=null){
                        //拼接全部文本
                        allRunText+=newRunText;
                    }
                    //继续深度获取文本
                    divFind=findBody.matcher(allRunText);
                }
                //重置查找,避免过多运行find找不到参数
                divFind=findBody.matcher(allRunText);
                //只处理占位符位置,可能存在其他文本,所以使用find
                if(divFind.find()){//之查找一个多余的不动
                    //直接拉取字符
                    key = new StringBuffer(allRunText).substring(divFind.start(2),divFind.end(2)).toString();
                    key = formatKey(key, idx);
                    Object _value= this.getValue(key, root);

                    //数据转String
                    String value=_value!=null?_value.toString():empty;
                    //直接替换占位符开始的部位
                    String rText=new StringBuffer(nowRunText).replace(divFind.start(), divFind.end(), value).toString();
                    //对单行中存在多个的进行再次捕捉,正则
                    Matcher mg = findBody.matcher(rText);
                    //查找新的占位符
                    while(mg.find()){
                        //查找新的key
                        String newKey=new StringBuffer(rText).substring(mg.start(2), mg.end(2)).toString();
                        newKey = formatKey(newKey, idx);
                        Object _value1 = this.getValue(newKey, root);
                        //查找新的数据
                        String value1=_value1!=null?_value1.toString():empty;
                        //覆盖赋值
                        rText=new StringBuffer(rText).replace(mg.start(), mg.end(), value1).toString();
                        //替换word文本
                        mg = findBody.matcher(rText);
                    }
                    //将文本置换
                    run.get(findIndex).setText(rText,0);

                    //清空剩余项(对深度处理的数据进行清理)
                    int g=runIndex;
                    //对深度处理的数据进行清除占位符
                    for (int i = 0; i < runDepth; i++) {
                        g++;
                        //获取要清理的文本
                        String clearText =run.get(g).getText(run.get(g).getTextPosition());
                        //获取要清理的正则规则
                        Matcher clearFind=surplus.matcher(clearText);
                        Matcher clearKey=keyInfo.matcher(clearText);
                        //寻找与规则相同的文本
                        if(clearFind.find()){
                            //清空规则内的信息(只清除第一个存在的规则文本)
                            clearText=new StringBuffer(clearText).replace(clearFind.start(),clearFind.end(), "").toString();
                        }
                        //完整规则,不进行查找,确保值只是英文以及数字
                        else if(clearKey.matches()){
                            clearText=new StringBuffer(clearText).replace(clearKey.start(),clearKey.end(), "").toString();
                        }
                        //不存在的直接删除文本开始的第一个字符
                        else{
                            clearText=clearText.substring(1);
                        }
                        //重新赋值
                        run.get(g).setText(clearText,0);

                        //如果文本中存在占位符头,将深度减去1在次使用
                        if(clearText.contains(Symbol)){
                            j--;
                        }
                    }
                    //跳过已经使用的深度循环
                    runIndex=j;
                }
            }
        }
    }

    private void tableParagraphSearchAndReplace(List<XWPFParagraph> paragraph, int idx) {
        for (XWPFParagraph para : paragraph) {
            List<XWPFRun> run = para.getRuns();
            // 循环文本(每一次循环确定一个占位符)
            replaceRun(run, idx);
        }
    }

    private void paragraphSearchAndReplace(List<XWPFParagraph> paragraph) {
        for (XWPFParagraph para : paragraph) {
            List<XWPFRun> run = para.getRuns();
            // 循环文本(每一次循环确定一个占位符)
            replaceRun(run, 0);
        }
    }

    private void copyParagraph(XWPFParagraph cellP, XWPFRun cellR, XWPFParagraph tmpP) {
        XWPFRun tmpR = null;
        if (tmpP.getRuns() != null && tmpP.getRuns().size() > 0) {
            tmpR = tmpP.getRuns().get(0);
        }
        // 复制字体信息
        if (tmpR != null) {
            cellR.setBold(tmpR.isBold());
            cellR.setItalic(tmpR.isItalic());
            cellR.setStrike(tmpR.isStrike());
            cellR.setUnderline(tmpR.getUnderline());
            cellR.setColor(tmpR.getColor());
            cellR.setTextPosition(tmpR.getTextPosition());
            if (tmpR.getFontSize() != -1) {
                cellR.setFontSize(tmpR.getFontSize());
            }
            if (tmpR.getFontFamily() != null) {
                cellR.setFontFamily(tmpR.getFontFamily());
            }
            if (tmpR.getCTR() != null) {
                if (tmpR.getCTR().isSetRPr()) {
                    CTRPr tmpRPr = tmpR.getCTR().getRPr();
                    if (tmpRPr.isSetRFonts()) {
                        CTFonts tmpFonts = tmpRPr.getRFonts();
                        CTRPr cellRPr = cellR.getCTR().isSetRPr() ? cellR
                                .getCTR().getRPr() : cellR.getCTR().addNewRPr();
                        CTFonts cellFonts = cellRPr.isSetRFonts() ? cellRPr
                                .getRFonts() : cellRPr.addNewRFonts();
                        cellFonts.setAscii(tmpFonts.getAscii());
                        cellFonts.setAsciiTheme(tmpFonts.getAsciiTheme());
                        cellFonts.setCs(tmpFonts.getCs());
                        cellFonts.setCstheme(tmpFonts.getCstheme());
                        cellFonts.setEastAsia(tmpFonts.getEastAsia());
                        cellFonts.setEastAsiaTheme(tmpFonts.getEastAsiaTheme());
                        cellFonts.setHAnsi(tmpFonts.getHAnsi());
                        cellFonts.setHAnsiTheme(tmpFonts.getHAnsiTheme());
                    }
                }
            }
        }
        // 复制段落信息
        cellP.setAlignment(tmpP.getAlignment());
        cellP.setVerticalAlignment(tmpP.getVerticalAlignment());
        cellP.setBorderBetween(tmpP.getBorderBetween());
        cellP.setBorderBottom(tmpP.getBorderBottom());
        cellP.setBorderLeft(tmpP.getBorderLeft());
        cellP.setBorderRight(tmpP.getBorderRight());
        cellP.setBorderTop(tmpP.getBorderTop());
        cellP.setPageBreak(tmpP.isPageBreak());
        if (tmpP.getCTP() != null) {
            if (tmpP.getCTP().getPPr() != null) {
                CTPPr tmpPPr = tmpP.getCTP().getPPr();
                CTPPr cellPPr = cellP.getCTP().getPPr() != null ? cellP
                        .getCTP().getPPr() : cellP.getCTP().addNewPPr();
                // 复制段落间距信息
                CTSpacing tmpSpacing = tmpPPr.getSpacing();
                if (tmpSpacing != null) {
                    CTSpacing cellSpacing = cellPPr.getSpacing() != null ? cellPPr
                            .getSpacing()
                            : cellPPr.addNewSpacing();
                    if (tmpSpacing.getAfter() != null) {
                        cellSpacing.setAfter(tmpSpacing.getAfter());
                    }
                    if (tmpSpacing.getAfterAutospacing() != null) {
                        cellSpacing.setAfterAutospacing(tmpSpacing
                                .getAfterAutospacing());
                    }
                    if (tmpSpacing.getAfterLines() != null) {
                        cellSpacing.setAfterLines(tmpSpacing.getAfterLines());
                    }
                    if (tmpSpacing.getBefore() != null) {
                        cellSpacing.setBefore(tmpSpacing.getBefore());
                    }
                    if (tmpSpacing.getBeforeAutospacing() != null) {
                        cellSpacing.setBeforeAutospacing(tmpSpacing
                                .getBeforeAutospacing());
                    }
                    if (tmpSpacing.getBeforeLines() != null) {
                        cellSpacing.setBeforeLines(tmpSpacing.getBeforeLines());
                    }
                    if (tmpSpacing.getLine() != null) {
                        cellSpacing.setLine(tmpSpacing.getLine());
                    }
                    if (tmpSpacing.getLineRule() != null) {
                        cellSpacing.setLineRule(tmpSpacing.getLineRule());
                    }
                }
                // 复制段落缩进信息
                CTInd tmpInd = tmpPPr.getInd();
                if (tmpInd != null) {
                    CTInd cellInd = cellPPr.getInd() != null ? cellPPr.getInd()
                            : cellPPr.addNewInd();
                    if (tmpInd.getFirstLine() != null) {
                        cellInd.setFirstLine(tmpInd.getFirstLine());
                    }
                    if (tmpInd.getFirstLineChars() != null) {
                        cellInd.setFirstLineChars(tmpInd.getFirstLineChars());
                    }
                    if (tmpInd.getHanging() != null) {
                        cellInd.setHanging(tmpInd.getHanging());
                    }
                    if (tmpInd.getHangingChars() != null) {
                        cellInd.setHangingChars(tmpInd.getHangingChars());
                    }
                    if (tmpInd.getLeft() != null) {
                        cellInd.setLeft(tmpInd.getLeft());
                    }
                    if (tmpInd.getLeftChars() != null) {
                        cellInd.setLeftChars(tmpInd.getLeftChars());
                    }
                    if (tmpInd.getRight() != null) {
                        cellInd.setRight(tmpInd.getRight());
                    }
                    if (tmpInd.getRightChars() != null) {
                        cellInd.setRightChars(tmpInd.getRightChars());
                    }
                }
            }
        }
    }

    private void setCellText(XWPFTableCell tmpCell, XWPFTableCell cell, String text) {
        CTTc cttc2 = tmpCell.getCTTc();
        CTTcPr ctPr2 = cttc2.getTcPr();

        CTTc cttc = cell.getCTTc();
        CTTcPr ctPr = cttc.addNewTcPr();

        //cell.setColor(tmpCell.getColor());
        //cell.setVerticalAlignment(tmpCell.getVerticalAlignment());

        if (ctPr2.getTcW() != null) {
            ctPr.addNewTcW().setW(ctPr2.getTcW().getW());
        }
        if (ctPr2.getVAlign() != null) {
            ctPr.addNewVAlign().setVal(ctPr2.getVAlign().getVal());
        }

        if (ctPr2.getTcBorders() != null) {
            ctPr.setTcBorders(ctPr2.getTcBorders());
        }


        if (cttc2.getPList().size() > 0) {
            CTP ctp = cttc2.getPList().get(0);
            if (ctp.getPPr() != null) {
                if (ctp.getPPr().getJc() != null) {
                    cttc.getPList().get(0).addNewPPr().addNewJc().setVal(
                            ctp.getPPr().getJc().getVal());
                }
            }
        }

        XWPFParagraph tmpP = tmpCell.getParagraphs().get(0);
        XWPFParagraph cellP = cell.getParagraphs().get(0);
        XWPFRun cellR = cellP.createRun();
        cellR.setText(text);
        copyParagraph(cellP, cellR, tmpP);
    }
}

