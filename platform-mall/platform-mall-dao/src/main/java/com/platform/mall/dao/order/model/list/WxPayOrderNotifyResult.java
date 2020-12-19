package com.platform.mall.dao.order.model.list;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信支付回调参数封装
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-24 14:43:02
 */
@Data
@NoArgsConstructor
@XStreamAlias("orderNotifyResult")
public class WxPayOrderNotifyResult {
  private static final long serialVersionUID = 5389718115223345496L;

  /**
   * 返回状态码.
   */
  @XStreamAlias("return_code")
  protected String returnCode;
  /**
   * 返回信息.
   */
  @XStreamAlias("return_msg")
  protected String returnMsg;

  //当return_code为SUCCESS的时候，还会包括以下字段：

  /**
   * 业务结果.
   */
  @XStreamAlias("result_code")
  private String resultCode;
  /**
   * 错误代码.
   */
  @XStreamAlias("err_code")
  private String errCode;
  /**
   * 错误代码描述.
   */
  @XStreamAlias("err_code_des")
  private String errCodeDes;
  /**
   * 公众账号ID.
   */
  @XStreamAlias("appid")
  private String appid;
  /**
   * 商户号.
   */
  @XStreamAlias("mch_id")
  private String mchId;
  /**
   * 服务商模式下的子公众账号ID.
   */
  @XStreamAlias("sub_appid")
  private String subAppId;
  /**
   * 服务商模式下的子商户号.
   */
  @XStreamAlias("sub_mch_id")
  private String subMchId;
  /**
   * 随机字符串.
   */
  @XStreamAlias("nonce_str")
  private String nonceStr;
  /**
   * 签名.
   */
  @XStreamAlias("sign")
  private String sign;
  
  @XStreamAlias("promotion_detail")
  private String promotionDetail;
  
  @XStreamAlias("device_info")
  private String deviceInfo;
  
  @XStreamAlias("openid")
  private String openid;
  
  @XStreamAlias("is_subscribe")
  private String isSubscribe;
  
  @XStreamAlias("sub_openid")
  private String subOpenid;
  
  @XStreamAlias("sub_is_subscribe")
  private String subIsSubscribe;
  
  @XStreamAlias("trade_type")
  private String tradeType;

  @XStreamAlias("bank_type")
  private String bankType;

  @XStreamAlias("total_fee")
  private Integer totalFee;

  @XStreamAlias("settlement_total_fee")
  private Integer settlementTotalFee;

  @XStreamAlias("fee_type")
  private String feeType;

  @XStreamAlias("cash_fee")
  private Integer cashFee;

  @XStreamAlias("cash_fee_type")
  private String cashFeeType;

  @XStreamAlias("coupon_fee")
  private Integer couponFee;

  @XStreamAlias("coupon_count")
  private Integer couponCount;

  @XStreamAlias("transaction_id")
  private String transactionId;

  @XStreamAlias("out_trade_no")
  private String outTradeNo;

  @XStreamAlias("attach")
  private String attach;

  @XStreamAlias("time_end")
  private String timeEnd;

  @XStreamAlias("version")
  private String version;

  @XStreamAlias("rate_value")
  private String rateValue;

  @XStreamAlias("sign_type")
  private String signType;


  /**
   * xml 转bean
   * */
  public static WxPayOrderNotifyResult XML2Bean(String xmlString) {
    XStream xstream = new XStream();
    xstream.processAnnotations(WxPayOrderNotifyResult.class);
    WxPayOrderNotifyResult result = (WxPayOrderNotifyResult) xstream.fromXML(xmlString);
    return result;
  }

}
