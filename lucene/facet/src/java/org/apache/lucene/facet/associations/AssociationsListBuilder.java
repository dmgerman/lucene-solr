begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
package|;
end_package

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|facet
operator|.
name|index
operator|.
name|CategoryListBuilder
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
name|facet
operator|.
name|index
operator|.
name|CountingListBuilder
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|ByteArrayDataOutput
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
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link AssociationsListBuilder} which encodes category-association value pairs.  * Every category-association pair is written under the respective association's  * {@link CategoryAssociation#getCategoryListID()}.  *<p>  *<b>NOTE:</b> associations list do not encode the counting list data. You  * should use {@link CountingListBuilder} to build that information and then  * merge the results of both {@link #build(IntsRef, Iterable)}.  */
end_comment

begin_class
DECL|class|AssociationsListBuilder
specifier|public
class|class
name|AssociationsListBuilder
implements|implements
name|CategoryListBuilder
block|{
DECL|field|associations
specifier|private
specifier|final
name|CategoryAssociationsContainer
name|associations
decl_stmt|;
DECL|field|output
specifier|private
specifier|final
name|ByteArrayDataOutput
name|output
init|=
operator|new
name|ByteArrayDataOutput
argument_list|()
decl_stmt|;
DECL|method|AssociationsListBuilder
specifier|public
name|AssociationsListBuilder
parameter_list|(
name|CategoryAssociationsContainer
name|associations
parameter_list|)
block|{
name|this
operator|.
name|associations
operator|=
name|associations
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|build
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|res
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CategoryPath
name|cp
range|:
name|categories
control|)
block|{
comment|// build per-association key BytesRef
name|CategoryAssociation
name|association
init|=
name|associations
operator|.
name|getAssociation
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|res
operator|.
name|get
argument_list|(
name|association
operator|.
name|getCategoryListID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
name|association
operator|.
name|getCategoryListID
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
name|int
name|maxBytesNeeded
init|=
literal|4
comment|/* int */
operator|+
name|association
operator|.
name|maxBytesNeeded
argument_list|()
operator|+
name|bytes
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|bytes
operator|.
name|length
operator|<
name|maxBytesNeeded
condition|)
block|{
name|bytes
operator|.
name|grow
argument_list|(
name|maxBytesNeeded
argument_list|)
expr_stmt|;
block|}
comment|// reset the output to write from bytes.length (current position) until the end
name|output
operator|.
name|reset
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|bytes
operator|.
name|bytes
operator|.
name|length
operator|-
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|ordinals
operator|.
name|ints
index|[
name|idx
operator|++
index|]
argument_list|)
expr_stmt|;
comment|// encode the association bytes
name|association
operator|.
name|serialize
argument_list|(
name|output
argument_list|)
expr_stmt|;
comment|// update BytesRef
name|bytes
operator|.
name|length
operator|=
name|output
operator|.
name|getPosition
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

