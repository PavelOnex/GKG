package configuration;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private String configName;
    private String agentId;
    private String brokerAddress;
    private String alpha;
    private String module;
    private String k;
    private String m;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }


    public Configuration(String configFileName) {
        this.configName = configFileName;
    }
    private static final String CONFIG_PATH_INTELLIJ = "target/classes/";
    private boolean isRelease = false;
    public void getConfiguration() {
        Properties prop = new Properties();
        //InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(CONFIG_PATH_INTELLIJ + configName);
        } catch (FileNotFoundException e) {
            isRelease = true;
        }
        if (isRelease) {
            try {
                inputStream = new FileInputStream(configName);
            } catch (FileNotFoundException e) {
                return;
            }
        }

        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            agentId = prop.getProperty("agentId");
            brokerAddress = prop.getProperty("brokerAddress");
            alpha = prop.getProperty("alpha");
            module = prop.getProperty("module");
            k=prop.getProperty("k");
            m=prop.getProperty("m");
        } else {
            System.out.println("Config file not found");
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAlpha() {
        return alpha;
    }

    public String getModule() {
        return module;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }
}
