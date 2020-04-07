package com.zero.classscan;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassTemplate {

    protected final String packageName;
    protected final Filter filter;

    protected ClassTemplate(String packageName, Filter filter) {
        this.packageName = packageName;
        this.filter = filter;
    }

    public final List<Class<?>> getClassList() {
        final List<Class<?>> classList = new ArrayList<>();
        try {
            final String resourcesPath = this.packageName.replace(".", "/");
            final Enumeration<URL> urls = getClassLoader().getResources(resourcesPath);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                if (url != null) {
                    final String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        final String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classList, packagePath, this.packageName);
                    }
                    else if (protocol.equals("jar")) {
                        final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        final JarFile jarFile = jarURLConnection.getJarFile();
                        final Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            final JarEntry jarEntry = jarEntries.nextElement();
                            final String jarEntryName = jarEntry.getName();
                            if (jarEntryName.endsWith(".class")) {
                                final String className = jarEntryName
                                        .substring(0, jarEntryName.lastIndexOf("."))
                                        .replaceAll("/", ".");
                                if (className.startsWith(this.packageName)) {
                                    doAddClass(classList, className);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classList;
    }

    private void addClass(final List<Class<?>> classList, final String packagePath, final String packageName) {
        final File[] files = new File(packagePath)
                .listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        if (files == null) {
            return;
        }
        for (final File file : files) {
            final String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (Strings.isNotBlank(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classList, className);
            }
            else {
                String subPackagePath = fileName;
                if (Strings.isNotBlank(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (Strings.isNotBlank(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classList, subPackagePath, subPackageName);
            }
        }
    }

    private void doAddClass(final List<Class<?>> classList, final String className) {
        System.out.println("doAddClass - " + className);
        final Class<?> cls = loadClass(className);
        if (this.filter != null && this.filter.filter(cls)) {
            return;
        }
        classList.add(cls);
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static Class<?> loadClass(final String className) {
        try {
            return Class.forName(className, false, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
