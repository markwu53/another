package com.wu.search.test;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "item")
public class Item implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        protected Integer id;
        protected Double price;
        protected Double save;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Double getPrice() {
                return price;
        }

        public void setPrice(Double price) {
                this.price = price;
        }

        public Double getSave() {
                return save;
        }

        public void setSave(Double save) {
                this.save = save;
        }

}
