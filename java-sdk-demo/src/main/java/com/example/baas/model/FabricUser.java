/*
 *  Copyright 2018 Aliyun.com All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.baas.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

/**
 * @author John
 * User 是一个 Fabric 应用程序需要自行实现的接口
 */
@Getter
@Setter
public class FabricUser implements User {

    private String name;
    private String organization;
    private String mspId;
    private String account;
    private Set<String> roles;
    Enrollment enrollment;
    private String affiliation;
}
