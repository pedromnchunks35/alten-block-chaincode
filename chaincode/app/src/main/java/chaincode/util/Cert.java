package chaincode.util;

import java.security.cert.X509Certificate;

public class Cert {
    public static boolean hasOrganizationalUnit(X509Certificate cert, String ouValue) {
        String subject = cert.getSubjectX500Principal().getName();
        return subject.contains("OU=" + ouValue);
    }
}
