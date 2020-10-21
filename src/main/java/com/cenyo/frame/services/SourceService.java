package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.repositories.SourceRepository;
import exeptions.DsmLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import requests.DsmAuth;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

@Service
public class SourceService {

    @Autowired
    public SourceRepository sourceRepository;

    @Transactional
    public List<Source> getAllSources() {
        return sourceRepository.findAll();
    }

    public boolean isPathValid(Source source) {
        switch (source.getType()) {
            case Local: return new File(source.getRootFolder()).exists() && new File(source.getRootFolder()).isDirectory();
            case Synology: {
                try {
                  DsmFileStationClient client =  DsmFileStationClient.login(DsmAuth.of(source.getHost(), source.getPort(), source.getUserName(), source.getPassword()));
                  return client.exists(source.getRootFolder());
                } catch (DsmLoginException e) {
                    return false;
                }
           }
       }
       return false;
    }

    public boolean tryConnect(Source source) {
         switch (source.getType()) {
            case Local: return true;
             case Synology:
                try {
                     DsmFileStationClient.login(DsmAuth.of(source.getHost(), source.getPort(), source.getUserName(), source.getPassword()));
                    return true;
                } catch (Exception e) {
                    return false;
                }
        }
        return false;
    }

    @Transactional
    public void save(Source source) {
        sourceRepository.save(source);
    }
}
