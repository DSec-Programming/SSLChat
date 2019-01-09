package ausprobieren;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import javax.security.auth.x500.X500PrivateCredential;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class OwnUtils
{
    public static String ROOT_ALIAS = "CA";

    private static ArrayList<String> userList;

    private final static String SIG_ALGO = "SHA512withRSA";

    private final static int KEY_LENGTH = 2048;
    
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long YEAR = 365 * DAY;
    
    private static Date now, caDate, serverDate, clientDate;
    
    private static int number = 1234;

    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastleJsseProvider());
        System.out.println("STARTED");
        init();
              
        KeyPair rootPair = generateRSAKeyPair();
        KeyPair serverPair = generateRSAKeyPair();
        
        X509Certificate ca = generateRootCert(rootPair, now, caDate);
        generateServerCert(rootPair.getPublic(), rootPair.getPrivate(), ca, now, serverDate);
        
        for(String s : userList)
        {
            KeyPair clientPair = generateRSAKeyPair();
            generateClientCert(s, rootPair.getPublic(), rootPair.getPrivate(), ca, now, clientDate);
        }
        System.out.println("FINISHED");
    }

    private static void init()
    {
        userList = new ArrayList<String>();
        userList.add("Böffgen, Matthias");
        userList.add("Bridel, Daniel");
        userList.add("Bruxmeier, Andreas");
        userList.add("Carpagne, Lars");
        userList.add("Densborn, Philip");
        userList.add("Eckstein, Jonas");
        userList.add("Emmesberger, Fabian");
        userList.add("Hein, Tamara");
        userList.add("Horne, Michael");
        userList.add("Kaypinger, Yanik");
        userList.add("Kessenich, Arne");
        userList.add("Krause, Jens Adrian");
        userList.add("Kremp, Philipp");
        userList.add("Nikolay, Laura");
        userList.add("Ranalletta, Johannes");
        userList.add("Reuter, Mario");
        userList.add("Rosan, Konstantin");
        userList.add("Schmidt, Tim");
        userList.add("Schmidt, Tobias");
        userList.add("Schneider, Philipp");
        userList.add("Schramm, Peter");
        userList.add("Wellmann, Julian");
        userList.add("Wolters, Nicolai");
        userList.add("Knorr, Konstantin");
        userList.add("Test, Test");
        
        now = new Date();
        caDate = new Date(now.getTime() + YEAR);
        serverDate = new Date(now.getTime() + DAY * 120);
        clientDate = new Date(now.getTime() + DAY * 30);

    }
    
    public static void writeKeyStore(String path, KeyStore store, char[] psw) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        File f = new File("src/keystores/" + path);
        FileOutputStream out = new FileOutputStream(f);
        store.store(out, psw);
        out.close();
    }
    
    public static void writeTrustStore(String path, KeyStore store, char[] psw) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        File f = new File("src/keystores/" + path);
        FileOutputStream out = new FileOutputStream(f);
        store.store(out, psw);
        out.close();
    }

    public static X509Certificate generateRootCert(KeyPair pair, Date notBefore, Date notAfter) throws Exception
    {
        X500NameBuilder subjectBuilder = new X500NameBuilder(RFC4519Style.INSTANCE);
        subjectBuilder.addRDN(RFC4519Style.cn, "Root CA");
        subjectBuilder.addRDN(RFC4519Style.c, "DE");
        subjectBuilder.addRDN(RFC4519Style.o, "Hochschule Trier");
        subjectBuilder.addRDN(RFC4519Style.ou, "IT");
        subjectBuilder.addRDN(RFC4519Style.l, "Trier");
        subjectBuilder.addRDN(RFC4519Style.st, "Rheinland-Pfalz");
        subjectBuilder.addRDN(RFC4519Style.description, "SSLChat Root-Zertifikat");
        X500Name subject = subjectBuilder.build();

        ContentSigner sigGen = new JcaContentSignerBuilder(SIG_ALGO).build(pair.getPrivate());
        X509v1CertificateBuilder certGen = new JcaX509v1CertificateBuilder(subject, BigInteger.valueOf(System.currentTimeMillis()), notBefore, notAfter, subject, pair.getPublic());
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certGen.build(sigGen));
        X500PrivateCredential credential = new X500PrivateCredential(cert, pair.getPrivate(), "Root CA");
        
        KeyStore store = KeyStore.getInstance("JKS");
        store.load(null, null);
        store.setKeyEntry(credential.getAlias(), credential.getPrivateKey(), "root1234".toCharArray(), new X509Certificate[]
        { credential.getCertificate() });
        
        writeKeyStore("ca-keystore.jks", store, "root1234".toCharArray());
        return cert;
    }

    public static X509Certificate generateServerCert(PublicKey entityKey, PrivateKey caKey, X509Certificate caCert, Date notBefore, Date notAfter) throws Exception
    {
        X500NameBuilder subjectBuilder = new X500NameBuilder(RFC4519Style.INSTANCE);
        subjectBuilder.addRDN(RFC4519Style.cn, "Server");
        subjectBuilder.addRDN(RFC4519Style.c, "DE");
        subjectBuilder.addRDN(RFC4519Style.o, "Hochschule Trier");
        subjectBuilder.addRDN(RFC4519Style.ou, "IT");
        subjectBuilder.addRDN(RFC4519Style.l, "Trier");
        subjectBuilder.addRDN(RFC4519Style.st, "Rheinland-Pfalz");
        subjectBuilder.addRDN(RFC4519Style.description, "Server-Zertifikat");
        X500Name subject = subjectBuilder.build();

        ContentSigner sigGen = new JcaContentSignerBuilder(SIG_ALGO).build(caKey);
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(caCert, BigInteger.valueOf(System.currentTimeMillis()), notBefore, notAfter, subject, entityKey).addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(caCert)).addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(entityKey)).addExtension(Extension.basicConstraints, true, new BasicConstraints(0)).addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certGen.build(sigGen));
        X500PrivateCredential credential = new X500PrivateCredential(cert, caKey, "Server");
        
        //Keystore
        KeyStore store = KeyStore.getInstance("JKS");
        store.load(null, null);
        store.setKeyEntry(credential.getAlias(), credential.getPrivateKey(), "server1234".toCharArray(), new X509Certificate[]
        { credential.getCertificate() });
        
        //Truststore
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("Root CA", caCert);
        
        writeKeyStore("server-keystore.jks", store, "server1234".toCharArray());
        writeTrustStore("server-truststore.jks", trustStore, "server1234".toCharArray());
        return cert;
    }

    public static X509Certificate generateClientCert(String name, PublicKey entityKey, PrivateKey caKey, X509Certificate caCert, Date notBefore, Date notAfter) throws Exception
    {
        X500NameBuilder subjectBuilder = new X500NameBuilder(RFC4519Style.INSTANCE);
        subjectBuilder.addRDN(RFC4519Style.cn, name);
        subjectBuilder.addRDN(RFC4519Style.c, "DE");
        subjectBuilder.addRDN(RFC4519Style.o, "Hochschule Trier");
        subjectBuilder.addRDN(RFC4519Style.ou, "IT");
        subjectBuilder.addRDN(RFC4519Style.l, "Trier");
        subjectBuilder.addRDN(RFC4519Style.st, "Rheinland-Pfalz");
        subjectBuilder.addRDN(RFC4519Style.description, "Client-Zertifikat für " + name);
        X500Name subject = subjectBuilder.build();

        ContentSigner sigGen = new JcaContentSignerBuilder(SIG_ALGO).build(caKey);
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(caCert, BigInteger.valueOf(System.currentTimeMillis()), notBefore, notAfter, subject, entityKey).addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(caCert)).addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(entityKey)).addExtension(Extension.basicConstraints, true, new BasicConstraints(0)).addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certGen.build(sigGen));
        X500PrivateCredential credential = new X500PrivateCredential(cert, caKey, buildSubString(name));
        
        //Keystore
        KeyStore store = KeyStore.getInstance("JKS");
        store.load(null, null);
        store.setKeyEntry(credential.getAlias(), credential.getPrivateKey(), (buildSubString(name) + number).toLowerCase().toCharArray(), new X509Certificate[]
        { credential.getCertificate() });
        
        //Truststore
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("Root CA", caCert);
        
        writeKeyStore("client-keystore-" + buildSubString(name) + ".jks", store, (buildSubString(name) + number).toLowerCase().toCharArray());
        writeTrustStore("client-truststore-" + buildSubString(name) + ".jks", trustStore, (buildSubString(name) + number).toLowerCase().toCharArray());
        return cert;
    }
    
    public static String buildSubString(String s)
    {
        int index = s.indexOf(",");
        String tmp = s.substring(0, index);
        return tmp;
    }

    public static KeyPair generateRSAKeyPair() throws Exception
    {
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
        kpGen.initialize(KEY_LENGTH, new SecureRandom());
        return kpGen.generateKeyPair();
    }
}
