package com.company;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Main {

    private static PostgreSQL postgreSQL;

    public static void main(String[] args) {
        //init postgresql
        postgreSQL = new PostgreSQL();
        // write your code here
        try {
            postgreSQL.createGeneralStatement("INSERT INTO AUTHOR(ID,NAME) VALUES (2,'test')");
            postgreSQL.closeStatement();
            postgreSQL.commitChanges();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void startReadDocument(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        File file = new File("input.txt");
        try {
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
