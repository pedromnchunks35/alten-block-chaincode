package chaincode.contract;

import java.io.IOException;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;

import chaincode.data.BiometricsDataInterface;
import chaincode.exceptions.InvalidCertificate;
import chaincode.exceptions.InvalidInputException;
import chaincode.exceptions.UserAlreadyExistsException;
import chaincode.exceptions.UserDoesNotExistException;

public interface OurContractInterface extends ContractInterface {
    public void createUser(Context ctx, String username, List<List<Integer>> table)
            throws UserAlreadyExistsException, InvalidInputException, InvalidCertificate, IOException;

    public BiometricsDataInterface getUser(Context ctx, String username)
            throws UserDoesNotExistException, InvalidInputException, InvalidCertificate, IOException,
            ClassNotFoundException;
}
