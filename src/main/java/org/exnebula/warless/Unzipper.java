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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Unzipper {

  private final ZipInputStream zipInputStream;
  private UnzipFilter filter;

  static private class FilterAll implements UnzipFilter {
    public boolean filter(String name) {
      return true;
    }
  }

  static public class PrefixUnzipFilter implements UnzipFilter {

    private final String prefix;

    public PrefixUnzipFilter(String prefix) {
      this.prefix = prefix;
    }

    public boolean filter(String name) {
      return name.startsWith(prefix);
    }
  }

  public Unzipper(InputStream archiveZip) {
    zipInputStream = new ZipInputStream(archiveZip);
  }

  public void setFilter(UnzipFilter filter) {
    this.filter = filter;
  }


  public void unzip(File targetDirectory) throws IOException {
    if(filter == null)
      filter = new FilterAll();
    handleAllEntries(targetDirectory);
  }

  private void handleAllEntries(File targetDirectory) throws IOException {
      ZipEntry zipentry = zipInputStream.getNextEntry();
      while (zipentry != null) {
        handleEntry(targetDirectory, zipentry);
        zipentry = zipInputStream.getNextEntry();
      }
      zipInputStream.close();
  }

  private void handleEntry(File targetDirectory, ZipEntry zipentry) throws IOException {
    String name = zipentry.getName();
    if (filter.filter(name)) {
      processFile(new File(targetDirectory, name), zipentry.isDirectory());
    }
    zipInputStream.closeEntry();
  }

  private void processFile(File destinationFile, boolean isDirectory) throws IOException {
    if (isDirectory) {
      makeParentDirectory(destinationFile);
    } else {
      makeParentDirectory(destinationFile.getParentFile());
      extractFile(destinationFile);
    }
  }

  private void makeParentDirectory(File parentFile) {
    if (!parentFile.exists()) parentFile.mkdirs();
  }

  private void extractFile(File dest) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(dest);
    byte[] buf = new byte[4096];
    int n = zipInputStream.read(buf);
    while ((n) > -1) {
      outputStream.write(buf, 0, n);
      n = zipInputStream.read(buf);
    }
    outputStream.close();
  }
}