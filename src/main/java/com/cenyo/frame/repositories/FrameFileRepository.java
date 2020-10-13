package com.cenyo.frame.repositories;

import com.cenyo.frame.entities.FrameFile;

import java.util.List;

public interface FrameFileRepository  extends BaseRepository<FrameFile, Long> {

    List<FrameFile> findByName(String name);
    List<FrameFile> findByRemotePath(String remotePath);
}
