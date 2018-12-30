package com.buyi.telephonebook.Utils;

import com.buyi.telephonebook.ContractBean;
import com.buyimingyue.framework.Utils.LogUtils;
import com.buyimingyue.framework.Utils.StringUtils;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/12/21.
 */

public class ExcelUtils {
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    public static Workbook getWorkbok(InputStream in, File file) throws Exception {
        if (file.getName().endsWith(EXCEL_XLS)) {  //Excel 2003
           return new HSSFWorkbook(in);
        } else if (file.getName().endsWith(EXCEL_XLSX)) {  // Excel 2007/2010 暂时不能用
             throw new Exception("不能读取.xlsx文件,请转换成.xls !");
        }
        return null;
    }

    private static Object getValue(Cell cell) {
        Object obj = null;
        switch (cell.getCellType()) {
            case BOOLEAN:
                obj = cell.getBooleanCellValue();
                break;
            case ERROR:
                obj = cell.getErrorCellValue();
                break;
            case NUMERIC:
                obj = cell.getNumericCellValue();
                break;
            case STRING:
                obj = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return obj;
    }
    //读excel文件
    public static void readExcelFile(File excelFile ,List<ContractBean>contactList){
        if (contactList == null)
            contactList = new ArrayList<>();
        try {
            // 同时支持Excel 2003、2007
            checkExcelVaild(excelFile);
            FileInputStream in = new FileInputStream(excelFile); // 文件流
           Workbook workbook = getWorkbok(in,excelFile);
            in.close();
            if (null == workbook)
                return;
//            Workbook workbook = WorkbookFactory.create(in); // 这种方式 Excel2003/2007/2010都是可以处理的

            int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
            /**
             * 设置当前excel中sheet的下标：0开始
             */
           Sheet sheet = workbook.getSheetAt(0);   // 遍历第一个Sheet
//            Sheet sheet = workbook.getSheetAt(2);   // 遍历第三个Sheet

            //获取总行数
//          System.out.println(sheet.getLastRowNum());

            // 为跳过第一行目录设置count
            int count = 0;
            for (int a = 0; a <sheet.getLastRowNum() ; a++) {
                Row row = sheet.getRow(a);
               try {
                    // 跳过第一和第二行的目录
                    if(count < 1 ) {
                        count++;
                        continue;
                    }

                    //如果当前行没有数据，跳出循环
                    if(StringUtils.isEmpty(row.getCell(0).toString())){
                        return;
                    }

                    //获取总列数(空格的不计算)
                    int columnTotalNum = row.getPhysicalNumberOfCells();
                    LogUtils.i("总列数：" + columnTotalNum);

                    LogUtils.i("最大列数：" + row.getLastCellNum());

                    //for循环的，不扫描空格的列
//                    for (Cell cell : row) {
//                    	System.out.println(cell);
//                    }
                    ContractBean contractBean = new ContractBean();
                    int end = row.getLastCellNum();
                    for (int i = 0; i < end; i++) {
                        Cell cell = row.getCell(i);
                        if(cell == null) {
                            LogUtils.i("null" + "\t");
                            continue;
                        }
                        Object obj = getValue(cell);
                        if (i == 0)
                            contractBean.name =obj.toString();
                        else if (i==1)
                            contractBean.phones.add(obj.toString());
                        LogUtils.i(obj + "\t");
                    }
                    contactList.add(contractBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //写入数据到时excel中去
    public static void write2ExcelFile(String path,List<ContractBean>contactList){
        if (contactList == null||contactList.size()<1)
            return;
        try {
            // 同时支持Excel 2003、2007
            File excelFile = new File(path);
            Workbook workbook = null;
            Sheet sheet = null;
            if (excelFile.exists()) {
                checkExcelVaild(excelFile);
                FileInputStream in = new FileInputStream(excelFile); // 文件流
                workbook = getWorkbok(in, excelFile);
                in.close();
                if (null == workbook)
                    return;
                sheet= workbook.getSheetAt(0);
            }else {
                workbook = new HSSFWorkbook();
                sheet = workbook.createSheet();
            }
            int a = sheet.getLastRowNum();
            if (sheet.getRow(a) != null) {
               a++;
            }
            for (int i = 0; i <contactList.size() ; i++) {
                Row row = sheet.createRow(a+i);
                Cell cell = row.createCell(0,CellType.STRING);
                cell.setCellValue(contactList.get(i).name);
                LogUtils.i("msg",contactList.get(i).name);
                for (int j = 0; j <contactList.get(i).phones.size() ; j++) {
                    if (StringUtils.isEmpty(contactList.get(i).phones.get(j)))
                        break;
                    Cell cell1 = row.createCell(1+j,CellType.STRING);
                    cell1.setCellValue(contactList.get(i).phones.get(j));
                    LogUtils.i("msg",contactList.get(i).phones.get(j));
                }
            }
            FileOutputStream fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 判断文件是否是excel
     * @throws Exception
     */
    public static void checkExcelVaild(File file) throws Exception{
        if(!file.exists()){
            throw new Exception("文件不存在");
        }
        if(!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){
            throw new Exception("文件不是Excel");
        }
    }
}
