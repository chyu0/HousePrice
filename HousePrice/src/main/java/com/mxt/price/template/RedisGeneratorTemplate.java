package com.mxt.price.template;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.mxt.price.utils.ProtoStuffSerializerUtils;

public class RedisGeneratorTemplate<T> {

	protected static String CACHENAME;// 缓存名  
	protected static final int CACHETIME = 60;// 默认缓存时间 60S  
	protected static final int CACHEHOUR = 60 * 60;// 默认缓存时间 1hr  
	protected static final int CACHEDAY = 60 * 60 * 24;// 默认缓存时间 1Day  
	protected static final int CACHEWEEK = 60 * 60 * 24 * 7;// 默认缓存时间 1week  
	protected static final int CACHEMONTH = 60 * 60 * 24 * 30;// 默认缓存时间 1month  
    
    @Autowired  
    private RedisTemplate<String, String> redisTemplate;  
    
    @SuppressWarnings("unchecked")
	private Class<T> entityClass = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]; 
    /**
     * 将一个对象以字节码形式存入redis，如果没有就插入，有就跳过
     * @param key
     * @param obj
     * @return 
     */
    public boolean putCache(String key, T obj) {  
        final byte[] bkey = key.getBytes();  
        final byte[] bvalue = ProtoStuffSerializerUtils.serialize(obj);  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.setNX(bkey, bvalue);  
            }  
        });  
        return result;  
    }  
  
    /**
     * 将一个对象以字节码形式存入redis，如果没有就插入，有就跳过，过期时间未expireTime
     * @param key
     * @param obj
     * @param expireTime
     */
    public void putCacheWithExpireTime(String key, T obj, final long expireTime) {  
        final byte[] bkey = key.getBytes();  
        final byte[] bvalue = ProtoStuffSerializerUtils.serialize(obj);  
        redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                connection.setEx(bkey, expireTime, bvalue);  
                return true;  
            }  
        });  
    }  
  
    /**
     * 将一个List对象以字节码形式存入redis，如果没有就插入，有就跳过
     * @param key
     * @param objList
     * @return
     */
    public boolean putListCache(String key, List<T> objList) {  
        final byte[] bkey = key.getBytes();  
        final byte[] bvalue = ProtoStuffSerializerUtils.serializeList(objList);  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.setNX(bkey, bvalue);  
            }  
        });  
        return result;  
    }  
  
    /**
     * 将一个List对象以字节码形式存入redis，如果没有就插入，有就跳过，过期时间未expireTime
     * @param key
     * @param objList
     * @param expireTime
     * @return
     */
    public boolean putListCacheWithExpireTime(String key, List<T> objList, final long expireTime) {  
        final byte[] bkey = key.getBytes();  
        final byte[] bvalue = ProtoStuffSerializerUtils.serializeList(objList);  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                connection.setEx(bkey, expireTime, bvalue);  
                return true;  
            }  
        });  
        return result;  
    }  
  
    /**
     * 从redis中获取字节码对象，并反序列话成Java对象
     * @param key
     * @param targetClass
     * @return
     */
    public T getCache(final String key) {  
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.get(key.getBytes());  
            }  
        });  
        if (result == null) {  
            return null;  
        }  
        return ProtoStuffSerializerUtils.deserialize(result, entityClass);  
    }  
  
    /**
     * 从redis中获取字节码对象，并反序列话成java List对象
     * @param key
     * @param targetClass
     * @return
     */
    public List<T> getListCache(final String key) {  
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.get(key.getBytes());  
            }  
        });  
        if (result == null) {  
            return null;  
        }  
        return ProtoStuffSerializerUtils.deserializeList(result, entityClass);  
    }  
  
    /** 
     * 精确删除key 
     * @param key 
     */  
    public void deleteCache(String key) {  
        redisTemplate.delete(key);  
    }  
  
    /** 
     * 模糊删除key 
     * @param pattern 
     */  
    public void deleteCacheWithPattern(String pattern) {  
        Set<String> keys = redisTemplate.keys(pattern);  
        redisTemplate.delete(keys);  
    }  
  
    /** 
     * 清空所有缓存 
     */  
    public void clearCache() {  
        deleteCacheWithPattern(RedisGeneratorTemplate.CACHENAME + "|*");  
    }

    /**
     * 从redis列表末尾插入values记录
     * @param key
     * @param values
     * @return 插入对象列表
     */
    public Long lPush(final String key , T value){
    	final byte[] bkey = key.getBytes();  
        final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);  
        return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lPush(bkey, bvalue);
            }  
        });  
    }
    
    /**
     * 从redis列表头部插入values记录
     * @param key
     * @param values
     * @return 插入对象列表
     */
    public Long rPush(final String key , T value){
    	final byte[] bkey = key.getBytes();  
        final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);  
        return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.rPush(bkey, bvalue);
            }  
        });  
    }
    
    /**
     * 从redis列表尾部移除一个对象
     * @param key
     * @return 移除的对象
     */
    public T lPop(final String key){
    	final byte[] bkey = key.getBytes();  
    	byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lPop(bkey);
            }  
        });  
    	return ProtoStuffSerializerUtils.deserialize(result, entityClass);
    }
    
    /**
     * 从redis列表尾部第一个对象
     * @param key
     * @return 移除的对象
     */
    public T rPop(final String key){
    	final byte[] bkey = key.getBytes();  
    	byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.rPop(bkey);
            }  
        });  
    	return ProtoStuffSerializerUtils.deserialize(result, entityClass);
    }
    
    /**
     * 从redis列表移除前count个值为value的元素
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Long lRem(final String key , final long count , T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);  
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lRem(bkey , count ,bvalue);
            }  
        });  
    }
    
    /**
     * 获取list中 start至end中的所有元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<T> lRange(final String key , final long start ,final long end){
    	final byte[] bkey = key.getBytes();  
    	List<byte[]> results = redisTemplate.execute(new RedisCallback<List<byte[]>>() {  
            @Override  
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lRange(bkey, start, end);
            }  
        });  
    	List<T> list = new ArrayList<T>();
    	for(byte[] result : results){
    		list.add(ProtoStuffSerializerUtils.deserialize(result, entityClass));
    	}
    	return list;
    }
    
    /**
     * 设置缓存名称
     * @param cacheName
     */
	private static void setCACHENAME(String cacheName) { CACHENAME = cacheName; }
	@Value("${cache_name}")
	public void setCACHENAMEVal(String cacheName) {setCACHENAME(cacheName);};
	
}
