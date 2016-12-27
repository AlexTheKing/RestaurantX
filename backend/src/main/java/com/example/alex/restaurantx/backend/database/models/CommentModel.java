package com.example.alex.restaurantx.backend.database.models;

import com.example.alex.restaurantx.backend.database.annotations.Table;
import com.example.alex.restaurantx.backend.database.annotations.dbText;

@Table("comments")
public class CommentModel {

    @dbText
    public static final String DISH_NAME = "dish_name";

    @dbText
    public static final String INSTANCE_ID = "instance_id";

    @dbText
    public static final String COMMENT = "comment";

}
