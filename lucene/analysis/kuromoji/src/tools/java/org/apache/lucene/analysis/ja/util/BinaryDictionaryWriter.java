begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|CodecUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|OutputStreamDataOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
operator|.
name|BinaryDictionary
import|;
end_import

begin_class
DECL|class|BinaryDictionaryWriter
specifier|public
specifier|abstract
class|class
name|BinaryDictionaryWriter
block|{
DECL|field|implClazz
specifier|protected
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|BinaryDictionary
argument_list|>
name|implClazz
decl_stmt|;
DECL|field|buffer
specifier|protected
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|targetMapEndOffset
DECL|field|lastWordId
DECL|field|lastSourceId
specifier|private
name|int
name|targetMapEndOffset
init|=
literal|0
decl_stmt|,
name|lastWordId
init|=
operator|-
literal|1
decl_stmt|,
name|lastSourceId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|targetMap
specifier|private
name|int
index|[]
name|targetMap
init|=
operator|new
name|int
index|[
literal|8192
index|]
decl_stmt|;
DECL|field|targetMapOffsets
specifier|private
name|int
index|[]
name|targetMapOffsets
init|=
operator|new
name|int
index|[
literal|8192
index|]
decl_stmt|;
DECL|field|posDict
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|posDict
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|BinaryDictionaryWriter
specifier|public
name|BinaryDictionaryWriter
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|BinaryDictionary
argument_list|>
name|implClazz
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|implClazz
operator|=
name|implClazz
expr_stmt|;
name|buffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * put the entry in map    * @return current position of buffer, which will be wordId of next entry    */
DECL|method|put
specifier|public
name|int
name|put
parameter_list|(
name|String
index|[]
name|entry
parameter_list|)
block|{
name|short
name|leftId
init|=
name|Short
operator|.
name|parseShort
argument_list|(
name|entry
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|short
name|rightId
init|=
name|Short
operator|.
name|parseShort
argument_list|(
name|entry
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|short
name|wordCost
init|=
name|Short
operator|.
name|parseShort
argument_list|(
name|entry
index|[
literal|3
index|]
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// build up the POS string
for|for
control|(
name|int
name|i
init|=
literal|4
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|String
name|part
init|=
name|entry
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|part
operator|.
name|length
argument_list|()
operator|>
literal|0
assert|;
if|if
condition|(
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|part
argument_list|)
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|posData
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|CSVUtil
operator|.
name|quoteEscape
argument_list|(
name|posData
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|entry
index|[
literal|8
index|]
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|CSVUtil
operator|.
name|quoteEscape
argument_list|(
name|entry
index|[
literal|8
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|entry
index|[
literal|9
index|]
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|CSVUtil
operator|.
name|quoteEscape
argument_list|(
name|entry
index|[
literal|9
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|fullPOSData
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|baseForm
init|=
name|entry
index|[
literal|10
index|]
decl_stmt|;
name|String
name|reading
init|=
name|entry
index|[
literal|11
index|]
decl_stmt|;
name|String
name|pronunciation
init|=
name|entry
index|[
literal|12
index|]
decl_stmt|;
comment|// extend buffer if necessary
name|int
name|left
init|=
name|buffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
comment|// worst case: two short, 3 bytes, and features (all as utf-16)
name|int
name|worstCase
init|=
literal|4
operator|+
literal|3
operator|+
literal|2
operator|*
operator|(
name|baseForm
operator|.
name|length
argument_list|()
operator|+
name|reading
operator|.
name|length
argument_list|()
operator|+
name|pronunciation
operator|.
name|length
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|worstCase
operator|>
name|left
condition|)
block|{
name|ByteBuffer
name|newBuffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|buffer
operator|.
name|limit
argument_list|()
operator|+
name|worstCase
operator|-
name|left
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|newBuffer
operator|.
name|put
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
operator|(
literal|"*"
operator|.
name|equals
argument_list|(
name|baseForm
argument_list|)
operator|||
name|baseForm
operator|.
name|equals
argument_list|(
name|entry
index|[
literal|0
index|]
argument_list|)
operator|)
condition|)
block|{
name|flags
operator||=
name|BinaryDictionary
operator|.
name|HAS_BASEFORM
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|reading
operator|.
name|equals
argument_list|(
name|toKatakana
argument_list|(
name|entry
index|[
literal|0
index|]
argument_list|)
argument_list|)
condition|)
block|{
name|flags
operator||=
name|BinaryDictionary
operator|.
name|HAS_READING
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|pronunciation
operator|.
name|equals
argument_list|(
name|reading
argument_list|)
condition|)
block|{
name|flags
operator||=
name|BinaryDictionary
operator|.
name|HAS_PRONUNCIATION
expr_stmt|;
block|}
assert|assert
name|leftId
operator|==
name|rightId
assert|;
assert|assert
name|leftId
operator|<
literal|4096
assert|;
comment|// there are still unused bits
comment|// add pos mapping
name|int
name|toFill
init|=
literal|1
operator|+
name|leftId
operator|-
name|posDict
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toFill
condition|;
name|i
operator|++
control|)
block|{
name|posDict
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|String
name|existing
init|=
name|posDict
operator|.
name|get
argument_list|(
name|leftId
argument_list|)
decl_stmt|;
assert|assert
name|existing
operator|==
literal|null
operator|||
name|existing
operator|.
name|equals
argument_list|(
name|fullPOSData
argument_list|)
assert|;
name|posDict
operator|.
name|set
argument_list|(
name|leftId
argument_list|,
name|fullPOSData
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putShort
argument_list|(
call|(
name|short
call|)
argument_list|(
name|leftId
operator|<<
literal|3
operator||
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putShort
argument_list|(
name|wordCost
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|flags
operator|&
name|BinaryDictionary
operator|.
name|HAS_BASEFORM
operator|)
operator|!=
literal|0
condition|)
block|{
assert|assert
name|baseForm
operator|.
name|length
argument_list|()
operator|<
literal|16
assert|;
name|int
name|shared
init|=
name|sharedPrefix
argument_list|(
name|entry
index|[
literal|0
index|]
argument_list|,
name|baseForm
argument_list|)
decl_stmt|;
name|int
name|suffix
init|=
name|baseForm
operator|.
name|length
argument_list|()
operator|-
name|shared
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|shared
operator|<<
literal|4
operator||
name|suffix
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|shared
init|;
name|i
operator|<
name|baseForm
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|putChar
argument_list|(
name|baseForm
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|flags
operator|&
name|BinaryDictionary
operator|.
name|HAS_READING
operator|)
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|isKatakana
argument_list|(
name|reading
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|reading
operator|.
name|length
argument_list|()
operator|<<
literal|1
operator||
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writeKatakana
argument_list|(
name|reading
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|reading
operator|.
name|length
argument_list|()
operator|<<
literal|1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reading
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|putChar
argument_list|(
name|reading
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|(
name|flags
operator|&
name|BinaryDictionary
operator|.
name|HAS_PRONUNCIATION
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// we can save 150KB here, but it makes the reader a little complicated.
comment|// int shared = sharedPrefix(reading, pronunciation);
comment|// buffer.put((byte) shared);
comment|// pronunciation = pronunciation.substring(shared);
if|if
condition|(
name|isKatakana
argument_list|(
name|pronunciation
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|pronunciation
operator|.
name|length
argument_list|()
operator|<<
literal|1
operator||
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writeKatakana
argument_list|(
name|pronunciation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|pronunciation
operator|.
name|length
argument_list|()
operator|<<
literal|1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pronunciation
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|putChar
argument_list|(
name|pronunciation
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|buffer
operator|.
name|position
argument_list|()
return|;
block|}
DECL|method|isKatakana
specifier|private
name|boolean
name|isKatakana
parameter_list|(
name|String
name|s
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|0x30A0
operator|||
name|ch
argument_list|>
literal|0x30FF
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|writeKatakana
specifier|private
name|void
name|writeKatakana
parameter_list|(
name|String
name|s
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
literal|0x30A0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toKatakana
specifier|private
name|String
name|toKatakana
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|char
name|text
index|[]
init|=
operator|new
name|char
index|[
name|s
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
literal|0x3040
operator|&&
name|ch
operator|<
literal|0x3097
condition|)
block|{
name|text
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|ch
operator|+
literal|0x60
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|text
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|text
argument_list|)
return|;
block|}
DECL|method|sharedPrefix
specifier|public
specifier|static
name|int
name|sharedPrefix
parameter_list|(
name|String
name|left
parameter_list|,
name|String
name|right
parameter_list|)
block|{
name|int
name|len
init|=
name|left
operator|.
name|length
argument_list|()
operator|<
name|right
operator|.
name|length
argument_list|()
condition|?
name|left
operator|.
name|length
argument_list|()
else|:
name|right
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|left
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
name|right
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
name|i
return|;
return|return
name|len
return|;
block|}
DECL|method|addMapping
specifier|public
name|void
name|addMapping
parameter_list|(
name|int
name|sourceId
parameter_list|,
name|int
name|wordId
parameter_list|)
block|{
assert|assert
name|wordId
operator|>
name|lastWordId
operator|:
literal|"words out of order: "
operator|+
name|wordId
operator|+
literal|" vs lastID: "
operator|+
name|lastWordId
assert|;
if|if
condition|(
name|sourceId
operator|>
name|lastSourceId
condition|)
block|{
assert|assert
name|sourceId
operator|>
name|lastSourceId
operator|:
literal|"source ids out of order: lastSourceId="
operator|+
name|lastSourceId
operator|+
literal|" vs sourceId="
operator|+
name|sourceId
assert|;
name|targetMapOffsets
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|targetMapOffsets
argument_list|,
name|sourceId
operator|+
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|lastSourceId
operator|+
literal|1
init|;
name|i
operator|<=
name|sourceId
condition|;
name|i
operator|++
control|)
block|{
name|targetMapOffsets
index|[
name|i
index|]
operator|=
name|targetMapEndOffset
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|sourceId
operator|==
name|lastSourceId
assert|;
block|}
name|targetMap
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|targetMap
argument_list|,
name|targetMapEndOffset
operator|+
literal|1
argument_list|)
expr_stmt|;
name|targetMap
index|[
name|targetMapEndOffset
index|]
operator|=
name|wordId
expr_stmt|;
name|targetMapEndOffset
operator|++
expr_stmt|;
name|lastSourceId
operator|=
name|sourceId
expr_stmt|;
name|lastWordId
operator|=
name|wordId
expr_stmt|;
block|}
DECL|method|getBaseFileName
specifier|protected
specifier|final
name|String
name|getBaseFileName
parameter_list|(
name|String
name|baseDir
parameter_list|)
block|{
return|return
name|baseDir
operator|+
name|File
operator|.
name|separator
operator|+
name|implClazz
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
return|;
block|}
comment|/**    * Write dictionary in file    * Dictionary format is:    * [Size of dictionary(int)], [entry:{left id(short)}{right id(short)}{word cost(short)}{length of pos info(short)}{pos info(char)}], [entry...], [entry...].....    * @throws IOException if an I/O error occurs writing the dictionary files    */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|baseDir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|baseName
init|=
name|getBaseFileName
argument_list|(
name|baseDir
argument_list|)
decl_stmt|;
name|writeDictionary
argument_list|(
name|baseName
operator|+
name|BinaryDictionary
operator|.
name|DICT_FILENAME_SUFFIX
argument_list|)
expr_stmt|;
name|writeTargetMap
argument_list|(
name|baseName
operator|+
name|BinaryDictionary
operator|.
name|TARGETMAP_FILENAME_SUFFIX
argument_list|)
expr_stmt|;
name|writePosDict
argument_list|(
name|baseName
operator|+
name|BinaryDictionary
operator|.
name|POSDICT_FILENAME_SUFFIX
argument_list|)
expr_stmt|;
block|}
comment|// TODO: maybe this int[] should instead be the output to the FST...
DECL|method|writeTargetMap
specifier|protected
name|void
name|writeTargetMap
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|File
argument_list|(
name|filename
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
specifier|final
name|DataOutput
name|out
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|BinaryDictionary
operator|.
name|TARGETMAP_HEADER
argument_list|,
name|BinaryDictionary
operator|.
name|VERSION
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numSourceIds
init|=
name|lastSourceId
operator|+
literal|1
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|targetMapEndOffset
argument_list|)
expr_stmt|;
comment|//<-- size of main array
name|out
operator|.
name|writeVInt
argument_list|(
name|numSourceIds
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//<-- size of offset array (+ 1 more entry)
name|int
name|prev
init|=
literal|0
decl_stmt|,
name|sourceId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|ofs
init|=
literal|0
init|;
name|ofs
operator|<
name|targetMapEndOffset
condition|;
name|ofs
operator|++
control|)
block|{
specifier|final
name|int
name|val
init|=
name|targetMap
index|[
name|ofs
index|]
decl_stmt|,
name|delta
init|=
name|val
operator|-
name|prev
decl_stmt|;
assert|assert
name|delta
operator|>=
literal|0
assert|;
if|if
condition|(
name|ofs
operator|==
name|targetMapOffsets
index|[
name|sourceId
index|]
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|0x01
argument_list|)
expr_stmt|;
name|sourceId
operator|++
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|prev
operator|+=
name|delta
expr_stmt|;
block|}
assert|assert
name|sourceId
operator|==
name|numSourceIds
operator|:
literal|"sourceId:"
operator|+
name|sourceId
operator|+
literal|" != numSourceIds:"
operator|+
name|numSourceIds
assert|;
block|}
finally|finally
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writePosDict
specifier|protected
name|void
name|writePosDict
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|File
argument_list|(
name|filename
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
specifier|final
name|DataOutput
name|out
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|BinaryDictionary
operator|.
name|POSDICT_HEADER
argument_list|,
name|BinaryDictionary
operator|.
name|VERSION
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|posDict
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|posDict
control|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|data
index|[]
init|=
name|CSVUtil
operator|.
name|parse
argument_list|(
name|s
argument_list|)
decl_stmt|;
assert|assert
name|data
operator|.
name|length
operator|==
literal|3
operator|:
literal|"malformed pos/inflection: "
operator|+
name|s
assert|;
name|out
operator|.
name|writeString
argument_list|(
name|data
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|data
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|data
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeDictionary
specifier|protected
name|void
name|writeDictionary
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|File
argument_list|(
name|filename
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
specifier|final
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|DataOutput
name|out
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|BinaryDictionary
operator|.
name|DICT_HEADER
argument_list|,
name|BinaryDictionary
operator|.
name|VERSION
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|buffer
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|WritableByteChannel
name|channel
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
name|os
argument_list|)
decl_stmt|;
comment|// Write Buffer
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
comment|// set position to 0, set limit to current position
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
assert|assert
name|buffer
operator|.
name|remaining
argument_list|()
operator|==
literal|0L
assert|;
block|}
finally|finally
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

