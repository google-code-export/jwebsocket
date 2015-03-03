/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.core.nio;

import java.io.IOException;
import java.nio.channels.ByteChannel;

/**
 * @author Santhosh Kumar T
 */
public interface SelectableByteChannel extends ByteChannel{
    long id();
    public void addInterest(int operation) throws IOException;
    public void removeInterest(int operation) throws IOException;

    public void attach(Object obj);
    public Object attachment();
}
