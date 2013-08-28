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
                    valorLexema += letra; 
                    lexema.add(valorLexema);
                    tipo.add("Referencia");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, expresionRegular);
                    posicionFila.add(fila);
                    posicionColumna.add(columna);
                    modificarColumna(valorLexema.length());
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
                    token.add(20);
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
                    token.add(20);
                    lexema.add(valorLexema);
                    tipo.add("Palabra Reservada");
                    hacerCambio(contenedor);
                    contenedor.insertString(contenedor.getLength(),valorLexema, palabraReservada);
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

    private void imprimirResultados() {
        System.out.println("*****************");
        for(int i = 0 ; i < lexema.size() ; i++){
            System.out.println(lexema.get(i) + "    " + posicionFila.get(i) + "         "+posicionColumna.get(i) + "     "  + token.get(i));
        }
        
    }

    private String limpiarCadena(String entrada) {
        return entrada.replaceAll("[\n\r\t]", "") + " " + " ";
        
       
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

    private boolean tienePunto(String valorLexema) {
        boolean retorno = false;
        if(valorLexema.lastIndexOf(".") > 0){
            retorno = true;
        }
        return retorno;
    }
    
        public ArrayList<String> getError() {
        return error;
    }

    public ArrayList<String> getLexema() {
        return lexema;
    }

    public ArrayList<String> getTipo() {
        return tipo;
    }

    public ArrayList<Integer> getPosicionFila() {
        return posicionFila;
    }

    public ArrayList<Integer> getPosicionColumna() {
        return posicionColumna;
    }

    public ArrayList<Integer> getPosicionFilaError() {
        return posicionFilaError;
    }

    public ArrayList<Integer> getPosicionColumnaError() {
        return posicionColumnaError;
    }

    public ArrayList<Integer> getToken() {
        return token;
    }

    
}
