package com.wu.search.one;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Search {

        public static void main(String[] args) throws IOException {
                new Search().go();
        }

        private static final String[] urls = { "http://www.staples.com/Laptops/cat_CL167289", "" };

        private void go() throws IOException {
                getAll();
                //parseAll();
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
                                String outfile = String.format("html%03d.html", page);
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
                                System.out.println(outfile);
                        }
                        remainingSet.removeAll(obtainedSet);
                        if (remainingSet.isEmpty()) {
                                break;
                        }
                }
        
                PrintWriter pw = new PrintWriter(new FileWriter("passing.cfg"));
                pw.println(String.format("maxPage=%d", maxPage));
                pw.println(String.format("obtainedSet=%s", StringUtils.join(obtainedSet.toArray(), ",")));
                pw.close();
        }

        private void getFirstPage() throws IOException {
                Document doc = Jsoup.connect(urls[0]).get();
                String html = doc.html();
                PrintWriter pw = new PrintWriter(new FileWriter("html001.html"));
                pw.println(html);
                pw.close();
                System.out.println("html001.html");
        }

        private Integer getMaxPage() throws IOException {
                Document doc = Jsoup.parse(new File("html001.html"), null);
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

        public void parseAll() throws IOException {
                Properties passingCfg = new Properties();
                passingCfg.load(this.getClass().getResourceAsStream("passing.cfg"));
                List<Integer> obtainedPages = new ArrayList<Integer>();
                for (String s : passingCfg.getProperty("obtainedSet").split(",")) {
                        obtainedPages.add(Integer.parseInt(s));
                }

                PrintWriter pwriter = new PrintWriter(new FileWriter("result.csv"));
                pwriter.println(StaplesLaptop.fieldNames());

                for (Integer page : obtainedPages) {
                        String file = String.format("html%03d.html", page);
                        Document doc = Jsoup.parse(new File(file), null);
                        int count = 1;
                        for (Element element : doc.select("#productDetail > li")) {
                                System.out.println(String.format("processing page %d item %d", page, count));
                                try {
                                        oneItem(element);
                                } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        System.out.println("------------------------");
                                        System.out.println(element.toString());
                                        System.out.println("------------------------");
                                }
                                count++;
                        }
                }
                pwriter.close();
        }

        private StaplesLaptop oneItem(Element container) {
                StaplesLaptop item = new StaplesLaptop();

                Elements es;

                es = container.select("div.item");
                item.setItemId(es.isEmpty() ? "" : es.first().text().split(" ")[1]);

                es = container.select("div.model");
                item.setModel(es.isEmpty() ? "" : es.first().text());

                es = container.select("div.name a");
                item.setHref(es.isEmpty() ? "" : es.first().attr("href"));

                es = container.select("div.reviewssnippet dd.stStars span");
                item.setRating(es.isEmpty() ? "" : es.first().text());

                es = container.select("div.reviewssnippet dd.stNum span");
                item.setReviewCount(es.isEmpty() ? "" : es.first().text());

                List<String> specs = new ArrayList<String>();
                for (Element e2 : container.select("div.name ul.bullets").first().select("li")) {
                        specs.add(e2.text());
                }
                item.setSpec(StringUtils.join(specs, "#").replaceAll(",", "~"));

                for (Element tr : container.select("div.price-container table.pricenew tr")) {
                        if (tr.hasClass("pwas")) {
                                item.setPriceOrig(tr.select("td").first().text());
                                continue;
                        }
                        if (tr.hasClass("psave")) {
                                item.setPriceSave(tr.select("td").first().text());
                                continue;
                        }
                        if (tr.hasClass("pr0m0")) {
                                item.setInstantSave(tr.select("td").first().text());
                                continue;
                        }
                        if (tr.html().contains("Rebate")) {
                                item.setRebate(tr.select("td").first().text());
                                continue;
                        }
                        if (tr.hasClass("total")) {
                                item.setPriceFinal(tr.select("td b").first().text());
                                continue;
                        }
                }

                es = container.select("div.price-container dd.pwas");
                item.setPriceOrig2(es.isEmpty() ? "" : es.first().select("del").first().text());

                es = container.select("div.price-container dd.pis i.price");
                item.setPriceFinal2(es.isEmpty() ? "" : es.first().text());

                es = container.select("div.price-container dd.psave i.price");
                item.setPriceSave2(es.isEmpty() ? "" : es.first().text());

                // pwriter.println(item.toString());
                return item;
        }

}
