package com.cenyo.frame.entities.source;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "source")
public class Source {

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

    private String host;

    private Integer port;

    private String userName;

    private String password;

    private String rootFolder;
}
