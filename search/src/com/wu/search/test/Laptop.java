package com.wu.search.test;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "laptop")
public class Laptop extends Item {
        protected String screenSize;

        public String getScreenSize() {
                return screenSize;
        }

        public void setScreenSize(String screenSize) {
                this.screenSize = screenSize;
        }

}
