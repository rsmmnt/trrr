
package com.company;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;



public class Main {

    public static void main(String[] args) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("prs.csv"));
            Map<String, Integer> attributes = new HashMap<>();
            attributes.put("Brand", 0);
            attributes.put("Model Year", 1);
            attributes.put("Body Material", 2);
            attributes.put("Product Line",3);
            attributes.put("Model",4);
            attributes.put("String Configuration", 5);
            attributes.put("Body Color", 6);
            attributes.put("Body Type", 7);
            attributes.put("Right-/ Left-Handed", 8);
            attributes.put("Price", 9);

            //String addr = sc.nextLine();
            String addrbase = new String("https://www.ebay.com/sch/i.html?_from=R40&_nkw=prs&_sacat=33034&LH_TitleDesc=0&_udlo=280&LH_BIN=1&_pgn=");

            for(int page = 2; page < 200; page++) {

                URL url = new URL(addrbase+page);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                    // System.out.println(inputLine);
                }
                in.close();
                Matcher m = Pattern.compile("(<a class=\"s-item__link\"[^<>]+href=\")(\\S+)(\")").matcher(content);
                Queue<String> links = new LinkedList<>();
                //HashSet<String> bigset = new HashSet();
                while (m.find()) {
                    //System.out.println(m.group(2));
                    links.add(m.group(2));
                    //bigset.add(m.group(2));
                }


                while (links.size() > 0) {
                    String lnk = links.poll();
                    //System.out.println(lnk);
                    Document doc;
                    try {
                        doc = Jsoup.connect(lnk).get();
                    }
                    catch(Exception e){
                        continue;
                    }
                    String result = new String();
                    String[] arr = new String[attributes.size()];
                    for (Element el : doc.select("#viTabs_0_is")) {

                        for (Element x : el.select("td.attrLabels")) {

                            String t = x.text().substring(0, x.text().length() - 1);
                            if (attributes.containsKey(t)) {
                                String tmp = x.nextElementSibling().text().replaceAll("'","");
                                if(!tmp.contains(",")) {
                                    arr[attributes.get(t)] = tmp;
                                }
                                else
                                {
                                    tmp = "'" + tmp + "'";
                                    arr[attributes.get(t)] = tmp;
                                }
                                }

                        }
                    }

                    Element price = doc.selectFirst("#prcIsum");
                    //System.out.println("Price " + price.text());
                    //
                    if(price == null)
                    {
                        continue;
                    }
                    String pr = price.text();
                    if (!pr.substring(0, 2).equals("US")) {
                        continue;
                    }

                    pr = pr.substring(4, pr.length());
                    pr = pr.replace(",", "");
                    arr[attributes.get("Price")] = pr;

                    for (int i = 0; i < arr.length; i++) {
                        if (i != 0) {
                            result += ",";
                        }
                        if (arr[i] != null) {
                            result += arr[i];
                        }
                    }
                    result+="\n";
                    writer.write(result);

                }
                System.out.println("Going through page " + page);
                writer.flush();
            }

        }
        catch( Exception e)
        {
            System.out.println("bad stuff happened");
            System.out.println(e.toString());
            System.out.println(e.getStackTrace()[0].getLineNumber());
        }
        // write your code here
    }
}
