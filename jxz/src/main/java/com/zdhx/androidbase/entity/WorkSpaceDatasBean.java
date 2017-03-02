
package com.zdhx.androidbase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 工作平台类
 */
public class WorkSpaceDatasBean implements Serializable {

    /**
     * dataList : [{"teacherpreselection":"0","createTime":"2017-01-14 12:46:01","highQuantity":"0","teacherfinalselection":"0","canDelete":"1","iconUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141246016342891218238204366210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245503547802448051708116210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","isOpen":"0","size":"63.45K","id":"20170114124601506404668446924067","checkStatus":"审核通过","browse":0,"name":"test.JPG","userId":"20130824153101056515376770730911","highQuantityName":"","userName":"杨淑明","checkStatusName":"","down":0},{"teacherpreselection":"0","createTime":"2017-01-14 12:45:59","highQuantity":"0","teacherfinalselection":"0","canDelete":"1","iconUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141246014820283078551413486210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245502217874002819553706210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","isOpen":"0","size":"18.52M","id":"20170114124559086791982373552643","checkStatus":"审核通过","browse":0,"name":"IMG_8089.JPG","userId":"20130824153101056515376770730911","highQuantityName":"","userName":"杨淑明","checkStatusName":"","down":0},{"teacherpreselection":"0","createTime":"2017-01-14 12:45:56","highQuantity":"0","teacherfinalselection":"0","canDelete":"1","iconUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245590620225700416319666210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245494076062652175419726210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","isOpen":"0","size":"16.77M","id":"20170114124556790116410233968306","checkStatus":"审核通过","browse":0,"name":"IMG_8088.JPG","userId":"20130824153101056515376770730911","highQuantityName":"","userName":"杨淑明","checkStatusName":"","down":0},{"teacherpreselection":"0","createTime":"2017-01-14 12:45:54","highQuantity":"0","teacherfinalselection":"0","canDelete":"1","iconUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245567685915781244558546210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245485860406679547703516210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","isOpen":"0","size":"16.63M","id":"20170114124554502713940820498292","checkStatus":"审核通过","browse":0,"name":"IMG_8087.JPG","userId":"20130824153101056515376770730911","highQuantityName":"","userName":"杨淑明","checkStatusName":"","down":0},{"teacherpreselection":"0","createTime":"2017-01-14 12:45:52","highQuantity":"0","teacherfinalselection":"0","canDelete":"1","iconUrl":"/il/module/teacherPreparation/img/icons/office_Word.png","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245399450755711896845146210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile","isOpen":"0","size":"20.97K","id":"20170114124552024847941075153567","checkStatus":"审核通过","browse":0,"name":"2、数字校园V2项目实施流程（了解）.docx","userId":"20130824153101056515376770730911","highQuantityName":"","userName":"杨淑明","checkStatusName":"","down":0}]
     * totalPage : 342
     * totalCount : 1709
     * status : 0
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
         * teacherpreselection : 0
         * createTime : 2017-01-14 12:46:01
         * highQuantity : 0
         * teacherfinalselection : 0
         * canDelete : 1
         * iconUrl : /component/attachment!download.action?checkUser=false&period=&downloadToken=201701141246016342891218238204366210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile
         * downUrl : /component/attachment!download.action?checkUser=false&period=&downloadToken=201701141245503547802448051708116210e537ab7425ada8f8cd2430ccb36f&configCode=chapterResourceFile
         * isOpen : 0
         * size : 63.45K
         * id : 20170114124601506404668446924067
         * checkStatus : 审核通过
         * browse : 0
         * name : test.JPG
         * userId : 20130824153101056515376770730911
         * highQuantityName :
         * userName : 杨淑明
         * checkStatusName :
         * down : 0
         */

        private String teacherpreselection;
        private String createTime;
        private String highQuantity;
        private String teacherfinalselection;
        private String canDelete;
        private String iconUrl;
        private String downUrl;
        private String isOpen;
        private String size;
        private String id;
        private String checkStatus;
        private int browse;
        private String name;
        private String userId;
        private String highQuantityName;
        private String userName;
        private String checkStatusName;
        private int downCount;
        private boolean isLoading;

        public boolean isLoading() {
            return isLoading;
        }

        public void setLoading(boolean loading) {
            isLoading = loading;
        }

        public int getDownCount() {
            return downCount;
        }

        public void setDownCount(int downCount) {
            this.downCount = downCount;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        private boolean isSelect;

        public String getResourceStyle() {
            return resourceStyle;
        }

        public void setResourceStyle(String resourceStyle) {
            this.resourceStyle = resourceStyle;
        }

        private String resourceStyle;
        private int down;

        public String getTeacherpreselection() {
            return teacherpreselection;
        }

        public void setTeacherpreselection(String teacherpreselection) {
            this.teacherpreselection = teacherpreselection;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getHighQuantity() {
            return highQuantity;
        }

        public void setHighQuantity(String highQuantity) {
            this.highQuantity = highQuantity;
        }

        public String getTeacherfinalselection() {
            return teacherfinalselection;
        }

        public void setTeacherfinalselection(String teacherfinalselection) {
            this.teacherfinalselection = teacherfinalselection;
        }

        public String getCanDelete() {
            return canDelete;
        }

        public void setCanDelete(String canDelete) {
            this.canDelete = canDelete;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getDownUrl() {
            return downUrl;
        }

        public void setDownUrl(String downUrl) {
            this.downUrl = downUrl;
        }

        public String getIsOpen() {
            return isOpen;
        }

        public void setIsOpen(String isOpen) {
            this.isOpen = isOpen;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(String checkStatus) {
            this.checkStatus = checkStatus;
        }

        public int getBrowse() {
            return browse;
        }

        public void setBrowse(int browse) {
            this.browse = browse;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getHighQuantityName() {
            return highQuantityName;
        }

        public void setHighQuantityName(String highQuantityName) {
            this.highQuantityName = highQuantityName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCheckStatusName() {
            return checkStatusName;
        }

        public void setCheckStatusName(String checkStatusName) {
            this.checkStatusName = checkStatusName;
        }

        public int getDown() {
            return down;
        }

        public void setDown(int down) {
            this.down = down;
        }
    }
}
