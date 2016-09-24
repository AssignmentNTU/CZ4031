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
    private PreparedStatement preparedStatement;
    private static long counter= 0;

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

    //general crappy insert without optimization
    public void createGeneralStatement(String sql) throws Exception{
        statement = connection.createStatement();
        statement.execute(sql);
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
        preparedStatementForAuthor = connection.prepareStatement("INSERT INTO author (name) VALUES (?)");
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
        preparedStatement.executeBatch();
    }


    //insert into Publication table
    public void createGeneralPreparedStatementPublication() throws Exception{
        preparedStatement = connection.prepareStatement("INSERT INTO publication(pubkey,title,year,journal,is_article," +
                "is_phdthesis,is_book,is_proceedings,is_website,is_incollection,is_masterthesis,is_inproceedings) VALUES " +
                "(?,?,?,?,?,?,?,?,?,?,?,?)");
    }

    public void addPublicationField(String pubKey,String title,int year,String journal,boolean is_article
                                    ,boolean is_phdthesis,boolean is_book,boolean is_proceedings,boolean is_website
                                    ,boolean is_incollection,boolean is_masterthesis,boolean is_inproceedings)

    throws Exception
    {
        preparedStatement.setString(1,pubKey);
        preparedStatement.setString(2,title);
        preparedStatement.setInt(3,year);
        preparedStatement.setString(4,journal);
        preparedStatement.setBoolean(5,is_article);
        preparedStatement.setBoolean(6,is_phdthesis);
        preparedStatement.setBoolean(7,is_book);
        preparedStatement.setBoolean(8,is_proceedings);
        preparedStatement.setBoolean(9,is_website);
        preparedStatement.setBoolean(10,is_incollection);
        preparedStatement.setBoolean(11,is_masterthesis);
        preparedStatement.setBoolean(12,is_inproceedings);
        preparedStatement.addBatch();
        counter++;
        if(counter %1000 == 0) System.out.println(new Date().toString()+" pubKey: "+pubKey+" title: "+title+" year: "+year+" journal: "+journal);
    }

    public void addFieldPubKeyForPublicationElement(String s) throws Exception{
        preparedStatement.setString(1,s);
    }

    public void addFieldTitleForPublicationElement(String s) throws Exception{
        preparedStatement.setString(2,s);
    }

    public void addFieldYearForPublicationElement(int year) throws Exception{
        preparedStatement.setInt(3,year);
    }

    public void addFieldJournalForPublicationElement(String s) throws Exception{
        preparedStatement.setString(4,s);
    }

    public void addFieldIsArticleForPublicationElement() throws Exception{
        addFieldBoolean(true,false,false,false,false,false,false,false);
    }

    public void addFieldIsPhdThesisForPublicationElement() throws Exception{
        addFieldBoolean(false,true,false,false,false,false,false,false);
    }

    public void addFieldIsBookForPublicationElement() throws Exception{
        addFieldBoolean(false,false,true,false,false,false,false,false);
    }

    public void addFieldIsProceedingsForPublicationElement() throws Exception{
        addFieldBoolean(false,false,false,true,false,false,false,false);
    }


    public void addFieldIsWebsiteForPublicationElement() throws Exception{
        addFieldBoolean(false,false,false,false,true,false,false,false);
    }


    public void addFieldIsInCollectionsForPublicationElement() throws Exception{
        addFieldBoolean(false,false,false,false,false,true,false,false);
    }


    public void addFieldIsMasterThesisForPublicationElement() throws Exception{
        addFieldBoolean(false,false,false,false,false,false,true,false);
    }

    public void addFieldIsInProceedingsForPublicationElement() throws Exception{
        addFieldBoolean(false,false,false,false,false,false,false,true);
    }

    public void addFieldBoolean(boolean is_article,boolean is_phdthesis,boolean is_book,boolean is_proceedings, boolean is_website,
                               boolean is_incollection , boolean is_masterthesis , boolean is_inproceedings) throws Exception{
        preparedStatement.setBoolean(5,is_article);
        preparedStatement.setBoolean(6,is_phdthesis);
        preparedStatement.setBoolean(7,is_book);
        preparedStatement.setBoolean(8,is_proceedings);
        preparedStatement.setBoolean(9,is_website);
        preparedStatement.setBoolean(10,is_incollection);
        preparedStatement.setBoolean(11,is_masterthesis);
        preparedStatement.setBoolean(12,is_inproceedings);
    }

    public void addBatch() throws Exception{
        preparedStatement.addBatch();
        counter++;
        if(counter %1000 == 0){
            System.out.println(new Date().toString()+" Article");
            preparedStatement.executeBatch();
            commitChanges();
        }
    }



}
