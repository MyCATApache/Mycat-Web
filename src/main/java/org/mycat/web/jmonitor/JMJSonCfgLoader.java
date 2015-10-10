package org.mycat.web.jmonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import com.alibaba.fastjson.JSONObject;

/**
 * load config from file
 * 
 * @author @author code_czp@126.com-2015年5月12日
 */
public class JMJSonCfgLoader {

    public static JSONObject loadConfig(String configFile) {
        try {
            if (configFile == null)
                throw new NullPointerException("configFile is null");

            File file = new File(configFile.replace("classpath:", ""));

            if (file.isDirectory())
                throw new RuntimeException(configFile + " is directory");
            if (!file.exists())
                throw new RuntimeException(configFile + " is not exist");
            if (file.length() == 0)
                throw new RuntimeException(configFile + " is empty");

            return JSONObject.parseObject(readConfigFile(configFile));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String readConfigFile(String configFile) throws IOException {

        if (configFile.startsWith("classpath:")) {
            return readConfigFromClassPath(configFile);
        }

        String line;
        FileReader in = new FileReader(configFile);
        BufferedReader br = new BufferedReader(in);
        StringBuffer json = new StringBuffer();
        while ((line = br.readLine()) != null) {
            json.append(line);
        }
        br.close();

        return json.toString();

    }

    private static String readConfigFromClassPath(String configFile) throws IOException {

        configFile = configFile.replace("classpath:", "");
        InputStream is = JMJSonCfgLoader.class.getResourceAsStream(configFile);
        if (is == null)
            is = JMJSonCfgLoader.class.getClassLoader().getResourceAsStream(configFile);
        if (is == null)
            is = ClassLoader.getSystemResourceAsStream(configFile);

        BufferedInputStream br = new BufferedInputStream(is);
        StringBuffer json = new StringBuffer();
        byte[] buf = new byte[1024];
        int size;
        while ((size = br.read(buf)) != -1) {
            json.append(new String(buf, 0, size));
        }
        br.close();

        return json.toString();
    }

    public static void main(String[] args) {
        final JSONObject loadConfig = loadConfig("classpath:./config.json");
        System.out.println(loadConfig);
    }
}
