package chaincode.contract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.json.JSONObject;

import chaincode.data.BiometricsData;
import chaincode.data.BiometricsDataInterface;
import chaincode.data.TicketDTO;
import chaincode.exceptions.InvalidCertificate;
import chaincode.exceptions.InvalidInputException;
import chaincode.exceptions.UserAlreadyExistsException;
import chaincode.exceptions.UserDoesNotExistException;
import chaincode.util.Cert;

@Contract
@Default
public class OurContract implements OurContractInterface {
    public OurContract() {
    }

    private boolean exists(Context ctx, BiometricsDataInterface data) {
        byte[] buffer = ctx.getStub().getState(data.getUsername());
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    @Override
    public BiometricsDataInterface getUser(Context ctx, String username)
            throws UserDoesNotExistException, InvalidInputException, InvalidCertificate, IOException,
            ClassNotFoundException {
        X509Certificate cert = ctx.getClientIdentity().getX509Certificate();
        if (!Cert.hasOrganizationalUnit(cert, "Biometrics")) {
            throw new InvalidCertificate("The given certificate is invalid for this operation");
        }
        if (username == null || username.equals("")) {
            throw new InvalidInputException("The username is not valid");
        }
        BiometricsDataInterface data = new BiometricsData(username, null, false);
        if (!this.exists(ctx, data)) {
            throw new InvalidInputException("The given user does not exist");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(ctx.getStub().getState(data.getUsername()));
        ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unchecked")
        List<List<Integer>> array = (List<List<Integer>>) ois.readObject();
        data.setTable(array);
        return data;
    }

    @Transaction
    @Override
    public void createUser(Context ctx, String username, List<List<Integer>> table)
            throws UserAlreadyExistsException, InvalidInputException, InvalidCertificate, IOException {
        X509Certificate cert = ctx.getClientIdentity().getX509Certificate();
        if (!Cert.hasOrganizationalUnit(cert, "Biometrics")) {
            throw new InvalidCertificate("The given certificate is invalid for this operation");
        }
        if (username == null || username.equals("")) {
            throw new InvalidInputException("The username is not valid");
        }
        if (table.size() < 5 || table.get(3).size() < 5) {
            throw new InvalidInputException("The table is not valid");
        }
        BiometricsDataInterface data = new BiometricsData(username, table, false);
        if (exists(ctx, data)) {
            throw new UserAlreadyExistsException();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(data.getTable());
        byte[] bytes = baos.toByteArray();
        ctx.getStub().putState(data.getUsername(), bytes);
    }

    @Transaction
    @Override
    public String requestTicket(Context ctx, String username)
            throws IOException, InvalidCertificate, InvalidKeySpecException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of("./src/main/java/chaincode/keypairs/public.pem"));
        String publicKeyString = Cert.byteArrayToString(publicKeyBytes);
        boolean isItValidPublicKey = Cert.isItValidPublicKey(publicKeyString);
        if (!isItValidPublicKey) {
            throw new InvalidCertificate("public key");
        }
        String publicKeyWithoutHeaders = Cert.removeX509Headers(publicKeyString);
        PublicKey publicKeyNative = Cert.getPublicKeyFromKeyWithNoHeaders(publicKeyWithoutHeaders);
        TicketDTO ticket = new TicketDTO();
        Cert.generateTicketDTO(ticket, Calendar.getInstance());
        JSONObject ticketJson = Cert.ticketDTOtoJsonObject(ticket, username);
        String encrypted = Cert.encryptTicket(ticketJson.toString(), publicKeyNative);
        return encrypted;
    }

}
