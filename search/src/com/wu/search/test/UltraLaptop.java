package com.wu.search.test;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "ultra_laptop")
public class UltraLaptop extends Laptop {
        protected Double weight;

        public Double getWeight() {
                return weight;
        }

        public void setWeight(Double weight) {
                this.weight = weight;
        }

}
