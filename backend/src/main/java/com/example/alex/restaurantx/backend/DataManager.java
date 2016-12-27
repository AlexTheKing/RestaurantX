package com.example.alex.restaurantx.backend;

import com.example.alex.restaurantx.backend.callbacks.IResultCallback;
import com.example.alex.restaurantx.backend.database.DatabaseHelper;
import com.example.alex.restaurantx.backend.database.models.CommentModel;
import com.example.alex.restaurantx.backend.database.models.DishModel;
import com.example.alex.restaurantx.backend.database.models.RateModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class DataManager {

    private static final String sErrorJsonTemplate = "{response:{\"error\":\"%s\"}}";
    private static final String errorResponse = String.format(sErrorJsonTemplate, Constants.JsonSettings.ERROR_REASON);

    static void getTypes(IResultCallback<String> callback) {
        IResultCallback<ResultSet> transactionCallback = new IResultCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet resultSet) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode rootObj = mapper.createObjectNode();
                ObjectNode responseObj = rootObj.putObject(Constants.JsonSettings.RESPONSE);
                ArrayNode typesArray = responseObj.putArray(Constants.JsonSettings.TYPES);
                List<String> typesList = new ArrayList<>();
                try {
                    while (resultSet.next()) {
                        final String type = resultSet.getString(DishModel.TYPE);
                        if (!typesList.contains(type)) {
                            typesList.add(type);
                            typesArray.add(type);
                        }
                    }
                    String jsonResponse = rootObj.toString();
                    callback.onSuccess(jsonResponse);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    callback.onSuccess(errorResponse);
                }
            }

            @Override
            public void onError(Exception ex) {
                callback.onSuccess(errorResponse);
                ex.printStackTrace();
            }
        };

        try {
            DatabaseHelper.getInstance(Constants.DATABASE_PATH).queryAsync(transactionCallback, DishModel.class, DatabaseHelper.sUniversalQuantificator, null);
        } catch (SQLException ex) {
            callback.onSuccess(errorResponse);
            ex.printStackTrace();
        }
    }

    static void getDishes(IResultCallback<String> callback) {
        IResultCallback<ResultSet> transactionCallback = new IResultCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet resultSet) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode rootObj = mapper.createObjectNode();
                ObjectNode responseObj = rootObj.putObject(Constants.JsonSettings.RESPONSE);
                List<String> typesList = new ArrayList<>();
                try {
                    while (resultSet.next()) {
                        final String name = resultSet.getString(DishModel.NAME);
                        final String type = resultSet.getString(DishModel.TYPE);
                        final String weight = resultSet.getString(DishModel.WEIGHT);
                        final float cost = resultSet.getFloat(DishModel.COST);
                        final String currency = resultSet.getString(DishModel.CURRENCY);
                        final String description = resultSet.getString(DishModel.DESCRIPTION);
                        final String ingredientsAsString = resultSet.getString(DishModel.INGREDIENTS);
                        final float averageEstimation = resultSet.getFloat(DishModel.AVERAGE_ESTIMATION);
                        final String bitmapUrl = resultSet.getString(DishModel.BITMAP_URL);
                        if (!typesList.contains(type)) {
                            typesList.add(type);
                            responseObj.putArray(type);
                        }
                        ArrayNode arrayNode = ((ArrayNode) responseObj.get(type));
                        ObjectNode dishObj = arrayNode.addObject();
                        dishObj.put(DishModel.NAME, name);
                        dishObj.put(DishModel.COST, cost);
                        dishObj.put(DishModel.CURRENCY, currency);
                        dishObj.put(DishModel.WEIGHT, weight);
                        dishObj.put(DishModel.DESCRIPTION, description);
                        dishObj.put(DishModel.AVERAGE_ESTIMATION, averageEstimation);
                        dishObj.put(DishModel.BITMAP_URL, bitmapUrl);
                        ArrayNode ingredientsArray = dishObj.putArray(DishModel.INGREDIENTS);
                        for (String ingredient : ingredientsAsString.split(Constants.JsonSettings.INGREDIENTS_SPLITTER)) {
                            ingredientsArray.add(ingredient);
                        }
                    }
                    String jsonResponse = rootObj.toString();
                    callback.onSuccess(jsonResponse);
                } catch (SQLException ex) {
                    callback.onSuccess(errorResponse);
                    ex.printStackTrace();
                }
            }

            @Override
            public void onError(Exception ex) {
                callback.onSuccess(errorResponse);
                ex.printStackTrace();
            }
        };

        try {
            DatabaseHelper.getInstance(Constants.DATABASE_PATH).queryAsync(transactionCallback, DishModel.class, DatabaseHelper.sUniversalQuantificator, null);
        } catch (SQLException ex) {
            callback.onSuccess(errorResponse);
            ex.printStackTrace();
        }
    }

    static void addRate(String dishName, String instanceId, int rate, IResultCallback<String> callback) {
        try {
            final DatabaseHelper helper = DatabaseHelper.getInstance(Constants.DATABASE_PATH);
            helper.createTables(new Class<?>[]{RateModel.class}, null);

            final IResultCallback<ResultSet> updateAverageEstimation = new IResultCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) {
                    int averageRating = 0;
                    int counter = 0;
                    try {
                        while (resultSet.next()) {
                            averageRating += resultSet.getInt(RateModel.RATING);
                            counter++;
                        }
                        HashMap<String, String> setValues = new HashMap<>();
                        String average = String.valueOf(((float) averageRating) / counter);
                        setValues.put(DishModel.AVERAGE_ESTIMATION, average);
                        helper.updateAsync(DishModel.class, setValues, DishModel.NAME + " = " + DatabaseHelper.getSqlString(dishName), null);
                        callback.onSuccess(average);
                    } catch (SQLException ex) {
                        callback.onSuccess(errorResponse);
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    callback.onSuccess(errorResponse);
                    ex.printStackTrace();
                }
            };

            final IResultCallback<Integer> insertRatingOfUser = new IResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    helper.queryAsync(updateAverageEstimation, RateModel.class,
                            RateModel.RATING,
                            RateModel.DISH_NAME + " = " + DatabaseHelper.getSqlString(dishName));
                }

                @Override
                public void onError(Exception ex) {
                    callback.onSuccess(errorResponse);
                    ex.printStackTrace();
                }
            };

            final IResultCallback<ResultSet> checkDishForExistance = new IResultCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) {
                    try {
                        if (resultSet.next()) {
                            final String whereClause = RateModel.INSTANCE_ID + " = " + DatabaseHelper.getSqlString(instanceId) + " AND " + RateModel.DISH_NAME + " = " + DatabaseHelper.getSqlString(dishName);
                            helper.queryAsync(new IResultCallback<ResultSet>() {
                                                  @Override
                                                  public void onSuccess(ResultSet resultSet) {
                                                      try {
                                                          HashMap<String, String> values = new HashMap<>();
                                                          values.put(RateModel.INSTANCE_ID, DatabaseHelper.getSqlString(instanceId));
                                                          values.put(RateModel.RATING, String.valueOf(rate));
                                                          values.put(RateModel.DISH_NAME, DatabaseHelper.getSqlString(dishName));
                                                          if (resultSet.next()) {
                                                              helper.updateAsync(RateModel.class, values, whereClause, insertRatingOfUser);
                                                          } else {
                                                              helper.insertAsync(RateModel.class, values, insertRatingOfUser);
                                                          }
                                                      } catch (SQLException ex) {
                                                          callback.onSuccess(errorResponse);
                                                          ex.printStackTrace();
                                                      }
                                                  }

                                                  @Override
                                                  public void onError(Exception ex) {
                                                      callback.onSuccess(errorResponse);
                                                      ex.printStackTrace();
                                                  }
                                              }, RateModel.class,
                                    RateModel.INSTANCE_ID + ", " + RateModel.DISH_NAME,
                                    whereClause);
                        }
                    } catch (SQLException ex) {
                        callback.onSuccess(errorResponse);
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    callback.onSuccess(errorResponse);
                    ex.printStackTrace();
                }
            };
            helper.queryAsync(checkDishForExistance, DishModel.class, DishModel.AVERAGE_ESTIMATION, DishModel.NAME + " = " + DatabaseHelper.getSqlString(dishName));
        } catch (SQLException ex) {
            callback.onSuccess(errorResponse);
            ex.printStackTrace();
        }
    }

    static void getComments(String dishName, IResultCallback<String> callback) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootObj = mapper.createObjectNode();
            ObjectNode responseObj = rootObj.putObject(Constants.JsonSettings.RESPONSE);
            ArrayNode commentsArray = responseObj.putArray(Constants.JsonSettings.COMMENTS);
            final DatabaseHelper helper = DatabaseHelper.getInstance(Constants.DATABASE_PATH);
            helper.createTables(new Class<?>[]{CommentModel.class}, null);
            helper.queryAsync(new IResultCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) {
                    try {
                        while (resultSet.next()) {
                            commentsArray.add(resultSet.getString(CommentModel.COMMENT));
                        }
                        callback.onSuccess(rootObj.toString());
                    } catch (SQLException ex) {
                        callback.onSuccess(errorResponse);
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    callback.onSuccess(errorResponse);
                    ex.printStackTrace();
                }
            }, CommentModel.class, CommentModel.COMMENT, CommentModel.DISH_NAME + " = " + DatabaseHelper.getSqlString(dishName));
        } catch (SQLException ex) {
            callback.onSuccess(errorResponse);
            ex.printStackTrace();
        }
    }

    static void addComment(final String dishName, final String instanceId, final String comment, final IResultCallback<String> callback) {
        try {
            final DatabaseHelper helper = DatabaseHelper.getInstance(Constants.DATABASE_PATH);
            helper.createTables(new Class<?>[]{CommentModel.class}, null);
            HashMap<String, String> values = new HashMap<>();
            values.put(CommentModel.DISH_NAME, DatabaseHelper.getSqlString(dishName));
            values.put(CommentModel.INSTANCE_ID, DatabaseHelper.getSqlString(instanceId));
            values.put(CommentModel.COMMENT, DatabaseHelper.getSqlString(comment));
            helper.queryAsync(new IResultCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet resultSet) {
                    try {
                        if (resultSet.next()) {
                            helper.insertAsync(CommentModel.class, values, new IResultCallback<Integer>() {
                                @Override
                                public void onSuccess(Integer integer) {
                                    getComments(dishName, new IResultCallback<String>() {
                                        @Override
                                        public void onSuccess(String response) {
                                            callback.onSuccess(response);
                                        }

                                        @Override
                                        public void onError(Exception ex) {
                                            callback.onSuccess(errorResponse);
                                            ex.printStackTrace();
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception ex) {
                                    callback.onSuccess(errorResponse);
                                    ex.printStackTrace();
                                }
                            });
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        callback.onSuccess(errorResponse);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    callback.onSuccess(errorResponse);
                    ex.printStackTrace();
                }
            }, DishModel.class, DishModel.NAME, DishModel.NAME + " = " + DatabaseHelper.getSqlString(dishName));
        } catch (SQLException ex) {
            callback.onSuccess(errorResponse);
            ex.printStackTrace();
        }
    }
}
