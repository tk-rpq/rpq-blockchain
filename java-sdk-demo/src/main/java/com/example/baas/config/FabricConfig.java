package com.example.baas.config;

import com.example.baas.model.FabricUser;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author John
 * @create 2019/7/9 14:21
 */
@Slf4j
@Configuration
@Import(FabricProperties.class)
public class FabricConfig {

    private FabricProperties fabricProperties;

    public FabricConfig(FabricProperties fabricProperties){
        this.fabricProperties=fabricProperties;
    }

    @Bean
    public HFClient hfClient(FabricUser fabricUser) throws InvalidArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(fabricUser);
        return client;
    }

    @Bean
    public Channel channel(NetworkConfig networkConfig,HFClient client){
        Channel channel = null;
        try {
            channel = client.loadChannelFromConfig(fabricProperties.getChannelName(), networkConfig);
        }catch (Exception e){
            log.error("init failed!!!");
            log.error(e.getMessage());
        }
        return channel;
    }

    @Bean
    public NetworkConfig getNetworkConfig() throws IOException, NetworkConfigurationException, InvalidArgumentException {
        ClassPathResource connectionProfile = new ClassPathResource(fabricProperties.getConnectionProfilePath());
        return NetworkConfig.fromJsonStream(connectionProfile.getInputStream());
    }

    @Bean
    public FabricUser getFabricUser(NetworkConfig networkConfig) throws
            IOException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InfoException,
            EnrollmentException {

        NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
        NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

        HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
        HFCAInfo cainfo = hfcaClient.info();
        log.info("CA name: " + cainfo.getCAName());
        log.info("CA version: " + cainfo.getVersion());

        log.info("Going to enroll user: " + fabricProperties.getUserName());
        Enrollment enrollment = hfcaClient.enroll(fabricProperties.getUserName(), fabricProperties.getSecret());
        log.info("Enroll user: " + fabricProperties.getUserName() +  " successfully.");

        FabricUser user = new FabricUser();
        user.setMspId(clientOrg.getMspId());
        user.setName(fabricProperties.getUserName());
        user.setOrganization(clientOrg.getName());
        user.setEnrollment(enrollment);
        return user;
    }
}
