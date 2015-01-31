package com.wu.search.one;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
        private PrintWriter pwriter;

        private void go() throws IOException {
                go3();
        }

        public void go3() throws IOException {
                pwriter = new PrintWriter(new FileWriter("result.csv"));
                for (int i = 1; i <= 36; i ++) {
                        String file = String.format("html%03d.html", i);
                        System.out.println(file);
                        Document doc = Jsoup.parse(new File(file), null);
                        //System.out.println(elements.size());
                        int count = 1;
                        for (Element element : doc.select("#productDetail > li")) {
                                // System.out.println(element.html());
                                // System.out.println(element.toString());
                                System.out.println(count);
                                oneItem(element);
                                count ++;
                        }
                }
                pwriter.close();
        }

        private void oneItem(Element container) {
                StaplesLaptop item = new StaplesLaptop();

                Element e;
                Elements es;
 
                es = container.select("div.item");
                item.setItemId(es.isEmpty()? "" : es.first().text().split(" ")[1]);

                es = container.select("div.model");
                item.setModel(es.isEmpty()? "null" : es.first().text());

                es = container.select("div.name a");
                item.setHref(es.isEmpty()? "null" : es.first().attr("href"));

                es = container.select("div.reviewssnippet dd.stStars span");
                item.setRating(es.isEmpty()? "null" : es.first().text());

                es = container.select("div.reviewssnippet dd.stNum span");
                item.setReviewCount(es.isEmpty()? "null" : es.first().text());

                List<String> specs = new ArrayList<String>();
                for (Element e2 : container.select("div.name ul.bullets").first().select("li")) {
                        specs.add(e2.text());
                }
                item.setSpec(StringUtils.join(specs, ","));

                es = container.select("div.price-container tr.price td");
                String priceOrig = es.isEmpty()? "null" : es.first().text();

                es = container.select("div.price-container tr.pr0m0 td");
                String priceSaving = es.isEmpty()? "null" : es.first().text();

                String priceFinal = "null";
                e = container.select("div.price-container tr.total td b").first();
                if (e == null) {
                        e = container.select("div.price-container dl.theprice dd.pis b i").first();
                        if (e != null) {
                                priceFinal = e.text();
                        }
                } else {
                        priceFinal = e.text();
                }
                //System.out.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s", itemId, model, href, rating, reviewCount, priceOrig, priceSaving, priceFinal));
                //pwriter.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s", itemId, model, href, rating, reviewCount, priceOrig, priceSaving, priceFinal));
        }

        public void go2() throws IOException {
                // http://www.staples.com/Laptops/cat_CL167289?pagenum=2
                String url = "http://www.staples.com/Laptops/cat_CL167289?pagenum=";
                // 1-36
                for (int i = 12; i <= 12; i++) {
                        String pageUrl = url + i;
                        String outfile = String.format("html%03d.html", i);
                        Document doc;
                        try {
                                doc = Jsoup.connect(pageUrl).get();
                        } catch (IOException e) {
                                e.printStackTrace();
                                continue;
                        }
                        PrintWriter pw = new PrintWriter(new FileWriter(outfile));
                        pw.println(doc.html());
                        pw.close();
                        System.out.println(outfile);
                }
                System.out.println("done");
        }

        public void go1() throws IOException {
                Document doc = Jsoup.connect(urls[0]).get();
                String html = doc.html();
                PrintWriter pw = new PrintWriter(new FileWriter("html001.html"));
                pw.println(html);
                pw.close();
                System.out.println("done");
        }

}
