package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.repositories.FrameFileRepository;
import com.cenyo.frame.utils.ImageDominantColor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import requests.DsmAuth;
import responses.Response;
import responses.filestation.DsmResponseFields;
import responses.filestation.lists.DsmListFolderResponse;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class FrameFileService {

    private String url;

    @Autowired
    public FrameFileRepository frameFileRepository;

    @Autowired
    public SourceService sourceService;

    //@Scheduled(cron = "0 */1 * ? * *")
    @Transactional
    public void updateBase() {

        sourceService.getAllSources().forEach(source -> {

        });
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

    public void handleSource(Source source) {
        if (source.getType() == Source.Type.Local) {

        }
    }

    @Transactional
    public List<FrameFile> getAllFrameFiles() {
        return frameFileRepository.findAll();
    }

    public Optional<FrameFile> selectRandomFile() {
        List<FrameFile> files = getAllFrameFiles();
        if(!files.isEmpty()) {
            Random random = new Random(files.size());
           return Optional.ofNullable(files.get(random.nextInt()));
        }
        return Optional.empty();
    }

    public Optional<String> getUrl() {
        selectRandomFile()
                .ifPresent(file -> {
                    this.url = file.getRemotePath();
                });
        if(url == null) {
            return Optional.empty();
        }
        return  Optional.of(new File(url).getAbsolutePath());
    }

    public String getFile() throws IOException {
        String url = getUrl().orElseThrow();
        File file = new File(url);
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        return getImageSize(file)+"|"+ getImageColors(url)+"|"+Base64.getEncoder().encodeToString(fileContent);
    }

    public String getImageColors(String imageUrl) throws IOException {
        BufferedImage in = ImageIO.read(new File(imageUrl));
        return ImageDominantColor.getHexColor(in).stream().limit(2).collect(Collectors.joining(","));
    }

    private String getImageSize(File file) throws IOException {
        BufferedImage bimg = ImageIO.read(file);
        int width          = bimg.getWidth();
        int height         = bimg.getHeight();
        return width+";"+height;
    }
}
