package com.cenyo.frame.entities.source;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("SYNOLOGY_SOURCE")
public class SynologySource extends Source{

    private String host;

    private Integer port;

    private String userName;

    private String password;

    @Override
    public List<String> listChildren(String rootPath) {
        return null;
    }

    @Override
    public boolean isFileExists(String path) {
        return false;
    }
}
