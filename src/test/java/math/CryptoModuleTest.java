package math;


import configuration.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class CryptoModuleTest {
    private CryptoModule cryptoModule;
    private Configuration configuration;

    @Before
    public void initTest() {
        configuration = new Configuration("config.properties");
        configuration.getConfiguration();
        cryptoModule = new CryptoModule(configuration);
    }

    @Test
    public void binpowTest() {
        System.out.println("TESTS running");
        assertEquals(8, cryptoModule.binpow(2, 3));
    }

    @Test
    public void raiseToPowWithNewOpTest() {
        long value = 10;
        long pow = 3;
        long val = value;
        for (int i = 1; i < pow; i++) {
            val = cryptoModule.newMultipleOperation(String.valueOf(val), String.valueOf(value));
        }
        assertEquals(val, cryptoModule.raiseToPowWithNewOp(value, pow));
    }

    @Test
    public void getMultiplyNeutralTest() {
        String val = "11";
        assertEquals(Long.parseLong(val), cryptoModule.newMultipleOperation(val, String.valueOf(cryptoModule.getMultiplyNeutral())));
    }

    @Test
    public void raiseAlphaXToPowerTest() {
        // 23^11
        // alpha = 23
        // x = 11
        long alphaX = 16;
        long y = 17;
        long k = Long.parseLong(configuration.getK());
        long m = Long.parseLong(configuration.getM());
        long mod = Long.parseLong(configuration.getModule());
        long inverseM = ArithmeticModule.modInverse(Integer.parseInt(configuration.getM()), Integer.parseInt(configuration.getModule()));
        long val1 = cryptoModule.raiseToPowWithNewOp(alphaX, k);
        long val1test = cryptoModule.raiseToPowWithNewOp(Long.parseLong(cryptoModule.multiplyNoModule("11", String.valueOf(k))));
        assertEquals(val1test, val1);
        long val2 = cryptoModule.raiseToPowWithNewOp(Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), String.valueOf(y))));
        long val2test = cryptoModule.raiseToPowWithNewOp(cryptoModule.raiseToPowWithNewOp(y), k);
        assertEquals(val2, val2test);
        long val3 = cryptoModule.raiseToPowWithNewOp(Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k),
                cryptoModule.multiplyNoModule(String.valueOf(k-1), String.valueOf(inverseM)))));
        long val4 = cryptoModule.raiseToPowWithNewOp(alphaX, Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(m), String.valueOf(y))));
        long val4test = cryptoModule.raiseToPowWithNewOp(23, Long.parseLong(cryptoModule.multiplyNoModule("11",
                cryptoModule.multiplyNoModule(String.valueOf(y), String.valueOf(m)))));
        assertEquals(val4test, val4);

        long result = cryptoModule.newMultipleOperation(String.valueOf(val1), String.valueOf(cryptoModule.newMultipleOperation(String.valueOf(val2),
                String.valueOf(cryptoModule.newMultipleOperation(String.valueOf(val3), String.valueOf(val4))))));
        // Можно убрать NoModule везде
        assertEquals((Long.parseLong(cryptoModule.multiplyNoModule("11", String.valueOf(k))) +
                Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), String.valueOf(y))) +
                Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k),
                        cryptoModule.multiplyNoModule(String.valueOf(k-1), String.valueOf(inverseM)))) +
                Long.parseLong(cryptoModule.multiplyNoModule("11",
                        cryptoModule.multiplyNoModule(String.valueOf(y), String.valueOf(m)))
                )) % mod, cryptoModule.newMultipleOperation("11", String.valueOf(y)));

        assertEquals(cryptoModule.raiseToPowWithNewOp(y), cryptoModule.raiseToPowWithNewOp(y + cryptoModule.getAdditiveNeutral()));
        // val1 * val2  = alpha^(xk + ky)
        assertEquals(cryptoModule.newMultipleOperation(String.valueOf(val1), String.valueOf(val2)) ,
                cryptoModule.raiseToPowWithNewOp((Long.parseLong(cryptoModule.multiplyNoModule("11", String.valueOf(k))) +
                        Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), String.valueOf(y)))) )
        );
        // val1 * val2 * val3 = alpha^(xk + ky + k(k-1)/m)
        assertEquals(cryptoModule.newMultipleOperation(String.valueOf(val3),
                cryptoModule.newMultipleOperationStr(String.valueOf(val1), String.valueOf(val2))) ,
                cryptoModule.raiseToPowWithNewOp((Long.parseLong(cryptoModule.multiplyNoModule("11", String.valueOf(k))) +
                        Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), String.valueOf(y)))) +
                        Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), cryptoModule.multiplyNoModule(String.valueOf(k-1),
                                String.valueOf(inverseM))))
                        )
        );
        // val1 * val2 * val3 * val4 = alpha^(xk + ky + k(k-1)/m + mxy)
        assertEquals(cryptoModule.newMultipleOperation(String.valueOf(val4), cryptoModule.newMultipleOperationStr(String.valueOf(val3),
                cryptoModule.newMultipleOperationStr(String.valueOf(val1), String.valueOf(val2)))) ,

                cryptoModule.raiseToPowWithNewOp((Long.parseLong(cryptoModule.multiplyNoModule("11", String.valueOf(k))) +
                        Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), String.valueOf(y)))) +
                        Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(k), cryptoModule.multiplyNoModule(String.valueOf(k-1),
                                String.valueOf(inverseM)))) +
                        Long.parseLong(cryptoModule.multiplyNoModule(String.valueOf(m), cryptoModule.multiplyNoModule("11", String.valueOf(y))))
                )
        );




        assertEquals(result, cryptoModule.raiseToPowWithNewOp(cryptoModule.newMultipleOperationNoMod("11", String.valueOf(y))));
        System.out.println(cryptoModule.raiseToPowWithNewOp(cryptoModule.newMultipleOperationNoMod("11", "110")));
        System.out.println(cryptoModule.raiseAlphaXToPower(14, 110));
        // a * K * K^(-1) = a
        System.out.println(cryptoModule.newMultipleOperation(String.valueOf(cryptoModule.newMultipleOperation("11", "21")), "115"));
        System.out.println("********TEST");
        System.out.println("Neutral " + cryptoModule.getMultiplyNeutral());
//        System.out.println("Nes " + cryptoModule.raiseToPowWithNewOpBigInt(new BigInteger(configuration.getAlpha()),
//                BigInteger.valueOf(cryptoModule.newMultipleOperationNoMod("115", "21"))).mod(new BigInteger(configuration.getModule())));
        long K = 21;
        System.out.println("Reverse " + cryptoModule.getReverseMultiplyElement(String.valueOf(K)));
        long X = 128;
        System.out.println("AlphaX " + cryptoModule.raiseToPowWithNewOp(X));
        System.out.println("AlphaX^K " + cryptoModule.raiseToPowWithNewOp(cryptoModule.newMultipleOperationNoMod(String.valueOf(X),
                String.valueOf(K))));
        System.out.println("AlphaX^K " + cryptoModule.raiseAlphaXToPower(cryptoModule.raiseToPowWithNewOp(X),
                K));
        System.out.println("AlphaX " + cryptoModule.raiseToPowWithNewOp(X));
        System.out.println("AlphaX^K " + cryptoModule.raiseToPowWithNewOp(cryptoModule.newMultipleOperationNoMod(String.valueOf(X),
                String.valueOf(K))));
        System.out.println("AlphaX^K " + cryptoModule.raiseAlphaXToPower(cryptoModule.raiseToPowWithNewOp(X),
                K));

        for (int i = 0; i < 3; i++) {
            System.out.println(cryptoModule.raiseAlphaXToPower(16,
                    Long.parseLong(cryptoModule.getReverseMultiplyElement(String.valueOf(K))) ));
        }
        for (int i = 1; i < 137; i++) {
            if (cryptoModule.raiseAlphaXToPower(16, i) == 134) {
                System.out.println("Otvet " + i + " Proiz " + cryptoModule.newMultipleOperation(String.valueOf(K), String.valueOf(i)));
            }
        }
        cryptoModule.getElementOrder();
        System.out.println(cryptoModule.newMultipleOperation(String.valueOf(K),
                String.valueOf(cryptoModule.getReverseMultiplyElement(String.valueOf(K)))));
    }
//    @Test
//    public void raiseAlphaXToPowerNeutralTest() {
//        // 23^11
//        // alpha = 23
//        // x = 11
//        long alphaX = 16;
//        long y = 13;
//        long k = Long.parseLong(configuration.getK());
//        long m = Long.parseLong(configuration.getM());
//        long mod = Long.parseLong(configuration.getModule());
//        long result = cryptoModule.raiseAlphaXToPower(alphaX, y);
//        System.out.println(result);
//        String reverseY = cryptoModule.getReverseMultiplyElement(String.valueOf(y));
//        System.out.println(reverseY);
//        long calculatedValue = cryptoModule.raiseAlphaXToPower(result, Long.parseLong(reverseY));
//        assertEquals(alphaX, cryptoModule.raiseAlphaXToPower(alphaX, cryptoModule.getMultiplyNeutral()));
//        assertEquals(alphaX, calculatedValue);
//    }


}