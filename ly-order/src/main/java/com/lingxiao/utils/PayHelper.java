package com.lingxiao.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.lingxiao.config.PayConfig;
import com.lingxiao.config.WxPayConfiguration;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.enums.OrderStatusEnum;
import com.lingxiao.enums.PayStatusEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.mapper.OrderMapper;
import com.lingxiao.mapper.OrderStatusMapper;
import com.lingxiao.pojo.Order;
import com.lingxiao.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
public class PayHelper {
    @Autowired
    private WXPay wxPay;
    @Autowired
    private PayConfig config;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderStatusMapper statusMapper;
    public String createOrder(Long orderId, Long totalPay, String desc){
        try {
            Map<String, String> data = new HashMap<>();
            //商品描述
            data.put("body",desc);
            //订单号
            data.put("out_trade_no",orderId.toString());
            //金额
            data.put("total_fee",totalPay.toString());
            //调用微信支付的终端ip
            data.put("spbill_create_ip","127.0.0.1");
            //回调地址
            data.put("notify_url",config.getNotifyUrl());
            //交易类型为扫码支付
            data.put("trade_type","NATIVE");
            Map<String, String> result = wxPay.unifiedOrder(data);

            isSuccess(result);
            //下单成功，获取支付二维码链接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("微信创建订单异常，失败原因: {}",e);
            e.printStackTrace();
            return null;
        }
    }

    public void isSuccess(Map<String,String> result){
        //判断通信标识
        String returnCode = result.get("return_code");
        if (WXPayConstants.FAIL.equals(returnCode)){
            log.error("微信下单通信失败，失败原因: {}",result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }

        //判断业务标识
        String resultCode = result.get("result_code");
        if (WXPayConstants.FAIL.equals(resultCode)){
            log.error("微信下单业务失败，失败原因: {}",result.get("err_code"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

    /**
     * 校验签名
     * @param result
     */
    public void isValidSign(Map<String,String> result) {
        try {
            String sha256Sign = WXPayUtil.generateSignature(
                            result,
                            config.getKey(),
                            WXPayConstants.SignType.HMACSHA256);
            String md5Sign = WXPayUtil.generateSignature(
                    result,
                    config.getKey(),
                    WXPayConstants.SignType.MD5);
            String realSign = result.get("sign");
            if (!StringUtils.equals(sha256Sign,realSign) && !StringUtils.equals(sha256Sign,md5Sign)){
                throw new LyException(ExceptionEnum.ILLEGA_PAY_SIGN);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.ILLEGA_PAY_SIGN);
        }
    }

    public PayStatusEnum queryPayState(Long id){
        Map<String,String> data = new HashMap<>();
        data.put("out_trade_no",id.toString());
        try {
            Map<String, String> result = wxPay.orderQuery(data);

            isSuccess(result);
            isValidSign(result);

            //判断金额是否一致
            String total_fee = result.get("total_fee");
            if (StringUtils.isBlank(total_fee)){
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            String outTradeNo = result.get("out_trade_no");
            Long orderId = Long.valueOf(outTradeNo);
            Order order = orderMapper.selectByPrimaryKey(orderId);
            Long actualPay = order.getActualPay();
            if (!total_fee.equals(String.valueOf(actualPay))){
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            String state = result.get("trade_state");
            if (WXPayConstants.SUCCESS.equals(state)){
                //修改订单状态
                OrderStatus status = new OrderStatus();
                status.setStatus(OrderStatusEnum.PAYED.code());
                status.setOrderId(orderId);
                status.setPaymentTime(new Date());
                int count = statusMapper.updateByPrimaryKeySelective(status);
                if (count != 1){
                    throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                return PayStatusEnum.SUCCESS;
            }
            if ("NOTPAY".equals(state) || "USERPAYING".equals(state)){
                return PayStatusEnum.NOT_PAY;
            }
            return PayStatusEnum.FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            return PayStatusEnum.NOT_PAY;
        }
    }
}
