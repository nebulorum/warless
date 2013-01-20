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

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZipUtilityTest {

  private File target;
  private InputStream archiveZip;

  @Before
  public void setUp() throws Exception {
    target = new File("target");
    archiveZip = this.getClass().getClassLoader().getResourceAsStream("archive.zip");
  }

  @Test
  public void computeMD5String() throws NoSuchAlgorithmException {
    assertEquals(stringMD5("some string"), "5ac749fbeec93607fc28d666be85e73a");
  }

  @Test
  public void makeSureWePadMD5() throws NoSuchAlgorithmException {
    assertEquals("0cc175b9c0f1b6a831c399e269772661", stringMD5("a"));
    assertEquals("00411460f7c92d2124a67ea0f4cb5f85", stringMD5("363"));
    assertEquals("0000000018e6137ac2caab16074784a6", stringMD5("jk8ssl"));
  }

  @Test
  public void unzipAll() throws IOException {
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("archive.zip");
    File target = new File("target");
    new ZipUtility().unzipAll(is, target);
    validateDirectory(target);
    cleanupDirectory(target);
  }

  @Test
  public void unzipAllWithFilter() throws IOException {
    new ZipUtility().unzipFiltered(archiveZip, target, new ZipUtility.PrefixUnzipFilter("folder_two"));
    assertFileExists(target, "folder_two/file_two.txt");
    assertFileNotExists(target, "file_base.txt");
    assertFileNotExists(target, "folder_one/file_one.txt");
    cleanupDirectory(target);
  }

  private String stringMD5(String s) throws NoSuchAlgorithmException {
    return new ZipUtility().streamMD5(new ByteArrayInputStream(s.getBytes()));
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