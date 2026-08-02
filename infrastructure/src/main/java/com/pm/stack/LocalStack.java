package com.pm.stack;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.stream.Collectors;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props); //calls Stack extension
        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabase("AuthServiceDB", "auth-service-db");

        DatabaseInstance patientServiceDb = createDatabase("PatientServiceDB", "patient-service-db");

        CfnHealthCheck authDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDbHealthCheck");

        CfnHealthCheck patientDbHealthCheck = createDbHealthCheck(patientServiceDb, "PatientServiceDbHealthCheck");

        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();
    }

    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                .maxAzs(2) //available in 2 zones in teh world
                .build();
    }

    private DatabaseInstance createDatabase(String id, String dbName) {
        return DatabaseInstance.Builder.create(this, id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_17_2)
                        .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY) //typically wouldn't do this in prod unless you specify a backup policy as well
                .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3) //tries 3 times once every 30 secs before reporting a failure
                        .build())
                .build(); //tells us whether the db is ready to accept connections or not
    }

    private CfnCluster createMskCluster() {
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream().map(
                                ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT").build())
                .build();
    }

    private Cluster createEcsCluster() {
        // auth-service.patient-management.local
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build())
                .build();
    }
}
