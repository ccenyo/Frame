package com.cenyo.frame.services;

import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.entities.FrameFileMeta;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.repositories.FrameFileMetaDataRepository;
import com.cenyo.frame.repositories.FrameFileRepository;
import com.cenyo.frame.repositories.SourceRepository;
import com.cenyo.frame.utils.ImageDominantColor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FrameFileService {

    private String url;

    @Autowired
    public FrameFileRepository frameFileRepository;

    @Autowired
    public FrameFileMetaDataRepository frameFileMetaDataRepository;

    @Autowired
    public SourceService sourceService;

    @Autowired
    public SourceRepository sourceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameFileService.class);

    @Scheduled(cron = "0 */10 * ? * *")
    @Transactional
    public void updateBase() {
        sourceRepository.findAll().forEach(this::handleSource);
    }

    public void handleSource(Source source) {

        if (source.getType() == Source.Type.Local) {
            if(source.getRootFolder() != null && !source.getRootFolder().isEmpty()) {
                File root = new File(source.getRootFolder());
                lookForFiles(root, source);
            }
        }
    }

    @Transactional
    public void lookForFiles(File rootFolder,  Source source) {

        try {
            FrameFile frameFile = new FrameFile();
            if(frameFileRepository.findByRemotePath(rootFolder.getAbsolutePath()).isEmpty()) {
                frameFile.setName(rootFolder.getName());
                frameFile.setSource(source);
                frameFile.setFeatured(false);
                frameFile.setType(FrameFile.Type.DIRECTORY);
                frameFile.setRemotePath(rootFolder.getAbsolutePath());
                frameFile.setFrameFileMeta(getMetaData(frameFile, rootFolder));
                frameFileRepository.save(frameFile);
            } else {
                frameFile = frameFileRepository.findByRemotePath(rootFolder.getAbsolutePath()).stream().findAny().get();
            }

            List<File> listFiles = Arrays.asList(Objects.requireNonNull(rootFolder.listFiles()));
            if (listFiles.size() > 0 ) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        lookForFiles(file, source);
                    } else if (isFileAccepted(file.getName()) && frameFileRepository.findByRemotePath(file.getAbsolutePath()).isEmpty()) {
                        FrameFile childFile = new FrameFile();
                        childFile.setName(file.getName());
                        childFile.setSource(source);
                        childFile.setFeatured(false);
                        childFile.setParent(frameFile);
                        if(isPicture(childFile.getName())) {
                            childFile.setType(FrameFile.Type.PICTURE);
                        } else {
                            childFile.setType(FrameFile.Type.VIDEO);
                        }
                        childFile.setRemotePath(file.getAbsolutePath());
                        frameFileRepository.save(childFile);
                        childFile.setFrameFileMeta(getMetaData(childFile, file));
                        frameFile.getChildren().add(childFile);
                        LOGGER.info("Adding new file "+childFile.getRemotePath());
                        frameFileRepository.save(childFile);
                    }
                }
                frameFileRepository.save(frameFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isPicture(String name) {
        for(String extension : FrameFile.pictureExtensions) {
            if(name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFileAccepted(String name) {
        for(String extension : FrameFile.pictureExtensions) {
            if(name.endsWith(extension)) {
                return true;
            }
        }
        for(String extension : FrameFile.videoExtensions) {
            if(name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    @Transactional
    public FrameFileMeta getMetaData(FrameFile frameFile, File rootFolder) {
        FrameFileMeta frameFileMeta = new FrameFileMeta();
        frameFileMeta.setLastModifiedTimeStamp(rootFolder.lastModified());
        frameFileMeta.setFrameFile(frameFile);
        frameFileMeta.setSize(rootFolder.getTotalSpace());
        if(rootFolder.isFile()) {
            frameFileMeta.setType(rootFolder.getName().substring(rootFolder.getName().lastIndexOf(".")+1));
        }
        frameFileMetaDataRepository.save(frameFileMeta);
        return frameFileMeta;
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
