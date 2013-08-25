/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author hugo
 */
public class Analizador {
    private ArrayList<String> error, lexema, tipo;
    private ArrayList<Integer> posicionFila, posicionColumna, token;
    private String valorLexema, cadenaSalida;
    private int fila, columna, posicionCarrete, inicioPalabra, pestania;
    private String palabraReservadaDigestivo[];
    private String palabraReservadaOseo[];
    private String extensiones[] = {".edu", ".org", ".com", ".gt"};
    private StyleContext estilo = new StyleContext();
    Style errorStyle = estilo.addStyle("error", null);
    Style normalText = estilo.addStyle("normal", null);
    Style palabraReservada = estilo.addStyle("reservada", null);
    Style expresionRegular = estilo.addStyle("regular", null);
    public static String newline = System.getProperty("line.separator");
    
    Analizador(int op){
        pestania = op;
        inicializar();
    }
    
    private void inicializar(){
        pestania = 0;
        fila = 1;
        columna = 1 ;
        posicionCarrete = 0;
        inicioPalabra = 0;
        token = new ArrayList();
        error = new ArrayList();
        lexema = new ArrayList();
        posicionFila = new ArrayList();
        posicionColumna = new ArrayList();
        tipo = new ArrayList();
        palabraReservadaDigestivo = new String[4];
        palabraReservadaOseo = new String[7];
        valorLexema = "";
        inicializarReservadas();
        StyleConstants.setForeground(errorStyle, Color.RED);
        StyleConstants.setForeground(normalText, Color.BLACK);
        StyleConstants.setForeground(palabraReservada, Color.BLUE);
        StyleConstants.setForeground(expresionRegular, Color.GREEN);
    }

    private void inicializarReservadas() {
        palabraReservadaDigestivo[0] = "boca";
        palabraReservadaDigestivo[1] = "faringe";
        palabraReservadaDigestivo[2] = "esofago";
        palabraReservadaDigestivo[3] = "estomago";
        palabraReservadaOseo[0] = "pelvis";
        palabraReservadaOseo[1] = "craneo";
        palabraReservadaOseo[2] = "maxilar";
        palabraReservadaOseo[3] = "costilla";
        palabraReservadaOseo[4] = "esternon";
        palabraReservadaOseo[5] = "femur";
        palabraReservadaOseo[6] = "coccix";
        }
    
    public Analizador(){
        inicializar();
    }
    
    private boolean belongsToAlphabetMinus(char entrada, String letra){
        boolean result = false;
        String conversion = ""+entrada;
        if ( Character.isLetter(entrada) && !conversion.equalsIgnoreCase(letra) ){
            result = true;
        }else{
            result = false;
        }
        return result;
        
    }
    
    private boolean comprobarPalabra(String palabra, int op){
        boolean result = false;
        ciclo1:
        switch(op){
            //Caso de palabras reservadas del digestivo
            case 0:
                for( int i = 0 ; i < 4 ; i++ ){
                    if ( palabra.equalsIgnoreCase(palabraReservadaDigestivo[i])){
                        result = true;
                        break ciclo1;
                    }
                }
            // Caso del oseo    
            case 1:
                for (int i = 0 ; i < 7 ; i++){
                    if ( palabra.equalsIgnoreCase(palabraReservadaOseo[i])){
                        result = true;
                        break ciclo1;
                    }
                }
            default:
                System.err.println("No has ingresado una opcion valida");
        }
        return result;
    }
    
    private boolean comprobarExtension(String expresion){
        boolean resultado = false;
        String extension = "";
        
        for (int i = expresion.lastIndexOf(expresion) -3  ; i == expresion.lastIndexOf(expresion)  ; i++){
            extension = extension + expresion.charAt(i);
        }
        
        ciclofor:
        for (int i = 0 ; i < 4 ; i++ ){
            if (extension.equalsIgnoreCase(extensiones[i])){
                resultado = true;
                break ciclofor;
            }
        }
        
        extension = "";
        
        for (int i = expresion.lastIndexOf(expresion) -2  ; i == expresion.lastIndexOf(expresion)  ; i++){
            extension = extension + expresion.charAt(i);
        }
        
        if( extension.equalsIgnoreCase(extensiones[3]) ) resultado = true;
        
        return resultado;
        
        
    }
    
    /**
     *
     * @param entrada
     * @param recipiente
     * @param op
     */
    public DefaultStyledDocument analizar(String entrada,  int op) throws BadLocationException{
        DefaultStyledDocument contenedor;
        contenedor = new DefaultStyledDocument(estilo);
        cadenaSalida = "";
        int estado = 0;
        char letra;
        entrada = limpiarCadena(entrada);
        System.out.println("La cadena limpia es: " + entrada);
        
        for(posicionCarrete = 0 ; posicionCarrete < entrada.length() ; posicionCarrete++){
            inicioPalabra = posicionCarrete;
            letra = entrada.charAt(posicionCarrete);
            switch(estado){
                case 0:
                    valorLexema = "";
                    if(belongsToAlphabetMinus(letra, "i") && belongsToAlphabetMinus(letra , "w")){
                        estado = 1;
                        valorLexema = valorLexema + letra;
                   }else if(letra == '3'){
                        estado = 2;
                        valorLexema = valorLexema + letra;
                    }else if( letra =='2' || letra == '1'){
                        estado = 3;
                        valorLexema = valorLexema + letra;
                    }else if(letra == '0'){
                        estado = 4;
                        valorLexema = valorLexema + letra;
                    }else if(Character.isDigit(letra) && letra != '0' &&
                            letra != '1' && letra != '2' && letra != '3'){
                        estado = 17;
                        valorLexema = valorLexema + letra;
                    }else if(letra == '['){
                        estado = 21;
                        valorLexema = valorLexema + letra;
                    }else if("w".equalsIgnoreCase(""+letra)){
                        estado = 23;
                        valorLexema = valorLexema + letra;
                    }else if("i".equalsIgnoreCase(""+letra)){
                        estado = 33;
                        valorLexema = valorLexema + letra;
                    }else if(letra == 32){
                        estado = 0;
                        hacerCambio(contenedor);
                        try {
                             contenedor.insertString(contenedor.getLength(),String.valueOf(letra), normalText);
                         } catch (BadLocationException ex) {
                            Logger.getLogger(Analizador.class.getName()).log(Level.SEVERE, null, ex);
                         } 
                        columna++;
                    }
                    else{
                        estado = 0;
                        error.add(""+letra);
                       hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),String.valueOf(letra), normalText);

                        
                        columna++;
                    }
                    break;
                    
                case 1:
                    if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema = valorLexema + letra;
                    }else{
                        estado = 0;
                        if(comprobarReservada(valorLexema, op) < 13){
                            token.add(comprobarReservada(valorLexema, op));
                            lexema.add(valorLexema);
                            tipo.add("Palabra Reservada");
                            hacerCambio(contenedor);
                            

                            contenedor.insertString(contenedor.getLength(),valorLexema, palabraReservada);
                            posicionFila.add(fila);
                            posicionColumna.add(columna);
                            modificarColumna(valorLexema.length());
                            
                        }else if(comprobarExtension(valorLexema)){
                            token.add(17);
                            lexema.add(valorLexema);
                            tipo.add("Referencia");
                           hacerCambio(contenedor);
                                contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                            
                            posicionFila.add(fila);
                            posicionColumna.add(columna);
                            modificarColumna(valorLexema.length());
                        }else{
                            token.add(18);
                            lexema.add(valorLexema);
                            tipo.add("ID");
                           hacerCambio(contenedor);
                            
                            contenedor.insertString(contenedor.getLength(),valorLexema, normalText);

                            posicionFila.add(fila);
                            posicionColumna.add(columna);
                            modificarColumna(valorLexema.length());
                        }
                        posicionCarrete--;
                    }
                    System.out.println(valorLexema);
                    break;
            }
        }
        imprimirResultados();
        return contenedor;
        
        
    }

    private boolean cambioFila() {
        boolean cambio = false;
        if(columna + valorLexema.length() >= 50){
            cambio = true;
        }else if(columna + 1 >= 80){
            cambio = true;
        }
        return cambio;
                
    }

    private int comprobarReservada(String valorLexema, int op){
        int  respuesta = 0;
        cicloReserva:
        switch(op){
            case 0:
                for(int i =0 ; i < 4 ; i++){
                    if(valorLexema.equalsIgnoreCase(palabraReservadaDigestivo[i])){
                        respuesta = i + 1;
                        break cicloReserva;
                    }
                }

            case 1:
                for(int i = 0; i < 7; i++){
                    if(valorLexema.equalsIgnoreCase(palabraReservadaOseo[i])){
                       respuesta = 7 + i;
                       break cicloReserva;
                    }
                }
                
            case 2:
                for(int i =0 ; i < 4 ; i++){
                    if(valorLexema.equalsIgnoreCase(palabraReservadaDigestivo[i])){
                        respuesta = i + 1;
                        break cicloReserva;
                    }
                }
                
                for(int i = 0; i < 7; i++){
                    if(valorLexema.equalsIgnoreCase(palabraReservadaOseo[i])){
                       respuesta = 7 + i;
                       break cicloReserva;
                    }
                }
                
            default:
                System.err.println(valorLexema + " no es una palabra reservada");
                respuesta = 18;
                
                
                
        }
        
        return respuesta;
       
    }

    private void imprimirResultados() {
        System.out.println("*****************");
        for(int i = 0 ; i < lexema.size() ; i++){
            System.out.println(lexema.get(i) + "    " + posicionFila.get(i) + "         "+posicionColumna.get(i) + "     "  + token.get(i));
        }
        
    }

    private String limpiarCadena(String entrada) {
        return entrada.replaceAll("[\n\r\t]", "") + " ";
       
    }

    private void modificarColumna(int length) {
        columna = columna + length;
    }

    private String relleno(){
        String salida = "";
        for(int i = 0 ; i < 80 ; i++){
            salida = salida + "";
        }
        salida = salida + newline;
        return salida;
    }
    
    private void escribirContenedor(DefaultStyledDocument contenedor, String vl, AttributeSet modo){
        try {
            contenedor.insertString(contenedor.getLength(),vl, modo );
        } catch (BadLocationException ex) {
            Logger.getLogger(Analizador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hacerCambio(DefaultStyledDocument contenedor) {
          if(cambioFila()){
              try {
                  contenedor.insertString(contenedor.getLength(), newline, normalText);
              } catch (BadLocationException ex) {
                  Logger.getLogger(Analizador.class.getName()).log(Level.SEVERE, null, ex);
              }
             fila++;
             columna = 1;
          }            
    }

    
}
