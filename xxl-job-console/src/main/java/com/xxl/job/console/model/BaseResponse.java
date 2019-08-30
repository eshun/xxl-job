package com.xxl.job.console.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.joda.time.DateTime;

import java.sql.Timestamp;

/**
 * @author esun
 */
@ApiModel("请求响应")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {

    public BaseResponse() {
        this.code = -1;
        this.message = null;
        this.timestamp = new Timestamp(new DateTime().getMillis());
    }

    /**
     * 请求是否成功
     */
    @ApiModelProperty("请求是否成功")
    private boolean success;
    /**
     * 成功或者失败的code错误码
     */
    @ApiModelProperty("请求状态码")
    private int code;
    /**
     * 成功时返回的数据
     */
    @ApiModelProperty("数据")
    private Object data;
    /**
     * 失败时返回具体的异常信息
     */
    @ApiModelProperty("异常信息")
    private String message;
    /**
     * 服务器当前时间（添加该字段的原因是便于查找定位请求时间，因为实际开发过程中服务器时间可能跟本地时间不一致，加上这个时间戳便于日后定位）
     */
    @ApiModelProperty("请求时间")
    private Timestamp timestamp;

    public void Success(Object data) {
        this.success = true;
        this.code = 0;
        this.message = null;
        this.data = data;
    }

    public void Fail(String message) {
        this.Fail(message, 500);
    }

    /**
     *
     * @param message
     * @param code
     *  201: '新建或修改数据成功。',
     *  202: '一个请求已经进入后台排队（异步任务）。',
     *  204: '删除数据成功。',
     *  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
     *  401: '用户没有权限（令牌、用户名、密码错误）。',
     *  403: '用户得到授权，但是访问是被禁止的。',
     *  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
     *  406: '请求的格式不可得。',
     *  410: '请求的资源被永久删除，且不会再得到的。',
     *  422: '当创建一个对象时，发生一个验证错误。',
     *  500: '服务器发生错误，请检查服务器。',
     *  502: '网关错误。',
     *  503: '服务不可用，服务器暂时过载或维护。',
     *  504: '网关超时。',
     */
    public void Fail(String message, int code) {
        this.success = true;
        this.code = code;
        this.message = message;
        this.data = null;
    }
}
