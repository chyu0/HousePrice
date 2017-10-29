package com.mxt.price.modal.common;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author maoxiaotai
 * @data 2017年10月22日 下午9:34:04
 * @Description 基础数据类
 */
public class BaseData implements Serializable {

	private static final long serialVersionUID = 4410811910741196927L;
	
	/**
	 * 最高价，BigDecimal类型，方便计算
	 */
	private BigDecimal maxPrice;
	
	/**
	 * 最低价，BigDecimal类型，方便计算
	 */
	private BigDecimal minPrice;
	
	/**
	 * 平均价，BigDecimal类型，方便计算
	 */
	private BigDecimal avgPrice;
	
	/**
	 * 平均价涨幅，BigDecimal，方便计算
	 */
	private BigDecimal avgPriceRise;

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	public BigDecimal getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}

	public BigDecimal getAvgPriceRise() {
		return avgPriceRise;
	}

	public void setAvgPriceRise(BigDecimal avgPriceRise) {
		this.avgPriceRise = avgPriceRise;
	}
}
