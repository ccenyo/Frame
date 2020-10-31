package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.entities.FrameFileMeta;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.repositories.FrameFileMetaDataRepository;
import com.cenyo.frame.repositories.FrameFileRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class LocalSourceService extends SourceService implements ISourceService{


    @Override
    public void extractContentsFromSource(Source source, String currentPath, FrameFile parent,  DsmFileStationClient client, FrameFileRepository frameFileRepository, FrameFileMetaDataRepository frameFileMetaDataRepository) {
        try {
            File rootFolder = new File(currentPath);
            FrameFile frameFile = new FrameFile();
            if(frameFileRepository.findByRemotePath(rootFolder.getAbsolutePath()).isEmpty()) {
                frameFile.setName(rootFolder.getName());
                frameFile.setSource(source);
                frameFile.setFeatured(false);
                frameFile.setParent(parent);
                frameFile.setType(FrameFile.Type.DIRECTORY);
                frameFile.setRemotePath(rootFolder.getAbsolutePath());
                frameFile.setFrameFileMeta(getMetaData(frameFile, rootFolder.getAbsolutePath(), client, frameFileMetaDataRepository));
                frameFileRepository.save(frameFile);
            } else {
                frameFile = frameFileRepository.findByRemotePath(rootFolder.getAbsolutePath()).stream().findAny().orElseThrow();
            }

            List<File> listFiles = Arrays.asList(Objects.requireNonNull(rootFolder.listFiles()));
            if (listFiles.size() > 0 ) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        extractContentsFromSource(source, file.getAbsolutePath(), frameFile, client, frameFileRepository, frameFileMetaDataRepository);
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
                        childFile.setFrameFileMeta(getMetaData(childFile, file.getAbsolutePath(), client, frameFileMetaDataRepository));
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

    @Override
    public FrameFileMeta getMetaData(FrameFile frameFile, String rootFolderPath, DsmFileStationClient client,  FrameFileMetaDataRepository frameFileMetaDataRepository) {
        File rootFolder = new File(rootFolderPath);
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


}
