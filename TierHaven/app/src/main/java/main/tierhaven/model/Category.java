package main.tierhaven.model;

/**
 * 
 */

import java.util.LinkedList;
import java.util.List;


/**
 * A Category holds a collection of Tier Lists that can all be put under the same umbrella term
 */

public class Category {

    private int id;
    private String name;
    private boolean isEmpty;


    /**
     * Empty Constructor
     */
    public Category (){
        id = 0;
        name = "";
        isEmpty=true;
    }

    public Category (int id, String name){
        this.id=id;
        this.name=name;
        isEmpty=true;
    }


    // GETTERS & SETTERS

    /**
     *
     * @return
     */
    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
    }

    public boolean isCategoryEmpty(){
        return this.isEmpty;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
