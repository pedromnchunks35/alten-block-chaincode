package Server.Operations;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import Server.exceptions.InvalidPubKeyException;

@RunWith(JUnit4.class)
public class CertOpsTest {
    @After
    public void clean() throws IOException {
        Path path_to_delete = Paths.get("./src/test/java/Server/publickey/public.pem");
        try {
            Files.delete(path_to_delete);
        } catch (Exception e) {
        }
    }

    @Test
    public void receive_file_test() throws IOException, InvalidPubKeyException {
        // ? Get the file
        Path path = Paths.get("./src/test/java/Server/keypairs/pair1/public.pem");
        byte[] bytes = Files.readAllBytes(path.toAbsolutePath());
        // ? Test the save functionality
        Path path_to_store = Paths.get("./src/test/java/Server/publickey/public.pem");
        CertOps.storeNewPublicKey(path_to_store.toAbsolutePath(), bytes);
    }

    @Test
    public void verify_signature_test() throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException,
            SignatureException, IOException {
        Path path_of_the_signature = Path.of("./src/test/java/Server/signedFile/signature");
        byte[] signature_bytes = Files.readAllBytes(path_of_the_signature);
        boolean result = CertOps.verifySignature(signature_bytes);
        assertEquals(result, true);
    }

    @Test
    public void verify_signatureFailure_test() throws IOException, InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException, SignatureException {
        Path path_of_the_signature = Path.of("./src/test/java/Server/signedFile/signature2");
        byte[] signature_bytes = Files.readAllBytes(path_of_the_signature);
        boolean result = CertOps.verifySignature(signature_bytes);
        assertEquals(result, false);
    }
}
