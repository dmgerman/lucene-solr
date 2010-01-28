begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|Directory
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
name|UnicodeUtil
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

begin_comment
comment|/** This stores a monotonically increasing set of<Term, TermInfo> pairs in a   Directory.  A TermInfos can be written once, in order.  */
end_comment

begin_class
DECL|class|TermInfosWriter
specifier|final
class|class
name|TermInfosWriter
block|{
comment|/** The file format version, a negative number. */
DECL|field|FORMAT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT
init|=
operator|-
literal|3
decl_stmt|;
comment|// Changed strings to true utf8 with length-in-bytes not
comment|// length-in-chars
DECL|field|FORMAT_VERSION_UTF8_LENGTH_IN_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_VERSION_UTF8_LENGTH_IN_BYTES
init|=
operator|-
literal|4
decl_stmt|;
comment|// NOTE: always change this if you switch to a new format!
DECL|field|FORMAT_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_VERSION_UTF8_LENGTH_IN_BYTES
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|output
specifier|private
name|IndexOutput
name|output
decl_stmt|;
DECL|field|lastTi
specifier|private
name|TermInfo
name|lastTi
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
decl_stmt|;
comment|// TODO: the default values for these two parameters should be settable from
comment|// IndexWriter.  However, once that's done, folks will start setting them to
comment|// ridiculous values and complaining that things don't work well, as with
comment|// mergeFactor.  So, let's wait until a number of folks find that alternate
comment|// values work better.  Note that both of these values are stored in the
comment|// segment, so that it's safe to change these w/o rebuilding all indexes.
comment|/** Expert: The fraction of terms in the "dictionary" which should be stored    * in RAM.  Smaller values use more memory, but make searching slightly    * faster, while larger values use less memory and make searching slightly    * slower.  Searching is typically not dominated by dictionary lookup, so    * tweaking this is rarely useful.*/
DECL|field|indexInterval
name|int
name|indexInterval
init|=
literal|128
decl_stmt|;
comment|/** Expert: The fraction of {@link TermDocs} entries stored in skip tables,    * used to accelerate {@link TermDocs#skipTo(int)}.  Larger values result in    * smaller indexes, greater acceleration, but fewer accelerable cases, while    * smaller values result in bigger indexes, less acceleration and more    * accelerable cases. More detailed experiments would be useful here. */
DECL|field|skipInterval
name|int
name|skipInterval
init|=
literal|16
decl_stmt|;
comment|/** Expert: The maximum number of skip levels. Smaller values result in     * slightly smaller indexes, but slower skipping in big posting lists.    */
DECL|field|maxSkipLevels
name|int
name|maxSkipLevels
init|=
literal|10
decl_stmt|;
DECL|field|lastIndexPointer
specifier|private
name|long
name|lastIndexPointer
decl_stmt|;
DECL|field|isIndex
specifier|private
name|boolean
name|isIndex
decl_stmt|;
DECL|field|lastTermBytes
specifier|private
name|byte
index|[]
name|lastTermBytes
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
DECL|field|lastTermBytesLength
specifier|private
name|int
name|lastTermBytesLength
init|=
literal|0
decl_stmt|;
DECL|field|lastFieldNumber
specifier|private
name|int
name|lastFieldNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|other
specifier|private
name|TermInfosWriter
name|other
decl_stmt|;
DECL|field|utf8Result
specifier|private
name|UnicodeUtil
operator|.
name|UTF8Result
name|utf8Result
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
decl_stmt|;
DECL|method|TermInfosWriter
name|TermInfosWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|.
name|other
operator|=
name|this
expr_stmt|;
block|}
DECL|method|TermInfosWriter
specifier|private
name|TermInfosWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|,
name|boolean
name|isIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|fis
argument_list|,
name|interval
argument_list|,
name|isIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|int
name|interval
parameter_list|,
name|boolean
name|isi
parameter_list|)
throws|throws
name|IOException
block|{
name|indexInterval
operator|=
name|interval
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
name|isIndex
operator|=
name|isi
expr_stmt|;
name|output
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
operator|(
name|isIndex
condition|?
literal|".tii"
else|:
literal|".tis"
operator|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
comment|// write format
name|output
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// leave space for size
name|output
operator|.
name|writeInt
argument_list|(
name|indexInterval
argument_list|)
expr_stmt|;
comment|// write indexInterval
name|output
operator|.
name|writeInt
argument_list|(
name|skipInterval
argument_list|)
expr_stmt|;
comment|// write skipInterval
name|output
operator|.
name|writeInt
argument_list|(
name|maxSkipLevels
argument_list|)
expr_stmt|;
comment|// write maxSkipLevels
assert|assert
name|initUTF16Results
argument_list|()
assert|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|,
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|term
operator|.
name|text
argument_list|,
literal|0
argument_list|,
name|term
operator|.
name|text
operator|.
name|length
argument_list|()
argument_list|,
name|utf8Result
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|term
operator|.
name|field
argument_list|)
argument_list|,
name|utf8Result
operator|.
name|result
argument_list|,
name|utf8Result
operator|.
name|length
argument_list|,
name|ti
argument_list|)
expr_stmt|;
block|}
comment|// Currently used only by assert statements
DECL|field|utf16Result1
name|UnicodeUtil
operator|.
name|UTF16Result
name|utf16Result1
decl_stmt|;
DECL|field|utf16Result2
name|UnicodeUtil
operator|.
name|UTF16Result
name|utf16Result2
decl_stmt|;
comment|// Currently used only by assert statements
DECL|method|initUTF16Results
specifier|private
name|boolean
name|initUTF16Results
parameter_list|()
block|{
name|utf16Result1
operator|=
operator|new
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|()
expr_stmt|;
name|utf16Result2
operator|=
operator|new
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// Currently used only by assert statement
DECL|method|compareToLastTerm
specifier|private
name|int
name|compareToLastTerm
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|byte
index|[]
name|termBytes
parameter_list|,
name|int
name|termBytesLength
parameter_list|)
block|{
if|if
condition|(
name|lastFieldNumber
operator|!=
name|fieldNumber
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|lastFieldNumber
argument_list|)
operator|.
name|compareTo
argument_list|(
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|fieldNumber
argument_list|)
argument_list|)
decl_stmt|;
comment|// If there is a field named "" (empty string) then we
comment|// will get 0 on this comparison, yet, it's "OK".  But
comment|// it's not OK if two different field numbers map to
comment|// the same name.
if|if
condition|(
name|cmp
operator|!=
literal|0
operator|||
name|lastFieldNumber
operator|!=
operator|-
literal|1
condition|)
return|return
name|cmp
return|;
block|}
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|lastTermBytes
argument_list|,
literal|0
argument_list|,
name|lastTermBytesLength
argument_list|,
name|utf16Result1
argument_list|)
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|termBytes
argument_list|,
literal|0
argument_list|,
name|termBytesLength
argument_list|,
name|utf16Result2
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
if|if
condition|(
name|utf16Result1
operator|.
name|length
operator|<
name|utf16Result2
operator|.
name|length
condition|)
name|len
operator|=
name|utf16Result1
operator|.
name|length
expr_stmt|;
else|else
name|len
operator|=
name|utf16Result2
operator|.
name|length
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
name|len
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|ch1
init|=
name|utf16Result1
operator|.
name|result
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|char
name|ch2
init|=
name|utf16Result2
operator|.
name|result
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|ch1
operator|!=
name|ch2
condition|)
return|return
name|ch1
operator|-
name|ch2
return|;
block|}
return|return
name|utf16Result1
operator|.
name|length
operator|-
name|utf16Result2
operator|.
name|length
return|;
block|}
comment|/** Adds a new<<fieldNumber, termBytes>, TermInfo> pair to the set.     Term must be lexicographically greater than all previous Terms added.     TermInfo pointers must be positive and greater than all previous.*/
DECL|method|add
name|void
name|add
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|byte
index|[]
name|termBytes
parameter_list|,
name|int
name|termBytesLength
parameter_list|,
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|compareToLastTerm
argument_list|(
name|fieldNumber
argument_list|,
name|termBytes
argument_list|,
name|termBytesLength
argument_list|)
operator|<
literal|0
operator|||
operator|(
name|isIndex
operator|&&
name|termBytesLength
operator|==
literal|0
operator|&&
name|lastTermBytesLength
operator|==
literal|0
operator|)
operator|:
literal|"Terms are out of order: field="
operator|+
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|fieldNumber
argument_list|)
operator|+
literal|" (number "
operator|+
name|fieldNumber
operator|+
literal|")"
operator|+
literal|" lastField="
operator|+
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|lastFieldNumber
argument_list|)
operator|+
literal|" (number "
operator|+
name|lastFieldNumber
operator|+
literal|")"
operator|+
literal|" text="
operator|+
operator|new
name|String
argument_list|(
name|termBytes
argument_list|,
literal|0
argument_list|,
name|termBytesLength
argument_list|,
literal|"UTF-8"
argument_list|)
operator|+
literal|" lastText="
operator|+
operator|new
name|String
argument_list|(
name|lastTermBytes
argument_list|,
literal|0
argument_list|,
name|lastTermBytesLength
argument_list|,
literal|"UTF-8"
argument_list|)
assert|;
assert|assert
name|ti
operator|.
name|freqPointer
operator|>=
name|lastTi
operator|.
name|freqPointer
operator|:
literal|"freqPointer out of order ("
operator|+
name|ti
operator|.
name|freqPointer
operator|+
literal|"< "
operator|+
name|lastTi
operator|.
name|freqPointer
operator|+
literal|")"
assert|;
assert|assert
name|ti
operator|.
name|proxPointer
operator|>=
name|lastTi
operator|.
name|proxPointer
operator|:
literal|"proxPointer out of order ("
operator|+
name|ti
operator|.
name|proxPointer
operator|+
literal|"< "
operator|+
name|lastTi
operator|.
name|proxPointer
operator|+
literal|")"
assert|;
if|if
condition|(
operator|!
name|isIndex
operator|&&
name|size
operator|%
name|indexInterval
operator|==
literal|0
condition|)
name|other
operator|.
name|add
argument_list|(
name|lastFieldNumber
argument_list|,
name|lastTermBytes
argument_list|,
name|lastTermBytesLength
argument_list|,
name|lastTi
argument_list|)
expr_stmt|;
comment|// add an index term
name|writeTerm
argument_list|(
name|fieldNumber
argument_list|,
name|termBytes
argument_list|,
name|termBytesLength
argument_list|)
expr_stmt|;
comment|// write term
name|output
operator|.
name|writeVInt
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|)
expr_stmt|;
comment|// write doc freq
name|output
operator|.
name|writeVLong
argument_list|(
name|ti
operator|.
name|freqPointer
operator|-
name|lastTi
operator|.
name|freqPointer
argument_list|)
expr_stmt|;
comment|// write pointers
name|output
operator|.
name|writeVLong
argument_list|(
name|ti
operator|.
name|proxPointer
operator|-
name|lastTi
operator|.
name|proxPointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|>=
name|skipInterval
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|ti
operator|.
name|skipOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isIndex
condition|)
block|{
name|output
operator|.
name|writeVLong
argument_list|(
name|other
operator|.
name|output
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastIndexPointer
argument_list|)
expr_stmt|;
name|lastIndexPointer
operator|=
name|other
operator|.
name|output
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|// write pointer
block|}
name|lastFieldNumber
operator|=
name|fieldNumber
expr_stmt|;
name|lastTi
operator|.
name|set
argument_list|(
name|ti
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
DECL|method|writeTerm
specifier|private
name|void
name|writeTerm
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|byte
index|[]
name|termBytes
parameter_list|,
name|int
name|termBytesLength
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: UTF16toUTF8 could tell us this prefix
comment|// Compute prefix in common with last term:
name|int
name|start
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|termBytesLength
operator|<
name|lastTermBytesLength
condition|?
name|termBytesLength
else|:
name|lastTermBytesLength
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|limit
condition|)
block|{
if|if
condition|(
name|termBytes
index|[
name|start
index|]
operator|!=
name|lastTermBytes
index|[
name|start
index|]
condition|)
break|break;
name|start
operator|++
expr_stmt|;
block|}
specifier|final
name|int
name|length
init|=
name|termBytesLength
operator|-
name|start
decl_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// write shared prefix length
name|output
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
comment|// write delta length
name|output
operator|.
name|writeBytes
argument_list|(
name|termBytes
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// write delta bytes
name|output
operator|.
name|writeVInt
argument_list|(
name|fieldNumber
argument_list|)
expr_stmt|;
comment|// write field num
if|if
condition|(
name|lastTermBytes
operator|.
name|length
operator|<
name|termBytesLength
condition|)
block|{
name|lastTermBytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|lastTermBytes
argument_list|,
name|termBytesLength
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|termBytes
argument_list|,
name|start
argument_list|,
name|lastTermBytes
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|lastTermBytesLength
operator|=
name|termBytesLength
expr_stmt|;
block|}
comment|/** Called to complete TermInfos creation. */
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|output
operator|.
name|seek
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// write size after format
name|output
operator|.
name|writeLong
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isIndex
condition|)
name|other
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

