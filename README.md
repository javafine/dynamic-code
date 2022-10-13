# dynamic-code
## 特性
* 动态加载：基于groovy的MOP机制实现的方法的动态部署和更新。
* 动态配置管理：动态加载的最佳使用场景。
* 动态转发请求：动态加载的典型使用场景
## 环境
jdk1.8、maven3.8.4、IntelliJ IDEA(Community Edition)、windows
## 代码结构
一个WEB示例工程，包含动态加载、动态配置的管理和示例
* FunctionManager.groovy：基于groovy的MOP机制实现动态加载。
* ConfigurationManager.java：实现配置文件的动态管理。
* sample.groovy：示例脚本代码，被动态部署和更新。
* conf_sample.groovy：示例配置文件，被动态管理的配置信息。
## 运行
直接在本工程的release目录下执行
`java -jar .\dynamic-code-0.1.0.jar`
或者：
1. 在本工程目录下执行`mvn gplus:compile package`
2. 将生成的`target/dynamic-code-0.1.0.jar`与`resources/code/sample.groovy`和`resources/conf/conf_sample.groovy`放到一个目录下执行`java -jar .\dynamic-code-0.1.0.jar`
## 测试
1. 请求`curl --location --request GET 'localhost:8080/file/sample/function/hello?name=anna'`
执行`code/sample.groovy`内的`hello`方法，返回`Hello : anna`。
2. 修改`code/sample.groovy`的`hello`方法代码。
3. 请求`curl --location --request GET 'localhost:8080/meta/reload/file/sample/function/confValue'`
重新加载`code/sample.groovy`获得更新后的结果。
## 联系方式
javafine@163.com