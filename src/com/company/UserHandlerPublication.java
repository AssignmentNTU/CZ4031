package com.company;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * This is just to handle the publication type
 * Created by LENOVO on 05/10/2016.
 */
public class UserHandlerPublication extends DefaultHandler {

    private boolean pages = false;
    private boolean title = false;
    private boolean year = false;

    private static String keySaved = "";

    private PostgreSQL postgreSQL;

    public UserHandlerPublication(PostgreSQL postgreSQL){
        this.postgreSQL = postgreSQL;
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {

        try {
            //all type of article is needed to get the publcication key
            if (qName.equalsIgnoreCase("title")) {
                title = true;
            }else if (qName.equalsIgnoreCase("year")) {
                year = true;
            } else if (qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("proceedings") || qName.equalsIgnoreCase("book")
                    || qName.equalsIgnoreCase("incollection")) {
                String key = attributes.getValue("key");
                keySaved = key;
                try {
                    postgreSQL.addFieldPubKeyForPublicationElement(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(qName.equalsIgnoreCase("pages")){
                pages = true;
            }
        }catch(Exception e){

        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {

        try {
            if (qName.equalsIgnoreCase("article")) {
                postgreSQL.addBatch();
            } else if (qName.equalsIgnoreCase("inproceedings")) {
                postgreSQL.addBatch();
            } else if (qName.equalsIgnoreCase("proceedings")) {
                postgreSQL.addBatch();
            } else if (qName.equalsIgnoreCase("book")) {
                postgreSQL.addBatch();
            } else if (qName.equalsIgnoreCase("incollection")) {
                postgreSQL.addBatch();
            }
        }catch(Exception e){

        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) {

        try {
            if (title) {
                postgreSQL.addPublicationTitle(new String(ch, start, length));
                title = false;
            } else if(year){
                postgreSQL.addPublicationYear(Integer.parseInt(new String(ch, start, length)));
                year = false;
            }else if(pages){
                postgreSQL.addPublicationTotalPages(new String(ch, start, length));
                pages = false;
            }

        } catch (Exception e) {

        }
    }



}
