package test.folderparser;

import ffrapid_protocol.packet.Metadata;
import folder_parser.FolderParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FolderParserTest {

    @org.junit.jupiter.api.Test
    void listSubDir() {
    }

    @org.junit.jupiter.api.Test
    void compareMetadata1() {
        String[] strings={"/.gitignore","/CC-Enunciado-TP2-2022.pdf"};
        List<String> ls=new ArrayList<>();
        for(String s: strings){
            ls.add(s);
        }
        Metadata metadata = Metadata.getMetadataFromNames(ls);
        FolderParser fp= new FolderParser();
        fp.compareMetadata1(metadata);

    }
}