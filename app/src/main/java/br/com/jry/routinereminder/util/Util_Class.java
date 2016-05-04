package br.com.jry.routinereminder.util;

import java.util.*;
import java.text.*;

/**
 * Created by Yuri on 02/05/2016. Teste GIT
 */
public class Util_Class {

    /**
     * Metodo para pegar a data e a hora no sistema.
     */

        private String getDateTime() {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            return dateFormat.format(date);
        }





}
