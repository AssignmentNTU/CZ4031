package com.company;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

/**
 * Created by LENOVO on 24/09/2016.
 */
public class MainSAX {

    public static void main(String args[]){
        File file = new File("dblp.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        PostgreSQL postgreSQL = new PostgreSQL();

        try {
            postgreSQL.createGeneralPreparedStatementAuthor();
            postgreSQL.createGeneralPreparedStatementPublication();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new UserHandler(postgreSQL);
            saxParser.parse(file,handler);
            postgreSQL.executeBatch();
            postgreSQL.commitChanges();
            postgreSQL.closeCOnnection();
        }catch(Exception e){
            try {
                postgreSQL.closeCOnnection();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


}
