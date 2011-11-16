begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|simpletext
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
name|index
operator|.
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|codecs
operator|.
name|FieldInfosWriter
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
name|IOContext
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * writes plaintext field infos files  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextFieldInfosWriter
specifier|public
class|class
name|SimpleTextFieldInfosWriter
extends|extends
name|FieldInfosWriter
block|{
comment|/** Extension of field infos */
DECL|field|FIELD_INFOS_EXTENSION
specifier|static
specifier|final
name|String
name|FIELD_INFOS_EXTENSION
init|=
literal|"inf"
decl_stmt|;
DECL|field|NUMFIELDS
specifier|static
specifier|final
name|BytesRef
name|NUMFIELDS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"number of fields "
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|static
specifier|final
name|BytesRef
name|NAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  name "
argument_list|)
decl_stmt|;
DECL|field|NUMBER
specifier|static
specifier|final
name|BytesRef
name|NUMBER
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  number "
argument_list|)
decl_stmt|;
DECL|field|ISINDEXED
specifier|static
specifier|final
name|BytesRef
name|ISINDEXED
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  indexed "
argument_list|)
decl_stmt|;
DECL|field|STORETV
specifier|static
specifier|final
name|BytesRef
name|STORETV
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term vectors "
argument_list|)
decl_stmt|;
DECL|field|STORETVPOS
specifier|static
specifier|final
name|BytesRef
name|STORETVPOS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term vector positions "
argument_list|)
decl_stmt|;
DECL|field|STORETVOFF
specifier|static
specifier|final
name|BytesRef
name|STORETVOFF
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term vector offsets "
argument_list|)
decl_stmt|;
DECL|field|PAYLOADS
specifier|static
specifier|final
name|BytesRef
name|PAYLOADS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  payloads "
argument_list|)
decl_stmt|;
DECL|field|NORMS
specifier|static
specifier|final
name|BytesRef
name|NORMS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  norms "
argument_list|)
decl_stmt|;
DECL|field|DOCVALUES
specifier|static
specifier|final
name|BytesRef
name|DOCVALUES
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  doc values "
argument_list|)
decl_stmt|;
DECL|field|INDEXOPTIONS
specifier|static
specifier|final
name|BytesRef
name|INDEXOPTIONS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  index options "
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|FieldInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|FIELD_INFOS_EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
try|try
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NUMFIELDS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
assert|assert
name|fi
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
operator|||
operator|!
name|fi
operator|.
name|storePayloads
assert|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|fi
operator|.
name|name
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NUMBER
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|number
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|ISINDEXED
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|isIndexed
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|STORETV
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|storeTermVector
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|STORETVPOS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|storePositionWithTermVector
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|STORETVOFF
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|storeOffsetWithTermVector
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|PAYLOADS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|storePayloads
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NORMS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
operator|!
name|fi
operator|.
name|omitNorms
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|DOCVALUES
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|"false"
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|fi
operator|.
name|getDocValues
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|INDEXOPTIONS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|fi
operator|.
name|indexOptions
operator|.
name|toString
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

