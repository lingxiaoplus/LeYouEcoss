package com.lingxiao.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;

@Data
public class PayConfig implements WXPayConfig {
    private String appID; //公众账号id
    private String mchID; //商户id
    private String key;  //生成签名的随机秘钥
    private int httpConnectTimeoutMs; //连接超时时间
    private int httpReadTimeoutMs; //读取超时时间
    private String notifyUrl; //回调地址
    @Override
    public InputStream getCertStream() {
        return null;
    }
}
