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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class UnzipperTest {

  private File target;
  private Unzipper unzipper;

  @Before
  public void setUp() throws Exception {
    target = new File("target");
    InputStream archiveZip = this.getClass().getClassLoader().getResourceAsStream("archive.zip");
    unzipper = new Unzipper(archiveZip);
  }

  @After
  public void tearDown() throws Exception {
    cleanupDirectory(target);
  }

  @Test
  public void unzipAll() throws IOException {
    unzipper.unzip(target);
    validateDirectory(target);
  }

  @Test(expected = IOException.class)
  public void unzipCantRunTwice() throws IOException {
    unzipper.unzip(target);
    unzipper.unzip(target);
  }

  @Test
  public void unzipAllWithFilter() throws IOException {
    unzipper.setFilter(new Unzipper.PrefixUnzipFilter("folder_two"));
    unzipper.unzip(target);
    assertFileExists(target, "folder_two/file_two.txt");
    assertFileNotExists(target, "file_base.txt");
    assertFileNotExists(target, "folder_one/file_one.txt");
  }

  private void validateDirectory(File dir) {
    assertFileExists(dir, "file_base.txt");
    assertFileExists(dir, "folder_one/file_one.txt");
    assertFolderPresent(dir, "folder_one");
    assertFileExists(dir, "folder_two/file_two.txt");
    assertFolderPresent(dir, "folder_two");
  }

  private void cleanupDirectory(File baseDirectory) {
    String files[] = {
      "file_base.txt", "folder_one/file_one.txt", "folder_one", "folder_two/file_two.txt", "folder_two"
    };
    for (String file : files) {
      //noinspection ResultOfMethodCallIgnored
      new File(baseDirectory, file).delete();
    }
  }

  private void assertFileExists(File baseDirectory, String relativePath) {
    File file = new File(baseDirectory, relativePath);
    assertTrue(file.getAbsolutePath() + " should exist", file.exists() && file.isFile());
  }

  private void assertFileNotExists(File baseDirectory, String relativePath) {
    File file = new File(baseDirectory, relativePath);
    assertTrue(file.getAbsolutePath() + " exists", !file.exists());
  }

  private void assertFolderPresent(File baseDirectory, String relativePath) {
    File file = new File(baseDirectory, relativePath);
    assertTrue(file.getAbsolutePath() + " should exist and be directory", file.exists() && file.isDirectory());
  }
}