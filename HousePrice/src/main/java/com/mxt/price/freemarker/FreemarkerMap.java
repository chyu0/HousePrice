package com.mxt.price.freemarker;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import com.mxt.price.handler.ExceptionHandler;
import com.mxt.price.utils.CommonUtils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:43:18
 * @Description FreeMarker扫描类，扫描Freemarker相关包，加载静态类
 */
public class FreemarkerMap extends ModelMap implements BeanFactoryPostProcessor{
	
	private static final long serialVersionUID = -4675940717727748450L;
	private List<String> locations;
	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class); 
	
	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	private FreemarkerMap(){}
	
	private static volatile FreemarkerMap instance;
	
	/**
	 * 懒加载
	 * @return
	 */
	public static FreemarkerMap getInstance(){
		if(instance == null){
			synchronized (FreemarkerMap.class) {
				if(instance == null){
					instance = new FreemarkerMap();
				}
			}
		}
		return instance;
	}	

	/**
	 * 后置处理器
	 */
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		loadCheckClassMethods(locations);
	}
	
	/**
	 * 根据扫描包的配置
	 * 加载需要检查的方法
	 */
	private static void loadCheckClassMethods(List<String> scanPackages) {
	    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	    MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
	    for (String basePackage : scanPackages) {
	        if (StringUtils.isBlank(basePackage)) {
	            continue;
	        }
	        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
	            ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/" + DEFAULT_RESOURCE_PATTERN ;
	        try {
	            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
	            for (Resource resource : resources) {
	                loadClassMethod(metadataReaderFactory, resource);
	            }
	        } catch (Exception e) {
	        	logger.error("初始化失败"+CommonUtils.exceptionToStr(e));
	        }
	 
	    }
	}
	
	/**
	 * 加载资源，判断里面的方法
	 *
	 * @param metadataReaderFactory spring中用来读取resource为class的工具
	 * @param resource              这里的资源就是一个Class
	 * @throws IOException
	 */
	private static void loadClassMethod(MetadataReaderFactory metadataReaderFactory, Resource resource) throws IOException {
	    try {
	        if (resource.isReadable()) {
	            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
	            if (metadataReader != null) {
	                String className = metadataReader.getClassMetadata().getClassName();
	                try {
	                    tryCacheMethod(className);
	                } catch (ClassNotFoundException e) {
	                	logger.error("检查" + className + "是否含有需要信息失败" + CommonUtils.exceptionToStr(e));
	                }
	            }
	        }
	    } catch (Exception e) {
	    	logger.error("判断类中的方法实现检测失败" + CommonUtils.exceptionToStr(e));
	    }
	}
	
	/**
	 * 把action下面的所有method遍历一次，标记他们是否需要进行xxx验证
	 * 如果需要，放入cache中
	 *
	 * @param fullClassName
	 * @throws TemplateModelException 
	 */
	private static void tryCacheMethod(String fullClassName) throws ClassNotFoundException, TemplateModelException {
	    Class<?> clz = Class.forName(fullClassName);
	    Method[] methods = clz.getDeclaredMethods();
	    for (Method method : methods) {
	    	int mod = method.getModifiers();
	        if (Modifier.isStatic(mod)&&Modifier.isPublic(mod)) {
	            BeansWrapper beansWrapper = BeansWrapper.getDefaultInstance();
	            TemplateHashModel model = beansWrapper.getStaticModels();
	            instance.put(clz.getSimpleName(), (TemplateHashModel)model.get(clz.getName()));
	            if(logger.isDebugEnabled()){
	            	logger.debug("加载类：" + clz.getName());
	            }
	            break;
	        }
	    }
	}
}
