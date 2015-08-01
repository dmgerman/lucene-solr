begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.rangetree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|rangetree
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
name|index
operator|.
name|SortedSetDocValues
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
name|TermsEnum
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

begin_class
DECL|class|RangeTreeSortedSetDocValues
class|class
name|RangeTreeSortedSetDocValues
extends|extends
name|SortedSetDocValues
block|{
DECL|field|rangeTreeReader
specifier|final
name|RangeTreeReader
name|rangeTreeReader
decl_stmt|;
DECL|field|delegate
specifier|final
name|SortedSetDocValues
name|delegate
decl_stmt|;
DECL|method|RangeTreeSortedSetDocValues
specifier|public
name|RangeTreeSortedSetDocValues
parameter_list|(
name|RangeTreeReader
name|rangeTreeReader
parameter_list|,
name|SortedSetDocValues
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|rangeTreeReader
operator|=
name|rangeTreeReader
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|getRangeTreeReader
specifier|public
name|RangeTreeReader
name|getRangeTreeReader
parameter_list|()
block|{
return|return
name|rangeTreeReader
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|nextOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|delegate
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|long
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termsEnum
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|termsEnum
argument_list|()
return|;
block|}
block|}
end_class

end_unit

