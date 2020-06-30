package com.fly.mytomcat.application;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.fly.mytomcat.annotation.RequestMap;
import com.fly.mytomcat.http.server.HttpServer;
import com.fly.mytomcat.interceptor.DefaultHandlerInterceptor;
import com.fly.mytomcat.interceptor.HandlerInterceptor;
import com.fly.mytomcat.util.PackageScanner;

import cn.hutool.core.util.NetUtil;
import cn.hutool.setting.Setting;

public class Application {
	
	//ServerSocket
	private static ServerSocket ss ;
	
	//线程池
	private static ThreadPoolExecutor threadPool ;
	
	//配置信息
	private static Setting configmap ;
	
	//http请求处理类注册中心
	private static Map<String, ClazzInfo> httpmap ;
	
	//端口号，默认8080
	private static int port ;
	
	//拦截器，默认com.fly.mytomcat.interceptor.DefaultHandlerInterceptor
	private static HandlerInterceptor interceptor;
	
	private final static Logger log = Logger.getLogger(Application.class);
	
	public static void startup() {

		long time = System.currentTimeMillis();
		init();
		time = System.currentTimeMillis() - time;
		log.info("mytomcat初始化完成，正在监听端口：" + port + " ，耗时：" + time/1000 + "s");
	}
	
	
	
	private static void init() {
		config_init();
		serversockert_init();
		interceptor_init();
		http_init();
		threadpool_init();
	}
	/**
	 * 初始化读取配置文件server.properties
	 * 
	 * 配置文件的具体格式 Setting配置文件类似于Properties文件，规则如下：
	 * 1、注释用#开头表示，只支持单行注释，空行和无法正常被识别的键值对也会被忽略，可作为注释，但是建议显式指定注释。同时在value之后不允许有注释，会被当作value的一部分。
	 * 2、键值对使用key = value 表示，key和value在读取时会trim掉空格，所以不用担心空格。
	 * 3、分组为中括号括起来的内容（例如配置文件中的[demo]），中括号以下的行都为此分组的内容，无分组相当于空字符分组，即[]。若某个key是name，分组是group，加上分组后的key相当于group.name。
	 * 4、支持变量，默认变量命名为 ${变量名}，变量只能识别读入行的变量，例如第6行的变量在第三行无法读取，例如配置文件中的${driver}会被替换为com.mysql.jdbc.Driver，为了性能，Setting创建的时候构造方法会指定是否开启变量替换，默认不开启。
	 */
	private static void config_init() {
		File file = new File("conf/server.properties");
		
		//配置文件不存在读取默认配置
		if(!file.exists()) {
			log.info("文件" + file.getAbsolutePath() + "不存在，加载默认配置");
			configmap = new Setting();
			return ;
		}
		configmap = new Setting(file.getAbsolutePath());
		log.info("已读取配置文件：" + file.getAbsolutePath());
	}
	
	/**
	 * 拦截器初始化
	 */
	private static void interceptor_init() {
		//当获取的值为空（null或者空白字符时，包括多个空格），返回默认值
		String interceptorpath = configmap.getStr("server.interceptor", "com.fly.mytomcat.interceptor.DefaultHandlerInterceptor");
		try {
			interceptor = (DefaultHandlerInterceptor) Class.forName(interceptorpath).getConstructor().newInstance();
			log.info("已初始化拦截器：" + interceptorpath);
		} catch (Exception e) {
			log.error("拦截器初始化失败");
			e.printStackTrace();
		} 
	}
	
	/**
	 * 扫描httpserver实现类,并注册到httpmap中
	 */
	private static void http_init() {
		httpmap = new HashMap<String, ClazzInfo>();
		//扫描内置HttpServer类
		String serverpakage = configmap.getStr("server.package", "com.fly.mytomcat.server");
		List<Class<?>> clazzes = PackageScanner.getClasssFromPackage(serverpakage);
		putintoHttpmap(clazzes);
		
		//扫描webapps下HttpServer类
		File file = new File("webapps") ;
		if(file.exists() && !file.isFile()) {
			for(File f : file.listFiles()) {
				String classPath = f.getAbsolutePath() + "/" + "WEB-INF/classes/";
				if(new File(classPath).exists()) {
					List<Class<?>> clazzs = PackageScanner.getClasssFromPackage("", classPath);
					putintoHttpmap(clazzs);
				}
			}
		}
		
		log.info("已成功加载" + httpmap.size() + "个httpserver实现类");
	}

	
	/**
	 * 初始化ss
	 */
	private static void serversockert_init() {
		port = configmap.getInt("server.port", 8080);
		
		if(!NetUtil.isUsableLocalPort(port)) {
            log.error(port +" 端口已经被占用了！程序已终止");
            System.exit(-1);
        }
		try {
			ss = new ServerSocket(port);
			log.info("serversocket初始化完成");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("serversocket初始化失败，程序已终止");
			System.exit(-1);
		} 
	}
	
	/**
	 * 线程池初始化
	 */
	private static void threadpool_init() {
		//当获取的值为空（null或者空白字符时，包括多个空格），返回默认值
		int min_spare_threads = configmap.getInt("server.mytomcat.min-spare-threads", 10);
		int max_threads = configmap.getInt("server.mytomcat.max-threads", 250);
		/**
		 * 第一个参数10 表示这个线程池初始化了10个线程在里面工作
		 * 第二个参数15 表示如果10个线程不够用了，就会自动增加到最多15个线程
		 * 第三个参数60 结合第四个参数TimeUnit.SECONDS，表示经过60秒，多出来的线程还没有接到活儿，就会回收，最后保持池子里就10个
		 * 第四个参数TimeUnit.SECONDS 如上
		 * 第五个参数 new LinkedBlockingQueue() 用来放任务的集合
		 */
		try {
			threadPool = new ThreadPoolExecutor(min_spare_threads, max_threads, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}catch(Exception e) {
			e.printStackTrace();
			log.error("线程池初始化失败！程序已终止");
			System.exit(-1);
		}	
		new MainThread(ss, threadPool).start();;
		log.info("线程池初始化完成，初始化数量：" + min_spare_threads + ",最大数量：" + max_threads);
	}
	
	public static Map<String, ClazzInfo> getHttpmap() {
		return httpmap;
	}

	public static HandlerInterceptor getInterceptor() {
		return interceptor;
	}


	private static void putintoHttpmap(List<Class<?>> clazzes) {
		if(null == clazzes)
			return ;
		for(Class<?> clazz : clazzes) {
			try {
				//如果该 Class 对象不表示指定类的子类（这里“子类”包括该类本身）。抛出：ClassCastException
				Class<? extends HttpServer> httpServerClass = clazz.asSubclass(HttpServer.class);
				RequestMap anno = httpServerClass.getAnnotation(RequestMap.class);
				//注释判断
				if(null == anno)
					continue ;
				String requestpath = anno.value();
				httpmap.put(requestpath, new ClazzInfo(clazz, null));
			} catch(ClassCastException e) {
				continue ;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
