begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|tokenattributes
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
name|analysis
operator|.
name|ja
operator|.
name|Token
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
name|analysis
operator|.
name|ja
operator|.
name|util
operator|.
name|ToStringUtil
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
name|AttributeReflector
import|;
end_import

begin_comment
comment|/**  * Attribute for {@link Token#getPartOfSpeech()}.  */
end_comment

begin_class
DECL|class|PartOfSpeechAttributeImpl
specifier|public
class|class
name|PartOfSpeechAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|PartOfSpeechAttribute
implements|,
name|Cloneable
block|{
DECL|field|token
specifier|private
name|Token
name|token
decl_stmt|;
annotation|@
name|Override
DECL|method|getPartOfSpeech
specifier|public
name|String
name|getPartOfSpeech
parameter_list|()
block|{
return|return
name|token
operator|==
literal|null
condition|?
literal|null
else|:
name|token
operator|.
name|getPartOfSpeech
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setToken
specifier|public
name|void
name|setToken
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|token
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
name|PartOfSpeechAttribute
name|t
init|=
operator|(
name|PartOfSpeechAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|String
name|partOfSpeech
init|=
name|getPartOfSpeech
argument_list|()
decl_stmt|;
name|String
name|partOfSpeechEN
init|=
name|partOfSpeech
operator|==
literal|null
condition|?
literal|null
else|:
name|ToStringUtil
operator|.
name|getPOSTranslation
argument_list|(
name|partOfSpeech
argument_list|)
decl_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|PartOfSpeechAttribute
operator|.
name|class
argument_list|,
literal|"partOfSpeech"
argument_list|,
name|partOfSpeech
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|PartOfSpeechAttribute
operator|.
name|class
argument_list|,
literal|"partOfSpeech (en)"
argument_list|,
name|partOfSpeechEN
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

