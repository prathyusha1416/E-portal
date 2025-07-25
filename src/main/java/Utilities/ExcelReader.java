package Utilities;

import org.apache.poi.ss.usermodel.*;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public static List<String[]> readCredentials(String fileName) {
        List<String[]> credentials = new ArrayList<>();

        try (InputStream fis = ExcelReader.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {
        	 Sheet sheet = workbook.getSheetAt(0); 
 
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String username = row.getCell(0).getStringCellValue().trim();
                    String password = row.getCell(1).getStringCellValue().trim();
                    credentials.add(new String[]{username, password});
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return credentials;
    }
}
