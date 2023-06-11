package com.grinder.cache;

import net.runelite.cache.util.GZip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 29/12/2019
 */
public class CachePacker {

    public static void main(String[] args) throws IOException {

        Cache cache = new Cache();

        final Path mapDataPath = Paths.get("/Users/stanbend/IdeaProjects/OpenRS2/repository/maps");
        for(final File file : Objects.requireNonNull(mapDataPath.toFile().listFiles())){
            if(file.getName().startsWith("m")){
                final byte[] data = Files.readAllBytes(file.toPath());
                final byte[] compressedData = GZip.compress(data);

//                cache.indices[4].writeFile(compressedData.length, compressedData, );

            } else if(file.getName().startsWith("l")){

            } else {
                System.out.println("Did not parse file '"+file.getName()+"'");
            }
        }

    }

}
