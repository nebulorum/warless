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

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class WarLessTest {

  private WarArchive archive;
  private WarTarget target;
  private File targetDirectory;
  private String appSubDirectory;
  private WarLess warLess;
  private String newSignature;

  @Before
  public void setUp() throws Exception {
    archive = mock(WarArchive.class);
    target = mock(WarTarget.class);
    targetDirectory = File.createTempFile("app", "dir");
    appSubDirectory = "webapp/" + Long.toHexString(System.currentTimeMillis()) + "/app";

    warLess = new WarLess(archive, target);
    when(target.getTargetDirectory()).thenReturn(targetDirectory);
    when(archive.getWebAppDirectory()).thenReturn(appSubDirectory);
    newSignature = buildMD5Digest("new");
  }

  @Test
  public void archiveIsFileSystem() throws IOException {
    File archivePath = File.createTempFile("archive", ".dir");
    mockArchiveIsDirectory(archivePath);

    warLess.resolve();
    verifyNotCheckedTargetDirectory();

    verifyGotArchivePathButNotArchive();
    verifyDidNotExpandArchive();
    verifyDidGetArchivePath();
    assertEquals(makeFinalDirectory(archivePath), warLess.getTargetDirectory());
  }

  @Test
  public void archiveIsJarAndTargetDoesNotExist_mustExpand() throws IOException {

    mockArchiveIsFile(newSignature, true);

    warLess.resolve();

    verifyCheckedTargetDirectory();
    verifyCheckArchiveSignatureAndPath();
    verifyDidNotGetArchivePath();
    verifyExpandedArchive();
    verifyTargetDirectoryIsExpandedDirectory();
  }

  @Test
  public void archiveIsJarAndTargetDoesExistsAndIsNotUpToDate_mustExtract() throws IOException {
    mockArchiveIsFile(newSignature, true);
    mockTargetWithSignature(buildMD5Digest("old"));

    warLess.resolve();

    verifyCheckedTargetDirectory();
    verifyCheckArchiveSignatureAndPath();
    verifyDidNotGetArchivePath();
    verifyExpandedArchive();
    verifyTargetDirectoryIsExpandedDirectory();
  }

  private void verifyTargetDirectoryIsExpandedDirectory() {
    assertEquals(makeFinalDirectory(targetDirectory), warLess.getTargetDirectory());
  }

  @Test
  public void archiveIsJarAndTargetDoesExistsAndIsUpToDate_mustDoNothing() throws IOException {
    boolean isArchive = true;
    mockArchiveIsFile(newSignature, isArchive);
    mockTargetWithSignature(newSignature);

    warLess.resolve();

    verifyCheckedTargetDirectory();
    verifyCheckArchiveSignatureAndPath();
    verifyDidNotGetArchivePath();
    verifyDidNotExpandArchive();
    verifyTargetDirectoryIsExpandedDirectory();
  }

  private void mockTargetWithSignature(String signature) throws IOException {
    when(target.currentMD5Digest()).thenReturn(signature);
  }

  private void verifyDidNotGetArchivePath() {
    verify(archive, never()).getArchivePath();
  }

  private void verifyDidGetArchivePath() {
    verify(archive, atLeastOnce()).getArchivePath();
  }

  private void verifyDidNotExpandArchive() {
    verify(archive, never()).extractWebApp(targetDirectory, appSubDirectory);
  }

  private void verifyCheckArchiveSignatureAndPath() {
    verify(archive, atLeastOnce()).isArchive();
    verify(archive, atLeastOnce()).getMD5Digest();
    verify(archive, atLeastOnce()).getWebAppDirectory();
  }

  private void verifyGotArchivePathButNotArchive() {
    verify(archive, atLeastOnce()).getArchivePath();
    verify(archive, atLeastOnce()).getWebAppDirectory();
    verify(archive, atLeastOnce()).isArchive();
  }

  private void verifyCheckedTargetDirectory() throws IOException {
    verify(target, atLeastOnce()).getTargetDirectory();
    verify(target, atLeastOnce()).currentMD5Digest();
  }

  private void verifyNotCheckedTargetDirectory() throws IOException {
    verify(target, never()).getTargetDirectory();
    verify(target, never()).currentMD5Digest();
  }

  private void mockArchiveIsFile(String newSignature, boolean isArchive) {
    when(archive.isArchive()).thenReturn(isArchive);
    when(archive.getMD5Digest()).thenReturn(newSignature);
  }

  private void mockArchiveIsDirectory(File archivePath) throws IOException {
    when(archive.isArchive()).thenReturn(false);
    when(archive.getArchivePath()).thenReturn(archivePath);
    when(target.currentMD5Digest()).thenReturn(null);
  }

  private File makeFinalDirectory(File archivePath) {
    return new File(archivePath, appSubDirectory);
  }

  private String buildMD5Digest(String digestText) {
    return MD5Digest.digestFromStream(new ByteArrayInputStream(digestText.getBytes()));
  }

  private void verifyExpandedArchive() throws IOException {
    verify(archive, times(1)).extractWebApp(targetDirectory, appSubDirectory);
    verify(target, times(1)).updateMD5Digest(newSignature);
  }

}