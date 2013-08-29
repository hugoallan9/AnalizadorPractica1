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
 * Clase de implementacion del automata finito determinista
 * para reconocer las expresiones id, fecha, año, numero entero
 * palabras reservadas, referencias, fuentes.
 * @author hugo
 */
public class Analizador {
    private ArrayList<String> error, lexema, tipo;
    private ArrayList<Integer> posicionFila, posicionColumna, posicionFilaError, posicionColumnaError, token;
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
    
    
    /**
     * Constructor de la clase Analizador
     * @param op Es un parametro que recibe el constructor para
     * saber en que modo trabajar. op = 0 significa que trabaja
     * para el aparato digestivo, op = 1 para el sistema oseo y op = 2
     * en ambos.
     */
    Analizador(int op){
        pestania = op;
        inicializar();
    }
    
    
    /**
     * Metodo que inicializa los componentes de la clase
     * analizador.
     */
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
        posicionFilaError = new ArrayList();
        posicionColumnaError = new ArrayList();
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

    /**
     * Metodo que inicializa el array que contiene las palabras
     * reservadas para el analizador.
     */
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
    
 
    /**
     * Metodo que verifica si un caracter pertenece a un alfabeto
     * reducido donde no se encuentra una letra en particular
     * @param entrada caracter que se desea analizar
     * @param letra letra que se desea quitar del alfabeto
     * @return Regresa verdadero si entrada pertenece al alfabeto reducido
     * y falso en caso contrario
     */
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
    
    /**
     * Metodo que comprueba si un id es una palabra reservada.
     * @param palabra ID que se desea analizar
     * @param op Si op = 0 solo marca como reservadas las del aparato digestivo, op = 1 para
     * el sistema oseo y op = 2 para ambos
     * @return Verdadero si la palabra es reservada
     */

    
    /**
     * Metodo que comprueba si un ID es una referencia web
     * @param expresion ID que se desea analizar
     * @return Verdadero en caso cumpla con ser referencia web con una extension permitida
     */
    private boolean comprobarExtension(String expresion){
        boolean resultado = false;
        String extension = "";
        
        if(expresion.length() > 3){
        try{
            for (int i = expresion.length() - 4  ; i < expresion.length()  ; i++){
                extension = extension + expresion.charAt(i);
                System.out.println("La expresion va quendando: " + extension);
            }
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println("La palabra es muy corta, debe ser un ID");
        }
        
        ciclofor:
        for (int i = 0 ; i < 4 ; i++ ){
            if (extension.equalsIgnoreCase(extensiones[i])){
                resultado = true;
                break ciclofor;
            }
        }
        
        extension = "";
        
        for (int i = expresion.length() - 3  ; i < expresion.length()  ; i++){
            extension = extension + expresion.charAt(i);
        }
        
        if( extension.equalsIgnoreCase(extensiones[3]) ) resultado = true;
        
       
        }
         return resultado;
        
    }
    
    /**
     * Metodo que analiza un string de entrada de manera lexica, aceptando o rechazando palabras
     * @param entrada String de entrada que se desea analizar
     * @param recipiente Es el contenedor del JTextPane en donde se esta escribiendo
     * @param op El modod en el que se desea trabajar, op = 0 para digestivo, op = 1 para oseo y
     * op = 2 para general.
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
                    }else if(letra == '{'){
                        estado = 28;
                        valorLexema += letra;
                    }else if("w".equalsIgnoreCase(""+letra)){
                        estado = 23;
                        valorLexema = valorLexema + letra;
                    }else if("i".equalsIgnoreCase(""+letra)){
                        estado = 33;
                        valorLexema = valorLexema + letra;
                    }else if(letra == ';' || letra == '.' || letra == ','){
                        estado = 56;
                        valorLexema += letra; 
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
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                       hacerCambio(contenedor);
                       contenedor.insertString(contenedor.getLength(),String.valueOf(letra), errorStyle);
                       columna++;
                    }
                    break;
                    
                case 1:
                    if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema = valorLexema + letra;
                    }else{
                        estado = 0;
                        if(comprobarReservada(valorLexema, op) <= 13){
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
                        }else if(tienePunto(valorLexema)){
                            estado = 0;
                            valorLexema += letra;
                            error.add(valorLexema);
                            hacerCambio(contenedor);
                            contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                            posicionFilaError.add(fila);
                            posicionColumnaError.add(columna);
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
                case 2:
                    if(letra == '0' || letra == '1'){
                        estado = 5;
                        valorLexema = valorLexema + letra;
                    }else if(Character.isDigit(letra)){
                        estado = 18;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                        }
                    break;
                case 3:
                    if(Character.isDigit(letra)){
                        estado = 5;
                        valorLexema += letra;
                    }else{
                         estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 4:
                    if(Character.isDigit(letra) && letra != '0'){
                        estado = 5;
                        valorLexema += letra;
                    }else if(letra == '0'){
                        estado = 18;
                        valorLexema += letra;
                    }else{
                         estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 5:
                    if(letra == '/'){
                        estado = 6;
                        valorLexema += letra;
                    }else if(letra == '-'){
                        estado = 7;
                        valorLexema += letra;
                    }else if(letra == '0'){
                        estado = 8;
                        valorLexema += letra;
                    }else if(letra == '1'){
                        estado = 9;
                        valorLexema += letra;
                    }else if(Character.isDigit(letra)){
                        estado = 19;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 6:
                    if(letra == '0'){
                        estado = 8;
                        valorLexema += letra;
                    }else if(letra == '1'){
                        estado = 9;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 7:
                    if(Character.isLetter(letra)){
                        estado = 10;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 8:
                    if(Character.isDigit(letra) && letra != '0'){
                        estado = 14;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 9:
                    if(letra == '0' || letra == '1' || letra == '2'){
                        estado = 14;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 10:
                    if(Character.isLetter(letra)){
                        estado = 10;
                        valorLexema += letra;
                    }else if(letra == '-'){
                        estado = 11;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 11:
                    if(Character.isDigit(letra)){
                        estado = 12;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 12:
                    if(Character.isDigit(letra)){
                        estado = 13;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 13:
                    estado = 0;
                    token.add(14);
                    lexema.add(valorLexema);
                    tipo.add("Fecha");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
                    posicionCarrete--;
                    break;
                case 14:
                    if(letra == '/'){
                        estado = 15;
                        valorLexema += letra;
                    }else if(Character.isDigit(letra)){
                        estado = 16;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 15:
                    if(Character.isDigit(letra)){
                        estado = 11;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 16:
                    if(Character.isDigit(letra)){
                        estado = 11;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 17:
                    if(Character.isDigit(letra) && letra != '0' && letra != '1'
                            && letra != '2' && letra != '3'){
                        estado = 18;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 18:
                    if(Character.isDigit(letra)){
                        estado = 19;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 19:
                    if(Character.isDigit(letra)){
                        estado = 20;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(19);
                        lexema.add(valorLexema);
                        tipo.add("Numero entero");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 20:
                    if(Character.isDigit(letra)){
                        estado = 17;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(15);
                        valorLexema += letra; 
                        lexema.add(valorLexema);
                        tipo.add("Año");
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 21:
                    if(letra == ']'){
                        estado = 22;
                        valorLexema += letra;
                    }else{
                        if(posicionCarrete == entrada.length() -1){
                            estado = 0;
                            valorLexema += letra;
                            error.add(valorLexema);
                            hacerCambio(contenedor);
                            contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                            posicionFilaError.add(fila);
                            posicionColumnaError.add(columna);
                            modificarColumna(valorLexema.length());
                        }
                        estado = 21;
                        valorLexema += letra;
                    }
                    break;
                case 22:
                    estado = 0;
                    token.add(16);
                    lexema.add(valorLexema);
                    tipo.add("Referencia");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
                    posicionCarrete--;
                    break;
                case 23:
                    if(letra == 'w' || letra == 'W'){
                        estado = 24;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        valorLexema += letra;
                        estado = 1;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 24:
                    if(letra == 'w' || letra == 'W'){
                        estado = 25;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        valorLexema += letra;
                        estado = 1;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 25:
                    if(letra == '.'){
                        estado = 26;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        valorLexema += letra;
                        estado = 1;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 26:
                    if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 26;
                        valorLexema += letra;
                    }else if(letra == '.'){
                        estado = 27;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 27:
                    if(Character.isLetter(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 28:
                    if(letra == 32 || Character.isDigit(letra) || Character.isLetter(letra)){
                        if(posicionCarrete == entrada.length() - 1){
                            estado = 0;
                            valorLexema += letra;
                            error.add(valorLexema);
                            hacerCambio(contenedor);
                            contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                            posicionFilaError.add(fila);
                            posicionColumnaError.add(columna);
                            modificarColumna(valorLexema.length());
                        }else{
                            estado = 28;
                            valorLexema += letra;
                        }
                    }else if(letra == ';'){
                        estado = 29;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 29:
                    if(letra == 32 || Character.isDigit(letra) || Character.isLetter(letra)){
                        if(posicionCarrete == entrada.length() - 1){
                            estado = 0;
                            valorLexema += letra;
                            error.add(valorLexema);
                            hacerCambio(contenedor);
                            contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                            posicionFilaError.add(fila);
                            posicionColumnaError.add(columna);
                            modificarColumna(valorLexema.length());
                        }else{
                            estado = 29;
                            valorLexema += letra;
                        }
                    }else if(letra == ';'){
                        estado = 30;
                        valorLexema += letra;
                    }else if(letra == '}'){
                        estado = 31;
                        valorLexema += letra;
                    }
                    else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 30:
                    if(letra == 32 || Character.isDigit(letra) || Character.isLetter(letra)){
                        if(posicionCarrete == entrada.length() - 1){
                            estado = 0;
                            valorLexema += letra;
                            error.add(valorLexema);
                            hacerCambio(contenedor);
                            contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                            posicionFilaError.add(fila);
                            posicionColumnaError.add(columna);
                            modificarColumna(valorLexema.length());
                        }else{
                            estado = 30;
                            valorLexema += letra;
                        }
                    }else if(letra == ';'){
                        estado = 32;
                        valorLexema += letra;
                    }else if(letra == '}'){
                        estado = 31;
                        valorLexema += letra;
                    }
                    else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 32:
                    if(letra == 32 || Character.isDigit(letra) || Character.isLetter(letra)){
                        if(posicionCarrete == entrada.length() - 1){
                            estado = 0;
                            valorLexema += letra;
                            error.add(valorLexema);
                            hacerCambio(contenedor);
                            contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                            posicionFilaError.add(fila);
                            posicionColumnaError.add(columna);
                            modificarColumna(valorLexema.length());
                        }else{
                            estado = 32;
                            valorLexema += letra;
                        }
                    }else if(letra == '}'){
                        estado = 31;
                        valorLexema += letra;
                    }
                    else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 31:
                    estado = 0;
                    token.add(16); 
                    lexema.add(valorLexema);
                    tipo.add("Referencia");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
                    posicionCarrete--;
                    break;
                case 33:
                    if(letra == 'n' || letra == 'N'){
                        estado = 34;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 34:
                    if(letra == 't' || letra == 'T'){
                        estado = 35;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 35:
                    if(letra == 'e' || letra == 'E'){
                        estado = 36;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 36:
                    if(letra == 's' || letra == 'S'){
                        estado = 37;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 37:
                    if(letra == 't' || letra == 'T'){
                        estado = 38;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 38:
                    if(letra == 'i' || letra == 'I'){
                        estado = 39;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 39:
                    if(letra == 'n' || letra == 'N'){
                        estado = 40;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 40:
                    if(letra == 'o' || letra == 'O'){
                        estado = 41;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 41:
                    if(letra == 32 && (op == 0 || op == 2)){
                        estado = 42;
                        valorLexema += letra;
                    }else if(Character.isLetter(letra) || Character.isDigit(letra)){
                        estado = 1;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        token.add(18);
                        lexema.add(valorLexema);
                        tipo.add("ID");
                        hacerCambio(contenedor); 
                        contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                        posicionFila.add(fila);
                        posicionColumna.add(columna);
                        modificarColumna(valorLexema.length());
                        posicionCarrete--;
                    }
                    break;
                case 42:
                    if(letra == 'g' || letra == 'G'){
                        estado = 43;
                        valorLexema += letra;
                    }else if(letra == 'd' || letra == 'D'){
                        estado = 48;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 43:
                    if(letra == 'r' || letra == 'G'){
                        estado = 44;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 44:
                    if(letra == 'u' || letra == 'U'){
                        estado = 45;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 45:
                    if(letra == 'e' || letra == 'E'){
                        estado = 46;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 46:
                    if(letra == 's' || letra == 'S'){
                        estado = 47;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 47:
                    if(letra == 'o' || letra == 'O'){
                        estado = 55;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 55:
                    estado = 0;
                    token.add(6);
                    lexema.add(valorLexema);
                    tipo.add("Palabra Reservada");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, palabraReservada);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
                    posicionCarrete--;
                    break;
                case 48:
                    if(letra == 'e' || letra == 'E'){
                        estado = 49;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 49:
                    if(letra == 'l' || letra == 'L'){
                        estado = 50;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 50:
                    if(letra == 'g' || letra == 'G'){
                        estado = 51;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 51:
                    if(letra == 'a' || letra == 'A'){
                        estado = 52;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 52:
                    if(letra == 'd' || letra == 'D'){
                        estado = 53;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 53:
                    if(letra == 'o' || letra == 'O'){
                        estado = 54;
                        valorLexema += letra;
                    }else{
                        estado = 0;
                        valorLexema += letra;
                        error.add(valorLexema);
                        hacerCambio(contenedor);
                        contenedor.insertString(contenedor.getLength(),valorLexema, errorStyle);
                        posicionFilaError.add(fila);
                        posicionColumnaError.add(columna);
                        modificarColumna(valorLexema.length());
                    }
                    break;
                case 54:
                    estado = 0;
                    token.add(5);
                    lexema.add(valorLexema);
                    tipo.add("Palabra Reservada");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, palabraReservada);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
                    posicionCarrete--;
                    break;
                case 56:
                    estado = 0;
                    token.add(30);
                    lexema.add(valorLexema);
                    tipo.add("Separador");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, normalText);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
                    posicionCarrete--;
                    break;
                
                    
                default:
                    System.err.println("Error de Programacion, ese estado no existe");
                    estado = 0;
                    
            }
        }
        imprimirResultados();
        return contenedor;
        
        
    }
    /**
     * Metodo que verifica si es necesario hacer un salto de linea al llegar a un 
     * limite de 50 caracteres
     * @return verdadero si es necesario hacer el cambio de linea y falso en caso
     * contrario
     */
    private boolean cambioFila() {
        boolean cambio = false;
        if(columna + valorLexema.length() >= 50){
            cambio = true;
        }else if(columna + 1 >= 80){
            cambio = true;
        }
        return cambio;
                
    }
 
    /**
     * Metodo que comprueba si un id es una palabra reservada.
     * @param valorLexema Metodo que comprueba si un id es una palabra reservada.
     * @param op Si op = 0 solo marca como reservadas las del aparato digestivo, op = 1 para
     * el sistema oseo y op = 2 para ambos
     * @return Verdadero si la palabra es reservada
     */
    private int comprobarReservada(String valorLexema, int op){
        int  respuesta = 50;
        cicloReserva:
        switch(op){
            case 0:
                for(int i =0 ; i < 4 ; i++){
                    if(valorLexema.equalsIgnoreCase(palabraReservadaDigestivo[i])){
                        respuesta = i + 1;
                        break cicloReserva;
                    }
                }
                break;


            case 1:
                for(int i = 0; i < 7; i++){
                    if(valorLexema.equalsIgnoreCase(palabraReservadaOseo[i])){
                       respuesta = 7 + i;
                       break cicloReserva;
                    }
                }
                break;

                
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
    
    /**
     * Metodo que va imprimiendo los resultados en consola, usado para encontrar errores
     */
    private void imprimirResultados() {
        System.out.println("*****************");
        for(int i = 0 ; i < lexema.size() ; i++){
            System.out.println(lexema.get(i) + "    " + posicionFila.get(i) + "         "+posicionColumna.get(i) + "     "  + token.get(i));
        }
        
    }

    /**
     * Metodo que limpia una cadena de los cambios de linea, tabuladores
     * y demas espacios insertados por el JTextPane
     * @param entrada String a limpiar
     * @return Regresa la cadena limpia
     */
    private String limpiarCadena(String entrada) {
        return entrada.replaceAll("[\n\r\t]", "") + " " + " ";
        
       
    }

    /**
     * Metodo para hacer el corrimiento de columna cuando 
     * una palabra es aceptada o agregada como error
     * @param length longitud de la palabra que recien
     * se termina de analizar
     */
    private void modificarColumna(int length) {
        columna = columna + length;
    }
    
   
   /**
    * Metodo que se utiliza para escribir en el StyledDocument con los estilos definidos
    * @param contenedor El contenedor al cual se le desea introducir datos
    * @param vl dato que se desea ingresar al contenedor
    * @param modo Estilo que se desea utilizar para introducir los datos.
    */ 
    private void escribirContenedor(DefaultStyledDocument contenedor, String vl, AttributeSet modo){
        try {
            contenedor.insertString(contenedor.getLength(),vl, modo );
        } catch (BadLocationException ex) {
            Logger.getLogger(Analizador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Metodo que introduce un cambio de line en el contenedor
     * @param contenedor  Contenedor al cual se le desea ingresar un cambio de linea
     */
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

    /**
     * Metodo que detecta si una expresion contiene un punto
     * @param valorLexema String a analizar
     * @return Verdadero en caso contenga un punto
     */
    private boolean tienePunto(String valorLexema) {
        boolean retorno = false;
        if(valorLexema.lastIndexOf(".") > 0){
            retorno = true;
        }
        return retorno;
    }
    /**
     *  Metodo para obtener la lista de errore
     * @return La lista de errores
     */
        public ArrayList<String> getError() {
        return error;
    }

    /**
     *  Metodo para obtener los lexemas
     * @return La lista de lexemas
     */
    public ArrayList<String> getLexema() {
        return lexema;
    }

    /**
     * Metodo para obtener la lista de tipos
     * @return La lista de tipos
     */
    public ArrayList<String> getTipo() {
        return tipo;
    }
    
    /**
     *  Metodo para obtener la lista de posiciones para los lexemas
     * @return Lista de posiciones de los lexemas
     */
    public ArrayList<Integer> getPosicionFila() {
        return posicionFila;
    }

    /**
     * Metodo para obtener la lista de posiciones de Columna para los lexemas
     * @return Lista de posiciones de Columna para lexemas
     */
    public ArrayList<Integer> getPosicionColumna() {
        return posicionColumna;
    }

    /**
     *  Metodo para obtener la lista de posiciones de fila para los errores
     * @return Lista de posiciones de Filas de los errores
     */
    public ArrayList<Integer> getPosicionFilaError() {
        return posicionFilaError;
    }

    /**
     * Metodo para obtener la lista de columnas de los errores
     * @return Lista de posiciones en columna de los errores
     */
    public ArrayList<Integer> getPosicionColumnaError() {
        return posicionColumnaError;
    }

    /**
     * Metodo para recuperar los tokens
     * @return Lista de tokens.
     */
    public ArrayList<Integer> getToken() {
        return token;
    }

    
}
