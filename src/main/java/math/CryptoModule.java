package math;

import static org.apache.commons.codec.binary.Hex.*;
import static org.apache.commons.io.FileUtils.*;

import java.io.*;
import java.math.BigInteger;
import java.security.Key;
import java.util.Base64;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import configuration.Configuration;
import constants.Constants;
import org.apache.commons.codec.DecoderException;

public class CryptoModule {
    private Configuration configuration;
    public CryptoModule(Configuration configuration) {
        this.configuration = configuration;
    }

    @Deprecated
    public void saveKey(String key, Constants.KEY keyType) {
        if (keyType.equals(Constants.KEY.PUBLIC_KEY)) {
            File file = new File("pkey.txt");
            try {
                writeStringToFile(file, key, "ISO-8859-1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (keyType.equals(Constants.KEY.PRIVATE_KEY)) {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            File file = new File("skey.txt");
            char[] hex = encodeHex(secretKey.getEncoded());
            try {
                writeStringToFile(file, new String(hex), "ISO-8859-1");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Deprecated
    public String loadKey(Constants.KEY keyType) {
        if (keyType.equals(Constants.KEY.PUBLIC_KEY)) {
            File file = new File("pkey.txt");
            String data = null;
            try {
                data = new String(readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        } else if (keyType.equals(Constants.KEY.PRIVATE_KEY)) {
            File file = new File("skey.txt");
            String data = null;
            try {
                data = new String(readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] encoded;
            try {
                encoded = decodeHex(data.toCharArray());
            } catch (DecoderException e) {
                e.printStackTrace();
                return null;
            }
            SecretKey sc =  new SecretKeySpec(encoded, "AES");
            return Base64.getEncoder().encodeToString(sc.getEncoded());
        }
        return null;
    }

    public void saveKeyValue(String secretValue, Constants.KEY keyType) {
        if (keyType.equals(Constants.KEY.PUBLIC_KEY)) {
            File file = new File("pkey.txt");
            try {
                writeStringToFile(file, secretValue, "ISO-8859-1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (keyType.equals(Constants.KEY.PRIVATE_KEY)) {
            String key = "Bar12345Bar12345"; // 128 bit key
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encrypted = cipher.doFinal(secretValue.getBytes());
                byte[] bytes = Base64.getEncoder().encode(encrypted);
                File file = new File("skey.txt");
                writeStringToFile(file, new String(bytes), "ISO-8859-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String loadKeyValue(Constants.KEY keyType) {
        if (keyType.equals(Constants.KEY.PUBLIC_KEY)) {
            File file = new File("pkey.txt");
            String data = null;
            try {
                data = new String(readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        } else if (keyType.equals(Constants.KEY.PRIVATE_KEY)) {
            //TODO: Key refactor
            String key = "Bar12345Bar12345";
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            File file = new File("skey.txt");
            String decrypted = "";
            try {
                String encryptedValue = readFileToString(file, "ISO-8859-1");
                byte[] bytes = Base64.getDecoder().decode(encryptedValue.getBytes());

                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                decrypted = new String(cipher.doFinal(bytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return decrypted;
        }
        return null;
    }

    @Deprecated
    public String raiseValueToPower(String power) {
        return this.raiseValueToPower(configuration.getAlpha(), power);
    }

    @Deprecated
    public String raiseValueToPower(String value, String power) {
        BigInteger bigValue = new BigInteger(value);
        BigInteger bigModule = new BigInteger(configuration.getModule());
        BigInteger bigSecretKey = new BigInteger(power);
        BigInteger res = bigValue.modPow(bigSecretKey, bigModule);
        return res.toString();
    }

    public String getReverseMultiplyElement(String value) {
//        BigInteger bigModule = new BigInteger(configuration.getModule());
//        BigInteger bigK = new BigInteger(configuration.getK());
//        BigInteger bigM = new BigInteger(configuration.getM());
//        BigInteger bigValue = new BigInteger(value);
        int k = Integer.parseInt(configuration.getK());
        int m = Integer.parseInt(configuration.getM());
        int module = Integer.parseInt(configuration.getModule());
        int element = Integer.parseInt(value);
        int numerator = (1 - k * m * element - k * k) % module;
        int denominator = ArithmeticModule.modInverse((m * m * (k * ArithmeticModule.modInverse(m, module) + element))
                , module);
        BigInteger answer = (new BigInteger(String.valueOf(numerator))).multiply(new BigInteger(String.valueOf(denominator)))
                .mod(new BigInteger(String.valueOf(module)));
        return answer.toString();
    }

    public String generateSecretKey() {
        Random rand = new Random(); // generate a random number
        return Integer.toString(rand.nextInt(Integer.parseInt(configuration.getModule())) - 1);
    }
//
    public String multiply(String str1, String str2) {
        BigInteger bigStr1 = new BigInteger(str1);
        BigInteger bigStr2 = new BigInteger(str2);
        BigInteger bigModule = new BigInteger(configuration.getModule());
        return bigStr1.multiply(bigStr2).mod(bigModule).toString();
    }
//
//    public String getAdditiveInverse(String value) {
//        BigInteger bigValue = new BigInteger(value);
//        bigValue = bigValue.negate();
//        BigInteger bigModule = new BigInteger(configuration.getModule());
//        while(bigValue.compareTo(BigInteger.ZERO) == -1) {
//            bigValue = bigValue.add(bigModule);
//        }
//        return bigValue.toString();
//    }


    public long binpow (int a, int n) {
        if (n == 0)
            return 1;
        if (n % 2 == 1)
            return binpow (a, n-1) * a;
        else {
            long b = binpow (a, n/2);
            return b * b;
        }
    }
    public long newMultipleOperation(String value1, String value2) {
        BigInteger bigValue1 = new BigInteger(value1);
        BigInteger bigValue2 = new BigInteger(value2);
        BigInteger bigModule = new BigInteger(configuration.getModule());
        BigInteger bigK = new BigInteger(configuration.getK());
        BigInteger bigM = new BigInteger(configuration.getM());
        BigInteger reverseM = new BigInteger(String.valueOf(ArithmeticModule
                .modInverse(Integer.parseInt(bigM.toString()), Integer.parseInt(bigModule.toString()))));
        BigInteger kdivm = bigK.multiply(reverseM);
        BigInteger bigValue = bigM.multiply(bigValue1.add(kdivm)).multiply(bigValue2.add(kdivm)).add(kdivm.negate());
        return bigValue.mod(bigModule).longValue();
    }


    public long raiseToPowWithNewOp(long value, long pow) {

        if (pow == 0)
            return getMultiplyNeutral();
        if (pow % 2 == 1)
            return newMultipleOperation(String.valueOf(raiseToPowWithNewOp(value, pow - 1)) , String.valueOf(value));
        else {
            long b = raiseToPowWithNewOp (value, pow/2);
            return newMultipleOperation(String.valueOf(b), String.valueOf(b));
        }
//        long val = value;
//        for (int i = 1; i< pow; i++) {
//            val = newMultipleOperation(String.valueOf(val), String.valueOf(value));
//        }
//        return val;
    }
    public long raiseToPowWithNewOp(long pow) {
        return this.raiseToPowWithNewOp(Long.parseLong(configuration.getAlpha()), pow);
    }

    public long getMultiplyNeutral() {
        long k = Long.parseLong(configuration.getK());
        long m = Long.parseLong(configuration.getM());
        long mod = Long.parseLong(configuration.getModule());
        long inverseM = ArithmeticModule.modInverse(Integer.parseInt(configuration.getM()), Integer.parseInt(configuration.getModule()));
        long val = -1*(k-1)*inverseM;
        while (val < 0) {
            val += mod;
        }
        return val;
    }
}