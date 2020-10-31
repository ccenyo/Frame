package com.cenyo.frame.entities.source;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity(name = "source")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="source_type")
public abstract class Source {

    public enum Type {
        Synology,
        Local
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    private String rootFolder;

    public  abstract List<String> listChildren(String rootPath);

    public  abstract boolean isFileExists(String path);
}
