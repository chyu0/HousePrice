package com.yunfang.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yunfang.enums.ExcelType;
import com.yunfang.modal.BaseData;
import com.yunfang.modal.DistrictDataMongo;
import com.yunfang.modal.HousePriceMongo;

/**
 * @author maoxiaotai
 * @created 2017-10-20
 * HousePrice Excel工具类
 */
public class HousePriceExcelUtils {
    
	private static Logger logger = LoggerFactory.getLogger(HousePriceExcelUtils.class); 
	
    /**
     * read 读取Excel文件，生成对应HousePrice2列表
     * @param file
     * @return 房价趋势列表
     * @throws IOException
     */
    @SuppressWarnings("resource")
	public static List<HousePriceMongo> getHousePriceMongosByExcel(File file){
        if (file == null) {
            return null;
        } 
        List<HousePriceMongo> list = new ArrayList<HousePriceMongo>();
        try{
        	String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1 , fileName.length());
            InputStream is = new FileInputStream(file);
            Workbook workbook = null;
            if (ExcelType.OFFICE_EXCEL_2003_POSTFIX.getValue().equals(suffix)) {
            	POIFSFileSystem fs = new POIFSFileSystem(is);
            	workbook = new HSSFWorkbook(fs);
            } else if (ExcelType.OFFICE_EXCEL_2010_POSTFIX.getValue().equals(suffix)) {
            	workbook = new XSSFWorkbook(is);
            }else{
            	return null;
            }
            //存储列-市的映射
            Map<Integer , String> districtMap = new HashMap<Integer , String>();
            // Read the Sheet
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                if (sheet == null) {
                    continue;
                }
                // Read the Row
                for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null) {
                        List<DistrictDataMongo> districtLists = new ArrayList<DistrictDataMongo>();
                        for(int index = 1 ; index < row.getLastCellNum() ; index++){
                        	if(rowNum == 0){
                        		districtMap.put(index, row.getCell(index).getStringCellValue());//列对应的市
                        	}else{
                        		DistrictDataMongo districtData = new DistrictDataMongo();
                        		BaseData baseData = new BaseData();
                        		baseData.setAvgPrice(new BigDecimal(row.getCell(index).getNumericCellValue()));
                        		districtData.setBaseData(baseData);
                        		districtData.setDistrict(districtMap.get(index));
                        		districtLists.add(districtData);
                        	}
                        }
                        
                        if(rowNum != 0){
                    		HousePriceMongo housePrice = new HousePriceMongo();
                    		housePrice.setProvince("湖北");
                    		housePrice.setCity("武汉");
                    		String date = DateFormatUtils.format(row.getCell(0).getDateCellValue(), "yyyy-MM");//日期
                    		housePrice.setDate(date);
                    		housePrice.setDistricts(districtLists);
                            list.add(housePrice);
                        }
                    }
                }
            }
        }catch(Exception e){
        	logger.error("Excel文件读取异常，file：" + file.getName() + "," + e);
        }
        return list;
    }
}