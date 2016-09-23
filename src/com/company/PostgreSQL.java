package com.company;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by LENOVO on 21/09/2016.
 */
public class PostgreSQL {

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;


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
    public void createGeneralStatement(String sql) throws Exception{
        statement = connection.createStatement();
        statement.execute(sql);
     }

     public void closeStatement() throws  Exception{
        statement.close();
     }

     public void commitChanges() throws Exception{
         connection.commit();
         connection.close();
     }


     //insert with optimization using PreparedStatement
    //insert for Author table
    public void createGeneralPreparedStatementAuthor() throws Exception{
        preparedStatement = connection.prepareStatement("INSERT INTO author (name) VALUES (?)");
    }

    public void addAuthorField(String authorName) throws Exception{
        preparedStatement.setString(1,authorName);
        preparedStatement.addBatch();
    }

    public void executeBatch()throws Exception{
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

    }


}
