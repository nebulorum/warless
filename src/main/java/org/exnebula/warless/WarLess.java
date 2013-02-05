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
import java.io.IOException;

public class WarLess {
  private final WarTarget target;
  private File targetDirectory;
  private final WarArchive archive;

  public WarLess(WarArchive archive, WarTarget target) {
    this.target = target;
    this.archive = archive;
  }

  public File getTargetDirectory() {
    return targetDirectory;
  }

  public void resolve() throws IOException {
    String subPath = archive.getWebAppDirectory();
    if (archive.isArchive()) {
      handleArchive(subPath);
    } else {
      handleFileSystem(subPath);
    }
  }

  private void handleFileSystem(String subPath) {
    targetDirectory = new File(archive.getArchivePath(), subPath);
  }

  private void handleArchive(String subPath) throws IOException {
    if (!localCopyUpToDate()) {
      extractArchive();
      target.updateMD5Digest(archive.getMD5Digest());
    }
    targetDirectory = new File(target.getTargetDirectory(), subPath);
  }

  private void extractArchive() throws IOException {
    archive.extractWebApp(target.getTargetDirectory());
  }

  private boolean localCopyUpToDate() throws IOException {
    return archive.getMD5Digest().equals(target.currentMD5Digest());
  }
}