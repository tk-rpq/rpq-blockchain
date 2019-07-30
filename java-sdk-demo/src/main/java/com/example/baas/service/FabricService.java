package com.example.baas.service;

import com.example.baas.config.FabricProperties;
import com.example.baas.model.BaseResponse;
import com.example.baas.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author John
 * @create 2019/7/11 9:45
 */
@Slf4j
@Service
public class FabricService {

    private FabricUtil fabricUtil;

    public FabricService(FabricProperties fabricProperties, HFClient client, Channel channel) {
        fabricUtil = new FabricUtil(fabricProperties, client, channel);
    }

    public void test() throws InterruptedException, InvalidArgumentException, ExecutionException, TimeoutException, ProposalException {
        log.debug("===================================");
        fabricUtil.executeTransaction("register", "jia", "1008619");
        fabricUtil.executeTransaction("query", "id", "1008619");

        log.debug("===================================");
        fabricUtil.executeTransaction("change", "1008619");
        fabricUtil.executeTransaction("query", "id", "1008619");
    }

    public BaseResponse register(UserInfo userInfo) {
        try {
            fabricUtil.executeTransaction("register", userInfo.getUserName(), userInfo.getUserId());
        } catch (Exception e) {
            log.error("注册失败");
            log.error(e.getMessage());
            return new BaseResponse(500, "注册失败");
        }
        return BaseResponse.success("success");
    }

    public BaseResponse query(String functionName, String[] args) {
        try {
            List<String> result = new ArrayList<>();
            Collection<ProposalResponse> proposalResponses = fabricUtil.queryByChainCode(functionName, args);
            for (ProposalResponse response:proposalResponses) {
                String payload = new String(response.getChaincodeActionResponsePayload());
                log.debug("===========================");
                log.debug(payload);
                log.debug("===========================");
                result.add(payload);
            }
            return BaseResponse.success("success",result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new  BaseResponse(500,"查询失败");
        }
    }

    public BaseResponse change(String id){
        try {
            fabricUtil.executeTransaction("change", id);
        } catch (Exception e) {
            log.error("状态修改失败");
            return new BaseResponse(500, "状态修改失败");
        }
        return BaseResponse.success("success");
    }

    public BaseResponse findAll() {
        return this.query("queryAll", null);
    }
}
