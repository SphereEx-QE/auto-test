package com.sphereex;

import com.sphereex.core.AutoTest;
import com.sphereex.core.Case;
import com.sphereex.core.DBInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private static final String packageName = "com.sphereex.cases";
    private static final List<File> files = new ArrayList<>();
    private static final List<Class> cases = new LinkedList<>();
    private static final List<String> features = new LinkedList<>();
    private static final List<String> tags = new LinkedList<>();
    private static final List<Case> needRunCases = new LinkedList<>();
    private static final List<String> needRunCaseNames = new LinkedList<>();
    private static int successNum = 0;
    private static int failedNum = 0;
    private static DBInfo dbInfo;
    public static void main(String[] args) throws Exception {
        parseArgs(args);
        caseScanner();
        filterFiles();
        filterCases();
        selectRunCases();
        run();
        System.out.printf("failed/success/total: [%d:%d:%d].%n", failedNum, successNum, cases.size());
        System.exit(0);
    }

    private static void parseArgs(String[] args) {
        if (args.length == 1) {
            Collections.addAll(needRunCaseNames, args[0].split(","));
        }
        String ip = System.getProperty("ip");
        String port = System.getProperty("port");
        String dbName = System.getProperty("dbname");
        String user = System.getProperty("user");
        String password = System.getProperty("password");
        String feature = System.getProperty("feature");
        String tag = System.getProperty("tag");
        if (null != ip && null != port && null != dbName && null != user && null != password) {
            dbInfo = new DBInfo(ip, Integer.parseInt(port), user, password, dbName);
        }
        if (null != feature) {
            Collections.addAll(features, feature.split(","));
        }
        if (null != tag) {
            Collections.addAll(tags, tag.split(","));
        }
    }

    static void run() {
        for (Case c : needRunCases) {
            try {
                c.start();
            } catch (Exception e) {
                logger.info(String.format("case %s throw exception", c.getCaseInfo().getName()));
                e.printStackTrace();
            }
            if (!c.getCaseInfo().isStatus()) {
                failedNum += 1;
                System.out.printf("case: %s, status: %b%n", c.getCaseInfo().getName(), c.getCaseInfo().isStatus());
            } else {
                successNum += 1;
            }
        }
    }

    public static void filterFiles() throws ClassNotFoundException {
        for (File f : files) {
            String fileName = f.getAbsolutePath();
            if (fileName.endsWith(".class")) {
                String noSuffixFileName = fileName.substring(8 + fileName.lastIndexOf("classes"), fileName.indexOf(".class"));
                String filePackage = noSuffixFileName.replace("/", ".");
                Class clazz = Class.forName(filePackage);
                if (null != clazz.getAnnotation(AutoTest.class)) {
                    cases.add(clazz);
                }
            }
        }
    }

    public static void filterCases() {
        Iterator<Class> iterator = cases.iterator();
        while (iterator.hasNext()) {
            Class clazz = iterator.next();
            if (null == clazz.getAnnotation(AutoTest.class)) {
                iterator.remove();
            }
        }
    }

    public static void selectRunCases() throws Exception {
        for (Class clazz : cases) {
            Case c = (Case) clazz.newInstance();
            if (!features.isEmpty() && !features.contains(c.getCaseInfo().getFeature())) {
                continue;
            }
            if (!tags.isEmpty() && !tags.contains(c.getCaseInfo().getTag())) {
                continue;
            }
            if (!needRunCaseNames.isEmpty() && !needRunCaseNames.contains(c.getCaseInfo().getName())) {
                continue;
            }
            c.setDbInfo(dbInfo);
            c.initCaseInfo();
            if (c.caseInfoIsNull()) {
                logger.warn(String.format("Case: %s caseInfo is null", clazz.getName()));
                continue;
            }
            needRunCases.add(c);
        }
    }

    public static void caseScanner() throws IOException, ClassNotFoundException {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", File.separator));
        scanURL(urls);
    }

    public static void scanURL(Enumeration<URL> urls) throws IOException, ClassNotFoundException {
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            switch (protocol) {
                case "file":
                    String filepath = URLDecoder.decode(url.getFile(), "UTF-8");
                    File file = new File(filepath);
                    scanFile(file);
                    break;
                case "jar":
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    if (null != connection) {
                        JarFile jarFile = connection.getJarFile();
                        if (null != jarFile) {
                            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                            while (jarEntryEnumeration.hasMoreElements()) {
                                JarEntry entry = jarEntryEnumeration.nextElement();
                                String jarEntryName = entry.getName();
                                if (jarEntryName.contains(".class") && jarEntryName.replace("/",".").startsWith(packageName)) {
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                                    Class<?> clazz = Class.forName(className);
                                    cases.add(clazz);
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void scanFile(File file) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                scanFile(f);
            }
        } else {
            files.add(file);
        }
    }
}
