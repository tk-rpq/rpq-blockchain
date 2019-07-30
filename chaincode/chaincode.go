/*
 * SPDX-License-Identifier: Apache-2.0
 */

package main

import (
	"fmt"
	"bytes"
	"encoding/json"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// Chaincode is the definition of the chaincode structure.
type Chaincode struct {
}
type UserInfo struct {
	Name string `json:"name"`
	Id string `json:"id"`
	UserStatus string `json:"user_status"`
}

// Init is called when the chaincode is instantiated by the blockchain network.
func (cc *Chaincode) Init(stub shim.ChaincodeStubInterface) sc.Response {
	fcn, params := stub.GetFunctionAndParameters()
	fmt.Println("Init()", fcn, params)
	return shim.Success(nil)
}

// Invoke is called as a result of an application request to run the chaincode.
func (cc *Chaincode) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	fcn, params := stub.GetFunctionAndParameters()
	switch fcn {
	case "register":
		return userRegister(stub,params)
	case "change":
		return changeUserStatus(stub,params)
	case "query":
		return queryUserInfo(stub,params)
	case "queryAll":
		return queryAllUserInfo(stub)
	default:
		return shim.Error(fmt.Sprintf("unsupported function: %s",fcn))
	}
}

func userRegister(stub shim.ChaincodeStubInterface,params []string) sc.Response {
	if len(params) !=2 {
		return shim.Error("no enough params")
	}
	name :=params[0]
	id :=params[1]
	if name==""||id==""{
		return shim.Error("invalid params")
	}

	userKey,_ := stub.CreateCompositeKey("userInfo",[]string{id})

	result,err :=stub.GetState(userKey)

	if err!=nil || result !=nil {
		return shim.Error("userInfo is exist")
	}

	userInfo:=&UserInfo{
		Name:name,
		Id:id,
		UserStatus:"1",
	}

	userInfoBytes,err := json.Marshal(userInfo)

	if err!=nil {
		return shim.Error(fmt.Sprintf("marshal user error %s", err))
	}

	if err :=stub.PutState(userKey,userInfoBytes);err!=nil{
		return shim.Error(fmt.Sprintf("put user error %s", err))
	}
	return shim.Success(nil)
}

func changeUserStatus(stub shim.ChaincodeStubInterface,params []string) sc.Response{

	if len(params) != 1 {
		return shim.Error("no enough params")
	}
	id :=params[0]
	if id == "" {
		return shim.Error("invalid params")
	}

	userKey,_ := stub.CreateCompositeKey("userInfo",[]string{id})

	result,err :=stub.GetState(userKey)

	if err!=nil || result ==nil {
		return shim.Error("the userInfo not found")
	}


	userInfo :=new(UserInfo)
	if err:=json.Unmarshal(result,userInfo);err !=nil{
		return shim.Error(fmt.Sprintf("unmarshal error: %s", err))
	}

	userInfo.UserStatus = "0"

	userInfoBytes,err:=json.Marshal(userInfo)

	if err!=nil {
		return shim.Error(fmt.Sprintf("marshal user error %s", err))
	}

	if err :=stub.PutState(userKey,userInfoBytes);err!=nil{
		return shim.Error(fmt.Sprintf("put user error %s", err))
	}

	//resp, _ := http.Get("http://192.168.242.1:8080/app/test?userId="+id)
	//defer resp.Body.Close()
	//body, _ := ioutil.ReadAll(resp.Body)
	//fmt.Printf("Get request result: %s\n", string(body))

	return shim.Success(nil)
}

func queryAllUserInfo(stub shim.ChaincodeStubInterface) sc.Response{
	
	resultsIterator, err := stub.GetQueryResult(`{"selector":{"user_status":{"$gte":0}}}`)
    if err != nil {
        return shim.Error(err.Error())
	}
	//最后释放迭代器资源
	defer resultsIterator.Close()
		
	var buffer bytes.Buffer
	bArrayMemberAlreadyWritten := false
    buffer.WriteString(`{"result":[`)

    for resultsIterator.HasNext() {
       	queryResponse, err := resultsIterator.Next() //获取迭代器中的每一个值
       	if err != nil {
           	return shim.Error("Fail")
		}
       	if bArrayMemberAlreadyWritten == true {
           	buffer.WriteString(",")
       	}
       	buffer.WriteString(string(queryResponse.Value)) //将查询结果放入Buffer中
       	bArrayMemberAlreadyWritten = true
    }
	buffer.WriteString(`]}`)

	return shim.Success(buffer.Bytes())	
}

func queryUserInfo(stub shim.ChaincodeStubInterface,params []string) sc.Response{
	if len(params) != 2 {
		return shim.Error("no enough params")
	}
	queryType:=params[0]
	queryValue:=params[1]
	if queryType =="" || queryValue=="" {
		return shim.Error("invalid params")
	}

	switch queryType {
	case "id":
		userKey,_ := stub.CreateCompositeKey("userInfo",[]string{queryValue})
		result,err := stub.GetState(userKey)
		if err!=nil || result ==nil {
			return shim.Error("the userInfo not found")
		}
		return shim.Success(result)
	case "alive":
		var queryString = fmt.Sprintf(`{"selector":{"user_status":"%s"}}`, queryValue)
		resultsIterator, err := stub.GetQueryResult(queryString)
    	if err != nil {
        	return shim.Error(err.Error())
		}
		//最后释放迭代器资源
		defer resultsIterator.Close()
		
		var buffer bytes.Buffer
		bArrayMemberAlreadyWritten := false
    	buffer.WriteString(`{"result":[`)

    	for resultsIterator.HasNext() {
        	queryResponse, err := resultsIterator.Next() //获取迭代器中的每一个值
        	if err != nil {
            	return shim.Error("Fail")
			}
        	if bArrayMemberAlreadyWritten == true {
            	buffer.WriteString(",")
        	}
        	buffer.WriteString(string(queryResponse.Value)) //将查询结果放入Buffer中
        	bArrayMemberAlreadyWritten = true
    	}
		buffer.WriteString(`]}`)

    	return shim.Success(buffer.Bytes())
	default:
		return shim.Error("unsupported query type")
	}
}
