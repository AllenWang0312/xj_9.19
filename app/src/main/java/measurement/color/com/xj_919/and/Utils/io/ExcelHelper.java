package measurement.color.com.xj_919.and.Utils.io;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import measurement.color.com.xj_919.and.fragment.datahistory.SimpleData;

/**
 * Created by wpc on 2016/10/22.
 */

public class ExcelHelper {

    public static final String[] title = {"编号", "日期", "时间", "结果", "备注"};

    public enum ExcelOutPutType {
        jxl, poi
    }


    public static boolean writeExcel(final ArrayList<SimpleData> Datas, String[] titles, String path, final String filename, ExcelOutPutType Type) {
        if (Type == ExcelOutPutType.jxl) {
            long start = System.currentTimeMillis();
            WritableWorkbook wwb = null;
            WritableSheet sheet = null;
            OutputStream os = null;
            try {
                os = new FileOutputStream(path + "/" + filename + ".xls");
                wwb = Workbook.createWorkbook(os);
                sheet = wwb.createSheet("第一个Sheet", 0);
                for (int i = 0; i < titles.length; i++) {
                    Label labelc = new Label(i, 0, titles[i]);
                    sheet.addCell(labelc);
                }
                for (int y = 0; y < Datas.size(); y++) {
                    ArrayList<String> line = Datas.get(y).getArray();
                    for (int x = 0; x < line.size(); x++) {
                        Label label = new Label(x, y, line.get(x));
                        try {
                            sheet.addCell(label);
                        } catch (RowsExceededException e) {
                            e.printStackTrace();
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
                return false;
            } finally {
                long end = System.currentTimeMillis();
                Log.i("导出成功:" + path + "/" + filename, "耗时" + (end - start) + "ms");
            }

            try {
                wwb.write();
                wwb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            return true;
        } else {
            try {
                // 创建对Excel工作簿文件的引用
                HSSFWorkbook workbook = new HSSFWorkbook();
                // 创建对工作表的引用。
                // 本例是按名引用（让我们假定那张表有着缺省名"Sheet1"）
                // 也可用getSheetAt(int index)按索引引用，
                // 在Excel文档中，第一张工作表的缺省索引是0，
                // 其语句为：HSSFSheet sheet = workbook.getSheetAt(0);
                HSSFSheet sheet = workbook.createSheet();
                for (int x = 0; x <= Datas.size(); x++) {
                    // 读取左上端单元
                    HSSFRow row = sheet.createRow((short) x);
                    ArrayList<String> line = null;
                    for (int y = 0; y < titles.length; y++) {

                        HSSFCell cell = row.createCell((short) y);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        if (x == 0) {
                            cell.setCellValue(titles[y]);
                        } else {
                            if (line == null) {
                                line = Datas.get(x - 1).getArray();
                            }
                            cell.setCellValue(line.get(y));
                        }
                    }
                    line = null;
                }
                FileOutputStream fOut = new FileOutputStream(path + "/" + filename + ".xls");
                workbook.write(fOut);
                fOut.flush();
                // 操作结束，关闭文件
                fOut.close();
                Log.i("outPutExcel", "success");
                return true;
            } catch (FileNotFoundException e) {
                Log.i("outPutExcel", "faild: FileNotFound");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                Log.i("outPutExcel", "faild: IOException");
                e.printStackTrace();
                return false;
            }
        }
    }

//    public static boolean writeExcel(final ArrayList<SimpleData> Datas, final String filename) {
//
//        long start = System.currentTimeMillis();
//        WritableWorkbook wwb = null;
//        WritableSheet sheet = null;
//        OutputStream os = null;
//        try {
//            os = new FileOutputStream(outputpath + "/" + filename + ".xls");
//            wwb = Workbook.createWorkbook(os);
//            sheet = wwb.createSheet("第一个Sheet", 0);
//            for (int i = 0; i < title.length; i++) {
//                Label labelc = new Label(i, 0, title[i]);
//                sheet.addCell(labelc);
//            }
//            for (int y = 0; y < Datas.size(); y++) {
//                ArrayList<String> line = SimpleData.getArray(Datas.get(y));
//                for (int x = 0; x < line.size(); x++) {
//                    Label label = new Label(x, y, line.get(x));
//                    try {
//                        sheet.addCell(label);
//                    } catch (RowsExceededException e) {
//                        e.printStackTrace();
//                    } catch (WriteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        } catch (RowsExceededException e) {
//            e.printStackTrace();
//        } catch (WriteException e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            long end = System.currentTimeMillis();
//            Log.i("导出成功:" + outputpath + "/" + filename, "耗时" + (end - start) + "ms");
//        }
//
//        try {
//            wwb.write();
//            wwb.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (WriteException e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }
}
