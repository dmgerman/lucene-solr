begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AttributeImpl
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
comment|/** Implementation class for {@link MaxNonCompetitiveBoostAttribute}.  * @lucene.internal  */
end_comment

begin_class
DECL|class|MaxNonCompetitiveBoostAttributeImpl
specifier|public
specifier|final
class|class
name|MaxNonCompetitiveBoostAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|MaxNonCompetitiveBoostAttribute
block|{
DECL|field|maxNonCompetitiveBoost
specifier|private
name|float
name|maxNonCompetitiveBoost
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|competitiveTerm
specifier|private
name|BytesRef
name|competitiveTerm
init|=
literal|null
decl_stmt|;
DECL|method|setMaxNonCompetitiveBoost
specifier|public
name|void
name|setMaxNonCompetitiveBoost
parameter_list|(
specifier|final
name|float
name|maxNonCompetitiveBoost
parameter_list|)
block|{
name|this
operator|.
name|maxNonCompetitiveBoost
operator|=
name|maxNonCompetitiveBoost
expr_stmt|;
block|}
DECL|method|getMaxNonCompetitiveBoost
specifier|public
name|float
name|getMaxNonCompetitiveBoost
parameter_list|()
block|{
return|return
name|maxNonCompetitiveBoost
return|;
block|}
DECL|method|setCompetitiveTerm
specifier|public
name|void
name|setCompetitiveTerm
parameter_list|(
specifier|final
name|BytesRef
name|competitiveTerm
parameter_list|)
block|{
name|this
operator|.
name|competitiveTerm
operator|=
name|competitiveTerm
expr_stmt|;
block|}
DECL|method|getCompetitiveTerm
specifier|public
name|BytesRef
name|getCompetitiveTerm
parameter_list|()
block|{
return|return
name|competitiveTerm
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|maxNonCompetitiveBoost
operator|=
name|Float
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
name|competitiveTerm
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|MaxNonCompetitiveBoostAttributeImpl
name|t
init|=
operator|(
name|MaxNonCompetitiveBoostAttributeImpl
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setMaxNonCompetitiveBoost
argument_list|(
name|maxNonCompetitiveBoost
argument_list|)
expr_stmt|;
name|t
operator|.
name|setCompetitiveTerm
argument_list|(
name|competitiveTerm
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

