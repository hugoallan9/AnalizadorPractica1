/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que permite leer y escribir en archivos .body
 * @author hugo
 */
public class Lectura {
    private String ruta;
    
    /**
     * Constructor de la clase
     * @param ruta Ruta del archivo que se desea leer
     */
    public Lectura(String ruta){
        this.ruta = ruta;
    }
    
    /**
     * Metodo que recupera la informacion del archivo
     * @return Returna una cadena con la informacion contenida en el archivo
     */
    public String leer(){
        String lectura = "";
        try {
            FileInputStream fstream = new FileInputStream(ruta);
            DataInputStream entrada = new DataInputStream(fstream);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
            try {
                String linea = "";
                while( (linea = buffer.readLine()) != null){
                    lectura += linea;
                }
            } catch (IOException ex) {
                Logger.getLogger(Lectura.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lectura.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lectura;
         
    }
    
}
