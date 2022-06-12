package com.platform.common.result;

/**
 *
 * @author WangJia
 * @date 2016/10/26
 * 共用参数封装、简化查询
 * 线程
 */
public class CommonParam {

    /**
     * 分页偏移量
     */
    private static ThreadLocal<Integer> offSet = new ThreadLocal<Integer>();

    /**
     * 分页页大小
     */
    private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();

    /**
     * 排序条件
     */
    private static ThreadLocal<String> orderCondition = new ThreadLocal<String>();

    /**
     * 排序方向
     */
    private static ThreadLocal<String> orderDirection = new ThreadLocal<String>();

    /**
     * 店铺集团code
     */
    private static ThreadLocal<String> tenantGroupCode = new ThreadLocal<String>();

    /**
     * 店铺门店code
     */
    private static ThreadLocal<String> tenantCode = new ThreadLocal<String>();

    /**
     * 用户账号
     * */
    private static ThreadLocal<String> userName = new ThreadLocal<String>();

    /**
     * 会员id
     * */
    private static ThreadLocal<Long> memberId = new ThreadLocal<Long>();

    public static Integer getOffSet() {
        return offSet.get();
    }

    public static void setOffSet(Integer offSet) {
        CommonParam.offSet.set(offSet);
    }

    public static Integer getPageSize() {
        return pageSize.get();
    }

    public static void setPageSize(Integer pageSize) {
        CommonParam.pageSize .set(pageSize);
    }

    public static String getOrderCondition() {
        return orderCondition.get();
    }

    public static void setOrderCondition(String orderCondition) {
        CommonParam.orderCondition.set(orderCondition);
    }

    public static String getOrderDirection() {
        return orderDirection.get();
    }

    public static void setOrderDirection(String orderDirection) {
        CommonParam.orderDirection.set(orderDirection);
    }

    public static String getTenantGroupCode() {
        return tenantGroupCode.get();
    }

    public static void setTenantGroupCode(String shopGroupCode) {
        CommonParam.tenantGroupCode.set(shopGroupCode);
    }


    public static String getTenantCode() {
        return tenantCode.get();
    }

    public static void setTenantCode(String shopCode) {
        CommonParam.tenantCode.set(shopCode);
    }

    public static String getUserName() {
        return userName.get();
    }

    public static void setUserName(String userName) {
        CommonParam.userName.set(userName);
    }

    public static void removeOffSet() {
        CommonParam.offSet.remove();
    }

    public static void removePageSize() {
        CommonParam.pageSize.remove();
    }

    public static void removeOrderCondition() {
        CommonParam.orderCondition.remove();
    }

    public static void removeOrderDirection() {
        CommonParam.orderDirection.remove();
    }

    public static void removeTenantGroupCode() {
        CommonParam.tenantGroupCode.remove();
    }

    public static void removeTenantCode() {
        CommonParam.tenantCode.remove();
    }

    public static void removeUserName() {
        CommonParam.userName.remove();
    }

    public static Long getMemberId() {
        return memberId.get();
    }

    public static void setMemberId(Long memberId) {
        CommonParam.memberId.set(memberId);
    }

    public static void removeMemberId() {
        CommonParam.memberId.remove();
    }

}
