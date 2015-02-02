package com.wu.search.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class Test {

        public static void main(String[] args) {
                new Test().go();
        }

        private SessionFactory sessionFactory;
        private Session session;

        public void go() {
                go1();
        }
 
        public void initHibernate() {
                Configuration conf = new Configuration().configure();
                ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
                sessionFactory = conf.buildSessionFactory(sr);
                session = sessionFactory.openSession();
        }

        public void shutdownHibernate() {
                session.close();
                sessionFactory.close();
        }
       
        public void go1() {
                initHibernate();
                Transaction t = session.beginTransaction();
                Product p = new Product();
                p.setName("laptop");
                session.persist("Product1", p);
                Product p2 = new Product();
                p2.setName("laptop2");
                session.persist("Product2", p2);
                t.commit();
                shutdownHibernate();
        }

}
