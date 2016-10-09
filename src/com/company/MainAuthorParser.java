package com.company;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

/**
 * Created by LENOVO on 10/10/2016.
 */
public class MainAuthorParser {
    public static void main(String args[]){
        File file = new File("dblp.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        PostgreSQL postgreSQL = new PostgreSQL();

        try {
            PostgreSQL postgreSQLOther = new PostgreSQL();
            postgreSQLOther.createGeneralPreparedStatementForPubAuthor();
            DefaultHandler otherHandler = new UserHandlerPubAuthor(postgreSQLOther);
            SAXParser saxParserOther = factory.newSAXParser();
            saxParserOther.parse(file,otherHandler);
            postgreSQLOther.executePubAuthor();
            postgreSQLOther.closeCOnnection();

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