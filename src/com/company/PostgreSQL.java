package com.company;

import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * Created by LENOVO on 21/09/2016.
 */
public class PostgreSQL {

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatementArticle;
    private PreparedStatement preparedStatementPhdThesis;



    private PreparedStatement preparedStatementBook;
    private PreparedStatement preparedStatementProceedings;
    private PreparedStatement preparedStatementWebsite;
    private PreparedStatement preparedStatementInCollection;
    private PreparedStatement preparedStatementMasterThesis;
    private PreparedStatement preparedStatementInProceedings;
    private ArrayList<PreparedStatement> listArticlePreparedStatement = new ArrayList<PreparedStatement>();



    private static long counter= 0;


    //just need to check journal
    private String journal = null;

    //this is additional PreparedStatement if we are using SAXParsing
    private PreparedStatement preparedStatementForAuthor;


    public PostgreSQL(){
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db"
                    ,"postgres","postgres");
            connection.setAutoCommit(false);
            System.out.println("database is open successfully");
            this.connection = connection;
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //general Statement
    public void createDistinctAuthorStatement() throws Exception{
        statement = connection.createStatement();
        statement.execute("INSERT INTO author (NAME) SELECT DISTINCT(NAME) from raw_author");
     }

     public void createAuthoredStatement() throws Exception{
         statement = connection.createStatement();
         statement.execute
                 ("INSERT INTO authored (AUTHOR_NAME,PUBLICATION_ID)\n" +
                         "SELECT author.NAME,publication.PUBLICATION_ID\n" +
                         "FROM author INNER JOIN publication on\n" +
                         "author.NAME = publication.AUTHOR_NAME");
     }

     public void createUniionPublicationStatement() throws Exception{
         statement = connection.createStatement();
         statement.execute("SELECT * INTO PUBLICATION\n" +
                 "   FROM ( SELECT * FROM article\n" +
                 "\t\t UNION \n" +
                 "\tSELECT * FROM phdthesis \n" +
                 "          \tUNION \n" +
                 "\tSELECT * FROM book \n" +
                 "\t\tUNION \n" +
                 "\tSELECT * FROM proceedings \n" +
                 "                 UNION \n" +
                 "\tSELECT * FROM website \n" +
                 "\t\tUNION \n" +
                 "\tSELECT * FROM incollection\n" +
                 "                 UNION \n" +
                 "\tSELECT * FROM masterthesis \n" +
                 "\t\tUNION \n" +
                 "\tSELECT * FROM inproceedings) AS a");
     }



     public void closeStatement() throws  Exception{
        statement.close();
     }

     public void commitChanges() throws Exception{
         connection.commit();
     }


     public void closeCOnnection() throws  Exception{
         connection.close();
     }

     //insert with optimization using PreparedStatement
    //insert for Author table
    public void createGeneralPreparedStatementAuthor() throws Exception{
        preparedStatementForAuthor = connection.prepareStatement("INSERT INTO raw_author (name) VALUES (?)");
    }

    public void addAuthorField(String authorName) throws Exception{
        preparedStatementForAuthor.setString(1,authorName);
        preparedStatementForAuthor.addBatch();
        counter++;
        if(counter % 1000 == 0){
            System.out.println(new Date().toString()+" authorName: "+authorName);
            preparedStatementForAuthor.executeBatch();
            commitChanges();
        }
    }

    public void executeBatch()throws Exception{
        preparedStatementForAuthor.executeBatch();
        for(PreparedStatement preparedStatement:listArticlePreparedStatement){
            preparedStatement.executeBatch();
        }
        //preparedStatementArticle.executeBatch();
    }


    //insert into article table
    public void createPreparedStatementArticle() throws Exception{
        preparedStatementArticle = connection.prepareStatement("INSERT INTO article(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementArticle);
    }


    //insert into  phdthesis
    public void createPrepareStatementPhdThesis() throws Exception{
        preparedStatementPhdThesis = connection.prepareStatement("INSERT INTO phdthesis (pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementPhdThesis);
    }


    //inset into books
    public void createPreparedStatementBook() throws Exception{
        preparedStatementBook = connection.prepareStatement("INSERT INTO book(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementBook);
    }



    //insert into proceedings
    public void createPreparedStatementProceedings() throws Exception{
        preparedStatementProceedings = connection.prepareStatement("INSERT INTO proceedings(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementProceedings);
    }



    //inset into website
    public void createPreparedStatementWebsite() throws Exception{
        preparedStatementWebsite = connection.prepareStatement("INSERT INTO website(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementWebsite);
    }


    //insert into incollection
    public void createPreparedStatementInCollection() throws Exception{
        preparedStatementInCollection = connection.prepareStatement("INSERT INTO incollection(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementInCollection);
    }


    //inset into masterthesis
    public void createPreparedStatementMasterThesis() throws Exception{
        preparedStatementMasterThesis = connection.prepareStatement("INSERT INTO masterthesis(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementMasterThesis);
    }


    //insert into inproceedings
    public void createPreparedStatementInProceedings() throws Exception{
        preparedStatementInProceedings = connection.prepareStatement("INSERT INTO inproceedings(pubkey,author_name,title,year,journal) VALUES"+
                "(?,?,?,?,?)");
        listArticlePreparedStatement.add(preparedStatementInProceedings);
    }

    //ARTICLE,PHD_THESIS,BOOK,PROCEEDINGS,WEBSITE,IN_COLLECTION,MASTER_THESIS,IN_PROCEEDINGS

    public void createAllStatementForPublication() throws Exception{
        createPreparedStatementArticle();
        createPrepareStatementPhdThesis();
        createPreparedStatementBook();
        createPreparedStatementProceedings();
        createPreparedStatementWebsite();
        createPreparedStatementInCollection();
        createPreparedStatementMasterThesis();
        createPreparedStatementInProceedings();
    }



    public void addFieldPubKeyForPublicationElement(PreparedStatement preparedStatement,String s) throws Exception{
        preparedStatement.setString(1,s);
    }

    public void addFieldAuthorNameForPublicationElement(PreparedStatement preparedStatement,String s) throws Exception{
        preparedStatement.setString(2,s);
    }

    public void addFieldTitleForPublicationElement(PreparedStatement preparedStatement,String s) throws Exception{
        preparedStatement.setString(3,s);
    }

    public void addFieldYearForPublicationElement(PreparedStatement preparedStatement,int year) throws Exception{
        preparedStatement.setInt(4,year);
    }

    public void addFieldJournalForPublicationElement(PreparedStatement preparedStatement,String s) throws Exception{
        journal = s;
        preparedStatement.setString(5,s);
    }


    public PreparedStatement getPreparedStatementArticle() {
        return preparedStatementArticle;
    }

    public PreparedStatement getPreparedStatementPhdThesis() {
        return preparedStatementPhdThesis;
    }

    public PreparedStatement getPreparedStatementBook() {
        return preparedStatementBook;
    }

    public PreparedStatement getPreparedStatementProceedings() {
        return preparedStatementProceedings;
    }

    public PreparedStatement getPreparedStatementWebsite() {
        return preparedStatementWebsite;
    }

    public PreparedStatement getPreparedStatementInCollection() {
        return preparedStatementInCollection;
    }

    public PreparedStatement getPreparedStatementMasterThesis() {
        return preparedStatementMasterThesis;
    }

    public PreparedStatement getPreparedStatementInProceedings() {
        return preparedStatementInProceedings;
    }

    public void addBatch(ArticleType.TYPE type) throws Exception{

        switch(type){
            case ARTICLE:
                if(journal.equals(null)){
                    preparedStatementArticle.setString(5,null);
                }
                preparedStatementArticle.addBatch();
                break;
            case PHD_THESIS:
                if(journal.equals(null)){
                    preparedStatementPhdThesis.setString(5,null);
                }
                preparedStatementPhdThesis.addBatch();
                break;
            case BOOK:
                if(journal.equals(null)){
                    preparedStatementBook.setString(5,null);
                }
                preparedStatementBook.addBatch();
                break;
            case PROCEEDINGS:
                if(journal.equals(null)){
                    preparedStatementProceedings.setString(5,null);
                }
                preparedStatementProceedings.addBatch();
                break;
            case WEBSITE:
                if(journal.equals(null)){
                    preparedStatementWebsite.setString(5,null);
                }
                preparedStatementWebsite.addBatch();
                break;
            case IN_COLLECTION:
                if(journal.equals(null)){
                    preparedStatementInCollection.setString(5,null);
                }
                preparedStatementInCollection.addBatch();
                break;
            case MASTER_THESIS:
                if(journal.equals(null)){
                    preparedStatementMasterThesis.setString(5,null);
                }
                preparedStatementMasterThesis.addBatch();
                break;
            case IN_PROCEEDINGS:
                if(journal.equals(null)){
                    preparedStatementInProceedings.setString(5,null);
                }
                preparedStatementInProceedings.addBatch();
                break;
        }
        counter++;
        if(counter %1000 == 0){
            System.out.println(new Date().toString()+" Article");
            for(PreparedStatement preparedStatement:listArticlePreparedStatement){
                preparedStatement.executeBatch();
            }
            commitChanges();
        }
    }





}
