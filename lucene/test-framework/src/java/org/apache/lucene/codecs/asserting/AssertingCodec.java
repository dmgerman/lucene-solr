begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|DocValuesFormat
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
name|FilterCodec
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
name|NormsFormat
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
name|TermVectorsFormat
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
name|lucene410
operator|.
name|Lucene410Codec
import|;
end_import

begin_comment
comment|/**  * Acts like {@link Lucene410Codec} but with additional asserts.  */
end_comment

begin_class
DECL|class|AssertingCodec
specifier|public
specifier|final
class|class
name|AssertingCodec
extends|extends
name|FilterCodec
block|{
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
operator|new
name|AssertingPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|vectors
specifier|private
specifier|final
name|TermVectorsFormat
name|vectors
init|=
operator|new
name|AssertingTermVectorsFormat
argument_list|()
decl_stmt|;
DECL|field|storedFields
specifier|private
specifier|final
name|StoredFieldsFormat
name|storedFields
init|=
operator|new
name|AssertingStoredFieldsFormat
argument_list|()
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|DocValuesFormat
name|docValues
init|=
operator|new
name|AssertingDocValuesFormat
argument_list|()
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|NormsFormat
name|norms
init|=
operator|new
name|AssertingNormsFormat
argument_list|()
decl_stmt|;
DECL|method|AssertingCodec
specifier|public
name|AssertingCodec
parameter_list|()
block|{
name|super
argument_list|(
literal|"Asserting"
argument_list|,
operator|new
name|Lucene410Codec
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|postings
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|vectors
return|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|storedFields
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|norms
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Asserting("
operator|+
name|delegate
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

