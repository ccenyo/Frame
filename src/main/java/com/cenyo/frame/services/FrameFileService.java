package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.entities.source.SynologySource;
import com.cenyo.frame.repositories.FrameFileMetaDataRepository;
import com.cenyo.frame.repositories.FrameFileRepository;
import com.cenyo.frame.repositories.SourceRepository;
import com.cenyo.frame.repositories.SynologySourceRepository;
import com.cenyo.frame.utils.HibernateUtil;
import com.cenyo.frame.utils.ImageUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import requests.DsmAuth;
import responses.Response;
import responses.filestation.transfert.DsmDownloadResponse;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FrameFileService {


    @Autowired
    public FrameFileRepository frameFileRepository;

    @Autowired
    public FrameFileMetaDataRepository frameFileMetaDataRepository;

    @Autowired
    public SourceService sourceService;

    @Autowired
    public SourceRepository sourceRepository;

    @Autowired
    public LocalSourceService localSourceService;

    @Autowired
    public SynologySourceService synologySourceService;

    @Autowired
    public SynologySourceRepository synologySourceRepository;

    public static String targetFolder = "src";

    private final   Map<Source.Type, ISourceService> typeISourceService = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameFileService.class);

    private boolean updateBaseRunning = false;

    @PostConstruct
    public void init() {
        typeISourceService.put(Source.Type.Local, localSourceService);
        typeISourceService.put(Source.Type.Synology, synologySourceService);
    }

    //@Scheduled(cron = "0 */60 * ? * *")
    @Transactional
    public void updateBase() {
        if(!updateBaseRunning) {
            updateBaseRunning = true;
            try {
                sourceRepository.findAll().forEach(this::handleSource);
                updateBaseRunning = false;
            } catch (Exception e) {
                updateBaseRunning = false;
            }
        } else {
            LOGGER.info("Update base already running");
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void handleSource(Source source) {
        DsmFileStationClient client = null;
        if(source instanceof SynologySource) {
            SynologySource synologySource = (SynologySource) source;
            client = DsmFileStationClient.login(DsmAuth.of(synologySource.getHost(), synologySource.getPort(), synologySource.getUserName(), synologySource.getPassword()));
        }
        typeISourceService.get(source.getType())
                .extractContentsFromSource(source,source.getRootFolder(), null, client, frameFileRepository, frameFileMetaDataRepository);
    }

    @Transactional
    public List<FrameFile> getAllFrameFiles() {
        return frameFileRepository.findAll();
    }

    public Optional<FrameFile> selectRandomFile() {
        List<FrameFile> files = getAllFrameFiles()
                .stream().filter(frameFile -> frameFile.getType().equals(FrameFile.Type.PICTURE))
                .collect(Collectors.toList());
        if(!files.isEmpty()) {
            Random random = new Random();
           return Optional.ofNullable(files.get(random.nextInt(files.size())));
        }
        return Optional.empty();
    }

    public Optional<String> getUrl() {
        String url = null;

        Optional<FrameFile> optionalSelectedFrameFile = selectRandomFile();
        if(optionalSelectedFrameFile.isPresent()) {
            FrameFile selectedFrameFile = optionalSelectedFrameFile.get();
            url = getFrameFileUtlPath(selectedFrameFile);
            unFeaturedPreviousPicture();
            selectedFrameFile.setFeatured(true);
            frameFileRepository.save(selectedFrameFile);
        }

        if(url == null) {
            return Optional.empty();
        }
        return  Optional.of(new File(url).getAbsolutePath());
    }

    @Transactional
    public void unFeaturedPreviousPicture() {
        frameFileRepository.findFirstByFeaturedIsTrue().ifPresent(previous -> {
            if(previous.getSource().getType().equals(Source.Type.Synology)) {
                File file = new File(FrameFileService.targetFolder+"/"+previous.getName());
                if(file.delete()) {
                    LOGGER.info("File "+file.getAbsolutePath()+" deleted");
                }
            }
            previous.setFeatured(false);
            frameFileRepository.save(previous);
        });
    }

    @Transactional
    public String getFrameFileUtlPath(FrameFile frameFile) {
        return switch (frameFile.getSource().getType()) {
            case Synology -> {
                SynologySource synologySource = synologySourceRepository.getOne(frameFile.getSource().getId());
                new File(targetFolder).mkdir();
                DsmFileStationClient client = DsmFileStationClient.login(DsmAuth.of(synologySource.getHost(), synologySource.getPort(), synologySource.getUserName(), synologySource.getPassword()));
                Response<DsmDownloadResponse> dsmDownloadResponse =  client.download(frameFile.getRemotePath(), targetFolder+"/").call();
                yield dsmDownloadResponse.getData().getFile().getAbsolutePath();
            }
            case Local -> frameFile.getRemotePath();
        };
    }

    public String getFile() throws IOException {
        String url = getUrl().orElseThrow();
        File file = new File(url);
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        return ImageUtils.getImageSize(file)+"|"+ ImageUtils.getImageColors(url)+"|"+Base64.getEncoder().encodeToString(fileContent);
    }
}
