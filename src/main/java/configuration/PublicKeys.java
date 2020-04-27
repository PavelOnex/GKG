package configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PublicKeys {
    public PublicKeys() {
    }
    private Map<String, String> agentIdToPublicKey;

    public Map<String, String> getAgentIdToPublicKey() {
        return agentIdToPublicKey;
    }

    public void setAgentIdToPublicKey(Map<String, String> agentIdToPublicKey) {
        this.agentIdToPublicKey = agentIdToPublicKey;
    }
    private boolean isRelease = false;
    private static final String CONFIG_PATH_INTELLIJ = "target/classes/";
    private static final String PUBLIC_KEY_DELIMITER = ", ";
    public static final String VALUES_DELIMITER = "-";
    private static final String PKEYS_NAME = "pkeys.properties";
    public void init() {
        Properties prop = new Properties();
        //InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configName);
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(CONFIG_PATH_INTELLIJ + PKEYS_NAME);
        } catch (FileNotFoundException e) {
            isRelease = true;
        }
        if (isRelease) {
            try {
                inputStream = new FileInputStream(PKEYS_NAME);
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
            agentIdToPublicKey = new HashMap<>();
            String[] publicKeys = prop.getProperty("publicKeys").split(PUBLIC_KEY_DELIMITER);
            for(String key : publicKeys) {
                String id = key.substring(0, key.indexOf(VALUES_DELIMITER));
                String pkey = key.substring(key.indexOf(VALUES_DELIMITER) + 1);
                agentIdToPublicKey.put(id, pkey);
            }

        }
    }


    public void refreshPKeys(String pkeys) {
        String filename;
        if (isRelease) {
            filename = PKEYS_NAME;
        } else {
            filename = CONFIG_PATH_INTELLIJ + PKEYS_NAME;
        }
        File configFile = new File(filename);
        try {
            Properties props = new Properties();
            props.setProperty("publicKeys", pkeys);
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, null);
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println("file does not exist");
        } catch (IOException ex) {
            System.out.println("IO Error");
        }
    }
}
