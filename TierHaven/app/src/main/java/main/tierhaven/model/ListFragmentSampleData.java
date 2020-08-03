package main.tierhaven.model;

import java.util.LinkedList;
import java.util.List;

import main.tierhaven.model.Category;
import main.tierhaven.model.TierList;

public class ListFragmentSampleData {


    public ListFragmentSampleData(){

    }


    public List<Category> getCategories(){
        List<Category> categories = new LinkedList<>();
        categories.add(new Category(0, "All"));
        categories.add(new Category(1,"Toys"));
        categories.add(new Category(2, "Movies"));
        categories.add(new Category(3, "Food"));
        categories.add(new Category(4, "Games"));
        categories.add(new Category(5, "Programming Languages"));
        categories.add(new Category(6,"Cartoon"));
        categories.add(new Category(7,"Shows"));

        return categories;
    }

}
