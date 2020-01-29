package com.tyut.utils;

import java.util.List;

public class GetConfigReq {
    /**
     * ret : 0
     * msg : succes,
     * datas : [{"ID":"  0","categoryName":"社团","state":"1"},{"ID":"1","categoryName":"原创","state":"1"},{"ID":"2","categoryName":"现货","state":"1"}]
     */


    private List<DatasBean> datas;

    public GetConfigReq() {
    }

    public GetConfigReq(List<DatasBean> datas) {

        this.datas = datas;
    }



    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class DatasBean {
        /**
         * ID :   0
         * categoryName : 社团
         * state : 1
         */

        private String ID;
        private String categoryName;

        public DatasBean(String ID, String categoryName) {
            this.ID = ID;
            this.categoryName = categoryName;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }


    }

    @Override
    public String toString() {
        return "GetConfigReq{" +
                "datas=" + datas +
                '}';
    }
}