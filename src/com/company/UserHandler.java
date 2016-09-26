package com.company;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by LENOVO on 24/09/2016.
 */
public class UserHandler extends DefaultHandler{

    /*
                <year>2017</year>
            <volume>20</volume>
            <journal>Acta -inf</journal>
            <url>db/joournam/edward</url>
            <ee>http://edward.sujono.com</ee>
    */

    //declare all the lement here
    private boolean author = false;
    private boolean title = false;
    private boolean journal = false;
    private boolean year = false;



    private PostgreSQL postgreSQL;

    public UserHandler(PostgreSQL postgreSQL){
        this.postgreSQL = postgreSQL;
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException{

        //all type of article is needed to get the publcication key
        if(qName.equalsIgnoreCase("author")){
            author = true;
        }else if(qName.equalsIgnoreCase("title")){
            title = true;
        }else if(qName.equalsIgnoreCase("journal")){
            journal = true;
        }else if(qName.equalsIgnoreCase("year")){
            year = true;
        }else if(qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("proceedings") || qName.equalsIgnoreCase("book")
                || qName.equalsIgnoreCase("incollection") || qName.equalsIgnoreCase("phdthesis") || qName.equalsIgnoreCase("mastersthesis") || qName.equalsIgnoreCase("www")){
            String key = attributes.getValue("key");
            try {
                postgreSQL.addFieldPubKeyForPublicationElement(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {

        try {
            if (qName.equalsIgnoreCase("article")) {
                postgreSQL.addFieldIsArticleForPublicationElement();
                postgreSQL.addBatch();
            } else if (qName.equalsIgnoreCase("inproceedings")) {
                postgreSQL.addFieldIsInProceedingsForPublicationElement();
                postgreSQL.addBatch();

            } else if (qName.equalsIgnoreCase("proceedings")) {
                postgreSQL.addFieldIsProceedingsForPublicationElement();
                postgreSQL.addBatch();

            } else if (qName.equalsIgnoreCase("book")) {
                postgreSQL.addFieldIsBookForPublicationElement();
                postgreSQL.addBatch();

            } else if (qName.equalsIgnoreCase("incollection")) {
                postgreSQL.addFieldIsInCollectionsForPublicationElement();
                postgreSQL.addBatch();

            } else if (qName.equalsIgnoreCase("phdthesis")) {
                postgreSQL.addFieldIsPhdThesisForPublicationElement();
                postgreSQL.addBatch();

            } else if (qName.equalsIgnoreCase("mastersthesis")) {
                postgreSQL.addFieldIsMasterThesisForPublicationElement();
                postgreSQL.addBatch();

            } else if (qName.equalsIgnoreCase("www")) {
                postgreSQL.addFieldIsWebsiteForPublicationElement();
                postgreSQL.addBatch();

            }
        }catch(Exception e){

        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) {

        try {
            if (author) {
                postgreSQL.addAuthorField(new String(ch, start, length));
                postgreSQL.addFieldAuthorNameForPublicationElement(new String(ch,start,length));
                author = false;
            } else if (title) {
                postgreSQL.addFieldTitleForPublicationElement(new String(ch, start, length));
                title = false;
            } else if(year){
                postgreSQL.addFieldYearForPublicationElement(Integer.parseInt(new String(ch, start, length)));
                year = false;
            }else if(journal){
                postgreSQL.addFieldJournalForPublicationElement(new String(ch, start, length));
                journal = false;
            }
        } catch (Exception e) {

        }
    }
}
