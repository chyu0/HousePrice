package com.mxt.price.modal;

import java.io.Serializable;
import java.math.BigDecimal;

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
}
