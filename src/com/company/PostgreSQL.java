package com.company;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by LENOVO on 21/09/2016.
 */
public class PostgreSQL {

    private Connection connection;
    private Statement statement;

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

    //general
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

     public ArrayList selectStatement(String sqlSelect, ArrayList<String> keyMap,String type) throws Exception{
         statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sqlSelect);
         ArrayList listReturned = new ArrayList();
         if(type.equals("Author")){
             while(resultSet.next()){
                 for(String key: keyMap){
                     int id = resultSet.getInt("ID");
                     String name = resultSet.getString("AUTHOR");
                     Author author = new Author(id,name);
                     listReturned.add(author);
                 }
             }
         }else if(type.equals("Authored")){
             while(resultSet.next()){
                 for(String key: keyMap){

                 }
             }
         }else{
             while(resultSet.next()){
                 for(String key: keyMap){

                 }
             }
         }
         return listReturned;
     }




}
