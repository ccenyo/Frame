package com.cenyo.frame.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "frame_file_meta")
public class FrameFileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_file_id")
    private FrameFile frameFile;

    private Long size;

    private String type;

    private Long lastAccessTimeStamp;

    private Long lastModifiedTimeStamp;

    private Long lastChangeTimeStamp;

    private Long createTimeStamp;

}
