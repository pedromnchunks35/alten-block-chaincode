package Server.Operations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Logger;

import Server.exceptions.InvalidPubKeyException;

public class CertOps {
    // ? Instance a logger
    public final static Logger logger = Logger.getLogger(CertOps.class.getName());

    /**
     * @param pemCertificate, the certificate in string form
     * @return The actual string of the public or private key
     */
    private static String removeX509Headers(String pemCertificate) {
        return pemCertificate
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // ? This is to remove the white space
    }

    /**
     * @param pemCertificate, a base64 string that we want to know if it is a public
     *                        key or not
     * @return boolean, true in case it is a valid public key otherwise false
     */
    private static boolean isItValidPublicKey(String pemCertificate) {
        // ? Check delimiters
        if (!pemCertificate.contains("-----BEGIN PUBLIC KEY-----")
                || !pemCertificate.contains("-----END PUBLIC KEY-----")) {
            return false;
        }
        // ? Remove the delimiters
        String base64key = removeX509Headers(pemCertificate);
        // ? Decode base64 to byte
        byte[] byte_form = Base64.getDecoder()
                .decode(base64key);
        // ? Try to generate a public key.. in case there are no error everything is
        // ? fine otherwise its a invalid public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(byte_form);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            logger.info("The given public key was not accepted");
            return false;
        }
        return true;
    }

    /**
     * @param path,    The path where we want to store our public key
     * @param content, The content of the supposed public key
     * @throws InvalidPubKeyException, In case the public key is invalid
     * @throws IOException,            In case there is something wrong with the
     *                                 Path
     */
    public static void storeNewPublicKey(Path path, byte[] content) throws InvalidPubKeyException, IOException {
        // ? Convert certificate to base65
        String public_key_string = new String(content, StandardCharsets.UTF_8);
        // ? Check if it is valid
        if (!isItValidPublicKey(public_key_string)) {
            throw new InvalidPubKeyException();
        }
        // ? Simply write the file
        Files.write(path, content);
    }

    /**
     * @param signature, This is the signature that we will verify with our public
     *                   key
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @return True case it is the correct signature, otherwise false
     */
    public static boolean verifySignature(byte[] signature)
            throws  InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, IOException {
        // ? Get original data
        Path path_original_data = Path.of("./src/main/java/Server/verifyFile/verify.txt");
        byte[] origin_data = Files.readAllBytes(path_original_data);

        // ? Load Public Key
        Path path_to_public_key = Path.of("./src/main/java/Server/publickey/public.pem");
        byte[] public_key_bytes = Files.readAllBytes(path_to_public_key.toAbsolutePath());

        // ? Remove the delimiters
        String public_key_without_header_base64 = removeX509Headers(
                new String(public_key_bytes, StandardCharsets.UTF_8));
        byte[] public_key_without_header_bytes = Base64.getDecoder().decode(public_key_without_header_base64);
        // ? Actually get the public key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(public_key_without_header_bytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        // ? Making the actual verification
        java.security.Signature signatureObj = java.security.Signature.getInstance("SHA256withRSA");
        signatureObj.initVerify(publicKey);
        signatureObj.update(origin_data);
        // ? Verification result
        return signatureObj.verify(signature);
    }
}
