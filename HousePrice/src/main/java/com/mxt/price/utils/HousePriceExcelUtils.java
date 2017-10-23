package com.mxt.price.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.mxt.price.enums.ExcelType;
import com.mxt.price.modal.BaseData;
import com.mxt.price.modal.CityData;
import com.mxt.price.modal.DistrictData;
import com.mxt.price.modal.HousePrice2;
import com.mxt.price.modal.PrivinceData;

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
	public static List<HousePrice2> getHousePricesByExcel(File file){
        if (file == null) {
            return null;
        } 
        List<HousePrice2> list = new ArrayList<HousePrice2>();
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
                        List<DistrictData> districtLists = new ArrayList<DistrictData>();
                        for(int index = 1 ; index < row.getLastCellNum() ; index++){
                        	if(rowNum == 0){
                        		districtMap.put(index, row.getCell(index).getStringCellValue());//列对应的市
                        	}else{
                        		DistrictData districtData = new DistrictData();
                        		BaseData baseData = new BaseData();
                        		baseData.setAvgPrice(new BigDecimal(row.getCell(index).getNumericCellValue()));
                        		districtData.setBaseData(baseData);
                        		districtData.setDistrict(districtMap.get(index));
                        		districtLists.add(districtData);
                        	}
                        }
                        
                        if(rowNum != 0){
                        	CityData cityData = new CityData();
                    		cityData.setCity("武汉市");
                    		cityData.setDistricts(districtLists);
                            
                    		PrivinceData privinceData = new PrivinceData();
                    		privinceData.setPrivince("湖北省");
                    		privinceData.setCitys(Arrays.asList(new CityData[]{cityData}));
                    		
                    		HousePrice2 housePrice2 = new HousePrice2();
                    		
                    		String date = DateFormatUtils.format(row.getCell(0).getDateCellValue(), "yyyy-MM");//日期
                    		housePrice2.setDate(date);
                    		housePrice2.setPrivinces(Arrays.asList(new PrivinceData[]{privinceData}));

                            list.add(housePrice2);
                        }
                    }
                }
            }
        }catch(Exception e){
        	logger.error("Excel文件读取异常，file：" + file.getName() + "," + CommonUtils.exceptionToStr(e));
        }
        return list;
    }
}