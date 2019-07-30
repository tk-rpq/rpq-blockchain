$(function(){
    loadAllUserInfo();
});

function loadAllUserInfo(){
    $.ajax({
        //请求方式
        type : "GET",
        //请求的媒体类型
        contentType: "application/json;charset=UTF-8",
        //请求地址
        url : "/api/v1/blockchain/users",
        //数据，json字符串
        //请求成功
        success : function(result) {
            loadUserInfo(result);
        },
        //请求失败，包含具体的错误信息
        error : function(e){
            console.log(e.status);
            console.log(e.responseText);
        }
    });
}

$("#registerBtn").bind("click",function () {
    alert("保存按钮被点击");
    var userName = $("#userName").val();
    var userId = $("#userId").val();
    var age = $("#age").val();
    $.ajax({
        //请求方式
        type : "POST",
        dataType:'json',
        //请求的媒体类型
        contentType: "application/json;charset=UTF-8",
        //请求地址
        url : "/api/v1/blockchain/register",
        //数据，json字符串
        data : JSON.stringify({
            "userName":userName,
            "userId":userId,
            "age":age
        }),
        //请求成功
        success : function(result) {
            alert("保存" + result.message);
        },
        //请求失败，包含具体的错误信息
        error : function(e){
            console.log(e.status);
            console.log(e.responseText);
        }
    });
});

function loadUserInfo(result){
    $("#userInfoTable").empty();
    var userInfoes = JSON.parse(result.data[0]).result;
    var htmlContent = "<thead>\n" +
        "            <tr>\n" +
        "                <th>#</th>\n" +
        "                <th>userName</th>\n" +
        "                <th>userId</th>\n" +
        "                <th>userStatus</th>\n" +
        "                <th>操作</th>\n" +
        "            </tr>\n" +
        "            </thead>" +
        "<tbody>";
    for (var i  = 0; i  < userInfoes.length; i ++) {
        htmlContent += "<tr>\n" +
            "                <th scope=\"row\">"+ (i+1) +"</th>\n" +
            "                <td>"+ userInfoes[i].name +"</td>\n" +
            "                <td>"+ userInfoes[i].id +"</td>\n" +
            "                <td>"+ userInfoes[i].user_status +"</td>\n" +
            "                <td>" +
            "<button type=\"button\" class=\"btn btn-sm btn-primary\" onclick=\"changeStatus("+ userInfoes[i].id +")\">修改状态</button>" +
            "</td>\n" +
            "            </tr>"
    }
    htmlContent += "</tbody>";
    $("#userInfoTable").html(htmlContent);
}

function changeStatus(userId){
    $.ajax({
        //请求方式
        type : "POST",
        //请求地址
        url : "/api/v1/blockchain/" + userId,
        //数据，json字符串
        success : function(result) {
            alert("状态修改" + result.message);
            loadAllUserInfo();
        },
        //请求失败，包含具体的错误信息
        error : function(e){
            console.log(e.status);
            console.log(e.responseText);
        }
    });
}

function loadUserInfoByStatus(status){
    $.ajax({
        //请求方式
        type : "GET",
        //请求地址
        url : "/api/v1/blockchain/alive/" + status,
        //数据，json字符串
        success : function(result) {
            loadUserInfo(result);
            alert("查询" + result.message);
        },
        //请求失败，包含具体的错误信息
        error : function(e){
            console.log(e.status);
            console.log(e.responseText);
        }
    });
}
