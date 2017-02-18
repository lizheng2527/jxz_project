
package com.zdhx.androidbase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 互动交流动态类
 */
public class Treads implements Serializable {

    /**
     * dataList : [{"child":[],"id":"20170112173521400998030703271900","content":"1111","createTime":"今天 17:35:21","replyCount":0,"imagePath":"/il/module/teacherPreparation/img/man.png","canDelete":"yes","praiseNames":[],"userName":"杨淑明(教师)","attachment":{"type":"other","iconList":[{"img":"/il/module/teacherPreparation/img/icons/_defaultVedio.png","fileName":"引导页 (2).avi","fileSize":"339.09K","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701121735198065831177922278906210e537ab7425ada8f8cd2430ccb36f&configCode=communcationVideo"}]},"praiseQuantity":0},{"child":[],"id":"20170112172531061541523433804280","content":"11111","createTime":"今天 17:25:31","replyCount":0,"imagePath":"/il/module/teacherPreparation/img/man.png","canDelete":"yes","praiseNames":[],"userName":"杨淑明(教师)","attachment":{"type":"picture","iconList":[{"fileName":"IMG_8086.JPG","img":"/component/attachment!showPic.action?checkUser=false&period=&downloadToken=201701121725528404457053607801496210e537ab7425ada8f8cd2430ccb36f&configCode=communcationPic","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701121725266307172350081657196210e537ab7425ada8f8cd2430ccb36f&configCode=communcationPic"},{"fileName":"IMG_8087.JPG","img":"/component/attachment!showPic.action?checkUser=false&period=&downloadToken=201701121725528451776207873400976210e537ab7425ada8f8cd2430ccb36f&configCode=communcationPic","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701121725275791791045139861686210e537ab7425ada8f8cd2430ccb36f&configCode=communcationPic"}]},"praiseQuantity":0}]
     * totalPage : 1
     * totalCount : 2
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
         * child : []
         * id : 20170112173521400998030703271900
         * content : 1111
         * createTime : 今天 17:35:21
         * replyCount : 0
         * imagePath : /il/module/teacherPreparation/img/man.png
         * canDelete : yes
         * praiseNames : []
         * userName : 杨淑明(教师)
         * attachment : {"type":"other","iconList":[{"img":"/il/module/teacherPreparation/img/icons/_defaultVedio.png","fileName":"引导页 (2).avi","fileSize":"339.09K","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701121735198065831177922278906210e537ab7425ada8f8cd2430ccb36f&configCode=communcationVideo"}]}
         * praiseQuantity : 0
         */

        private String id;
        private String content;
        private String createTime;
        private int replyCount;
        private String imagePath;
        private String canDelete;
        private String userName;
        private AttachmentBean attachment;
        private int praiseQuantity;
        private List<DataListBean> child;
        private List<String> praiseNames;

        private Boolean isLaunch = false;
        private int allReplyCount;

        public int getAllReplyCount() {
            return allReplyCount;
        }

        public void setAllReplyCount(int allReplyCount) {
            this.allReplyCount = allReplyCount;
        }

        public Boolean getLaunch() {
            return isLaunch;
        }

        public void setLaunch(Boolean launch) {
            isLaunch = launch;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getCanDelete() {
            return canDelete;
        }

        public void setCanDelete(String canDelete) {
            this.canDelete = canDelete;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public AttachmentBean getAttachment() {
            return attachment;
        }

        public void setAttachment(AttachmentBean attachment) {
            this.attachment = attachment;
        }

        public int getPraiseQuantity() {
            return praiseQuantity;
        }

        public void setPraiseQuantity(int praiseQuantity) {
            this.praiseQuantity = praiseQuantity;
        }

        public List<DataListBean> getChild() {
            return child;
        }

        public void setChild(List<DataListBean> child) {
            this.child = child;
        }

        public List<String> getPraiseNames() {
            return praiseNames;
        }

        public void setPraiseNames(List<String> praiseNames) {
            this.praiseNames = praiseNames;
        }

        public static class AttachmentBean {
            /**
             * type : other
             * iconList : [{"img":"/il/module/teacherPreparation/img/icons/_defaultVedio.png","fileName":"引导页 (2).avi","fileSize":"339.09K","downUrl":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201701121735198065831177922278906210e537ab7425ada8f8cd2430ccb36f&configCode=communcationVideo"}]
             */

            private String type;
            private List<IconListBean> iconList;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<IconListBean> getIconList() {
                return iconList;
            }

            public void setIconList(List<IconListBean> iconList) {
                this.iconList = iconList;
            }

            public static class IconListBean {
                /**
                 * img : /il/module/teacherPreparation/img/icons/_defaultVedio.png
                 * fileName : 引导页 (2).avi
                 * fileSize : 339.09K
                 * downUrl : /component/attachment!download.action?checkUser=false&period=&downloadToken=201701121735198065831177922278906210e537ab7425ada8f8cd2430ccb36f&configCode=communcationVideo
                 */

                private String img;
                private String fileName;
                private String fileSize;
                private String downUrl;
                private String address;

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }

                public String getFileName() {
                    return fileName;
                }

                public void setFileName(String fileName) {
                    this.fileName = fileName;
                }

                public String getFileSize() {
                    return fileSize;
                }

                public void setFileSize(String fileSize) {
                    this.fileSize = fileSize;
                }

                public String getDownUrl() {
                    return downUrl;
                }

                public void setDownUrl(String downUrl) {
                    this.downUrl = downUrl;
                }
            }
        }
    }
}

