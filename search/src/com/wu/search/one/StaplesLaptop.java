package com.wu.search.one;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@SuppressWarnings("serial")
public class StaplesLaptop implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String itemId;
        private String model;
        private String href;
        private Double rating;
        private Integer reviewCount;
        private String spec;
        private Double priceOrig;
        private Double priceSave;
        private Double instantSave;
        private Double rebate;
        private Double priceFinal;
        private Double priceOrig2;
        private Double priceFinal2;
        private Double priceSave2;
        private Integer version;
        private Date updatedDate;

        public StaplesLaptop() {
        }

        public Integer getId() {
                return id;
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

        public Double getRating() {
                return rating;
        }

        public Integer getReviewCount() {
                return reviewCount;
        }

        public String getSpec() {
                return spec;
        }

        public Double getPriceOrig() {
                return priceOrig;
        }

        public Double getPriceSave() {
                return priceSave;
        }

        public Double getInstantSave() {
                return instantSave;
        }

        public Double getRebate() {
                return rebate;
        }

        public Double getPriceFinal() {
                return priceFinal;
        }

        public Double getPriceOrig2() {
                return priceOrig2;
        }

        public Double getPriceFinal2() {
                return priceFinal2;
        }

        public Double getPriceSave2() {
                return priceSave2;
        }

        public void setId(Integer id) {
                this.id = id;
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

        public void setRating(Double rating) {
                this.rating = rating;
        }

        public void setReviewCount(Integer reviewCount) {
                this.reviewCount = reviewCount;
        }

        public void setSpec(String spec) {
                this.spec = spec;
        }

        public void setPriceOrig(Double priceOrig) {
                this.priceOrig = priceOrig;
        }

        public void setPriceSave(Double priceSave) {
                this.priceSave = priceSave;
        }

        public void setInstantSave(Double instantSave) {
                this.instantSave = instantSave;
        }

        public void setRebate(Double rebate) {
                this.rebate = rebate;
        }

        public void setPriceFinal(Double priceFinal) {
                this.priceFinal = priceFinal;
        }

        public void setPriceOrig2(Double priceOrig2) {
                this.priceOrig2 = priceOrig2;
        }

        public void setPriceFinal2(Double priceFinal2) {
                this.priceFinal2 = priceFinal2;
        }

        public void setPriceSave2(Double priceSave2) {
                this.priceSave2 = priceSave2;
        }

        public Integer getVersion() {
                return version;
        }

        public Date getUpdatedDate() {
                return updatedDate;
        }

        public void setVersion(Integer version) {
                this.version = version;
        }

        public void setUpdatedDate(Date updatedDate) {
                this.updatedDate = updatedDate;
        }

}
