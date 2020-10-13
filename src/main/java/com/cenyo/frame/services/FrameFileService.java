package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.repositories.FrameFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import requests.DsmAuth;
import responses.Response;
import responses.filestation.DsmResponseFields;
import responses.filestation.lists.DsmListFolderResponse;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.Random;

@Service
public class FrameFileService {

    private String url;

    @Autowired
    public FrameFileRepository frameFileRepository;

    //@Scheduled(cron = "0 */1 * ? * *")
    @Transactional
    public void updateBase() {
        DsmFileStationClient client = DsmFileStationClient.login(DsmAuth.fromResource("env.properties"));
        Response<DsmListFolderResponse> response = client.ls("/homes/cenyo/Drive/Moments/Web/2019-08-15").call();
        List<DsmResponseFields.Files> files = response.getData().getFiles();
        Random r = new Random();
        DsmResponseFields.Files f =  files.get(r.nextInt(files.size()));
        File destinationFolder = new File("img");
        if(!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }
        client.download(f.getPath(), "img").call();
        url = "img/"+f.getName();

    }

    public String getUrl() {
        updateBase();
        return new File(url).getAbsolutePath();
    }
}
