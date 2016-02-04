begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|tokenattributes
package|;
end_package

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

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UScript
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link ScriptAttribute} that stores the script  * as an integer.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ScriptAttributeImpl
specifier|public
class|class
name|ScriptAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|ScriptAttribute
implements|,
name|Cloneable
block|{
DECL|field|code
specifier|private
name|int
name|code
init|=
name|UScript
operator|.
name|COMMON
decl_stmt|;
comment|/** Initializes this attribute with<code>UScript.COMMON</code> */
DECL|method|ScriptAttributeImpl
specifier|public
name|ScriptAttributeImpl
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getCode
specifier|public
name|int
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
annotation|@
name|Override
DECL|method|setCode
specifier|public
name|void
name|setCode
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|UScript
operator|.
name|getName
argument_list|(
name|code
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getShortName
specifier|public
name|String
name|getShortName
parameter_list|()
block|{
return|return
name|UScript
operator|.
name|getShortName
argument_list|(
name|code
argument_list|)
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
name|code
operator|=
name|UScript
operator|.
name|COMMON
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
name|ScriptAttribute
name|t
init|=
operator|(
name|ScriptAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setCode
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|ScriptAttributeImpl
condition|)
block|{
return|return
operator|(
operator|(
name|ScriptAttributeImpl
operator|)
name|other
operator|)
operator|.
name|code
operator|==
name|code
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|code
return|;
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
comment|// when wordbreaking CJK, we use the 15924 code Japanese (Han+Hiragana+Katakana) to
comment|// mark runs of Chinese/Japanese. our use is correct (as for chinese Han is a subset),
comment|// but this is just to help prevent confusion.
name|String
name|name
init|=
name|code
operator|==
name|UScript
operator|.
name|JAPANESE
condition|?
literal|"Chinese/Japanese"
else|:
name|getName
argument_list|()
decl_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|ScriptAttribute
operator|.
name|class
argument_list|,
literal|"script"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

