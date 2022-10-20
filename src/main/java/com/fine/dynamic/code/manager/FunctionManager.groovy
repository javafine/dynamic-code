package com.fine.dynamic.code.manager

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.system.ApplicationHome
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service

@Service
@Scope('singleton')
class FunctionManager {
	public static String CODE_PATH_DEFAULT = "release\\code"
	public static String CODE_DIR_DEFAULT = "code"

	Logger logger = LoggerFactory.getLogger(FunctionManager.class)
	GroovyScriptEngine groovyScriptEngine

	private static String getCodefDir() {
		ApplicationHome home = new ApplicationHome(FunctionManager.class)
		if (home==null || home.getSource()==null){
			//开发环境junit启动时
			return CODE_PATH_DEFAULT
		}
		File execParentDir = home.getSource().getParentFile()
		File file = new File(execParentDir, CODE_DIR_DEFAULT)
		if (file.exists()) {
			return file.getPath()
		}
		//开发环境main方法启动时
		return CODE_PATH_DEFAULT
	}

//	@Value('${dynamic.code}')
//	void setCODE(String CODE) {
//		FunctionManager.CODE = CODE
//	}
	private static FunctionManager instance = new FunctionManager()
	private static final String FLAG_NULL_FUNCTION = "doCall=public java.lang.String com.cyou.cybaas.code.manager.FunctionManager.nullFunction"
	/**
	 * @Title: findObject
	 * @Description: 根据文件名生成对象
	 * @param name 文件名，无路径要求
	 * @return    
	 * GroovyObject 根据文件生成的对象
	 * @throws
	 */
	GroovyObject findObject(String name) {
		if(groovyScriptEngine==null){
			groovyScriptEngine = new GroovyScriptEngine([getCodefDir()] as String[])
		}
		return (GroovyObject) groovyScriptEngine.loadScriptByName(name).newInstance()
	} 

	/**
	 * @Title: addFunction
	 * @Description: 根据文件名和其中的方法名生成方法
	 * @param fileName 文件名（无后缀）
	 * @param functionName 方法名
	 * @return    
	 * boolean 类的元对象是否已更新成功
	 * @throws
	 */
	boolean loadFunction(String fileName, String functionName){
		logger.info("loadFunction fileName : "+fileName)
		//这个对象是否会回收
		GroovyObject groovyObject = findObject(fileName+".groovy")
		if(groovyObject == null){
			logger.info("loadFunction can not found fileName : "+fileName)
			return false
		}
		String mopName = fileName+"-"+functionName
		//functionName方法的指针指向新生成对象的方法
		FunctionManager.metaClass."${mopName}" = groovyObject.&"${functionName}"
		//更新实例，后续请求才会指向新的方法
		instance = new FunctionManager()
		logger.info("loadFunction load  : "+mopName)
		return(null!=FunctionManager.metaClass.pickMethod(mopName, [Map.class, Map.class] as Class[]))
//		return ((ExpandoMetaClass)FunctionManager.metaClass).hasMetaMethod(mopName, [Map.class, Map.class] as Class[])
	}

	boolean reloadFunction(String fileName, String functionName){
		GroovyObject groovyObject = findObject(fileName+".groovy")
		if(groovyObject == null){
			logger.info("reloadFunction can not found fileName : "+fileName)
			return false
		}
		String mopName = fileName+"-"+functionName
		//文件内不存在的方法将被置为空方法
		if(null==groovyObject.getMetaClass().pickMethod(functionName, [Map.class, Map.class] as Class[])){
			logger.info("reloadFunction nullFunction : "+mopName)
			FunctionManager.metaClass."${mopName}"=instance.&nullFunction
			return false
		}

		return loadFunction(fileName, functionName)
	}
	
	String removeFunction(String fileName, String functionName){
		String mopName = fileName+"-"+functionName
		if(FunctionManager.metaClass.pickMethod(mopName, [Map.class, Map.class] as Class[])!=null){
			FunctionManager.metaClass."${mopName}"=instance.&nullFunction
			instance = new FunctionManager()
			return "removeFunction successfull"
		}
		return "removeFunction not found "+functionName
	}

	String nullFunction(Map mapParam, Map mapHeader){
		logger.info("execute null function, param : "+mapParam+", header : "+mapHeader)
		return "removed"
	}

//	String showFunction(String mopName){
//		if(((ExpandoMetaClass)FunctionManager.metaClass).hasMetaMethod(mopName, [Map.class, Map.class] as Class[])){
//			return FunctionManager.metaClass.getMetaMethod(mopName, [Map.class, Map.class] as Class[]).dump()
//		}
//	}

	/**
	 * @Title: execute
	 * @Description: Controller调用此方法执行instance的方法名为f的方法
	 * @param functionName 方法名
	 * @param mapParam 参数
	 * @param mapHeader 请求头数据
	 * @return
	 * Object 方法返回对象
	 * @throws
	 */
	Object execute(String fileName, String functionName, Map<String, Object> mapParam, Map<String, String> mapHeader) {
		return execute(fileName+"-"+functionName, mapParam, mapHeader)
	}
	Object execute(String mopName, Map<String, Object> mapParam, Map<String, String> mapHeader){
		Object ret = instance."${mopName}"(mapParam, mapHeader)
		if("removed".equals(ret)) {
			logger.info("Function removed：" + mopName)
		}
		return ret
	}

	/**
	 * @Title: methodMissing
	 * @Description: Groovy supports the concept of methodMissing. This method differs from invokeMethod in that it is only invoked in the case of a failed method dispatch when no method can be found for the given name and/or the given arguments
	 * 				请求instance没有的方法都会执行这个方法，从请求头里获取的x-cy-appid组装文件名查找functionName后更新实例
	 * @param functionName 未找到的方法名
	 * @param args 方法参数
	 * @return
	 * Object 执行加载后的方法或者抛出异常
	 * @throws MissingMethodException 不存在方法，也没有找到可加载的方法
	 */
	Object methodMissing(String mopName, def args){
		String fileName = mopName.split("-")[0]
		String functionName = mopName.split("-")[1]
		if(loadFunction(fileName, functionName)){
			logger.info("methodMissing and loadFunction, functionName:"+functionName)
			return instance."${mopName}"(args[0], args[1])
		} else {
			logger.info("methodMissing : "+functionName)
			return "methodMissing error"
		}
	}

}
