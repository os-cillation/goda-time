/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.goda.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kebernet
 */
public class CollectionUtils {


    public static <T> List<T> subList(List<T> src, int start, int end) {
        ArrayList<T> result = new ArrayList<T>();
        for(int i=start; i <= end && i < src.size(); i++ ){
            result.add(src.get(i));
        }
        return result;
    }

}
