/*
 * Copyright (C) 2013-2013 - Thomas Santana <tms@exnebula.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.exnebula.warless;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class WarArchiveTest {

  private File targetDirectory;

  @After
  public void tearDown() throws Exception {
    if (targetDirectory != null) {
      new File(targetDirectory, "folder_two/file_two.txt").delete();
      new File(targetDirectory, "folder_two").delete();
    }
  }

  @Test
  public void withArchive_mustBeArchiveAndHaveDigest() throws IOException {
    File archiveFile = getTestArchiveZip();
    WarArchive warArchive = new WarArchive(archiveFile, "folder_two");

    assertEquals(archiveFile, warArchive.getArchivePath());
    assertTrue("is Archive", warArchive.isArchive());
    assertEquals("7fce64b83143011c7307aed5dbf5faf0", warArchive.getMD5Digest());
  }

  @Test
  public void withDirectory_mustNotBeArchiveAndHaveNoDigest() throws IOException {
    File archiveFile = new File("src/java/org");
    WarArchive warArchive = new WarArchive(archiveFile, "folder_two");

    assertEquals(archiveFile, warArchive.getArchivePath());
    assertFalse("is not Archive", warArchive.isArchive());
    assertEquals(null, warArchive.getMD5Digest());
  }

  @Test
  public void archivePathMatchesConstructor() {
    File archiveFile = new File("myArchive.zip");
    String appDir = "web/app";

    WarArchive warArchive = new WarArchive(archiveFile, appDir);

    assertEquals(archiveFile, warArchive.getArchivePath());
    assertEquals(appDir, warArchive.getWebAppDirectory());
  }

  @Test
  public void expandSelectedFilesInRealArchive() throws IOException {
    File archiveFile = getTestArchiveZip();
    WarArchive warArchive = new WarArchive(archiveFile, "folder_two");
    targetDirectory = new File("target");

    warArchive.extractWebApp(targetDirectory);

    assertTrue("folder_two does not exists", isDirectory(new File(targetDirectory, "folder_two")));
    assertFalse("folder_one exists", isDirectory(new File(targetDirectory, "folder_one")));
  }

  @Test
  public void canGetArchiveFromClass() {
    String appDir = "some/folder";
    WarArchive warArchive = WarArchive.create(Test.class, appDir);

    assertTrue(warArchive.isArchive());
    assertEquals(appDir, warArchive.getWebAppDirectory());
    assertEquals("junit-4.8.1.jar", warArchive.getArchivePath().getName());
  }

  @Test
  public void canGetDirectoryFromClass() {
    String appDir = "another/folder";
    WarArchive warArchive = WarArchive.create(this.getClass(), appDir);

    assertFalse(warArchive.isArchive());
    assertEquals(appDir, warArchive.getWebAppDirectory());
    assertEquals("test-classes", warArchive.getArchivePath().getName());
  }

  private File getTestArchiveZip() {
    URL resource = this.getClass().getClassLoader().getResource("archive.zip");
    assert (resource != null);
    return new File(resource.getFile());
  }

  private boolean isDirectory(File folder) {
    return folder.exists() && folder.isDirectory();
  }
}