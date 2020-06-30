package com.fly.mytomcat.util;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

public class PackageScanner {
	
	
	
	
	private final static Logger log = Logger.getLogger(PackageScanner.class);
	    /**
	     * 获得包下面的所有的class
	     *
	     * @param
	     *
	     * @return List包含所有class的实例
	     */

    public static List<Class<?>> getClasssFromPackage(String packageName) {
        List<Class<?>> clazzs = new ArrayList<>();
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {

                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    String classPath = filePath.replace(packageDirName, "");
                    findClassInPackageByFile(packageName, classPath, recursive, clazzs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazzs;
    }
    
    /**
     * 获取带有路径与包名下的所有class
     * @param packageName  包名
     * @param classPath   文件路径（不包含包路径）
     * @return
     */
    public static List<Class<?>> getClasssFromPackage(String packageName, String classPath) {
        List<Class<?>> clazzs = new ArrayList<>();
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名对应的路径名称
		String packageDirName = packageName.replace('.', '/');
		classPath = classPath.endsWith("/")? classPath : classPath + "/";
		try {
			URL url = new URL("file:" + classPath + packageDirName);
			String protocol = url.getProtocol();

			if ("file".equals(protocol)) {
				findClassInPackageByFile(packageName, classPath, recursive, clazzs);
			}
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} 
                
        return clazzs;
    }
    
    

    /**
     * 在package对应的路径下找到所有的class
     */
    private static void findClassInPackageByFile(String packageName, String classPath, final boolean recursive,
                                               List<Class<?>> clazzs) {
    	classPath = classPath.endsWith("/")? classPath : classPath + "/";
    	String filePath = classPath + packageName.replace(".", "/");
    	File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });
        for (File file : dirFiles) {
            if (file.isDirectory()) {
            	String pName = packageName.equals("")?file.getName() : packageName + "." + file.getName();
                findClassInPackageByFile(pName, classPath, recursive, clazzs);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzs.add(findClass(classPath, packageName, className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
    /**
     * 获取本地任意路径下的class
     * @param classPath
     * @param className
     * @return
     */
    public static Class<?> findClass(String classPath,String className){        
    	return findClass(classPath, "", className);
	}
    
    /**
     * 
     * @param classPath   类路径
     * @param packageName   包名
     * @param className   类名
     * @return
     */
    public static Class<?> findClass(String classPath, String packageName, String className){ 
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		//获取路径classes的路径 
		ClassLoader custom;
		try {
			URL classes = new URL("file:/" + classPath);
			custom = new URLClassLoader(new URL[]{classes}, systemClassLoader);
			
			if(className != null && !packageName.equals("")) {
				className = packageName + "." + className;
			}
			Class<?> clazz = custom.loadClass(className);
			return clazz;
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("没有找到类路径：" + classPath + "下的  " + className);
			//e.printStackTrace();
		}
			
		return null;
	}
    
}
