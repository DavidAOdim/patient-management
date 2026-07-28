package com.pm.billingservice.grpc;

import org.slf4j.Logger;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override //overriding the createBillingAccount method that gets created in the BillingServiceImpleBase class
    public void createBillingAccount(billing.BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {
        log.info("createBillingAccount request received {}", billingRequest.toString());

        //Business logic - e.g save to database, perform calculations etc.

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build(); //dummy data to simulate what would happen^

        responseObserver.onNext(response); //used to send response from grpc back to patient service client (can return as many responses allowing for real time telemetry vs REST)
        responseObserver.onCompleted(); //response completed and ready to end cycle
    }
}
