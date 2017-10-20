package com.mxt.price.enums;

public enum ExcelType {
	
	OFFICE_EXCEL_2003_POSTFIX(0 , "xls") , OFFICE_EXCEL_2010_POSTFIX(1 ,"xlsx");
	
	private Integer type;
	private String value;
	
	private ExcelType(Integer type,String value){
		this.type = type;
		this.value = value;
	}
	
	public Integer getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}

	public static String getValueByType(Integer type){
		for(ExcelType t : ExcelType.values()){
			if(t.getType().equals(type)){
				return t.getValue();
			}
		}
		return null;
	}

}
