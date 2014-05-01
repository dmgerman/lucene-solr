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
name|FieldInfosFormat
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
name|FieldInfosReader
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
DECL|class|CrankyFieldInfosFormat
class|class
name|CrankyFieldInfosFormat
extends|extends
name|FieldInfosFormat
block|{
DECL|field|delegate
specifier|final
name|FieldInfosFormat
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyFieldInfosFormat
name|CrankyFieldInfosFormat
parameter_list|(
name|FieldInfosFormat
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
DECL|method|getFieldInfosReader
specifier|public
name|FieldInfosReader
name|getFieldInfosReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getFieldInfosReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfosWriter
specifier|public
name|FieldInfosWriter
name|getFieldInfosWriter
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
literal|"Fake IOException from FieldInfosFormat.getFieldInfosWriter()"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CrankyFieldInfosWriter
argument_list|(
name|delegate
operator|.
name|getFieldInfosWriter
argument_list|()
argument_list|,
name|random
argument_list|)
return|;
block|}
DECL|class|CrankyFieldInfosWriter
specifier|static
class|class
name|CrankyFieldInfosWriter
extends|extends
name|FieldInfosWriter
block|{
DECL|field|delegate
specifier|final
name|FieldInfosWriter
name|delegate
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|CrankyFieldInfosWriter
name|CrankyFieldInfosWriter
parameter_list|(
name|FieldInfosWriter
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
name|String
name|segmentSuffix
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
literal|"Fake IOException from FieldInfosWriter.write()"
argument_list|)
throw|;
block|}
name|delegate
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|segmentName
argument_list|,
name|segmentSuffix
argument_list|,
name|infos
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

