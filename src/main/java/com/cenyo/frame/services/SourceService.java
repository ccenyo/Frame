package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.source.LocalSource;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.entities.source.SourceDTO;
import com.cenyo.frame.entities.source.SynologySource;
import com.cenyo.frame.repositories.LocalSourceRepository;
import com.cenyo.frame.repositories.SourceRepository;
import com.cenyo.frame.repositories.SynologySourceRepository;
import exeptions.DsmLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import requests.DsmAuth;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SourceService {

    @Autowired
    public SourceRepository sourceRepository;

    @Autowired
    public LocalSourceRepository localSourceRepository;

    @Autowired
    public SynologySourceRepository synologySourceRepository;

    @Transactional
    public List<SourceDTO> getAllSources() {
        List<SourceDTO> sourceDTOS = new ArrayList<>();
        List<Source> sources = sourceRepository.findAll();

        sourceDTOS.addAll(sources.stream().filter(source -> source instanceof LocalSource)
                .map(source -> SourceDTO.fromLocalSource((LocalSource) source))
                .collect(Collectors.toList()));

        sourceDTOS.addAll(sources.stream().filter(source -> source instanceof SynologySource)
                .map(source -> SourceDTO.fromSynologySource((SynologySource) source))
                .collect(Collectors.toList()));

        return sourceDTOS;
    }

    public boolean isPathValid(SourceDTO source) {
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

    public boolean tryConnect(SourceDTO source) {
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
    public Source save(SourceDTO source) {
       return switch (source.getType()) {
            case Local -> localSourceRepository.save((LocalSource) source.toSource());
            case Synology -> synologySourceRepository.save((SynologySource) source.toSource());
        };
    }
}
