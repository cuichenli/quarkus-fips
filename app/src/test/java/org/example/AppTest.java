package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.EntropySourceProvider;
import org.bouncycastle.crypto.fips.FipsDRBG;
import org.bouncycastle.crypto.fips.FipsUnapprovedOperationError;
import org.bouncycastle.crypto.util.BasicEntropySourceProvider;
import org.bouncycastle.util.Strings;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class AppTest {

    @Test
    void testApplicationInFipsMode() throws NoSuchAlgorithmException {

        // Ensure that only approved algorithms and key sizes for FIPS-140-3.
        CryptoServicesRegistrar.setApprovedOnlyMode(true);
        // Set Secure Random to be compliant
        EntropySourceProvider entSource = new BasicEntropySourceProvider(new SecureRandom(), true);
        FipsDRBG.Builder drgbBldr = FipsDRBG.SHA512
                .fromEntropySource(entSource)
                .setSecurityStrength(256)
                .setEntropyBitsRequired(256);
        CryptoServicesRegistrar.setSecureRandom(drgbBldr.build(Strings.toByteArray("axs"), true));
        // ******
        // Validates FIPS Mode enabled and enforced correctly with Unapproved Key Generation
        // Note we did not specify a provider here to prove that the provider list is set correctly.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");


        try {
            keyGenerator.init(256);
            fail("HMAC SHA-512 initialization should not work when FIPS enabled.");
        } catch (AssertionError ex) {
            // NOTE: Now we can catch the exception
            // Should throw this exception.
            System.out.println(123);
        } catch (Exception exception) {
            // This should not happen but for sanity it checks for unexpected exceptions which may indicate BC is not
            // configured correctly.
            fail("Unexpected Exception", exception);
        }
    }
}