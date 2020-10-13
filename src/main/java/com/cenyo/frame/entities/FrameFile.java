package com.cenyo.frame.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "frame_file")
@Getter
@Setter
public class FrameFile {

    public enum Type {
        DIRECTORY,
        PICTURE,
        VIDEO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String remotePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FrameFile parent;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    private boolean featured;

    @OneToOne(fetch = FetchType.LAZY)
    private FrameFileMeta frameFileMeta;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FrameFile> children = new LinkedList<>();
}