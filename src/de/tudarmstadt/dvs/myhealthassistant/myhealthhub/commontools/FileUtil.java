/* 
 * Copyright (C) 2014 TU Darmstadt, Hessen, Germany.
 * Department of Computer Science Databases and Distributed Systems
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public final class FileUtil {

   private FileUtil() {
   }

   public static void copyFile(File src, File dst) throws IOException {
      FileChannel inChannel = new FileInputStream(src).getChannel();
      FileChannel outChannel = new FileOutputStream(dst).getChannel();
      try {
         inChannel.transferTo(0, inChannel.size(), outChannel);
      } finally {
         if (inChannel != null) {
            inChannel.close();
         }
         if (outChannel != null) {
            outChannel.close();
         }
      }
   }
}