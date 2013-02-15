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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class WarArchive {
  private final File archiveFile;
  private final String appDirectory;

  public WarArchive(File archiveFile, String appDirectory) {
    this.archiveFile = archiveFile;
    this.appDirectory = appDirectory;
  }

  File getArchivePath() {
    return archiveFile;
  }

  String getWebAppDirectory() {
    return appDirectory;
  }

  boolean isArchive() {
    return (archiveFile != null) && archiveFile.exists() && archiveFile.isFile();
  }

  void extractWebApp(File targetDirectory) throws IOException {
    Unzipper unzipper = new Unzipper(new FileInputStream(archiveFile));
    unzipper.setFilter(new Unzipper.PrefixUnzipFilter(appDirectory));
    unzipper.unzip(targetDirectory);
  }

  String getMD5Digest() throws IOException {
    if (isArchive())
      return MD5Digest.digestFromStream(new FileInputStream(archiveFile));
    else
      return null;
  }

  public static WarArchive create(Class<?> aClass, String appDirectory) {
    String container = aClass.getProtectionDomain().getCodeSource().getLocation().getFile();
    try {
      return new WarArchive(new File(URLDecoder.decode(container, "UTF-8")), appDirectory);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }
}