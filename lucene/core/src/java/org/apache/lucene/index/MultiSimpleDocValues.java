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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Codec
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
name|index
operator|.
name|IndexReader
operator|.
name|ReaderClosedListener
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
name|store
operator|.
name|RAMDirectory
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
name|Bits
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
name|BytesRef
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
name|IOUtils
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
name|Version
import|;
end_import

begin_class
DECL|class|MultiSimpleDocValues
specifier|public
class|class
name|MultiSimpleDocValues
block|{
comment|// moved to src/java so SlowWrapper can use it... uggggggh
DECL|method|simpleNormValues
specifier|public
specifier|static
name|NumericDocValues
name|simpleNormValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaves
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|simpleNormValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|FieldInfo
name|fi
init|=
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|r
argument_list|)
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
operator|||
name|fi
operator|.
name|hasNorms
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|null
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
name|NumericDocValues
name|norms
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|simpleNormValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norms
operator|!=
literal|null
condition|)
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// assert anyReal; // nocommit: unsafe until 4.0 is done
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
name|NumericDocValues
name|norms
decl_stmt|;
try|try
block|{
name|norms
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|simpleNormValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
comment|// WTF? should be EMPTY?
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|norms
operator|.
name|get
argument_list|(
name|docID
operator|-
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
operator|.
name|docBase
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
DECL|method|simpleNumericValues
specifier|public
specifier|static
name|NumericDocValues
name|simpleNumericValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaves
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
name|NumericDocValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|anyReal
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
name|NumericDocValues
name|values
decl_stmt|;
try|try
block|{
name|values
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|docID
operator|-
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
operator|.
name|docBase
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
block|}
DECL|method|simpleBinaryValues
specifier|public
specifier|static
name|BinaryDocValues
name|simpleBinaryValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaves
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
name|BinaryDocValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|anyReal
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
name|BinaryDocValues
name|values
decl_stmt|;
try|try
block|{
name|values
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|values
operator|.
name|get
argument_list|(
name|docID
operator|-
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
operator|.
name|docBase
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
name|BinaryDocValues
operator|.
name|MISSING
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
DECL|method|simpleSortedValues
specifier|public
specifier|static
name|SortedDocValues
name|simpleSortedValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaves
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
name|SortedDocValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|anyReal
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// its called slow-wrapper for a reason right?
specifier|final
name|Directory
name|scratch
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|config
operator|.
name|setCodec
argument_list|(
name|Codec
operator|.
name|forName
argument_list|(
literal|"SimpleText"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|scratch
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|newLeaves
init|=
operator|new
name|ArrayList
argument_list|<
name|AtomicReader
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
specifier|final
name|AtomicReader
name|a
init|=
name|ctx
operator|.
name|reader
argument_list|()
decl_stmt|;
name|newLeaves
operator|.
name|add
argument_list|(
operator|new
name|FilterAtomicReader
argument_list|(
name|a
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// lie
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addIndexes
argument_list|(
name|newLeaves
operator|.
name|toArray
argument_list|(
operator|new
name|AtomicReader
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|newR
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
assert|assert
name|newR
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
name|r
operator|.
name|addReaderClosedListener
argument_list|(
operator|new
name|ReaderClosedListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|newR
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|newR
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

