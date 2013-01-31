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

import java.io.*;

public class WarTarget {
  private final static String SIGNATURE_FILE_NAME = "archive.md5";
  private File targetDirectory;

  public WarTarget(File targetDirectory) {
    this.targetDirectory = targetDirectory;
  }

  public File getTargetDirectory() {
    return targetDirectory;
  }

  public File getSignatureFile() {
    return new File(targetDirectory, SIGNATURE_FILE_NAME);
  }

  public String currentMD5Digest() throws IOException {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(getSignatureFile()));
      return reader.readLine();
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public void updateMD5Digest(String signature) throws IOException {
    createDirectoryIfNotPresent();
    saveSignatureToFile(signature);
  }

  private void saveSignatureToFile(String signature) throws FileNotFoundException {
    PrintWriter out = new PrintWriter(new FileOutputStream(getSignatureFile()));
    out.println(signature);
    out.close();
  }

  private void createDirectoryIfNotPresent() throws IOException {
    if (!targetDirectory.exists() && !targetDirectory.mkdirs())
      throw new IOException("Failed to create directories: " + targetDirectory.getAbsolutePath());
  }
}