package edu.tum.ase.authentication_controller.jwt;

import edu.tum.ase.authentication_controller.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeyStoreManager {
    private KeyStore keyStore;
    private String keyAlias = "aseprojectkey";
    private char[] password = "asease".toCharArray();

    public KeyStoreManager() throws KeyStoreException, IOException {
        loadKeyStore();
    }

    public void loadKeyStore() throws KeyStoreException, IOException {
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fis = null;

        try {
// Get the path to the keystore file in the resources folder
            File keystoreFile = ResourceUtils.getFile("./src/main/java/edu/tum/ase/authentication_controller/jwt/ase_project.keystore");
            fis = new FileInputStream(keystoreFile);
            keyStore.load(fis, password);
            keyAlias = keyStore.aliases().nextElement();
        } catch (Exception e) {
            System.err.println("Error when loading KeyStore");
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    protected PublicKey getPublicKey() {
        try {
            Certificate cert = keyStore.getCertificate(keyAlias);
            return cert.getPublicKey();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected Key getPrivateKey() {
        try {
            return keyStore.getKey(keyAlias,password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}