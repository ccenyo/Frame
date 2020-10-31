package com.cenyo.frame.services;

import clients.DsmFileStationClient;
import com.cenyo.frame.entities.FrameFile;
import com.cenyo.frame.entities.FrameFileMeta;
import com.cenyo.frame.entities.source.Source;
import com.cenyo.frame.repositories.FrameFileMetaDataRepository;
import com.cenyo.frame.repositories.FrameFileRepository;

public interface ISourceService {

    void extractContentsFromSource(Source source, String currentPath, FrameFile parent,  DsmFileStationClient client, FrameFileRepository frameFileRepository, FrameFileMetaDataRepository frameFileMetaDataRepository);

    FrameFileMeta getMetaData(FrameFile frameFile, String rootFolder, DsmFileStationClient client,  FrameFileMetaDataRepository frameFileMetaDataRepository);
}
