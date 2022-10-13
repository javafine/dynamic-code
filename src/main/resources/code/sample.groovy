import com.fine.dynamic.code.manager.ConfigurationManager
import com.fine.dynamic.code.util.OkHttpUtil

class sample {
	private ConfigObject config = ConfigurationManager.getConfig("conf_sample");

	String hello(Map mapParam, Map mapHeader) {
		return "Hello : "+mapParam.get("name");
	}

	Object confValue(Map mapParam, Map mapHeader) {
		println(config.conf_sample)
		return config.conf_sample.shape.color.property;
	}

	Object retransfer(Map mapParam, Map mapHeader) {
		return OkHttpUtil.get("http://httpbin.org/get", mapParam, mapHeader);
	}

}
