package com.example.baas.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author John
 * @create 2019/7/9 13:59
 */
@Data
@ApiModel("用户信息")
public class UserInfo {

    @NotBlank
    private String userName;

    @NotBlank
    private String userId;

    private Integer age;
}
