begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.cranky
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|cranky
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
name|Random
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
name|StoredFieldsFormat
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
name|StoredFieldsReader
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
name|StoredFieldsWriter
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
name|IndexableField
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
name|MergeState
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
name|SegmentInfo
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

begin_class
DECL|class|CrankyStoredFieldsFormat
class|class
name|CrankyStoredFieldsFormat
extends|extends
name|StoredFieldsFormat
block|{
DECL|field|delegate
specifier|final
name|StoredFieldsFormat
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyStoredFieldsFormat
name|CrankyStoredFieldsFormat
parameter_list|(
name|StoredFieldsFormat
name|delegate
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|StoredFieldsReader
name|fieldsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|fieldsReader
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|fn
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|StoredFieldsWriter
name|fieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsFormat.fieldsWriter()"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CrankyStoredFieldsWriter
argument_list|(
name|delegate
operator|.
name|fieldsWriter
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|context
argument_list|)
argument_list|,
name|random
argument_list|)
return|;
block|}
DECL|class|CrankyStoredFieldsWriter
specifier|static
class|class
name|CrankyStoredFieldsWriter
extends|extends
name|StoredFieldsWriter
block|{
DECL|field|delegate
specifier|final
name|StoredFieldsWriter
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyStoredFieldsWriter
name|CrankyStoredFieldsWriter
parameter_list|(
name|StoredFieldsWriter
name|delegate
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsWriter.finish()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|finish
argument_list|(
name|fis
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|int
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsWriter.merge()"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsWriter.close()"
argument_list|)
throw|;
block|}
block|}
comment|// per doc/field methods: lower probability since they are invoked so many times.
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsWriter.startDocument()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
specifier|public
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsWriter.finishDocument()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|IndexableField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException from StoredFieldsWriter.writeField()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|writeField
argument_list|(
name|info
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

