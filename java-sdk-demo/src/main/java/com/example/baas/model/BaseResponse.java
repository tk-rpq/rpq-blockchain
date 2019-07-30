package com.example.baas.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Date;

/**
 * @author John
 * @create 2019/7/9 14:01
 */
@Data
@ApiModel("响应信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T>{

    private Integer status;

    private String message;

    private T data;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd hh:mm:ss")
    private String timestamp = FastDateFormat.getInstance("yyyy-MM-dd hh:mm:ss").format(new Date());

    public BaseResponse(Integer status,String message){
        this.status=status;
        this.message=message;
    }

    public BaseResponse(Integer status,String message,T data){
        this.status = status;
        this.message = message;
        this.data=data;
    }

    public static <T> BaseResponse<T> success(@Nullable String message,@Nullable T date){
        return new BaseResponse<>(HttpStatus.OK.value(), message, date);
    }

    public static <T> BaseResponse<T> success(@Nullable String message){
        return new BaseResponse<>(HttpStatus.OK.value(), message, null);
    }
}
