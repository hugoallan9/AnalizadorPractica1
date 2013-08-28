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
import java.util.Scanner;

/**
 *
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
    
    EscrituraTexto( String nombre , String titulo){
        try{
            f = new File( nombre );
            escritora = new FileWriter(f);
            temporal = new BufferedWriter(escritora);
            impresora =  new PrintWriter(temporal);
            
            impresora.write("<HTML lang = \" es \" >"  + "\n");
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
    
}
