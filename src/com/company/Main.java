package com.company;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static com.sun.org.apache.xerces.internal.impl.Constants.JDK_ENTITY_EXPANSION_LIMIT;


public class Main {

    private static PostgreSQL postgreSQL;

    public static void main(String[] args) {
        //init postgresql
        postgreSQL = new PostgreSQL();
        // write your code here
        try {
            startReadDocument();
            postgreSQL.commitChanges();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    public static void startReadDocument() throws Exception{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        File file = new File("dblp.xml");
        dbFactory.setAttribute(JDK_ENTITY_EXPANSION_LIMIT, "0");
        try {
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            //this is intentionally create DOM Tree which means accessing any element first have the same worst complexity
            Document doc = documentBuilder.parse(file);
            doc.getDocumentElement().normalize();
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.ARTICLE,"article");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.INPROCEEDINGS,"inproceedings");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.PROCEEDINGS,"proceedings");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.BOOK,"book");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.INCOLLECTION,"incollection");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.PHDTHESIS,"phdthesis");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.MASTERTHESIS,"masterthesis");
            startReadPublicationElement(doc, PublicationType.PUBLICATION_TYPE.WEBSITE,"www");
            startReadAuthorElement(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void startReadAuthorElement(Document doc) throws  Exception{
        NodeList nodeList = doc.getElementsByTagName("author");
        postgreSQL.createGeneralPreparedStatementAuthor();
        for(int i = 0 ; i < nodeList.getLength() ; i++){
            Node node = nodeList.item(i);
            postgreSQL.addAuthorField(node.getTextContent());
        }
        postgreSQL.executeBatch();
    }


    public static void startReadPublicationElement(Document doc, PublicationType.PUBLICATION_TYPE type,String xmlElement) throws Exception{
        //start to get article type
        NodeList nodeList = doc.getElementsByTagName(xmlElement);
        postgreSQL.createGeneralPreparedStatementPublication();
        for(int i  = 0 ; i < nodeList.getLength() ; i++){
            Node node = nodeList.item(i);
            Element element  = (Element) node;
            NamedNodeMap attributes = node.getAttributes();
            //get the attribute first
            String key = attributes.getNamedItem("key").getTextContent();
            String title = element.getElementsByTagName("title").item(0).getTextContent();
            String journal = element.getElementsByTagName("journal").item(0).getTextContent();
            int year  = Integer.parseInt(element.getElementsByTagName("year").item(0).getTextContent());
            //effeciency can be optimized even more
            boolean[] listBooolean = new boolean[8];
            switch (type){
                case ARTICLE:
                    looping(0,listBooolean);
                    break;
                case PHDTHESIS:
                    looping(1,listBooolean);
                    break;
                case BOOK:
                    looping(2,listBooolean);
                    break;
                case PROCEEDINGS:
                    looping(3,listBooolean);
                    break;
                case WEBSITE:
                    looping(4,listBooolean);
                    break;
                case INCOLLECTION:
                    looping(5,listBooolean);
                    break;
                case MASTERTHESIS:
                    looping(6,listBooolean);
                    break;
                case INPROCEEDINGS:
                    looping(7,listBooolean);
                    break;
            }
            postgreSQL.addPublicationField(key,title,year,journal,listBooolean[0],listBooolean[1],listBooolean[2],listBooolean[3],listBooolean[4]
            ,listBooolean[5],listBooolean[6],listBooolean[7]);
        }
        postgreSQL.executeBatch();
    }


    public static void looping(int k,boolean[] listBoolean){
        listBoolean[k] = true;
        for(int i = 0 ; i < k ;i++){
            listBoolean[i] = false;
        }

        for(int i = k+1 ;  i <  listBoolean.length ; i++){
            listBoolean[i] = false;
        }
    }


}
