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

    public UserHandler(PostgreSQL postgreSQL){
        this.postgreSQL = postgreSQL;
    }

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException{

       try {
           //all type of article is needed to get the publcication key
           if (qName.equalsIgnoreCase("author")) {
               author = true;
           } else if (qName.equalsIgnoreCase("title")) {
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

               //need to check every publication type
               if (qName.equalsIgnoreCase("article")) {
                   postgreSQL.addArticlePubKey(key);
                   article  = true;
               } else if (qName.equalsIgnoreCase("inproceedings")) {
                    postgreSQL.addInProceedingsPubKey(key);
                    inProceedings = true;
               } else if (qName.equalsIgnoreCase("proceedings")) {
                    postgreSQL.addProceedingsPubKey(key);
                    proocedings = true;
               } else if (qName.equalsIgnoreCase("book")) {
                    postgreSQL.addBookPubKey(key);
                   book = true;
               } else if (qName.equalsIgnoreCase("incollection")) {
                    postgreSQL.addInCollectionPubKey(key);
                   inCollection = true;
               }
           }
           //for article type
           else if (qName.equalsIgnoreCase("journal")) {
                journal = true;
           }else if(qName.equalsIgnoreCase("month")){
               month = true;
           }else if(qName.equalsIgnoreCase("number")){
                number = true;
           }else if(qName.equalsIgnoreCase("volume")){
                volume = true;
           }
           //for InCollection Type and InProceedings
           else if(qName.equalsIgnoreCase("booktitle")){
               bookTitle = true;
           }
           //for Book Type
           else if(qName.equalsIgnoreCase("isbn")){
                isbn = true;
           }else if(qName.equalsIgnoreCase("series")){
                series = true;
           }
           //for Proceeding Type
           else if(qName.equalsIgnoreCase("publisher")){
                publisher = true;
           }else if(qName.equalsIgnoreCase("pages")){
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
                postgreSQL.addBatchForArticle();
                postgreSQL.addBatch();
                article = false;
            } else if (qName.equalsIgnoreCase("inproceedings")) {
                postgreSQL.addBatchForInProceedings();
                postgreSQL.addBatch();
                inProceedings = false;
            } else if (qName.equalsIgnoreCase("proceedings")) {
                postgreSQL.addBatchProceedings();
                postgreSQL.addBatch();
                proocedings = false;
            } else if (qName.equalsIgnoreCase("book")) {
                postgreSQL.addBatchBook();
                postgreSQL.addBatch();
                book = false;
            } else if (qName.equalsIgnoreCase("incollection")) {
                postgreSQL.addBatchForInCollection();
                postgreSQL.addBatch();
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
                postgreSQL.addAuthorField(new String(ch, start, length));
                //when there still inside article we need to put into pubauthor
                if(article || inProceedings || inCollection || proocedings || book) {
                    postgreSQL.addPubAuthorForPubKey(keySaved);
                    postgreSQL.addPubAuthorForName(new String(ch, start, length));
                    postgreSQL.addBatchForPubAuthor();
                }
                author = false;
            } else if (title) {
                postgreSQL.addPublicationTitle(new String(ch, start, length));
                title = false;
            } else if(year){
                postgreSQL.addPublicationYear(Integer.parseInt(new String(ch, start, length)));
                year = false;
            }else if(pages){
                postgreSQL.addPublicationTotalPages(new String(ch, start, length));
                pages = false;
            }
            else if(journal){
                if(article){
                    postgreSQL.addArticleJournal(new String(ch, start, length));
                }
                journal = false;
            }else if(month){
                if(article){
                    postgreSQL.addArticleMonth(Integer.parseInt(new String(ch, start, length)));
                }
                month = false;
            }else if(volume){
                //volume just can be contained in proceedings and article
                if(article){
                    postgreSQL.addArticleVolume(Integer.parseInt(new String(ch, start, length)));
                }else if(proocedings){
                    postgreSQL.addProceedingsVolume(Integer.parseInt(new String(ch, start, length)));
                }
                volume = false;
            }else if(number){
                if(article){
                    postgreSQL.addArticleNumber(Integer.parseInt(new String(ch, start, length)));
                }
                number = false;
            }else if(bookTitle){
                if(inCollection){
                    postgreSQL.addInCollectionBookTitle(new String(ch, start, length));
                }else if(inProceedings){
                    postgreSQL.addInProceedingsBookTitle(new String(ch, start, length));
                }else if(proocedings){
                    postgreSQL.addProceedingsBookTitle(new String(ch, start, length));
                }
                bookTitle = false;
            }else if(isbn){
                if(book){
                    postgreSQL.addBookISBN(new String(ch, start, length));
                }else if(proocedings){
                    postgreSQL.addProceedingsISBN(new String(ch, start, length));
                }
                isbn = false;
            }else if(series){
                if(book){
                    postgreSQL.addBookSeries(new String(ch, start, length));
                }else if(proocedings){
                    postgreSQL.addProceedingSeries(new String(ch, start, length));
                }
                series = false;
            }else if(publisher){
                if(proocedings){
                    postgreSQL.addProceedingsPublisher(new String(ch, start, length));
                }
                publisher = false;
            }
        } catch (Exception e) {

        }
    }



}
