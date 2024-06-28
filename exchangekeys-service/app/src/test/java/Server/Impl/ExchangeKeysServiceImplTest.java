package Server.Impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.exchangekeys.Confirmation;
import io.grpc.exchangekeys.ExchangeKeysGrpc;
import io.grpc.exchangekeys.File;
import io.grpc.exchangekeys.ExchangeKeysGrpc.ExchangeKeysStub;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;

@RunWith(JUnit4.class)
public class ExchangeKeysServiceImplTest {
    @Rule
    public GrpcCleanupRule cleanup = new GrpcCleanupRule();
    Server server;
    ManagedChannel channel;
    ExchangeKeysStub asyncStub;
    public static final Logger logger = Logger.getLogger(ExchangeKeysServiceImplTest.class.getName());

    @Before
    public void setUp() throws IOException {
        String server_name = InProcessServerBuilder.generateName();
        server = cleanup.register(
                InProcessServerBuilder.forName(server_name)
                        .addService(new ExchangeKeysServiceImpl())
                        .directExecutor()
                        .build()
                        .start());
        channel = cleanup.register(
                InProcessChannelBuilder.forName(server_name)
                        .directExecutor()
                        .build());
        asyncStub = ExchangeKeysGrpc.newStub(channel);
    }

    @After
    public void clean() {
        server.shutdown();
        channel.shutdown();
    }

    @Test
    public void changeKey_test() throws Exception {
        // ? TEST THE CHANGE KEY NORMAL
        // ? Read our file
        Path pub_key_path = Path.of("./src/test/java/Server/keypairs/pair1/public.pem");
        byte[] pub_key = Files.readAllBytes(pub_key_path.toAbsolutePath());
        // ? To await the response
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> reference = new AtomicReference<String>();
        // ? Simply send a public key
        StreamObserver<Confirmation> response = new StreamObserver<Confirmation>() {
            @Override
            public void onNext(Confirmation reply) {
                reference.set(
                        reply.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error trying to get the message");
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Done");
                latch.countDown();
            }

        };
        // ? Check if it actually reached the server
        StreamObserver<File> req = asyncStub.changeKey(response);
        int min_range = 0;
        int max_range = 0;
        int chunk_size = 50;
        int chunk_number = 1;
        while (pub_key.length - 1 != min_range) {
            byte[] chunk_to_send = new byte[chunk_size];
            max_range += chunk_size;
            if (max_range > pub_key.length - 1) {
                max_range = pub_key.length - 1;
                chunk_to_send = new byte[max_range - min_range];
            }
            int i = 0;
            while (min_range != max_range) {
                chunk_to_send[i] = pub_key[min_range];
                min_range++;
                i++;
            }
            req.onNext(
                    File.newBuilder()
                            .setChunk(ByteString.copyFrom(chunk_to_send))
                            .setChunkNumber(chunk_number)
                            .setChunkSize(chunk_size)
                            .setFullSize(pub_key.length)
                            .setName("public.key")
                            .build());
        }
        req.onCompleted();
        // ? Check the content of the received file
        if (!latch.await(1, TimeUnit.MINUTES)) {
            throw new Exception();
        }
        assertEquals(reference.get(), "Success");
    }

    @Test
    public void changeKeyFailure_test() throws Exception {
        // ? TEST THE CHANGE KEY NORMAL
        // ? Read our file
        Path pub_key_path = Path.of("./src/test/java/Server/keypairs/fakepair/public.pem");
        byte[] pub_key = Files.readAllBytes(pub_key_path);
        // ? To await the response
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> reference = new AtomicReference<String>();
        // ? Simply send a public key
        StreamObserver<Confirmation> response = new StreamObserver<Confirmation>() {
            @Override
            public void onNext(Confirmation reply) {
                reference.set(
                        reply.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error trying to get the message");
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Done");
                latch.countDown();
            }

        };
        // ? Check if it actually reached the server
        StreamObserver<File> req = asyncStub.changeKey(response);
        int min_range = 0;
        int max_range = 0;
        int chunk_size = 50;
        int chunk_number = 1;
        while (pub_key.length - 1 != min_range) {
            byte[] chunk_to_send = new byte[chunk_size];
            max_range += chunk_size;
            if (max_range > pub_key.length - 1) {
                max_range = pub_key.length - 1;
                chunk_to_send = new byte[max_range - min_range];
            }
            int i = 0;
            while (min_range != max_range) {
                chunk_to_send[i] = pub_key[min_range];
                min_range++;
                i++;
            }
            req.onNext(
                    File.newBuilder()
                            .setChunk(ByteString.copyFrom(chunk_to_send))
                            .setChunkNumber(chunk_number)
                            .setChunkSize(chunk_size)
                            .setFullSize(pub_key.length)
                            .setName("public.key")
                            .build());
        }
        req.onCompleted();
        // ? Check the content of the received file
        if (!latch.await(1, TimeUnit.MINUTES)) {
            throw new Exception();
        }
        assertEquals(reference.get().contains("Error in the store of the key: InvalidPubKeyException ->"), true);
        // ? TEST THE CHANGE KEY NORMAL
        // ? Read our file
        pub_key_path = Path.of("./src/test/java/Server/keypairs/fakepair/public2.pem");
        pub_key = Files.readAllBytes(pub_key_path);
        // ? To await the response
        CountDownLatch latch2 = new CountDownLatch(1);
        AtomicReference<String> reference2 = new AtomicReference<String>();
        // ? Simply send a public key
        response = new StreamObserver<Confirmation>() {
            @Override
            public void onNext(Confirmation reply) {
                reference2.set(
                        reply.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error trying to get the message");
                latch2.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Done");
                latch2.countDown();
            }

        };
        // ? Check if it actually reached the server
        req = asyncStub.changeKey(response);
        min_range = 0;
        max_range = 0;
        chunk_size = 50;
        chunk_number = 1;
        while (pub_key.length - 1 != min_range) {
            byte[] chunk_to_send = new byte[chunk_size];
            max_range += chunk_size;
            if (max_range > pub_key.length - 1) {
                max_range = pub_key.length - 1;
                chunk_to_send = new byte[max_range - min_range];
            }
            int i = 0;
            while (min_range != max_range) {
                chunk_to_send[i] = pub_key[min_range];
                min_range++;
                i++;
            }
            req.onNext(
                    File.newBuilder()
                            .setChunk(ByteString.copyFrom(chunk_to_send))
                            .setChunkNumber(chunk_number)
                            .setChunkSize(chunk_size)
                            .setFullSize(pub_key.length)
                            .setName("public.key")
                            .build());
        }
        req.onCompleted();
        // ? Check the content of the received file
        if (!latch2.await(1, TimeUnit.MINUTES)) {
            throw new Exception();
        }
        assertEquals(reference.get().contains("Error in the store of the key: InvalidPubKeyException ->"), true);
    }

    @Test
    public void checkCurrentKey_test() throws InterruptedException, Exception {
        // ? TEST THE CHANGE KEY NORMAL
        // ? Read signed file using openssl
        Path path = Path.of("./src/test/java/Server/signedFile/signature");
        byte[] signature = Files.readAllBytes(path);
        // ? To await the response
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> reference = new AtomicReference<String>();
        // ? Simply send a public key
        StreamObserver<Confirmation> response = new StreamObserver<Confirmation>() {
            @Override
            public void onNext(Confirmation reply) {
                reference.set(
                        reply.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error trying to get the message");
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Done");
                latch.countDown();
            }

        };
        // ? Check if it actually reached the server
        StreamObserver<File> req = asyncStub.checkCurrentKey(response);
        int min_range = 0;
        int max_range = 0;
        int chunk_size = 50;
        int chunk_number = 1;
        while (signature.length != min_range) {
            byte[] chunk_to_send = new byte[chunk_size];
            max_range += chunk_size;
            if (max_range > signature.length) {
                max_range = signature.length;
                chunk_to_send = new byte[max_range - min_range];
            }
            int i = 0;
            while (min_range != max_range) {
                chunk_to_send[i] = signature[min_range];
                min_range++;
                i++;
            }
            req.onNext(
                    File.newBuilder()
                            .setChunk(ByteString.copyFrom(chunk_to_send))
                            .setChunkNumber(chunk_number)
                            .setChunkSize(chunk_size)
                            .setFullSize(signature.length)
                            .setName("signature")
                            .build());
        }
        req.onCompleted();
        // ? Check the content of the received file
        if (!latch.await(1, TimeUnit.MINUTES)) {
            throw new Exception();
        }
        assertEquals(reference.get(), "Ok");
    }

    @Test
    public void checkCurrentKeyFailure_test() throws InterruptedException, Exception {
        // ? TEST THE CHANGE KEY NORMAL
        // ? Read signed file using openssl
        Path path = Path.of("./src/test/java/Server/signedFile/signature2");
        byte[] signature = Files.readAllBytes(path);
        // ? To await the response
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> reference = new AtomicReference<String>();
        // ? Simply send a public key
        StreamObserver<Confirmation> response = new StreamObserver<Confirmation>() {
            @Override
            public void onNext(Confirmation reply) {
                reference.set(
                        reply.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error trying to get the message");
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Done");
                latch.countDown();
            }

        };
        // ? Check if it actually reached the server
        StreamObserver<File> req = asyncStub.checkCurrentKey(response);
        int min_range = 0;
        int max_range = 0;
        int chunk_size = 50;
        int chunk_number = 1;
        while (signature.length != min_range) {
            byte[] chunk_to_send = new byte[chunk_size];
            max_range += chunk_size;
            if (max_range > signature.length) {
                max_range = signature.length;
                chunk_to_send = new byte[max_range - min_range];
            }
            int i = 0;
            while (min_range != max_range) {
                chunk_to_send[i] = signature[min_range];
                min_range++;
                i++;
            }
            req.onNext(
                    File.newBuilder()
                            .setChunk(ByteString.copyFrom(chunk_to_send))
                            .setChunkNumber(chunk_number)
                            .setChunkSize(chunk_size)
                            .setFullSize(signature.length)
                            .setName("signature")
                            .build());
        }
        req.onCompleted();
        // ? Check the content of the received file
        if (!latch.await(1, TimeUnit.MINUTES)) {
            throw new Exception();
        }
        assertEquals(reference.get(), "Nok");
    }
}
