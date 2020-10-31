package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.entities.FrameFileMeta;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.repositories.FrameFileMetaDataRepository;
import com.cenyo.frame.repositories.FrameFileRepository;
import org.springframework.stereotype.Service;
import requests.filestation.DsmRequestParameters;
import requests.filestation.action.DsmDirSizeRequest;
import responses.Response;
import responses.filestation.DsmResponseFields;
import responses.filestation.action.DsmDirSizeResponse;
import responses.filestation.lists.DsmListFolderResponse;
import utils.DsmUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SynologySourceService extends SourceService implements ISourceService{

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void extractContentsFromSource(Source source, String currentPath, FrameFile parent,  DsmFileStationClient client, FrameFileRepository frameFileRepository, FrameFileMetaDataRepository frameFileMetaDataRepository) {
        try {
            FrameFile frameFile = new FrameFile();
            if(frameFileRepository.findByRemotePath(currentPath).isEmpty()) {

                if(client.exists(currentPath)) {
                    frameFile.setName(currentPath.substring(currentPath.lastIndexOf("/")+1));
                    frameFile.setSource(source);
                    frameFile.setFeatured(false);
                    frameFile.setParent(parent);
                    frameFile.setType(FrameFile.Type.DIRECTORY);
                    frameFile.setRemotePath(currentPath);
                    frameFile.setFrameFileMeta(getMetaData(frameFile, currentPath, client, frameFileMetaDataRepository));
                    frameFileRepository.save(frameFile);
                }
            } else {
                frameFile = frameFileRepository.findByRemotePath(currentPath).stream().findAny().orElseThrow();
            }
            Response<DsmListFolderResponse> listFolderResponse = client.ls(currentPath).call();
            if(listFolderResponse.isSuccess()) {
                List<DsmResponseFields.Files> listFiles = listFolderResponse.getData().getFiles();

                if (listFiles.size() > 0) {
                    for (DsmResponseFields.Files file : listFiles) {
                        if (file.isIsdir()) {
                            extractContentsFromSource(source, file.getPath(), parent, client, frameFileRepository, frameFileMetaDataRepository);
                        } else if (isFileAccepted(file.getName()) && frameFileRepository.findByRemotePath(file.getPath()).isEmpty()) {
                            FrameFile childFile = new FrameFile();
                            childFile.setName(file.getName());
                            childFile.setSource(source);
                            childFile.setFeatured(false);
                            childFile.setParent(frameFile);
                            if (isPicture(childFile.getName())) {
                                childFile.setType(FrameFile.Type.PICTURE);
                            } else {
                                childFile.setType(FrameFile.Type.VIDEO);
                            }
                            childFile.setRemotePath(file.getPath());
                            frameFileRepository.save(childFile);
                            childFile.setFrameFileMeta(getMetaData(childFile, file.getPath(), client, frameFileMetaDataRepository));
                            frameFile.getChildren().add(childFile);
                            LOGGER.info("Adding new file " + childFile.getRemotePath());
                            frameFileRepository.save(childFile);
                        }
                    }
                    frameFileRepository.save(frameFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public FrameFileMeta getMetaData(FrameFile frameFile, String rootFolder, DsmFileStationClient client, FrameFileMetaDataRepository frameFileMetaDataRepository) {

        DsmResponseFields.Files file = getFileInfo(client, rootFolder);

        FrameFileMeta frameFileMeta = new FrameFileMeta();
        frameFileMeta.setLastModifiedTimeStamp(file.getAdditional().getTime().getMtime());
        frameFileMeta.setCreateTimeStamp(file.getAdditional().getTime().getCrtime());
        frameFileMeta.setLastAccessTimeStamp(file.getAdditional().getTime().getAtime());
        frameFileMeta.setLastChangeTimeStamp(file.getAdditional().getTime().getCtime());
        frameFileMeta.setFrameFile(frameFile);

        if(!file.isIsdir()) {
            frameFileMeta.setSize(file.getAdditional().getSize());
            frameFileMeta.setType(file.getName().substring(file.getName().lastIndexOf(".")+1));
        } else {
            DsmDirSizeRequest dsmDirSizeRequest =  client.getSize(rootFolder);
            Response<DsmDirSizeResponse> dirSizeResponse = dsmDirSizeRequest.start();
            Response<DsmDirSizeResponse> resultResponse;
            do {
                resultResponse = dsmDirSizeRequest.status(dirSizeResponse.getData().getTaskid());
            } while (!resultResponse.getData().isFinished());

            if(resultResponse.isSuccess()) {
                frameFileMeta.setSize(resultResponse.getData().getTotal_size());
            }
        }
        frameFileMetaDataRepository.save(frameFileMeta);
        return frameFileMeta;
    }


    DsmResponseFields.Files getFileInfo(DsmFileStationClient client, String filePath) {
        Optional<DsmResponseFields.Files> any = client.ls(DsmUtils.extractRootFolderPath(filePath))
                .addAdditionalInfo(DsmRequestParameters.Additional.TIME)
                .call()
                .getData()
                .getFiles()
                .stream().filter(f -> f.getName().equals(DsmUtils.extractFileName(filePath)))
                .findAny();

        return any.orElseThrow();
    }

}
