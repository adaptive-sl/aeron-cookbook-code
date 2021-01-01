/*
 * Copyright 2019-2021 Shaun Laurens.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aeroncookbook.archive;

import java.io.File;
import java.io.IOException;

public class Utils
{
    public static File createTempDir()
    {
        final File tempDir;
        try
        {
            tempDir = File.createTempFile("archive", "tmp");
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        if (!tempDir.delete())
        {
            throw new IllegalStateException("Cannot delete tmp file!");
        }

        if (!tempDir.mkdir())
        {
            throw new IllegalStateException("Cannot create folder!");
        }

        return tempDir;
    }
}
