/*
 * Copyright (c) 2008, Esko Luontola. All Rights Reserved.
 *
 * This file is part of Darkstar EXP.
 *
 * Darkstar EXP is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License version 2 as published by 
 * the Free Software Foundation and distributed hereunder to you.
 *
 * Darkstar EXP is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.orfjackal.darkstar.exp;

import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import jdave.Group;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.darkstar.integration.DarkstarServer;
import net.orfjackal.darkstar.integration.util.StreamWaiter;
import net.orfjackal.darkstar.integration.util.TempDirectory;
import org.junit.runner.RunWith;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * @author Esko Luontola
 * @since 18.6.2008
 */
@RunWith(JDaveRunner.class)
@Group({"slow", "integration"})
public class AppRootAsRelativeToAppPropertiesFileSpec extends Specification<Object> {

    private static final int TIMEOUT = 5000;

    private DarkstarServer server;
    private StreamWaiter waiter;
    private Properties appProps;

    public void create() {
        server = new DarkstarServer(new File("."));
        waiter = new StreamWaiter(new ByteArrayOutputStream());

        appProps = new Properties();
        appProps.setProperty(DarkstarServer.APP_NAME, "HellWorld");
        appProps.setProperty(DarkstarServer.APP_LISTENER, HelloWorld.class.getName());
        appProps.setProperty(DarkstarServer.APP_PORT, DarkstarServer.APP_PORT_DEFAULT);
    }

    public void destroy() {
        waiter.dispose();
    }

    private void startsUpAndUsesDataDir(File dataDir, File configFile) throws TimeoutException {
        specify(dataDir.listFiles().length == 0);
        try {
            server.start(configFile);
            waiter.setStream(server.getSystemOut());
            waiter.waitForBytes(HelloWorld.STARTUP_MSG.getBytes(), TIMEOUT);
        } finally {
            server.shutdown();
        }
        specify(dataDir.listFiles().length > 5);
    }

    private static void writeToFile(File file, Properties properties) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            properties.store(out, null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class WhenAppPropertiesFileIsInCurrentDirectory {

        private File configFile;
        private TempDirectory appRootParentTemp;
        private File appRoot;
        private File dataDir;

        public Object create() throws IOException {
            File currentDir = new File(".").getCanonicalFile();
            configFile = new File(currentDir, "HelloWorld.properties");

            appRootParentTemp = new TempDirectory(new File(currentDir, "data.tmp"));
            appRootParentTemp.create();

            appRoot = new File(appRootParentTemp.getDirectory(), "HelloWorld");
            dataDir = new File(appRoot, "dsdb");
            dataDir.mkdirs();
            return null;
        }

        public void destroy() {
            configFile.delete();
            appRootParentTemp.dispose();
        }

        public void absoluteAppRootIsAbsolute() throws TimeoutException {
            appProps.setProperty(DarkstarServer.APP_ROOT, appRoot.getAbsolutePath());
            writeToFile(configFile, appProps);
            startsUpAndUsesDataDir(dataDir, configFile);
        }

        public void relativeAppRootIsRelativeToCurrentDirectory() throws TimeoutException {
            appProps.setProperty(DarkstarServer.APP_ROOT, "data.tmp" + File.separator + "HelloWorld");
            writeToFile(configFile, appProps);
            startsUpAndUsesDataDir(dataDir, configFile);
        }
    }

    public class WhenAppPropertiesFileIsInAnotherDirectory {

        private TempDirectory anotherDirTemp;
        private File configFile;
        private File appRoot;
        private File dataDir;

        public Object create() {
            anotherDirTemp = new TempDirectory();
            anotherDirTemp.create();

            File anotherDir = anotherDirTemp.getDirectory();
            configFile = new File(anotherDir, "HelloWorld.properties");

            appRoot = new File(anotherDir, "data.tmp" + File.separator + "HelloWorld");
            dataDir = new File(appRoot, "dsdb");
            dataDir.mkdirs();
            return null;
        }

        public void destroy() {
            anotherDirTemp.dispose();
        }

        public void absoluteAppRootIsAbsolute() throws TimeoutException {
            appProps.setProperty(DarkstarServer.APP_ROOT, appRoot.getAbsolutePath());
            writeToFile(configFile, appProps);
            startsUpAndUsesDataDir(dataDir, configFile);
        }

        public void relativeAppRootIsRelativeToDirectoryOfAppPropertiesFile() throws TimeoutException {
            appProps.setProperty(DarkstarServer.APP_ROOT, "data.tmp" + File.separator + "HelloWorld");
            writeToFile(configFile, appProps);
            startsUpAndUsesDataDir(dataDir, configFile);
        }
    }


    public static class HelloWorld implements AppListener, Serializable {
        private static final long serialVersionUID = 1L;

        private static final String STARTUP_MSG = "Hello world!";

        public void initialize(Properties props) {
            System.out.println(STARTUP_MSG);
        }

        public ClientSessionListener loggedIn(ClientSession session) {
            return null;
        }
    }
}
