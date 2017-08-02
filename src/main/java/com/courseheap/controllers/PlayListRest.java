package com.courseheap.controllers;

/**
 * Created by ashish.p on 2/8/17.
 */

import com.courseheap.services.PlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletionStage;

@RestController
public class PlayListRest {

    @Autowired
    PlayListService playListService;

    @GetMapping(value = "/private/getPlaylist/{courseId}", produces = {"application/json"})
    @ResponseBody
    public CompletionStage<ResponseEntity<String>> getPLayListsByCourse(@PathVariable("courseId") Long id) {
        return playListService.getPLayListsByCourse(id);
    }

    @GetMapping(value = "/getPlaylist/{courseId}", produces = {"application/json"})
    @ResponseBody
    public CompletionStage<ResponseEntity<String>> getAllPLayLists(@PathVariable("courseId") Long id) {
        return playListService.getPLayListsByCourse(id);
    }
}
