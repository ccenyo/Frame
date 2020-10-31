package com.cenyo.frame.controllers;

import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.entities.source.SourceDTO;
import com.cenyo.frame.services.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/source")
@CrossOrigin(origins = "http://localhost:4200")
public class SourceController {

    @Autowired
    public SourceService sourceService;

    @RequestMapping("/getAll")
    public List<SourceDTO> getAllSource() {
        return sourceService.getAllSources();
    }

    @RequestMapping("/checkFolder")
    public boolean isPathExist(@RequestBody SourceDTO source) {
        return sourceService.isPathValid(source);
    }

    @RequestMapping("/tryConnexion")
    public boolean tryConnexion(@RequestBody SourceDTO source) {
        return sourceService.tryConnect(source);
    }

    @RequestMapping("/save")
    public Source save(@RequestBody SourceDTO source) {
       return sourceService.save(source);
    }
}
