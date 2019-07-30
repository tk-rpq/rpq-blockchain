package com.example.baas.service;

import com.example.baas.config.FabricProperties;
import com.example.baas.model.BaseResponse;
import com.example.baas.model.Block;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author John
 */
@Slf4j
@Data
public class FabricUtil {

    private String chaincodeName;
    private String version;
    private ChaincodeID ccId;
    private long waitTime = 6000;

    private HFClient client;
    private Channel channel;

    public FabricUtil(FabricProperties fabricProperties, HFClient client, Channel channel) {
        this.chaincodeName = fabricProperties.getChaincodeName();
        this.version = fabricProperties.getChaincodeVersion();
        this.client = client;
        this.channel = channel;

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chaincodeName)
                .setVersion(version);
        ccId = chaincodeIDBuilder.build();

        try {
            channel.initialize();
            channel.registerBlockListener(blockEvent -> {
                log.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(), blockEvent.getPeer()));
            });
        } catch (Exception e) {
            log.error("Channel init failed !!!");
        }

    }

    public void executeTransaction(String func, String... args) throws InvalidArgumentException, ProposalException, InterruptedException, ExecutionException, TimeoutException {
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);

        transactionProposalRequest.setFcn(func);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(waitTime);

        List<ProposalResponse> successful = new LinkedList<>();
        List<ProposalResponse> failed = new LinkedList<>();

        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {

            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                String payload = new String(response.getChaincodeActionResponsePayload());
                log.info(String.format("[√] Got success response from peer %s => payload: %s", response.getPeer().getName(), payload));
                successful.add(response);
            } else {
                String status = response.getStatus().toString();
                String msg = response.getMessage();
                log.warn(String.format("[×] Got failed response from peer %s => %s: %s ", response.getPeer().getName(), status, msg));
                failed.add(response);
            }
        }

        log.info("Sending transaction to orderers...");
        channel.sendTransaction(successful).thenApply(transactionEvent -> {
            Block block = new Block(transactionEvent.getTransactionID(),transactionEvent.getBlockEvent().getBlockNumber());
            log.info("Orderer response: txid" + transactionEvent.getTransactionID());
            log.info("Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
            return BaseResponse.success("success",block);
        }).exceptionally(e -> {
            log.error("Orderer exception happened: ", e);
            return new BaseResponse<>(500,"事务提交错误");
        }).get(waitTime, TimeUnit.SECONDS);

    }

    /**
     * 查询
     *
     * @param functionName
     * @param args
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public Collection<ProposalResponse> queryByChainCode(String functionName, String[] args)
            throws InvalidArgumentException, ProposalException {
        QueryByChaincodeRequest queryRequest = client.newQueryProposalRequest();
        queryRequest.setChaincodeID(ccId);
        queryRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        queryRequest.setProposalWaitTime(waitTime);
        queryRequest.setFcn(functionName);
        if (args != null) {
            queryRequest.setArgs(args);
        }
        Collection<ProposalResponse> response = channel.queryByChaincode(queryRequest);

        return response;
    }

    private void printChannelInfo(HFClient client, Channel channel) throws
            ProposalException, InvalidArgumentException, IOException {
        log.info("===================================");
        BlockchainInfo channelInfo = channel.queryBlockchainInfo();

        log.info("Channel height: " + channelInfo.getHeight());
        for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
            BlockInfo returnedBlock = channel.queryBlockByNumber(current);
            final long blockNumber = returnedBlock.getBlockNumber();

            log.info(String.format("Block #%d has previous hash id: %s", blockNumber, Hex.encodeHexString(returnedBlock.getPreviousHash())));
            log.info(String.format("Block #%d has data hash: %s", blockNumber, Hex.encodeHexString(returnedBlock.getDataHash())));
            log.info(String.format("Block #%d has calculated block hash is %s",
                    blockNumber, Hex.encodeHexString(SDKUtils.calculateBlockHash(client, blockNumber, returnedBlock.getPreviousHash(), returnedBlock.getDataHash()))));
        }

    }

    public void closeChannel(Channel channel) {
        log.info("Shutdown channel.");
        channel.shutdown(true);
    }
}
