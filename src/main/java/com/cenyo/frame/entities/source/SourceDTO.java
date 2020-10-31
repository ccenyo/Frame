package com.cenyo.frame.entities.source;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceDTO {

    private Long id;

    private String name;

    private Source.Type type;

    private String rootFolder;

    private String host;

    private Integer port;

    private String userName;

    private String password;

    public static SourceDTO fromLocalSource(LocalSource localSource) {
        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.setName(localSource.getName());
        sourceDTO.setRootFolder(localSource.getRootFolder());
        sourceDTO.setType(localSource.getType());
        sourceDTO.setId(localSource.getId());
        return sourceDTO;
    }

    public SourceDTO() {
    }

    public static SourceDTO fromSynologySource(SynologySource synologySource) {
        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.setName(synologySource.getName());
        sourceDTO.setRootFolder(synologySource.getRootFolder());
        sourceDTO.setType(synologySource.getType());
        sourceDTO.setHost(synologySource.getHost());
        sourceDTO.setId(synologySource.getId());
        sourceDTO.setUserName(synologySource.getUserName());
        sourceDTO.setPort(synologySource.getPort());
        sourceDTO.setPassword(synologySource.getPassword());
        return sourceDTO;
    }

    public Source toSource() {
         return switch(this.type) {
             case Synology -> {
                 SynologySource synologySource = new SynologySource();
                 synologySource.setId(this.getId());
                 synologySource.setName(this.getName());
                 synologySource.setRootFolder(this.getRootFolder());
                 synologySource.setType(this.getType());
                 synologySource.setHost(this.getHost());
                 synologySource.setPassword(this.getPassword());
                 synologySource.setUserName(this.getUserName());
                 synologySource.setPort(this.getPort());
                 yield synologySource;
             }
             case Local -> {
                 LocalSource localSource = new LocalSource();
                 localSource.setId(this.getId());
                 localSource.setName(this.getName());
                 localSource.setRootFolder(this.getRootFolder());
                 localSource.setType(this.getType());
                 yield localSource;
             }
         };
    }
}
