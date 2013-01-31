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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class WarTargetTest {

  private File targetDirectory;
  private WarTarget warTarget;

  @Before
  public void setUp() throws Exception {
    targetDirectory = createTempDirectoryName();
    warTarget = new WarTarget(targetDirectory);
  }

  @After
  public void tearDown() throws Exception {
    targetDirectory.delete();
    warTarget.getSignatureFile().delete();
  }

  @Test
  public void getDirectoryWeBuild() throws IOException {
    assertEquals(targetDirectory, warTarget.getTargetDirectory());
  }

  @Test
  public void getMD5DigestOfMissingDirectory() throws IOException {
    assertFalse("directory exists", targetDirectory.exists());
    assertFalse(warTarget.getSignatureFile().exists());
    assertEquals(null, warTarget.currentMD5Digest());
  }

  @Test
  public void afterUpdateOfSignature_shouldReadNewSignature() throws IOException {
    String signature = MD5Digest.digestFromStream(new ByteArrayInputStream(("some md5" + Long.toHexString(System.currentTimeMillis())).getBytes()));
    WarTarget warTarget = new WarTarget(targetDirectory);
    warTarget.updateMD5Digest(signature);
    assertTrue("directory does not exists", targetDirectory.exists());
    assertTrue("Signature file does not exists", warTarget.getSignatureFile().exists());
    assertEquals(signature, warTarget.currentMD5Digest());
  }

  private static File createTempDirectoryName() throws IOException {
    final File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
    if (!(temp.delete()))
      throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
    return (temp);
  }
}