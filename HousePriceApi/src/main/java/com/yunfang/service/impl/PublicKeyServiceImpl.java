package com.yunfang.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.yunfang.dao.PublicKeyMongoDao;
import com.yunfang.modal.PublicKey;
import com.yunfang.service.PublicKeyService;

/**
 * 公匙service
 * @author maoxiaotai
 * @data 2017年11月2日 下午2:16:52
 * @Description TODO
 */
@Service
public class PublicKeyServiceImpl implements PublicKeyService {

	@Resource
	private PublicKeyMongoDao publicKeyDao;
	
	@Override
	public void save(PublicKey publicKey) {
		publicKeyDao.save(publicKey);
	}
	
	@Override
	public PublicKey findVerifyByKey(String publicKey) {
		Query query = new Query();
		Criteria criteria = Criteria.where("publicKey").is(publicKey);
		query.addCriteria(criteria);
		return publicKeyDao.findOnePublicKey(query);
	}

	@Override
	public List<PublicKey> findAllKeys() {
		return publicKeyDao.getAllPublicKeys();
	}

	@Override
	public void update(PublicKey publicKey) {
		Criteria criteria = Criteria.where("publicKey").is(publicKey.getPublicKey());
		Query query = new Query();
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("verifyFlag", publicKey.getVerifyFlag());
		publicKeyDao.update(query, update);
	}

}
