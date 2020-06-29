// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.CommentData;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        Query query = new Query("CommentData").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query); 
      
        String lang = request.getParameter("lang");
        System.out.println(lang);

      
        List<CommentData> commentlog = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String userName = (String) entity.getProperty("userName");
            String userComment = (String) entity.getProperty("userComment");
            long timestamp = (long) entity.getProperty("timestamp");

            String Comment = translateComment(userComment, lang);
            
            System.out.println(Comment);

            CommentData task = new CommentData(id, timestamp, userName, Comment);

            commentlog.add(task);
        }


        // Convert the comments to JSON
        String json = convertToJsonUsingGson(commentlog);

        // Send the JSON as the response
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }
  

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userComment = request.getParameter("userComment");
        String userName = request.getParameter("userName");
        long timestamp = System.currentTimeMillis();

        Entity commentEntity = new Entity("CommentData");
        commentEntity.setProperty("userName", userName);
        commentEntity.setProperty("userComment", userComment);
        commentEntity.setProperty("timestamp", timestamp);
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        // Redirect back to the HTML page.
        response.sendRedirect("/index.html");
    }
    
    private String convertToJsonUsingGson(List<CommentData> commentlog) {
        Gson gson = new Gson();
        String json = gson.toJson(commentlog);
        return json;
    }

    // Do the translation. So that when the function is called the 
    // translated text will display
    private String translateComment(String Comment, String lang) {
        if (lang == null){
            Translate translate = TranslateOptions.getDefaultInstance().getService();
            Translation translation = 
                translate.translate(Comment, Translate.TranslateOption.targetLanguage("en"));
            String translatedText = translation.getTranslatedText();
            System.out.println(Comment);
            return translatedText;
        }

        else{
            System.out.println(Comment);
            Translate translate = TranslateOptions.getDefaultInstance().getService();
            Translation translation = 
                translate.translate(Comment, Translate.TranslateOption.targetLanguage(lang));
            String translatedText = translation.getTranslatedText();
            return translatedText;
        }
    }

}