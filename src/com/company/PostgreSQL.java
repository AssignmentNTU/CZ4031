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
    //this preparedStatement is for publication general
    private PreparedStatement preparedStatement;
    private static long counter= 0;
    private static long counterLimit = 10;

    //this is additional PreparedStatement if we are using SAXParsing
    private PreparedStatement preparedStatementForAuthor;

    //preparedStatement for subclass of the publication
    private PreparedStatement preparedStatementForArticle;
    private PreparedStatement preparedStatementForInCollection;
    private PreparedStatement preparedStatementForInProceedings;
    private PreparedStatement preparedStatementForBooks;
    private PreparedStatement preparedStatementForProceedings;


    //PrepardSatement for editor
    private PreparedStatement preparedStatementForPubAuthor;

    //to save the article if it is null
    private String journalSaved = null;
    private int month = -1,number = -1,volume = -1;

    //to save book
    private String isbn = null,series = null;
    private String publishers = null,bookTitle = null;



    public PostgreSQL(){
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db"
                    ,"postgres","1234567890");
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

    //general crappy insert without optimization
    public void createDistinctAuthorStatement() throws Exception{
        statement = connection.createStatement();
        statement.execute("INSERT INTO author (AUTHOR_NAME) SELECT DISTINCT(AUTHOR_NAME) from raw_author");
     }

//     public void createAuthoredStatement() throws Exception{
//         statement = connection.createStatement();
//         statement.execute
//                 ("INSERT INTO authored (AUTHOR_NAME,PUBLICATION_ID)\n" +
//                         "SELECT author.NAME,publication.PUBLICATION_ID\n" +
//                         "FROM author INNER JOIN publication on\n" +
//                         "author.NAME = publication.AUTHOR_NAME");
//     }


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
        preparedStatementForAuthor = connection.prepareStatement("INSERT INTO raw_author (AUTHOR_NAME) VALUES (?)");
    }



    public void addAuthorField(String authorName) throws Exception{
        preparedStatementForAuthor.setString(1,authorName);
        preparedStatementForAuthor.addBatch();
        counter++;
        if(counter % counterLimit == 0){
            System.out.println(new Date().toString()+" authorName: "+authorName);
            preparedStatementForAuthor.executeBatch();
            commitChanges();
        }
    }


    public void executeBatch()throws Exception{
        preparedStatementForAuthor.executeBatch();
        preparedStatementForArticle.executeBatch();
        preparedStatementForInCollection.executeBatch();
        preparedStatementForInProceedings.executeBatch();
        preparedStatementForBooks.executeBatch();
        preparedStatementForProceedings.executeBatch();
    }

    public void executePublication() throws Exception{
        preparedStatement.executeBatch();
    }

    public void executePubAuthor() throws Exception{
        preparedStatementForPubAuthor.executeBatch();
    }


    //insert into Publication table
    public void createGeneralPreparedStatementPublication() throws Exception{
        preparedStatement = connection.prepareStatement("INSERT INTO publication(pubkey,title,year,pages" +
                ") VALUES " +
                "(?,?,?,?)");
    }

    public void addFieldPubKeyForPublicationElement(String s) throws Exception{
        preparedStatement.setString(1,s);
    }

    public void addPublicationTitle(String s) throws Exception{
        preparedStatement.setString(2,s);
    }

    public void addPublicationYear(int y) throws Exception{
        preparedStatement.setInt(3,y);
    }

    public void addPublicationTotalPages(String s) throws Exception{
        preparedStatement.setString(4,s);
    }



    public void addBatch() throws Exception{
        preparedStatement.addBatch();
        counter++;
        if(counter %counterLimit == 0){
            System.out.println(new Date().toString()+" Article");
            preparedStatement.executeBatch();
            commitChanges();
        }
    }




    //set the preparedStatement for all type of publication

    public void createGeneralPreparedStatementForArticle() throws Exception{
        preparedStatementForArticle = connection.prepareStatement("INSERT INTO article(pubkey,journal,month,volume,number" +
                ") VALUES " +
                "(?,?,?,?,?)");
    }

    //addto particular field of article
    public void addArticlePubKey(String s) throws Exception{
        preparedStatementForArticle.setString(1,s);
    }

    public void addArticleJournal(String s) throws Exception{
        journalSaved = s;
        preparedStatementForArticle.setString(2,s);
    }

    public void addArticleMonth(int s) throws Exception{
        month = s;
        preparedStatementForArticle.setInt(3,s);
    }

    public void addArticleVolume(int s) throws Exception{
        volume = s;
        preparedStatementForArticle.setInt(4,s);
    }

    public void addArticleNumber(int s) throws Exception{
        number = s;
        preparedStatementForArticle.setInt(5,s);
    }

    public void addBatchForArticle() throws Exception{
        if(journalSaved == null){
            preparedStatementForArticle.setString(1,null);
        }
        if(month == -1){
            preparedStatementForArticle.setInt(3,-1);
        }
        if(volume == -1){
            preparedStatementForArticle.setInt(4,-1);
        }
        if(number == -1){
            preparedStatementForArticle.setInt(5,-1);
        }
        month = -1;
        number = -1;
        volume = -1;
        journalSaved = null;
        preparedStatementForArticle.addBatch();
        counter++;
        if(counter % counterLimit == 0){
            System.out.println(new Date().toString()+" Article");
            preparedStatementForArticle.executeBatch();
            commitChanges();
        }
    }

    /***************************************/




    public void createGeneralPreparedStatementForInCollection() throws Exception{

        preparedStatementForInCollection = connection.prepareStatement("INSERT INTO incollection(pubkey,BOOK_TITLE" +
                ") VALUES " +
                "(?,?)");
    }

    public void addInCollectionPubKey(String s) throws Exception{
        preparedStatementForInCollection.setString(1,s);
    }


    public void addInCollectionBookTitle(String s) throws Exception{
        bookTitle = s;
        preparedStatementForInCollection.setString(2,s);
    }

    public void addBatchForInCollection() throws Exception{
        if(bookTitle == null)    preparedStatementForInCollection.setString(2,"");
        bookTitle = null;
        counter++;
        preparedStatementForInCollection.addBatch();
        if(counter %counterLimit == 0){
            System.out.println(new Date().toString()+" InCollections");
            preparedStatementForInCollection.executeBatch();
            commitChanges();
        }
    }


    /***************************************/


    public void createGeneralPreparedStatementForInProceedings() throws  Exception{
        preparedStatementForInProceedings = connection.prepareStatement("INSERT INTO inproceedings(pubkey,book_title" +
                ") VALUES " +
                "(?,?)");
    }



    public void addInProceedingsPubKey(String s) throws Exception{
        preparedStatementForInProceedings.setString(1,s);
    }


    public void addInProceedingsBookTitle(String s) throws Exception{
        bookTitle = s;
        preparedStatementForInProceedings.setString(2,s);
    }

    public void addBatchForInProceedings() throws Exception{
        if(bookTitle == null)preparedStatementForInProceedings.setString(2,null);
        bookTitle = null;
        preparedStatementForInProceedings.addBatch();
        counter++;
        if(counter %counterLimit == 0){
            System.out.println(new Date().toString()+" InProceedings");
            preparedStatementForInProceedings.executeBatch();
            commitChanges();
        }
    }



    /***************************************/


    public void createGeneralPreparedStatementForBook() throws Exception{
        preparedStatementForBooks =  connection.prepareStatement("INSERT INTO book(pubkey,isbn,series" +
                ") VALUES " +
                "(?,?,?)");
    }


    public void addBookPubKey(String s) throws Exception{
        preparedStatementForBooks.setString(1,s);
    }

    public void addBookISBN(String s) throws Exception{
        isbn = s;
        preparedStatementForBooks.setString(2,s);
    }

    public void addBookSeries(String s) throws Exception{
        series = s;
        preparedStatementForBooks.setString(3,s);
    }

    public void addBatchBook() throws Exception{
        if(isbn == null){
            preparedStatementForBooks.setString(2,null);
        }
        if(series == null){
            preparedStatementForBooks.setString(3,null);
        }
        isbn = null;
        series = null;
        preparedStatementForBooks.addBatch();
        counter++;
        if(counter %counterLimit == 0){
            System.out.println(new Date().toString()+" InProceedings");
            preparedStatementForBooks.executeBatch();
            commitChanges();
        }
    }




    /*******************************************/



    public void createGeneralPreparedStatementForProceedings() throws Exception {
        preparedStatementForProceedings =   connection.prepareStatement("INSERT INTO proceedings(pubkey,book_title,publisher,series,volume,isbn" +
                ") VALUES " +
                "(?,?,?,?,?,?)");
    }



    public void addProceedingsPubKey(String s) throws Exception{
        preparedStatementForProceedings.setString(1,s);
    }

    public void addProceedingsBookTitle(String s) throws Exception{
        bookTitle = s;
        preparedStatementForProceedings.setString(2,s);
    }
    public void addProceedingsPublisher(String s) throws Exception{
        publishers = null;
        preparedStatementForProceedings.setString(3,s);
    }
    public void addProceedingSeries(String s) throws Exception{
        series = s;
        preparedStatementForProceedings.setString(4,s);
    }
    public void addProceedingsVolume(int s) throws Exception{
        volume = s;
        preparedStatementForProceedings.setInt(5,s);
    }
    public void addProceedingsISBN(String s) throws Exception{
        isbn = s;
        preparedStatementForProceedings.setString(6,s);
    }

    public void addBatchProceedings() throws Exception{
        if(volume == -1){
            preparedStatementForProceedings.setInt(5,-1);
        }
        if(isbn == null){
            preparedStatementForProceedings.setString(6,null);
        }

        if(series == null){
            preparedStatementForProceedings.setString(4,null);
        }
        if(bookTitle == null){
            preparedStatementForProceedings.setString(2,null);
        }
        if(publishers == null){
            preparedStatementForProceedings.setString(3,null);
        }
        isbn = null;
        series = null;
        volume = -1;
        publishers = null;
        bookTitle = null;
        counter++;
        preparedStatementForProceedings.addBatch();
        if(counter %counterLimit == 0){
            System.out.println(new Date().toString()+" Proceedings");
            preparedStatementForProceedings.executeBatch();
            commitChanges();
        }
    }







    /*********************************************************/


    public void createGeneralPreparedStatementForPubAuthor() throws Exception{
        preparedStatementForPubAuthor = connection.prepareStatement("INSERT INTO pubauthor(AUTHOR_NAME,PUBKEY" +
                ") VALUES " +
                "(?,?)");
    }


    public void addPubAuthorForName(String s) throws Exception{
        preparedStatementForPubAuthor.setString(1,s);
    }

    public void addPubAuthorForPubKey(String s) throws Exception{
        preparedStatementForPubAuthor.setString(2,s);
    }

    public void addBatchForPubAuthor() throws Exception{
        preparedStatementForPubAuthor.addBatch();
        counter++;
        if(counter % counterLimit == 0){
            System.out.println(new Date().toString()+" PubAuthor");
            preparedStatementForPubAuthor.executeBatch();
            commitChanges();
        }
    }




    public void setAllPreparedStatement() throws Exception{
        createGeneralPreparedStatementForProceedings();
        createGeneralPreparedStatementForBook();
        createGeneralPreparedStatementForInProceedings();
        createGeneralPreparedStatementForInCollection();
        createGeneralPreparedStatementForArticle();
    }



}
