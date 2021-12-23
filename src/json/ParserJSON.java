package json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ParserJSON {
    public static String jsonToHtml( Object obj ) {
        StringBuilder html = new StringBuilder( );

        try {
            if (obj instanceof JSONObject jsonObject) {
                List<String> keys = jsonObject.keySet().stream().toList();
                html.append("<div class=\"json_object\">");

                if (keys.size() > 0) {
                    for (String key : keys) {

                        html.append("<div><span class=\"json_key\"><pre><b>")
                                .append(key).append("</b></span> -> \n\t");

                        Object val = jsonObject.get(key);

                        html.append( jsonToHtml( val ) );
                        html.append("<pre></div>");
                    }
                }

                html.append("</div>");

            } else if (obj instanceof JSONArray array) {
                for (Object o : array) {
                    html.append("<pre>\t");
                    html.append(jsonToHtml(o));
                    html.append("<pre>");
                }
            } else {
                html.append( obj );
            }
        } catch (Exception e) {
            System.out.println("erro - json2html ["+e.getMessage()+"] - ["+ e +"]");
        }

        return html.toString( );
    }

    public static JSONObject parser(File file)  {
        JSONParser jp = new JSONParser();
        JSONObject ret = new JSONObject();
        try (FileReader reader = new FileReader(file)){
            Object obj = jp.parse(reader);

            ret = (JSONObject) obj;
        } catch (Exception e){
            System.out.println("erro - json parser ["+e.getMessage()+"] - ["+ e +"]");
        }

        return ret;
    }

    public static JSONObject listar(){
        File directoryPath = new File(System.getProperty("user.dir"));
        JSONObject obj = new JSONObject();
        File[] filesList = directoryPath.listFiles();

        if (filesList != null) {

            for(File file : filesList) {
                JSONArray ficheiro = new JSONArray();
                ficheiro.add("file name: "+file.getName()+" ; ");
                ficheiro.add("file path: "+file.getAbsolutePath()+" ; ");
                ficheiro.add("file last update: "+file.lastModified()+" ; ");
                ficheiro.add("file size: "+file.getTotalSpace()+" ; ");
                obj.put("Name: "+file.getName(),ficheiro);
            }
            System.out.println(obj);
        }

        return obj;
    }

    public static JSONObject logs(){
        String s = System.getProperty("user.dir");
        System.out.println(s+"\\aaa.txt");
        return parser(new File(s+"\\aaa.txt"));
    }
    //JSON PARSER
    /*
    public static void main(String[] args) throws IOException {
        String s = System.getProperty("user.dir");
        System.out.println(s);
        JSONObject obj = new JSONObject();
        File curDir = FFSync.getCurrentDirectory();
        String curDirPath = curDir.getPath();
        int index = curDir.getPath().length() - curDir.getName().length() - 1;
        Files.walkFileTree(Paths.get(curDirPath), new HashSet<>(), 2, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                File f = new File(String.valueOf(dir));
                if (f.isDirectory()) {
                    String nova = f.getPath().substring(index);
                    JSONArray ficheiro = new JSONArray();
                    ficheiro.add("file name: "+f.getName()+" ; ");
                    ficheiro.add("file path: "+nova+" ; ");
                    //ficheiro.add("file path: "+f.getAbsolutePath()+" ; ");
                    ficheiro.add("file last update: "+f.lastModified()+" ; ");
                    ficheiro.add("file size: "+f.getTotalSpace()+" ; ");
                    obj.put("Name: "+nova,ficheiro);
                  //  System.out.println(ficheiro);
                }
                // System.out.println("preVisitDirectory: " + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                File f = new File(String.valueOf(file));
                String nova = f.getPath().substring(index);
                JSONArray ficheiro = new JSONArray();
                ficheiro.add("file name: "+f.getName()+" ; ");
                ficheiro.add("file path: "+nova+" ; ");
                //ficheiro.add("file path: "+f.getAbsolutePath()+" ; ");
                ficheiro.add("file last update: "+f.lastModified()+" ; ");
                ficheiro.add("file size: "+f.getTotalSpace()+" ; ");
                obj.put("Name: "+nova,ficheiro);
                //System.out.println(ficheiro);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.out.println("visitFileFailed: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                // System.out.println("postVisitDirectory: " + dir);
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println(obj);
    }*/

}

