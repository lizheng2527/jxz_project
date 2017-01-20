
package com.zdhx.androidbase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 互动交流动态类
 */
public class OperateBean implements Serializable {

    /**
     * dataList : [{"id":"20161221084409715501652656751145","createTime":"2016-12-21 08:44:09","userName":"杨恩桐"},{"id":"20161221085748146609841117757880","createTime":"2016-12-21 08:57:48","userName":"徐一鸣"},{"id":"20161221085845594243908899280232","createTime":"2016-12-21 08:58:45","userName":"冀文博"},{"id":"20161221090110846254000291015568","createTime":"2016-12-21 09:01:10","userName":"孙彦凯"},{"id":"20161221092334373165502610195334","createTime":"2016-12-21 09:23:34","userName":"丁小雷"},{"id":"20161221094738781168033828143602","createTime":"2016-12-21 09:47:38","userName":"孙可彬"},{"id":"20161221095948524558256415027306","createTime":"2016-12-21 09:59:48","userName":"朱雪健"},{"id":"20161221104248869112673498865286","createTime":"2016-12-21 10:42:48","userName":"吕禹婷"},{"id":"20161221111831647661089810452139","createTime":"2016-12-21 11:18:31","userName":"李可欣"},{"id":"20161221112945910848095330241559","createTime":"2016-12-21 11:29:45","userName":"赵方悦"},{"id":"20161221113132846686711453669110","createTime":"2016-12-21 11:31:32","userName":"刘颖媚"}]
     * totalPage : 1
     * totalCount : 11
     * status : 1
     */

    private int totalPage;
    private int totalCount;
    private String status;
    private List<DataListBean> dataList;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public static class DataListBean {
        /**
         * id : 20161221084409715501652656751145
         * createTime : 2016-12-21 08:44:09
         * userName : 杨恩桐
         */

        private String id;
        private String createTime;
        private String userName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
