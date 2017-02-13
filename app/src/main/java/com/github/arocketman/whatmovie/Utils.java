package com.github.arocketman.whatmovie;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;

/**
 * Created by Andreuccio on 04/02/2017.
 */

public class Utils {

    /**
     * Merges together array l1 and l2.
     * @param l1 the first array
     * @param l2 the second array will be concatenated to l1
     * @param <T>
     * @return concatenation of l1 with l2.
     */
    public static <T> ArrayList<T> concatenate(ArrayList<T> l1 , ArrayList<T> l2){
        for(T elem : l2)
            l1.add(elem);
        return l1;
    }

    /**
     * Builds the swipeview with specific characteristics
     * @param mSwipeView
     */
    static void buildSwipeView(SwipePlaceHolderView mSwipeView) {
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setWidthSwipeDistFactor(2f)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(0)
                        .setRelativeScale(0.01f));
    }
}
