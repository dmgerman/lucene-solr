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
name|util
operator|.
name|Objects
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
name|DocValuesProducer
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
name|FieldsProducer
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
name|NormsProducer
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
name|TermVectorsReader
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

begin_comment
comment|/**   * A<code>FilterCodecReader</code> contains another CodecReader, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality.  */
end_comment

begin_class
DECL|class|FilterCodecReader
specifier|public
class|class
name|FilterCodecReader
extends|extends
name|CodecReader
block|{
comment|/**     * The underlying CodecReader instance.     */
DECL|field|in
specifier|protected
specifier|final
name|CodecReader
name|in
decl_stmt|;
comment|/**    * Creates a new FilterCodecReader.    * @param in the underlying CodecReader instance.    */
DECL|method|FilterCodecReader
specifier|public
name|FilterCodecReader
parameter_list|(
name|CodecReader
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldsReader
specifier|public
name|StoredFieldsReader
name|getFieldsReader
parameter_list|()
block|{
return|return
name|in
operator|.
name|getFieldsReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectorsReader
specifier|public
name|TermVectorsReader
name|getTermVectorsReader
parameter_list|()
block|{
return|return
name|in
operator|.
name|getTermVectorsReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNormsReader
specifier|public
name|NormsProducer
name|getNormsReader
parameter_list|()
block|{
return|return
name|in
operator|.
name|getNormsReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesReader
specifier|public
name|DocValuesProducer
name|getDocValuesReader
parameter_list|()
block|{
return|return
name|in
operator|.
name|getDocValuesReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPostingsReader
specifier|public
name|FieldsProducer
name|getPostingsReader
parameter_list|()
block|{
return|return
name|in
operator|.
name|getPostingsReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|getLiveDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|in
operator|.
name|getFieldInfos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|numDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|in
operator|.
name|maxDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|in
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|in
operator|.
name|removeCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

