package com.cenyo.frame.entities.source;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("LOCAL_SOURcE")
public class LocalSource extends Source {
    @Override
    public List<String> listChildren(String rootPath) {
        File rootFile = new File(rootPath);
        return Arrays.stream(Objects.requireNonNull(rootFile.listFiles()))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFileExists(String path) {
        return new File(path).exists();
    }
}
