package com.wu.search.test;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Id;

@SuppressWarnings("serial")
@Entity
@Table(name = "Product1")
@SecondaryTable(name = "Product2")
public class Product implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String name;

        public Product() {
        }

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }
        
        public String getName() {
                return name;
        }
        
        public void setName(String name) {
                this.name = name;
        }

}
