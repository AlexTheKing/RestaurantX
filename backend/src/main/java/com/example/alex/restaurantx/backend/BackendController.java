package com.example.alex.restaurantx.backend;

import com.example.alex.restaurantx.backend.callbacks.IResultCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BackendController {

    @RequestMapping("/api/types")
    public String getTypes() {
        final String[] response = new String[1];
        DataManager.getTypes(new IResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                response[0] = s;
            }

            @Override
            public void onError(Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
        return response[0];
    }

    @RequestMapping("/api/dishes")
    public String getDishes() {
        final String[] response = new String[1];
        DataManager.getDishes(new IResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                response[0] = s;
            }

            @Override
            public void onError(Exception ex) {
            }
        });
        return response[0];
    }

    @RequestMapping("/api/comments")
    public String getComments(@RequestParam("name") String dishName) {
        final String[] response = new String[1];
        DataManager.getComments(dishName, new IResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                response[0] = s;
            }

            @Override
            public void onError(Exception ex) {
            }
        });
        return response[0];
    }

    @RequestMapping("/api/rate")
    public String addRate(@RequestParam("name") String dishName, @RequestParam("id") String instanceId, @RequestParam("rate") int rate) {
        final String[] response = new String[1];
        DataManager.addRate(dishName, instanceId, rate, new IResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                response[0] = s;
            }

            @Override
            public void onError(Exception ex) {
            }
        });
        return response[0];
    }

    @RequestMapping("/api/comment")
    public String addComment(@RequestParam("name") String dishName, @RequestParam("id") String instanceId, @RequestParam("comment") String comment) {
        final String[] response = new String[1];
        DataManager.addComment(dishName, instanceId, comment, new IResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                response[0] = s;
            }

            @Override
            public void onError(Exception ex) {
            }
        });
        return response[0];
    }
}
