package com.example.baas.controller.api;

import com.example.baas.model.BaseResponse;
import com.example.baas.model.UserInfo;
import com.example.baas.service.FabricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author John
 * @create 2019/7/9 13:46
 */
@Slf4j
@Api("BlockChain 接口")
@RestController
@RequestMapping(value = "/api/v1/blockchain", produces = "application/json;charset=utf-8")
public class BlockchainController {

    private FabricService fabricService;

    public BlockchainController(FabricService fabricService){
        this.fabricService=fabricService;
    }

    @ApiOperation("测试接口")
    @GetMapping("/test")
    public BaseResponse test() {
        try {
            fabricService.test();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return BaseResponse.success("success");
    }

    @ApiOperation("用户信息注册")
    @PostMapping("/register")
    public BaseResponse register(@RequestBody @Validated UserInfo userInfo) {
        return fabricService.register(userInfo);
    }

    @ApiOperation("根据用户id，查询信息")
    @GetMapping("/id/{id}")
    public BaseResponse queryUserById(@PathVariable("id") String id) {
        return fabricService.query("query", new String[]{"id", id});
    }

    @ApiOperation("根据状态，查询信息")
    @GetMapping("/alive/{status}")
    public BaseResponse queryUserByStatus(@PathVariable("status") String status) {
        return fabricService.query("query",new String[]{"alive",status});
    }

    @ApiOperation("修改用户状态")
    @PostMapping("/{id}")
    public BaseResponse change(@PathVariable("id") String id) {
        return fabricService.change(id);
    }

    @ApiOperation("查询全部用户")
    @GetMapping("/users")
    public BaseResponse findAll(){
        return fabricService.findAll();
    }
}
