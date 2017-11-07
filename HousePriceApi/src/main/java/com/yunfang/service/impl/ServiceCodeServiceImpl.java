package com.yunfang.service.impl;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.yunfang.dao.ServiceCodeDao;
import com.yunfang.modal.ServiceCode;
import com.yunfang.service.ServiceCodeService;

@Service
public class ServiceCodeServiceImpl implements ServiceCodeService {
	
	@Resource
	private ServiceCodeDao serviceCodeDao;

	@Override
	public void addNewServiceCode(String code, String serviceName, String methodName) {
		if(getServiceByCode(code) == null){
			ServiceCode serviceCode = new ServiceCode();
			serviceCode.setCode(code);
			serviceCode.setMethodName(methodName);
			serviceCode.setServiceName(serviceName);
			serviceCodeDao.save(serviceCode);
		}
	}

	@Override
	public ServiceCode getServiceByCode(String code) {
		Query query = new Query();
		Criteria criteria = Criteria.where("code").is(code);
		query.addCriteria(criteria);
		return serviceCodeDao.getServiceCodeOne(query);
	}

}
