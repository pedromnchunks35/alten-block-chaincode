package Server.Impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import Server.Operations.CertOps;
import Server.exceptions.InvalidPubKeyException;
import io.grpc.exchangekeys.Confirmation;
import io.grpc.exchangekeys.File;
import io.grpc.exchangekeys.ExchangeKeysGrpc.ExchangeKeysImplBase;
import io.grpc.stub.StreamObserver;

public class ExchangeKeysServiceImpl extends ExchangeKeysImplBase {
    public static final Logger logger = Logger.getLogger(ExchangeKeysServiceImpl.class.getName());

    @Override
    public StreamObserver<File> changeKey(StreamObserver<Confirmation> res) {
        return new StreamObserver<File>() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            @Override
            public void onNext(File value) {
                logger.info("Adding a new chunk to the byte array");
                baos.writeBytes(value.getChunk().toByteArray());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error in the transmission");
                res.onNext(Confirmation.newBuilder()
                        .setMessage("Error in the transmission " + t.getMessage())
                        .build());
                res.onCompleted();
            }

            @Override
            public void onCompleted() {
                Path path = Path.of("./src/main/java/Server/publickey/public.pem");
                try {
                    CertOps.storeNewPublicKey(path, baos.toByteArray());
                } catch (InvalidPubKeyException e) {
                    res.onNext(Confirmation.newBuilder()
                            .setMessage("Error in the store of the key: InvalidPubKeyException -> " + e.getMessage())
                            .build());
                    res.onCompleted();
                } catch (IOException e) {
                    res.onNext(Confirmation.newBuilder()
                            .setMessage("Error with the buffer ->" + e.getMessage())
                            .build());
                    res.onCompleted();
                }
                res.onNext(Confirmation.newBuilder()
                        .setMessage("Success")
                        .build());
                res.onCompleted();
            }

        };
    }

    @Override
    public StreamObserver<File> checkCurrentKey(StreamObserver<Confirmation> res) {
        return new StreamObserver<File>() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            @Override
            public void onNext(File value) {
                logger.info("Adding a new chunk to the byte array");
                baos.writeBytes(value.getChunk().toByteArray());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error in the transmission");
                res.onNext(Confirmation.newBuilder()
                        .setMessage("Error in the transmission " + t.getMessage())
                        .build());
                res.onCompleted();
            }

            @Override
            public void onCompleted() {
                boolean check = false;
                try {
                    check = CertOps.verifySignature(baos.toByteArray());
                } catch (Exception e) {
                    res.onNext(Confirmation.newBuilder()
                            .setMessage("Error with the buffer ->" + e.getMessage())
                            .build());
                    e.printStackTrace();
                    res.onCompleted();
                }

                res.onNext(Confirmation.newBuilder()
                        .setMessage(check ? "Ok" : "Nok")
                        .build());
                res.onCompleted();
            }

        };
    }
}
