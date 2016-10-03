package com.company;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by LENOVO on 04/10/2016.
 */
public class UserHandlerPubAuthor extends DefaultHandler {

    /*
                <year>2017</year>
            <volume>20</volume>
            <journal>Acta -inf</journal>
            <url>db/joournam/edward</url>
            <ee>http://edward.sujono.com</ee>
    */

    //declare all the lement here
    private boolean author = false;
    private boolean pages = false;
    private boolean title = false;
    private boolean journal = false;
    private boolean year = false;

    //for article type
    private boolean month = false,volume = false,number = false;
    //for inCollection
    private boolean bookTitle = false;
    //for book type
    private boolean isbn = false,series = false;
    //for procceding type
    private boolean publisher = false;
    //for the type of the article
    private boolean article=false,inCollection = false,inProceedings = false,book = false,proocedings = false;


    private static String keySaved = "";

    private PostgreSQL postgreSQL;

    public UserHandlerPubAuthor(PostgreSQL postgreSQL){
        this.postgreSQL = postgreSQL;
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {

        try {
            //all type of article is needed to get the publcication key
            if (qName.equalsIgnoreCase("author")) {
                author = true;
            }  else if (qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("proceedings") || qName.equalsIgnoreCase("book")
                    || qName.equalsIgnoreCase("incollection")) {
                String key = attributes.getValue("key");
                keySaved = key;

                //need to check every publication type
                if (qName.equalsIgnoreCase("article")) {
                    article  = true;
                } else if (qName.equalsIgnoreCase("inproceedings")) {
                    inProceedings = true;
                } else if (qName.equalsIgnoreCase("proceedings")) {
                    proocedings = true;
                } else if (qName.equalsIgnoreCase("book")) {
                    book = true;
                } else if (qName.equalsIgnoreCase("incollection")) {
                    inCollection = true;
                }
            }
        }catch(Exception e){

        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {

        try{
            if (qName.equalsIgnoreCase("article")) {
                article = false;
            } else if (qName.equalsIgnoreCase("inproceedings")) {
                inProceedings = false;
            } else if (qName.equalsIgnoreCase("proceedings")) {
                proocedings = false;
            } else if (qName.equalsIgnoreCase("book")) {
                book = false;
            } else if (qName.equalsIgnoreCase("incollection")) {
                inCollection = false;
            }
        }catch(Exception e){

        }


    }

    @Override
    public void characters(char ch[],
                           int start, int length) {

        try {
            if (author) {
                //when there still inside article we need to put into pubauthor
                if(article || inProceedings || inCollection || proocedings || book) {
                    postgreSQL.addPubAuthorForPubKey(keySaved);
                    postgreSQL.addPubAuthorForName(new String(ch, start, length));
                    postgreSQL.addBatchForPubAuthor();
                }
                author = false;
            }
        } catch (Exception e) {

        }
    }



}
