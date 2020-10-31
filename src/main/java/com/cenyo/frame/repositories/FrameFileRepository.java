package com.cenyo.frame.repositories;

import com.cenyo.frame.entities.FrameFile;

import java.util.List;
import java.util.Optional;

public interface FrameFileRepository  extends BaseRepository<FrameFile, Long> {

    List<FrameFile> findByName(String name);
    List<FrameFile> findByRemotePath(String remotePath);

    Optional<FrameFile> findFirstByFeaturedIsTrue();
}
