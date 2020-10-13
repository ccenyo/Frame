package com.cenyo.frame.controllers;

import com.cenyo.frame.services.FrameFileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Base64;


@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "http://localhost:4200")
public class ImageController {

    @Autowired
    public FrameFileService frameFileService;

    @RequestMapping("/current")
    public String getAllFolders() throws IOException {

        byte[] fileContent = FileUtils.readFileToByteArray(new File(frameFileService.getUrl()));
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
