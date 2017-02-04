package com.github.arocketman.whatmovie;

import java.util.ArrayList;

/**
 * Created by Andreuccio on 04/02/2017.
 */

public class Utils {

    public static <T> ArrayList<T> concatenate(ArrayList<T> l1 , ArrayList<T> l2){
        for(T elem : l2)
            l1.add(elem);
        return l1;
    }

}
