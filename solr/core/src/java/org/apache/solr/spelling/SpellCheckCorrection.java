begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|analysis
operator|.
name|Token
import|;
end_import

begin_class
DECL|class|SpellCheckCorrection
specifier|public
class|class
name|SpellCheckCorrection
block|{
DECL|field|original
specifier|private
name|Token
name|original
decl_stmt|;
DECL|field|originalAsString
specifier|private
name|String
name|originalAsString
init|=
literal|null
decl_stmt|;
DECL|field|correction
specifier|private
name|String
name|correction
decl_stmt|;
DECL|field|numberOfOccurences
specifier|private
name|int
name|numberOfOccurences
decl_stmt|;
DECL|method|getOriginal
specifier|public
name|Token
name|getOriginal
parameter_list|()
block|{
return|return
name|original
return|;
block|}
DECL|method|getOriginalAsString
specifier|public
name|String
name|getOriginalAsString
parameter_list|()
block|{
if|if
condition|(
name|originalAsString
operator|==
literal|null
operator|&&
name|original
operator|!=
literal|null
condition|)
block|{
name|originalAsString
operator|=
name|original
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|originalAsString
return|;
block|}
DECL|method|setOriginal
specifier|public
name|void
name|setOriginal
parameter_list|(
name|Token
name|original
parameter_list|)
block|{
name|this
operator|.
name|original
operator|=
name|original
expr_stmt|;
name|this
operator|.
name|originalAsString
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getCorrection
specifier|public
name|String
name|getCorrection
parameter_list|()
block|{
return|return
name|correction
return|;
block|}
DECL|method|setCorrection
specifier|public
name|void
name|setCorrection
parameter_list|(
name|String
name|correction
parameter_list|)
block|{
name|this
operator|.
name|correction
operator|=
name|correction
expr_stmt|;
block|}
DECL|method|getNumberOfOccurences
specifier|public
name|int
name|getNumberOfOccurences
parameter_list|()
block|{
return|return
name|numberOfOccurences
return|;
block|}
DECL|method|setNumberOfOccurences
specifier|public
name|void
name|setNumberOfOccurences
parameter_list|(
name|int
name|numberOfOccurences
parameter_list|)
block|{
name|this
operator|.
name|numberOfOccurences
operator|=
name|numberOfOccurences
expr_stmt|;
block|}
block|}
end_class

end_unit

