package com.fine.dynamic.code.controller;

import com.fine.dynamic.code.manager.ConfigurationManager;
import com.fine.dynamic.code.manager.FunctionManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 9:52 2022/10/10
 */
@RestController
public class ManagerController {
    public static String PARAM_PATHNAME = "pathName";
    public static String REQUEST_BODY = "requestBody";

    @Resource
    FunctionManager functionManager;
    @Resource
    private ConfigurationManager configurationManager;

    /**
     * 执行fileName文件中functionName方法
     * @param fileName 代码文件名
     * @param functionName 方法名
     * @return functionName执行结果
     */
    @RequestMapping(value = {"/file/{fileName}/function/{functionName}"}, produces="application/json;charset=UTF-8")
    Object execute(@RequestBody(required=false) String body, @PathVariable(name = "fileName") String fileName, @PathVariable(name = "functionName") String functionName, HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> mapParam = new HashMap<String, Object>();
        if(body!=null && !body.isEmpty()){
            mapParam.put(REQUEST_BODY, body);
        }
//		mapParam.putAll(request.getParameterMap());
        Enumeration<String> paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement();
            mapParam.put(paramName, request.getParameter(paramName));
        }

        Map<String, String> mapHeader = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            mapHeader.put(headerName.toLowerCase(), request.getHeader(headerName));
        }
        return functionManager.execute(fileName, functionName, mapParam, mapHeader);
    }

    /**
     * 加载fileName文件中functionName方法，一般情况下先请求{@link ManagerController#remove(String, String)}
     * @param fileName 代码文件名
     * @param functionName 方法名
     * @return boolean值，成功为true
     */
    @RequestMapping("/meta/load/file/{fileName}/function/{functionName}")
    Object load(@PathVariable(name = "fileName") String fileName, @PathVariable(name = "functionName") String functionName){
        return functionManager.loadFunction(fileName, functionName);
    }

    /**
     * 重新加载fileName文件中functionName方法
     * @param fileName 代码文件名
     * @param functionName 方法名
     * @return boolean值，成功为true
     */
    @RequestMapping("/meta/reload/file/{fileName}/function/{functionName}")
    Object reload(@PathVariable(name = "fileName") String fileName, @PathVariable(name = "functionName") String functionName){
        return functionManager.reloadFunction(fileName, functionName);
    }

    /**
     * 去除fileName文件中functionName方法
     * @param fileName 代码文件名
     * @param functionName 方法名
     * @return 字符串“removeFunction successfull”
     */
    @RequestMapping("/meta/remove/file/{fileName}/function/{functionName}")
    Object remove(@PathVariable(name = "fileName") String fileName, @PathVariable(name = "functionName") String functionName){
        return functionManager.removeFunction(fileName, functionName);
    }

    /**
     * 根据fileName重新加载配置文件
     * @param fileName 配置文件名，没有后缀
     * @return json对象形式的文件内容
     */
    @RequestMapping(value = "/conf/load", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public Object load(String fileName) {
        if(fileName!=null && !fileName.isEmpty()) {
            return configurationManager.load(fileName);
        }
        return "please input valid fileName without file suffix";
    }

    /**
     * @param fileName 配置文件名，没有后缀
     * @return json对象形式的文件内容
     */
    @RequestMapping(value = "/conf/get/{fileName}")
    public Object getConfig(@PathVariable(name = "fileName") String fileName) {
        return ConfigurationManager.getConfig(fileName);
    }

}
