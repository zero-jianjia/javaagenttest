package com.zero.classscan;


/**
 * 支持包名下的子包名遍历，并使用Annotation(内注)来过滤一些不必要的内部类，提高命中精度。
 * <p>
 * 通过Thread.currentThread().getContextClassLoader()获取ClassLoader实例
 * 将包名转为路径名后，做为参数传给CloassLoader.getResources()，以得到该路径下所有资源的URL;
 * 通过URL.getProtocol()方法，判断资源是在本地(file:)或是第三方jar包(jar:)内;
 * 在本地的类直接文件遍历即可;
 */

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 用于获取指定包名下的所有类名.<br/>
 * 并可设置是否遍历该包名下的子包的类名.<br/>
 * 并可通过Annotation(内注)来过滤，避免一些内部类的干扰.<br/>
 *
 * ClassScanner
 * getClassListByAnnotation
 *
 */
public final class ClassUtil {

    public final static List<Class<?>> getClassList(String packageName, boolean isRecursive, Class<? extends Annotation> annotation) {
        List<Class<?>> classList = new ArrayList<>();
        String resourcesPath = packageName.replace(".", "/");


        try {
            final Enumeration<URL> urls = getClassLoader().getResources(resourcesPath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url == null) {
                    continue;
                }

                // protocol为file或jar
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String path = url.getPath().replaceAll("%20", " ");
                    classList.addAll(loadClassFromFile(path, packageName, isRecursive));
                }
                else if ("jar".equals(protocol)) {
                    classList.addAll(loadClassFromJar(url, packageName, isRecursive));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classList;
    }

    private static List<Class<?>> loadClassFromFile(String resourcePath, String packageName, boolean isRecursive) {
        final File[] files = new File(resourcePath)
                .listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        if (files == null) {
            return Collections.emptyList();
        }

        List<Class<?>> loadClassList = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {    // .class 文件
                String className = getClassName(packageName, fileName);
                loadClassList.add(loadClassFromFile(className));
            }
            else { // 文件夹
                if (isRecursive) {  // 需要递归查找文件夹/包名下的类
                    String subResourcePath = resourcePath + "/" + fileName;
                    String subPackageName = packageName + "." + fileName;
                    List<Class<?>> subLoadClassList = loadClassFromFile(subResourcePath, subPackageName, isRecursive);
                    loadClassList.addAll(subLoadClassList);
                }
            }
        }
        return loadClassList;
    }

    public static List<Class<?>> loadClassFromJar(URL url, String packageName, boolean isRecursive) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();

        List<Class<?>> loadClassList = new ArrayList<>();

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();

            /*
             * 类似：
             * a/b/
             * a/b/c/
             * a/b/c/D.class
             */
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(".class")) {
                int index = jarEntryName.lastIndexOf("/");
                String pkg = jarEntryName.substring(0, jarEntryName.lastIndexOf("/"))
                        .replace("/", ".");
                String simpleClassName = jarEntryName.substring(index + 1);

                if (pkg.equals(packageName) || (isRecursive && pkg.startsWith(packageName))) {
                    String className = getClassName(pkg, simpleClassName);
                    loadClassList.add(loadClassFromFile(className));
                }
            }
        }

        return loadClassList;
    }

    private static Class<?> loadClassFromFile(final String className) {
        try {
            return Class.forName(className, false, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static String getClassName(String pkgName, String fileName) {
        int endIndex = fileName.lastIndexOf(".");
        String clazz = null;
        if (endIndex >= 0) {
            clazz = fileName.substring(0, endIndex);
        }
        String clazzName = null;
        if (clazz != null) {
            clazzName = pkgName + "." + clazz;
        }
        return clazzName;
    }


}


