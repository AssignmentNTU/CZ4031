package com.company;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.PreparedStatement;

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

    //for the publication type
    private boolean article = false;
    private boolean inProceedings = false;
    private boolean proceedings = false;
    private boolean book = false;
    private boolean inCollection = false;
    private boolean phdThesis = false;
    private boolean masterThesis = false;
    private boolean www = false;


    private PostgreSQL postgreSQL;

    public UserHandler(PostgreSQL postgreSQL){
        this.postgreSQL = postgreSQL;
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException{

        //all type of article is needed to get the publcication key
        try {
            if (qName.equalsIgnoreCase("author")) {
                author = true;
            } else if (qName.equalsIgnoreCase("title")) {
                title = true;
            } else if (qName.equalsIgnoreCase("journal")) {
                journal = true;
            } else if (qName.equalsIgnoreCase("year")) {
                year = true;
            } else if (qName.equalsIgnoreCase("article")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementArticle(),key);
                article = true;
            } else if (qName.equalsIgnoreCase("inproceedings")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementInProceedings(),key);
                inProceedings = true;
            } else if (qName.equalsIgnoreCase("proceedings")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementProceedings(),key);
                proceedings = true;
            } else if (qName.equalsIgnoreCase("book")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementBook(),key);
                book = true;
            } else if (qName.equalsIgnoreCase("incollection")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementInCollection(),key);
                inCollection = true;
            } else if (qName.equalsIgnoreCase("phdthesis")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementPhdThesis(),key);
                phdThesis = true;
            } else if (qName.equalsIgnoreCase("mastersthesis")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementMasterThesis(),key);
                masterThesis = true;
            } else if (qName.equalsIgnoreCase("www")) {
                String key = attributes.getValue("key");
                postgreSQL.addFieldPubKeyForPublicationElement(postgreSQL.getPreparedStatementWebsite(),key);
                www = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {

        try {
            if (qName.equalsIgnoreCase("article")) {
                postgreSQL.addBatch(ArticleType.TYPE.ARTICLE);
                article = false;
            } else if (qName.equalsIgnoreCase("inproceedings")) {
                postgreSQL.addBatch(ArticleType.TYPE.IN_PROCEEDINGS);
                inProceedings = false;
            } else if (qName.equalsIgnoreCase("proceedings")) {
                postgreSQL.addBatch(ArticleType.TYPE.PROCEEDINGS);
                proceedings = false;
            } else if (qName.equalsIgnoreCase("book")) {
                postgreSQL.addBatch(ArticleType.TYPE.BOOK);
                book = false;
            } else if (qName.equalsIgnoreCase("incollection")) {
                postgreSQL.addBatch(ArticleType.TYPE.IN_COLLECTION);
                inCollection = false;
            } else if (qName.equalsIgnoreCase("phdthesis")) {
                postgreSQL.addBatch(ArticleType.TYPE.PHD_THESIS);
                phdThesis = false;
            } else if (qName.equalsIgnoreCase("mastersthesis")) {
                postgreSQL.addBatch(ArticleType.TYPE.MASTER_THESIS);
                masterThesis = false;
            } else if (qName.equalsIgnoreCase("www")) {
                postgreSQL.addBatch(ArticleType.TYPE.WEBSITE);
                www = false;
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
                if(getPreparedStatement()!= null)postgreSQL.addFieldAuthorNameForPublicationElement(getPreparedStatement(),new String(ch, start, length));
                author = false;
            } else if (title) {
                postgreSQL.addFieldTitleForPublicationElement(getPreparedStatement(),new String(ch, start, length));
                title = false;
            } else if(year){
                postgreSQL.addFieldYearForPublicationElement(getPreparedStatement(),Integer.parseInt(new String(ch, start, length)));
                year = false;
            }else if(journal){
                postgreSQL.addFieldJournalForPublicationElement(getPreparedStatement(),new String(ch, start, length));
                journal = false;
            }
        } catch (Exception e) {

        }
    }

    public PreparedStatement getPreparedStatement(){
        PreparedStatement preparedStatement = null;
        if(article){
            preparedStatement = postgreSQL.getPreparedStatementArticle();
        }else if(inProceedings){
            preparedStatement = postgreSQL.getPreparedStatementInProceedings();
        }else if(proceedings){
            preparedStatement = postgreSQL.getPreparedStatementProceedings();
        }else if(book){
            preparedStatement = postgreSQL.getPreparedStatementBook();
        }else if(inCollection){
            preparedStatement = postgreSQL.getPreparedStatementInCollection();
        }else if(phdThesis){
            preparedStatement = postgreSQL.getPreparedStatementPhdThesis();
        }else if(masterThesis){
            preparedStatement = postgreSQL.getPreparedStatementMasterThesis();
        }else if(www){
            preparedStatement = postgreSQL.getPreparedStatementWebsite();
        }
        return preparedStatement;
    }

}
