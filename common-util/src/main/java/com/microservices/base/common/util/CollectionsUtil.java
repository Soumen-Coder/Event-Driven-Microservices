package com.microservices.base.common.util;

import java.util.ArrayList;
import java.util.List;
//We could have made this spring component so that it can injected and used easily but we don't want to include spring dependency for this generic module
//As an alternative we can use static method to ease in access to the method without  creating object each time we need this utility
//Let's do it via Singleton design pattern
//Singleton design pattern -> Initialization on-demand holder provides thread safe and lazy approach. Create single instance lazily.
public class CollectionsUtil {
    //No Constructors allowed to create object each time it is needed
    private CollectionsUtil(){
    }

    //Static inner class
    private static class CollectionsUtilHolder {
        static final CollectionsUtil INSTANCE = new CollectionsUtil();
    }

    //Called directly on the CollectionsUtil class, instead of creating an instance
    public static CollectionsUtil getInstance(){
         return CollectionsUtilHolder.INSTANCE; //Instance will not be created until we call the getInstance method
    }

    //Generic type, can be used for any type
    public <T> List<T> getListFromIterable(Iterable<T> iterable){
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
