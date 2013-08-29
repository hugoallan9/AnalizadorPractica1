/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Clase que permite generar las tablas de errores y
 * de simbolos en html
 * @author hugo
 */
public class EscrituraTexto {
    private File f;
    private FileWriter escritora;
    private BufferedWriter temporal;
    private PrintWriter impresora;
    File archive = null;
    FileReader lector = null;
    Scanner tempo = null;
    
    /**
     * Constructor de la clase 
     * @param nombre nombre del archivo
     * @param titulo Titulo del html
     */
    EscrituraTexto( String nombre , String titulo){
        try{
            f = new File(nombre);
            escritora = new FileWriter(f);
            temporal = new BufferedWriter(escritora);
            impresora =  new PrintWriter(temporal);
            
            impresora.write("<HTML lang = \" es \" >"  + "\n");
            impresora.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" + "\n");
            impresora.write("\t <HEAD>" + "\n");
            impresora.write("\t\t <TITLE> " + titulo + "</TITLE> \n");
            impresora.write("<style type=\"text/css\">\n" +
            "<!--\n" +
            "@import url(\"style.css\");\n" +
            "-->\n" +
            "</style>");
            impresora.write("\t </HEAD> \n");
            impresora.write("\t <BODY> \n");
            impresora.write("<div align = \"center\"> \n");
            impresora.write("\t <b>" + "<font size = 7>" + titulo + "</font>"+ "</b> \n");
            impresora.write("</div> \n");
            
            temporal.newLine();
            impresora.close();
            temporal.close();
            
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Segundo constructor de la clase
     * @param nombre nombre del fichero
     * @param contenido contenido a guardar en el .body
     * @param op Forma de guardar
     */
    EscrituraTexto( String nombre, String contenido, int op){
        try{
            f = new File( nombre );
            escritora = new FileWriter(f);
            temporal = new BufferedWriter(escritora);
            impresora =  new PrintWriter(temporal);
            
            impresora.write(contenido);
            
            temporal.newLine();
            impresora.close();
            temporal.close();
            
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     *  Metodo que genera el html con la tabla de errores
     * @param error Lista de errores obtenida en el analisis
     * @param columna Posicion en columna de los errores
     * @param fila  Posicion en fila de los errores
     */
    public void escribirErrores(ArrayList<String> error, ArrayList<Integer> columna, ArrayList<Integer> fila){
        try{
            escritora = new FileWriter(f,true);
            temporal = new BufferedWriter(escritora);
            impresora =  new PrintWriter(temporal);
            
            impresora.append( "<div align = \"center\"> \n");
            impresora.append("      <table id=\"newspaper-b\" width=\"100\" cellspacing=\"2\" cellpadding=\"25\" border=\"0\">");
            impresora.append("<thead>\n" +
            "<tr>\n" +
            "<th scope=\"col\"> No. Error </th>\n" +
            "<th scope=\"col\"> Simbolo</th>\n" +
            "<th scope=\"col\"> No. Linea</th>\n" +
            "<th scope=\"col\"> No. Columna </th>\n" +
            "</tr>\n" +
            "</thead>");
            
            impresora.append("<tbody>");
            for ( int i = 0 ; i < error.size()  ; i++){
                impresora.append("<tr>\n" +
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + (i+1) +    "\n" +
                "   </font>\n" +
                "   </td>\n" );
                 impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                +  error.get(i) +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                  impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + fila.get(i)  +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + columna.get(i)  +  "\n" +
                "   </font>\n" +
                "   </td>\n" + "</tr>");
                
               
            }
            
                impresora.append("</table>\n " + "</div> \n" + "</tbody>" + "</BODY> \n" + "</HTML>");
                impresora.close();
                temporal.close();
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
            
        }
    }
    /**
     * Metodo que genera el html de los simbolos
     * @param lexema El lexema
     * @param columna Posicion del lexema
     * @param fila Posicion en fila del lexema
     * @param tipo Tipo del lexema
     * @param token Numero del token
     */
    public void escribirSimbolos(ArrayList<String> lexema, ArrayList<Integer> columna, ArrayList<Integer> fila,
            ArrayList<String> tipo, ArrayList<Integer> token){
        try{
            escritora = new FileWriter(f,true);
            temporal = new BufferedWriter(escritora);
            impresora =  new PrintWriter(temporal);
            
            impresora.append( "<div align = \"center\"> \n");
            impresora.append("      <table id=\"newspaper-b\" width=\"100\" cellspacing=\"2\" cellpadding=\"25\" border=\"0\">");
            impresora.append("<thead>\n" +
            "<tr>\n" +
            "<th scope=\"col\"> No. Palabra </th>\n" +
            "<th scope=\"col\"> Token</th>\n" +
            "<th scope=\"col\"> Tipo</th>\n" +
            "<th scope=\"col\"> Lexema</th>\n" +
            "<th scope=\"col\"> Linea </th>\n" +
            "<th scope=\"col\">  Columna </th>\n" +
            "<th scope=\"col\">  Palabra Reservada </th>\n" +
            "</tr>\n" +
            "</thead>");
            
            impresora.append("<tbody>");
            for ( int i = 0 ; i < lexema.size()  ; i++){
                impresora.append("<tr>\n" +
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + (i+1) +    "\n" +
                "   </font>\n" +
                "   </td>\n" );
                 impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                +  token.get(i) +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                  impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + tipo.get(i)  +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                  impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + lexema.get(i)  +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                  impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + fila.get(i)  +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                  impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                + columna.get(i)  +  "\n" +
                "   </font>\n" +
                "   </td>\n" );
                impresora.append(
                "    <td >\n" +
                "   <font face=\"verdana, arial, helvetica\" size=2>\n" 
                +  esReservada(token.get(i)) +  "\n" +
                "   </font>\n" +
                "   </td>\n" + "</tr>");
                
               
            }
            
                impresora.append("</table>\n " + "</div> \n" + "</tbody>" + "</BODY> \n" + "</HTML>");
                impresora.close();
                temporal.close();
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
            
        }
    }
    
    /**
     * Metodo que analiza segun el token si una palabra es reservada o no
     * @param get Token a analizar
     * @return Regresa SI en caso sea reservada y NO en caso no lo sea.
     */
    private String esReservada(Integer get) {
        if(get <= 13){
            return "SI";
        }else{
            return "NO";
        }
    }
    
    
    
    
}
