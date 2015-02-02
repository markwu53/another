package com.wu.search.one;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Search {

        public static void main(String[] args) throws IOException {
                new Search().go();
        }

        private static final Logger logger = Logger.getLogger(Search.class);
        private static final String[] urls = { "http://www.staples.com/Laptops/cat_CL167289", "" };
        private SessionFactory sessionFactory;
        private Session session;
        private List<StaplesLaptop> newListing;
        private Map<String, StaplesLaptop> currentListingMap;

        public void go() throws IOException {
                PropertyConfigurator.configure("conf/log4j.properties");
                //getAll();
                initHibernate();
                //deleteAll();
                //parseAll();
                //saveAll();
                processListing();
                //query();
                shutdownHibernate();
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

        public void deleteAll() {
                Transaction t = session.beginTransaction();
                Query query = session.createQuery("delete from StaplesLaptop");
                query.executeUpdate();
                logger.info("deleted all");
                t.commit();
        }

        public void saveAll() {
                Transaction t = session.beginTransaction();
                for (StaplesLaptop a: newListing) {
                        session.persist(a);
                }
                t.commit();
                logger.info("all saved");
        }

        @SuppressWarnings("unchecked")
        public void query() {
                //List<StaplesLaptop> list = session.createQuery("from StaplesLaptop where updatedDate=(select max(updatedDate) from StaplesLaptop)").list();
                //List<StaplesLaptop> list = session.createQuery("from StaplesLaptop").list();
                Query query = session.createQuery("from StaplesLaptop where itemId=:id order by updatedDate desc");
                query.setString("id", "1373644");
                query.setMaxResults(1);
                List<StaplesLaptop> list = query.list();
                if (list.isEmpty()) {
                        logger.info("empty");
                        return;
                }
                StaplesLaptop a = list.get(0);
                ArchivedLaptop b = new ArchivedLaptop(a);
                Transaction t = session.beginTransaction();
                //session.evict(a);
                session.persist(b);
                t.commit();
                logger.info("saved");
        }

        public void getAll() throws IOException {
                getFirstPage();
                Integer maxPage = getMaxPage();

                Set<Integer> remainingSet = new TreeSet<Integer>();
                Set<Integer> obtainedSet = new TreeSet<Integer>();

                obtainedSet.add(1);

                for (int i = 2; i <= maxPage; i++) {
                        remainingSet.add(i);
                }

                String url = "http://www.staples.com/Laptops/cat_CL167289?pagenum=";
                for (int i = 0; i < 3; i++) {
                        // try 3 times
                        for (Integer page : remainingSet) {
                                String pageUrl = url + page;
                                String outfile = String.format("working/html%03d.html", page);
                                Document doc;
                                try {
                                        doc = Jsoup.connect(pageUrl).get();
                                        PrintWriter pw = new PrintWriter(new FileWriter(outfile));
                                        pw.println(doc.html());
                                        pw.close();
                                        obtainedSet.add(page);
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        continue;
                                }
                                logger.info(outfile);
                        }
                        remainingSet.removeAll(obtainedSet);
                        if (remainingSet.isEmpty()) {
                                break;
                        }
                }

                PrintWriter pw = new PrintWriter(new FileWriter("working/passing.cfg"));
                pw.println(String.format("maxPage=%d", maxPage));
                pw.println(String.format("obtainedSet=%s", StringUtils.join(obtainedSet.toArray(), ",")));
                pw.close();
        }

        private void getFirstPage() throws IOException {
                Document doc = Jsoup.connect(urls[0]).get();
                String html = doc.html();
                File first = new File("working/html001.html");
                first.getParentFile().mkdirs();
                PrintWriter pw = new PrintWriter(first);
                pw.println(html);
                pw.close();
                logger.info("working/html001.html");
        }

        private Integer getMaxPage() throws IOException {
                Document doc = Jsoup.parse(new File("working/html001.html"), null);
                Elements es = doc.select("div.tabContainer div.perpage").first().select("a");
                Integer max = 1;
                for (Element e : es) {
                        try {
                                Integer page = Integer.parseInt(e.text());
                                max = Math.max(max, page);
                        } catch (NumberFormatException ex) {
                                continue;
                        }
                }
                return max;
        }

        @SuppressWarnings("unchecked")
        public void processListing() {
                currentListingMap = new HashMap<String, StaplesLaptop>();
                List<StaplesLaptop> currentListing = session.createQuery("from StaplesLaptop").list();
                for (StaplesLaptop a: currentListing) {
                        currentListingMap.put(a.getItemId(), a);
                }
                for (StaplesLaptop a: newListing) {
                        String itemId = a.getItemId();
                        if (itemId == null) {
                                continue;
                        }
                        logger.debug(String.format("processing item %s", itemId));
                        if (!currentListingMap.containsKey(itemId)) {
                                logger.info(String.format("found new item: %s", itemId));
                                continue;
                        }
                        StaplesLaptop b = currentListingMap.get(itemId);
                        if (a.getPriceFinal2().compareTo(b.getPriceFinal2()) != 0) {
                                logger.info(String.format("price change for item %s: from %f to %f", itemId, b.getPriceFinal2(), a.getPriceFinal2()));
                        }
                }

                logger.info("archive current listing");
                Transaction t = session.beginTransaction();
                for (StaplesLaptop a: currentListing) {
                        Query query = session.createQuery("from ArchivedLaptop where itemId=:itemId order by updatedDate desc")
                                        .setString("itemId", a.getItemId()).setMaxResults(1);
                        List<ArchivedLaptop> before = query.list();
                        if (before.isEmpty()) {
                                //new item
                                ArchivedLaptop arch = new ArchivedLaptop(a);
                                arch.setCreatedDate(a.getUpdatedDate());
                                arch.setUpdatedDate(a.getUpdatedDate());
                                session.persist(arch);
                                continue;
                        }
                        ArchivedLaptop b = before.get(0);
                        if (a.getPriceFinal2().compareTo(b.getPriceFinal2()) != 0) {
                                // new change
                                ArchivedLaptop arch = new ArchivedLaptop(a);
                                arch.setCreatedDate(a.getUpdatedDate());
                                arch.setUpdatedDate(a.getUpdatedDate());
                                session.persist(arch);
                        } else {
                                // no change, update
                                b.setUpdatedDate(a.getUpdatedDate());
                                session.persist(b);
                        }
                }
                t.commit();
                logger.info("archiving done");

                logger.info("delete current listing");
                t = session.beginTransaction();
                Query query = session.createQuery("delete from StaplesLaptop");
                query.executeUpdate();
                t.commit();

                logger.info("insert new listing");
                t = session.beginTransaction();
                for (StaplesLaptop a: newListing) {
                        session.persist(a);
                }
                t.commit();

                logger.info("process listing done");
        }

        public void parseAll() throws IOException {
                Properties passingCfg = new Properties();
                FileInputStream fis = new FileInputStream("working/passing.cfg");
                passingCfg.load(fis);
                fis.close();
                List<Integer> obtainedPages = new ArrayList<Integer>();
                for (String s : passingCfg.getProperty("obtainedSet").split(",")) {
                        obtainedPages.add(Integer.parseInt(s));
                }

                newListing = new ArrayList<StaplesLaptop>();
                Date updatedDate = new Date();

                for (Integer page : obtainedPages) {
                        String file = String.format("working/html%03d.html", page);
                        Document doc = Jsoup.parse(new File(file), null);
                        int count = 1;
                        for (Element element : doc.select("#productDetail > li")) {
                                logger.info(String.format("processing page %d item %d", page, count));
                                StaplesLaptop laptop = oneItem(element);
                                laptop.setUpdatedDate(updatedDate);
                                newListing.add(laptop);
                                //session.persist(laptop);
                                //session.save(laptop);
                                count++;
                        }
                }
                logger.info("parsing done.");
        }

        private StaplesLaptop oneItem(Element container) {
                StaplesLaptop item = new StaplesLaptop();

                Elements es;

                /*
                es = container.select("div.item");
                if (!es.isEmpty()) {
                        String[] splits = es.first().text().split("\\s+");
                        if (splits.length == 2) {
                                item.setItemId(splits[1]);
                        } else {
                                item.setItemId(splits[splits.length-1]);
                        }
                }
                */

                es = container.select("div.model");
                if (!es.isEmpty()) {
                        String model = es.first().text();
                        //"Model T100TAM-C12-GR"
                        //String[] splits = model.split("\\s+");
                        //String[] splits = model.split("\\&nbsp\\;");
                        String[] splits = model.split("Model.");
                        if (splits.length == 2) {
                                item.setModel(splits[1]);
                        } else {
                                item.setModel(model);
                        }
                }

                es = container.select("div.name a");
                if (!es.isEmpty()) {
                        String href = es.first().attr("href");
                        item.setHref(href);
                        String[] splits = href.split("product_");
                        if (splits.length == 2) {
                                item.setItemId(splits[1]);
                        }
                }

                es = container.select("div.reviewssnippet dd.stStars span");
                if (!es.isEmpty()) {
                        try {
                                item.setRating(Double.parseDouble(es.first().text()));
                        } catch (NumberFormatException ex) {
                                logger.warn("exception parsing [rating]");
                        }
                }

                es = container.select("div.reviewssnippet dd.stNum span");
                if (!es.isEmpty()) {
                        try {
                                item.setReviewCount(Integer.parseInt(es.first().text()));
                        } catch (NumberFormatException ex) {
                                logger.warn("exception parsing [review count]");
                        }
                }

                es = container.select("div.name ul.bullets");
                if (!es.isEmpty()) {
                        es = es.first().select("li");
                        if (!es.isEmpty()) {
                                List<String> specs = new ArrayList<String>();
                                for (Element e: es) {
                                        specs.add(e.text());
                                }
                                item.setSpec(StringUtils.join(specs, "\n"));
                        }
                }

                for (Element tr : container.select("div.price-container table.pricenew tr")) {
                        if (tr.hasClass("pwas")) {
                                es = tr.select("td");
                                if (!es.isEmpty()) {
                                        String priceString = es.first().text();
                                        if (priceString.contains("$")) {
                                                try {
                                                        item.setPriceOrig(Double.parseDouble(priceString.split("\\$")[1]));
                                                } catch (NumberFormatException ex) {
                                                        logger.warn("exception parsing [original price]");
                                                }
                                        }
                                }
                                continue;
                        }
                        if (tr.hasClass("psave")) {
                                es = tr.select("td");
                                if (!es.isEmpty()) {
                                        String priceString = es.first().text();
                                        if (priceString.contains("$")) {
                                                try {
                                                        item.setPriceSave(Double.parseDouble(priceString.split("\\$")[1]));
                                                } catch (NumberFormatException ex) {
                                                        logger.warn("exception parsing [save]");
                                                }
                                        }
                                }
                                continue;
                        }
                        if (tr.hasClass("pr0m0")) {
                                es = tr.select("td");
                                if (!es.isEmpty()) {
                                        String priceString = es.first().text();
                                        if (priceString.contains("$")) {
                                                try {
                                                        item.setInstantSave(Double.parseDouble(priceString.split("\\$")[1]));
                                                } catch (NumberFormatException ex) {
                                                        logger.warn("exception parsing [instant saving]");
                                                }
                                        }
                                }
                                continue;
                        }
                        if (tr.html().contains("Rebate")) {
                                es = tr.select("td");
                                if (!es.isEmpty()) {
                                        String priceString = es.first().text();
                                        if (priceString.contains("$")) {
                                                try {
                                                        item.setRebate(Double.parseDouble(priceString.split("\\$")[1]));
                                                } catch (NumberFormatException ex) {
                                                        logger.warn("exception parsing [rebate]");
                                                }
                                        }
                                }
                                continue;
                        }
                        if (tr.hasClass("total")) {
                                es = tr.select("td b");
                                if (!es.isEmpty()) {
                                        String priceString = es.first().text();
                                        if (priceString.contains("$")) {
                                                try {
                                                        item.setPriceFinal(Double.parseDouble(priceString.split("\\$")[1]));
                                                } catch (NumberFormatException ex) {
                                                        logger.warn("exception parsing [price final]");
                                                }
                                        }
                                }
                                continue;
                        }
                }

                es = container.select("div.price-container dd.pwas");
                if (!es.isEmpty()) {
                        String priceString = es.first().text();
                        if (priceString.contains("$")) {
                                try {
                                        item.setPriceOrig2(Double.parseDouble(priceString.split("\\$")[1]));
                                } catch (NumberFormatException ex) {
                                        logger.warn("exception parsing [price orig 2]");
                                }
                        }
                }

                es = container.select("div.price-container dd.pis i.price");
                if (!es.isEmpty()) {
                        String priceString = es.first().text();
                        if (priceString.contains("$")) {
                                try {
                                        item.setPriceFinal2(Double.parseDouble(priceString.split("\\$")[1]));
                                } catch (NumberFormatException ex) {
                                        logger.warn("exception parsing [price final 2]");
                                }
                        }
                }

                es = container.select("div.price-container dd.psave i.price");
                if (!es.isEmpty()) {
                        String priceString = es.first().text();
                        if (priceString.contains("$")) {
                                try {
                                        item.setPriceSave2(Double.parseDouble(priceString.split("\\$")[1]));
                                } catch (NumberFormatException ex) {
                                        logger.warn("exception parsing [price save 2]");
                                }
                        }
                }
                
                es = container.select("div.mathstory-container > script");
                if (!es.isEmpty()) {
                        String script = es.toString();
                        if (script.contains("stockMessage")) {
                                item.setInStock(false);
                        }
                }

                return item;
        }

}
