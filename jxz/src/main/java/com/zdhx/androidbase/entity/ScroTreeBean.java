
package com.zdhx.androidbase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 互动交流动态类
 */
public class ScroTreeBean implements Serializable {

    private List<ListBean> eclassList;
    private List<ListBean> schoolList;
    private List<ListBean> gradeList;

    public List<ListBean> getEclassList() {
        return eclassList;
    }

    public void setEclassList(List<ListBean> eclassList) {
        this.eclassList = eclassList;
    }

    public List<ListBean> getSchoolList() {
        return schoolList;
    }

    public void setSchoolList(List<ListBean> schoolList) {
        this.schoolList = schoolList;
    }

    public List<ListBean> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<ListBean> gradeList) {
        this.gradeList = gradeList;
    }


    public static class ListBean {
        /**
         * id : 20160902121018933533331941260144
         * parentId : 20130418090946624479556372135419
         * type : eclass
         * name : 1班
         */

        private String id;
        private String parentId;
        private String type;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
