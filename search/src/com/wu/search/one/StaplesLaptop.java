package com.wu.search.one;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StaplesLaptop implements Serializable {

        private String itemId;
        private String model;
        private String href;
        private String rating;
        private String reviewCount;
        private String spec;
        private String priceOrig;
        private String priceSave;
        private String instantSave;
        private String rebate;
        private String priceFinal;
        private String priceOrig2;
        private String priceFinal2;
        private String priceSave2;

        public StaplesLaptop() {
                itemId = "";
                model = "";
                href = "";
                rating = "";
                reviewCount = "";
                spec = "";
                priceOrig = "";
                priceSave = "";
                instantSave = "";
                rebate = "";
                priceFinal = "";
                priceOrig2 = "";
                priceFinal2 = "";
                priceSave2 = "";
        }

        public String getItemId() {
                return itemId;
        }

        public String getModel() {
                return model;
        }

        public String getHref() {
                return href;
        }

        public String getRating() {
                return rating;
        }

        public String getReviewCount() {
                return reviewCount;
        }

        public String getSpec() {
                return spec;
        }

        public String getPriceOrig() {
                return priceOrig;
        }

        public String getPriceSave() {
                return priceSave;
        }

        public String getInstantSave() {
                return instantSave;
        }

        public String getRebate() {
                return rebate;
        }

        public String getPriceFinal() {
                return priceFinal;
        }

        public String getPriceOrig2() {
                return priceOrig2;
        }

        public String getPriceFinal2() {
                return priceFinal2;
        }

        public String getPriceSave2() {
                return priceSave2;
        }

        public void setItemId(String itemId) {
                this.itemId = itemId;
        }

        public void setModel(String model) {
                this.model = model;
        }

        public void setHref(String href) {
                this.href = href;
        }

        public void setRating(String rating) {
                this.rating = rating;
        }

        public void setReviewCount(String reviewCount) {
                this.reviewCount = reviewCount;
        }

        public void setSpec(String spec) {
                this.spec = spec;
        }

        public void setPriceOrig(String priceOrig) {
                this.priceOrig = priceOrig;
        }

        public void setPriceSave(String priceSave) {
                this.priceSave = priceSave;
        }

        public void setInstantSave(String instantSave) {
                this.instantSave = instantSave;
        }

        public void setRebate(String rebate) {
                this.rebate = rebate;
        }

        public void setPriceFinal(String priceFinal) {
                this.priceFinal = priceFinal;
        }

        public void setPriceOrig2(String priceOrig2) {
                this.priceOrig2 = priceOrig2;
        }

        public void setPriceFinal2(String priceFinal2) {
                this.priceFinal2 = priceFinal2;
        }

        public void setPriceSave2(String priceSave2) {
                this.priceSave2 = priceSave2;
        }

}
