
package org.github.suhorukov.gitblit.example;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.eclipse.jetty.runner.Runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 */
public class GitblitExample {

    public static final String GITBLIT_WAR = "gitblit.war";

    public static void main(String[] args) throws Exception{
        File gitblit = new File(System.getProperty("java.io.tmpdir"), GITBLIT_WAR);
        try (InputStream warInput = GitblitExample.class.getResourceAsStream(String.format("/%s", GITBLIT_WAR));
             FileOutputStream warOutput = new FileOutputStream(gitblit);){
            IOUtils.copyLarge(warInput, warOutput);
        }

        File gitblitDirectory = new File(System.getProperty("user.home"), gitblit.getName().replace(".war",""));

        if(!gitblitDirectory.exists()){
            gitblitDirectory.mkdir();
            ZipUnArchiver unArchiver = new ZipUnArchiver();
            unArchiver.setSourceFile(gitblit);
            unArchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG,"Logger"));
            unArchiver.setDestDirectory(gitblitDirectory);
            unArchiver.extract();

            File dataPath = new File(System.getProperty("user.home"), ".gitblit_data");
            if(!dataPath.exists()){ dataPath.mkdir(); };
            File webXml = new File(gitblitDirectory.getAbsoluteFile(), "WEB-INF/web.xml");
            String webXmlContent = IOUtils.toString(webXml.toURI());
            try (FileOutputStream outputStream = new FileOutputStream(webXml)){
                IOUtils.write(webXmlContent.replace("${contextFolder}/WEB-INF/data", dataPath.getAbsolutePath()), outputStream);
            }
        }
        Runner.main(new String[]{gitblitDirectory.getAbsolutePath()});
    }
}