package com.mxt.price.template;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.mxt.price.utils.ProtoStuffSerializerUtils;

/**
 * redistemplate ， 操作redis
 * @author maoxiaotai
 * @data 2017年10月25日 下午9:17:40
 * @Description 对redis做操作
 */
public class RedisGeneratorTemplate<T> {

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
    protected boolean putCache(String key, T obj) {  
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
    protected void putCacheWithExpireTime(String key, T obj, final long expireTime) {  
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
     * 从redis中获取字节码对象，并反序列话成Java对象
     * @param key
     * @param targetClass
     * @return
     */
    protected T getCache(final String key) {  
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
    protected List<T> getListCache(String key) {  
    	final byte[] bkey = key.getBytes();
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.get(bkey);  
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
    protected void deleteCache(String key) {  
        redisTemplate.delete(key);  
    }  
  
    /** 
     * 模糊删除key 
     * @param pattern 
     */  
    protected void deleteCacheWithPattern(String pattern) {  
        Set<String> keys = redisTemplate.keys(pattern);  
        redisTemplate.delete(keys);  
    }  
  
    /**
     * 从redis列表末尾插入value对象
     * @param key
     * @param value
     * @return 插入对象列表
     */
	protected Long lPush(String key , @SuppressWarnings("unchecked") T... values){
    	return lPush(key ,Arrays.asList(values));
    }
    
    /**
     * 从redis列表头部插入value对象
     * @param key
     * @param values
     * @return 插入对象列表
     */
	protected Long rPush(String key , @SuppressWarnings("unchecked") T... values){
    	return rPush(key , Arrays.asList(values));
    }
	
	/**
	 * 在列表头部插入values的列表数据
	 * @param key
	 * @param values
	 * @return
	 */
	protected Long lPush(String key , List<T> values){
		if(values == null || values.size() <= 0){
			return null;
		}
		final byte[] bkey = key.getBytes();  
    	final byte[][] bvalues = new byte[values.size()][];
    	for(int i=0 ; i<values.size() ; i++){
    		bvalues[i] = ProtoStuffSerializerUtils.serialize(values.get(i));
    	}
        return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lPush(bkey, bvalues);
            }  
        });  
	}
	
	/**
	 * 在列表尾部插入values的列表数据
	 * @param key
	 * @param values 带插入数据
	 * @return
	 */
	protected Long rPush(String key , List<T> values){
		if(values == null || values.size() <= 0){
			return null;
		}
		final byte[] bkey = key.getBytes();  
    	final byte[][] bvalues = new byte[values.size()][];
    	for(int i=0 ; i<values.size() ; i++){
    		bvalues[i] = ProtoStuffSerializerUtils.serialize(values.get(i));
    	}
        return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.rPush(bkey, bvalues);
            }  
        });  
	}
    
    /**
     * 从列表中移除最后一个元素，并返回
     * @param key
     * @return 移除的对象
     */
    protected T lPop(String key){
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
     * 从列表中移除第一个元素，并返回
     * @param key
     * @return 移除的对象
     */
    protected T rPop(String key){
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
    protected Long lRem(String key , final long count , T value){
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
     * 截取出start到end之间的列表
     * @param key
     * @param start
     * @param end
     */
    protected void lTrim(String key , final long start ,final long end){
    	final byte[] bkey = key.getBytes();  
    	redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                connection.lTrim(bkey, start, end);
                return true;
            }  
        });  
    }
    
    /**
     * 获取List列表长度
     * @param key
     */
    protected Long lLen(String key){
    	final byte[] bkey = key.getBytes();  
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lLen(bkey);
            }  
        });  
    }
    
    /**
     * 更新list中索引index的值
     * @param key
     * @param index
     * @param value
     */
    protected void lSet(String key ,final long index, T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                connection.lSet(bkey, index, bvalue);
                return true;
            }  
        });  
    }
    
    /**
     * 获取list中 start至end中的所有元素，超出范围，获取所有元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    protected List<T> lRange(final String key , final long start ,final long end){
    	final byte[] bkey = key.getBytes();  
    	List<byte[]> results = redisTemplate.execute(new RedisCallback<List<byte[]>>() {  
            @Override  
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lRange(bkey, start, end);
            }  
        });  
    	if(results == null || results.size() <= 0){
    		return null;
    	}
    	List<T> list = new ArrayList<T>();
    	for(byte[] result : results){
    		list.add(ProtoStuffSerializerUtils.deserialize(result, entityClass));
    	}
    	return list;
    }
    
    /**
     * 获取索引为index的对象
     * @param key
     * @param index
     * @return
     */
    protected T lIndex(String key , final long index){
    	final byte[] bkey = key.getBytes();  
    	byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.lIndex(bkey, index);
            }  
        });
    	if(result == null){
    		return null;
    	}
    	return ProtoStuffSerializerUtils.deserialize(result, entityClass);
    }
    
    /**
     * set集合中插入元素，分值为score，如果该元素存在，则更新分值
     * @param key
     * @param score
     * @param value
     * @return
     */
    protected Boolean zAdd(String key , final double score , T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zAdd(bkey , score , bvalue);
            }  
        });  
    }
    
    /**
     * 获取set集合大小
     * @param key
     * @return
     */
    protected Long zCard(String key){
    	final byte[] bkey = key.getBytes();  
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zCard(bkey);
            }  
        });  
    }
    
    /**
     * 获取有序集合分数由低到高的排名
     * @param key
     * @return
     */
    protected Long zRank(String key, T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRank(bkey, bvalue);
            }  
        });  
    }
    
    /**
     * 获取有序集合分数由高到低的排名
     * @param key
     * @return
     */
    protected Long zRevRank(String key, T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRevRank(bkey, bvalue);
            }  
        });  
    }
    
    /**
     * 获取分值在minScore到maxScore之间的元素个数
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    protected Long zCount(String key ,final double minScore ,final double maxScore){
    	final byte[] bkey = key.getBytes();  
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zCount(bkey, minScore, maxScore);
            }  
        });  
    }
    
    /**
     * 为对象value加increment分
     * @param key
     * @param increment
     * @param value
     * @return
     */
    protected Double zIncrBy(String key , final double increment , T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Double>() {  
            @Override  
            public Double doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zIncrBy(bkey, increment, bvalue);
            }  
        });  
    }
    
    /**
     * 求sets集合交集，分数是所有交集元素score的和，并存入destkey
     * @param destKey
     * @param sets
     * @return
     */
    protected Long zInterStore(String destKey , List<T> sets){
    	if(sets == null || sets.size() <= 0){
    		return null;
    	}
    	final byte[] bkey = destKey.getBytes();  
    	final byte[][] bsets = new byte[sets.size()][];
    	for(int i = 0 ; i < sets.size() ;i++){
    		bsets[i] = ProtoStuffSerializerUtils.serialize(sets.get(i));
    	}
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zInterStore(bkey, bsets);
            }  
        });  
    }
    
    /**
     * 求sets集合交集，分数从aggregate来判断是求和，最大，最小值作为分值，weight为每个集合成的系数
     * @param destKey
     * @param aggregate
     * @param weights
     * @param sets
     * @return
     */
    protected Long zInterStore(String destKey , final RedisZSetCommands.Aggregate aggregate, final int[] weights, List<T> sets){
    	if(sets==null || weights.length != sets.size()){
    		return 0L;
    	}
    	final byte[] bkey = destKey.getBytes();  
    	final byte[][] bsets = new byte[sets.size()][];
    	for(int i = 0 ; i < sets.size() ;i++){
    		bsets[i] = ProtoStuffSerializerUtils.serialize(sets.get(i));
    	}
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zInterStore(bkey, aggregate, weights, bsets);
            }  
        });  
    }
    
    /**
     * 求sets集合交集，分数是所有交集元素score的和，并存入destkey
     * @param destKey
     * @param sets
     * @return
     */
    protected Long zInterStore(String destKey , @SuppressWarnings("unchecked") T... sets){
    	return zInterStore(destKey, Arrays.asList(sets));
    }
    
    /**
     * 求sets集合并集，并存入destkey
     * @param destKey
     * @param sets
     * @return
     */
    protected Long zUnionStore(String destKey , List<T> sets){
    	if(sets == null || sets.size() <= 0){
    		return null;
    	}
    	final byte[] bkey = destKey.getBytes();  
    	final byte[][] bsets = new byte[sets.size()][];
    	for(int i = 0 ; i < sets.size() ;i++){
    		bsets[i] = ProtoStuffSerializerUtils.serialize(sets.get(i));
    	}
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zUnionStore(bkey, bsets);
            }  
        });  
    }
    
    /**
     * 求sets集合并集，并存入destkey
     * @param destKey
     * @param sets
     * @return
     */
    protected Long zUnionStore(String destKey , @SuppressWarnings("unchecked") T... sets){
    	return zUnionStore(destKey, Arrays.asList(sets));
    }
    
    /**
     * 求sets集合并集，分数从aggregate来判断是求和，最大，最小值作为分值，weight为每个集合成的系数
     * @param destKey
     * @param aggregate
     * @param weights
     * @param sets
     * @return
     */
    protected Long zUnionStore(String destKey , final RedisZSetCommands.Aggregate aggregate, final int[] weights, List<T> sets){
    	if(sets == null || weights.length != sets.size()){
    		return null;
    	}
    	final byte[] bkey = destKey.getBytes();  
    	final byte[][] bsets = new byte[sets.size()][];
    	for(int i = 0 ; i < sets.size() ;i++){
    		bsets[i] = ProtoStuffSerializerUtils.serialize(sets.get(i));
    	}
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zUnionStore(bkey, aggregate, weights, bsets);
            }  
        });  
    }
    
    /**
     * 获取Set集合中set至end间所有元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    protected List<T> zRange(String key , final long start ,final long end){
    	final byte[] bkey = key.getBytes();  
    	Set<byte[]> sets =  redisTemplate.execute(new RedisCallback<Set<byte[]>>() {  
            @Override  
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRange(bkey, start, end);
            }  
        }); 
    	if(sets == null || sets.size() <= 0){
    		return null;
    	}
    	List<T> newSets = new ArrayList<T>();
    	for(byte[] set : sets){
    		newSets.add(ProtoStuffSerializerUtils.deserialize(set, entityClass));
    	}
    	return newSets;
    }
    
    /**
     * 获取Set集合中set至end间所有元素，并由高到低排列
     * @param key
     * @param start
     * @param end
     * @return
     */
    protected List<T> zRevRange(String key , final long start ,final long end){
    	final byte[] bkey = key.getBytes();  
    	Set<byte[]> sets =  redisTemplate.execute(new RedisCallback<Set<byte[]>>() {  
            @Override  
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRevRange(bkey, start, end);
            }  
        }); 
    	if(sets == null || sets.size() <= 0){
    		return null;
    	}
    	List<T> newSets = new ArrayList<T>();
    	for(byte[] set : sets){
    		newSets.add(ProtoStuffSerializerUtils.deserialize(set, entityClass));
    	}
    	return newSets;
    }
    
    /**
     * 获取分值在minScore至maxScore之间的所有元素
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    protected List<T> zRangeByScore(String key , final double minScore ,final double maxScore){
    	final byte[] bkey = key.getBytes();  
    	Set<byte[]> sets =  redisTemplate.execute(new RedisCallback<Set<byte[]>>() {  
            @Override  
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRangeByScore(bkey, minScore, maxScore);
            }  
        }); 
    	if(sets == null || sets.size() <= 0){
    		return null;
    	}
    	List<T> newSets = new ArrayList<T>();
    	for(byte[] set : sets){
    		newSets.add(ProtoStuffSerializerUtils.deserialize(set, entityClass));
    	}
    	return newSets;
    }
    
    /**
     * 获取分值在minScore至maxScore之间的所有元素，并由高到低排列
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    protected List<T> zRevRangeByScore(String key , final double minScore ,final double maxScore){
    	final byte[] bkey = key.getBytes();  
    	Set<byte[]> sets =  redisTemplate.execute(new RedisCallback<Set<byte[]>>() {  
            @Override  
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRevRangeByScore(bkey, minScore, maxScore);
            }  
        }); 
    	if(sets == null || sets.size() <= 0){
    		return null;
    	}
    	List<T> newSets = new ArrayList<T>();
    	for(byte[] set : sets){
    		newSets.add(ProtoStuffSerializerUtils.deserialize(set, entityClass));
    	}
    	return newSets;
    }
    
    /**
     * 移除values的所有元素
     * @param key
     * @param values
     * @return
     */
    protected Long zRem(String key , List<T> values){
    	final byte[] bkey = key.getBytes();  
    	final byte[][] bvalues = new byte[values.size()][];
    	for(int index = 0 ;index < values.size() ;index++){
    		bvalues[index] = ProtoStuffSerializerUtils.serialize(values.get(index));
    	}
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRem(bkey, bvalues);
            }  
        }); 
    }
    
    /**
     * 移除start至end的所有角色
     * @param key
     * @param start
     * @param end
     * @return
     */
    protected Long zRemRange(String key , final long start , final long end){
    	final byte[] bkey = key.getBytes();  
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRemRange(bkey, start , end);
            }  
        }); 
    }
    
    /**
     * 移除分值为minScore至maxScore之间的元素
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    protected Long zRemRangeByScore(String key, final double minScore ,final double maxScore){
    	final byte[] bkey = key.getBytes();  
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zRemRangeByScore(bkey, minScore , maxScore);
            }  
        }); 
    }
    
    /**
     * 获取value对应的分值
     * @param key
     * @param value
     * @return
     */
    protected Double zScore(String key , T value){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Double>() {  
            @Override  
            public Double doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.zScore(bkey, bvalue);
            }  
        }); 
    }
    
    /**
     * 向Set集合中插入一个元素，如果元素大小超过限制，则移除分值最小的元素
     * @param key
     * @param limit
     * @param value
     * @param score
     * @return
     */
    protected Boolean zAddRemMinScore(String key , final long limit , T value , final double score){
    	final byte[] bkey = key.getBytes();  
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
            	connection.zAdd(bkey, score , bvalue);
            	long count = connection.zCard(bkey);//元素个数
                if(count > limit){
                	connection.zRemRange(bkey, 0 , count - limit);
                }
                return true;
            }  
        }); 
    }
    
    /**
     * 插入hash
     * @param key
     * @param field
     * @param value
     * @return
     */
    protected Boolean hSet(String key , String field , T value){
    	final byte[] bkey = key.getBytes(); 
    	final byte[] bfield = field.getBytes();
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hSet(bkey, bfield, bvalue);
            }  
        }); 
    }
    
    /**
     * 不存在时插入hash，存在时不插入
     * @param key
     * @param field
     * @param value
     * @return
     */
    protected Boolean hSetNX(String key, String field, T value){
    	final byte[] bkey = key.getBytes(); 
    	final byte[] bfield = field.getBytes();
    	final byte[] bvalue = ProtoStuffSerializerUtils.serialize(value);
    	return redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hSetNX(bkey, bfield, bvalue);
            }  
        }); 
    }
    
    /**
     * 从hash中获取对象
     * @param key
     * @param field
     * @return
     */
    protected T hGet(String key , String field){
    	final byte[] bkey = key.getBytes(); 
    	final byte[] bfield = field.getBytes();
    	byte[] result =  redisTemplate.execute(new RedisCallback<byte[]>() {  
            @Override  
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hGet(bkey, bfield);
            }  
        }); 
    	if(result == null){
    		return null;
    	}
    	return ProtoStuffSerializerUtils.deserialize(result, entityClass);
    }
    
    /**
     * 获取fields对应的对象集合列表
     * @param key
     * @param fields
     * @return
     */
    protected List<T> hMGet(String key, List<String> fields){
    	if(fields==null || fields.size()<=0){
    		return null;
    	}
    	final byte[] bkey = key.getBytes(); 
    	final byte[][] bfields = new byte[fields.size()][];
    	for(int index = 0; index < fields.size() ; index++){
    		bfields[index] = fields.get(index).getBytes();
    	}
    	List<byte[]> result =  redisTemplate.execute(new RedisCallback<List<byte[]>>() {  
            @Override  
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hMGet(bkey, bfields);
            }  
        }); 
    	if(result == null || result.size() <= 0){
    		return null;
    	}
    	List<T> values = new ArrayList<T>();
    	for(byte[] value : result){
    		values.add(ProtoStuffSerializerUtils.deserialize(value, entityClass));
    	}
    	return values;
    }
    
    /**
     * 获取fields对应的对象集合列表
     * @param key
     * @param fields
     * @return
     */
    protected List<T> hMGet(String key, String... fields){
    	return hMGet(key, Arrays.asList(fields));
    }
    
    /**
     * 批量插入hash
     * @param key
     * @param hashes
     */
    protected void hMSet(String key, Map<String, T> hashes){
    	if(hashes == null || hashes.size() <= 0){
    		return ;
    	}
    	final byte[] bkey = key.getBytes(); 
    	final Map<byte[],byte[]> bhashes = new HashMap<byte[],byte[]>();
    	for(Map.Entry<String, T> entry : hashes.entrySet()){
    		bhashes.put(entry.getKey().getBytes(), ProtoStuffSerializerUtils.serialize(entry.getValue()));
    	}
    	redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                connection.hMSet(bkey, bhashes);
                return true;
            }  
        }); 
    }
    
    /**
     * 获取hash的大小
     * @param key
     * @return
     */
    protected Long hLen(String key){
    	final byte[] bkey = key.getBytes();
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hLen(bkey);
            }  
        }); 
    }
    
    /**
     * 判断域field是否存在
     * @param key
     * @param field
     * @return
     */
    protected Boolean hExists(String key , String field){
    	final byte[] bkey = key.getBytes();
    	final byte[] bfield = field.getBytes();
    	return redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hExists(bkey ,bfield);
            }  
        }); 
    }
    
    /**
     * 从hash中删除键为fields集合中的所有元素
     * @param key
     * @param fields
     * @return
     */
    protected Long hDel(String key, List<String> fields){
    	if(fields == null || fields.size() <=0 ){
    		return 0L;
    	}
    	final byte[] bkey = key.getBytes();
    	final byte[][] bfields = new byte[fields.size()][];
    	for(int index = 0 ; index < fields.size() ;index ++){
    		bfields[index] = fields.get(index).getBytes();
    	}
    	return redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hDel(bkey , bfields);
            }  
        }); 
    }
    
    /**
     * 删除所有键在fields内的元素
     * @param key
     * @param fields
     * @return
     */
    protected Long hDel(String key , String... fields){
    	return hDel(key , Arrays.asList(fields));
    }
    
    /**
     * 获取hash所有key
     * @return
     */
    protected Set<String> hKeys(String key){
    	final byte[] bkey = key.getBytes();
    	Set<byte[]> bkeys =  redisTemplate.execute(new RedisCallback<Set<byte[]>>() {  
            @Override  
            public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hKeys(bkey);
            }  
        }); 
    	if(bkeys == null || bkeys.size() <= 0){
    		return null;
    	}
    	Set<String> sets = new HashSet<String>();
    	for(byte[] k : bkeys){
    		sets.add(ProtoStuffSerializerUtils.deserialize(k, String.class));
    	}
    	return sets;
    }
    
    /**
     * 获取hash所有value
     * @return
     */
    protected Set<T> hVals(String key){
    	final byte[] bkey = key.getBytes();
    	List<byte[]> bvalues =  redisTemplate.execute(new RedisCallback<List<byte[]>>() {  
            @Override  
            public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hVals(bkey);
            }  
        }); 
    	if(bvalues == null || bvalues.size() <= 0){
    		return null;
    	}
    	Set<T> sets = new HashSet<T>();
    	for(byte[] v : bvalues){
    		sets.add(ProtoStuffSerializerUtils.deserialize(v, entityClass));
    	}
    	return sets;
    }
    
    /**
     * 获取所有hash
     * @param key
     * @return
     */
    protected Map<String,T> hGetAll(final String key){
    	Map<byte[],byte[]> bresult = redisTemplate.execute(new RedisCallback<Map<byte[],byte[]>>() {  
            @Override  
            public Map<byte[],byte[]> doInRedis(RedisConnection connection) throws DataAccessException {  
                return connection.hGetAll(key.getBytes());
            }  
        }); 
    	if(bresult == null || bresult.size() <= 0){
    		return null;
    	}
    	Map<String , T> result = new HashMap<String, T>();
    	if(bresult != null && bresult.size() > 0){
    		for(Map.Entry<byte[], byte[]> entry : bresult.entrySet()){
    			result.put(ProtoStuffSerializerUtils.deserialize(entry.getKey(), String.class), ProtoStuffSerializerUtils.deserialize(entry.getValue(), entityClass));
    		}
    	}
    	return result;
    }
    
}
