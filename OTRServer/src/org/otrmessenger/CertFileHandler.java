package org.otrmessenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by sfrolov on 4/8/17.
 */
public class CertFileHandler {
    protected File CertFile;

    CertFileHandler(String filename) {
        CertFile = new File(filename);
    }

    public X509Certificate getCertKey() {
        CertificateFactory fact = null;
        try {
            fact = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(CertFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        X509Certificate cer = null;
        try {
            cer = (X509Certificate) fact.generateCertificate(is);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return cer;
    }
}
