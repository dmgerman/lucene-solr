begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|bloom
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|codecs
operator|.
name|PostingsFormat
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
name|lucene40
operator|.
name|Lucene40Codec
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
name|BasePostingsFormatTestCase
import|;
end_import

begin_comment
comment|/**  * Basic tests for BloomPostingsFormat  */
end_comment

begin_class
DECL|class|TestBloomPostingsFormat
specifier|public
class|class
name|TestBloomPostingsFormat
extends|extends
name|BasePostingsFormatTestCase
block|{
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
operator|new
name|TestBloomFilteredLucene40Postings
argument_list|()
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
init|=
operator|new
name|Lucene40Codec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|postings
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
block|}
end_class

end_unit

