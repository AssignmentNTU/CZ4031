package com.company;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

/**
 * Created by LENOVO on 10/10/2016.
 */
public class MainPubAuthor {

    public static void main(String args[]){
        File file = new File("dblp.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        PostgreSQL postgreSQL = new PostgreSQL();

        try {
            //parse publication first
            //just need to parse all the subtype of publication and author
            postgreSQL = new PostgreSQL();
            postgreSQL.createGeneralPreparedStatementAuthor();
            postgreSQL.setAllPreparedStatement();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new UserHandler(postgreSQL);
            saxParser.parse(file,handler);
            postgreSQL.executeBatch();
            postgreSQL.createDistinctAuthorStatement();
            postgreSQL.closeStatement();
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
