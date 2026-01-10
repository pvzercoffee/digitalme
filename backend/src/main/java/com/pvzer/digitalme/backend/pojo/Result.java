package com.pvzer.digitalme.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    Integer code;
    String msg;
    Object data;

    public static Result success(){

        Result result = new Result();
        result.code = 1;
        result.msg = "success";

        return result;
    }

    public static Result success(Object data){

        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        result.data = data;

        return result;
    }

    public static Result success(String msg,Object data){

        Result result = new Result();
        result.code = 1;
        result.msg = msg;
        result.data = data;

        return result;
    }

    public static Result error(String msg){

        Result result = new Result();
        result.code = 0;
        result.msg = msg;

        return result;
    }
}
