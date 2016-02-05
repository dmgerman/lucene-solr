begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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

begin_comment
comment|/** Default implementation of {@link FlagsAttribute}. */
end_comment

begin_class
DECL|class|FlagsAttributeImpl
specifier|public
class|class
name|FlagsAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|FlagsAttribute
implements|,
name|Cloneable
block|{
DECL|field|flags
specifier|private
name|int
name|flags
init|=
literal|0
decl_stmt|;
comment|/** Initialize this attribute with no bits set */
DECL|method|FlagsAttributeImpl
specifier|public
name|FlagsAttributeImpl
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getFlags
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
annotation|@
name|Override
DECL|method|setFlags
specifier|public
name|void
name|setFlags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
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
name|flags
operator|=
literal|0
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
name|FlagsAttributeImpl
condition|)
block|{
return|return
operator|(
operator|(
name|FlagsAttributeImpl
operator|)
name|other
operator|)
operator|.
name|flags
operator|==
name|flags
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
name|flags
return|;
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
name|FlagsAttribute
name|t
init|=
operator|(
name|FlagsAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setFlags
argument_list|(
name|flags
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
name|reflector
operator|.
name|reflect
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|,
literal|"flags"
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

