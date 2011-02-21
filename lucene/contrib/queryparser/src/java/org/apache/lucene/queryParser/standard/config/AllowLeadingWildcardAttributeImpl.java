begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|standard
operator|.
name|processors
operator|.
name|AllowLeadingWildcardProcessor
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

begin_comment
comment|/**  * This attribute is used by {@link AllowLeadingWildcardProcessor} processor and  * must be defined in the {@link QueryConfigHandler}. It basically tells the  * processor if it should allow leading wildcard.<br/>  *   * @see org.apache.lucene.queryParser.standard.config.AllowLeadingWildcardAttribute  */
end_comment

begin_class
DECL|class|AllowLeadingWildcardAttributeImpl
specifier|public
class|class
name|AllowLeadingWildcardAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|AllowLeadingWildcardAttribute
block|{
DECL|field|allowLeadingWildcard
specifier|private
name|boolean
name|allowLeadingWildcard
init|=
literal|false
decl_stmt|;
comment|// default in 2.9
DECL|method|setAllowLeadingWildcard
specifier|public
name|void
name|setAllowLeadingWildcard
parameter_list|(
name|boolean
name|allowLeadingWildcard
parameter_list|)
block|{
name|this
operator|.
name|allowLeadingWildcard
operator|=
name|allowLeadingWildcard
expr_stmt|;
block|}
DECL|method|isAllowLeadingWildcard
specifier|public
name|boolean
name|isAllowLeadingWildcard
parameter_list|()
block|{
return|return
name|this
operator|.
name|allowLeadingWildcard
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|other
operator|instanceof
name|AllowLeadingWildcardAttributeImpl
operator|&&
operator|(
operator|(
name|AllowLeadingWildcardAttributeImpl
operator|)
name|other
operator|)
operator|.
name|allowLeadingWildcard
operator|==
name|this
operator|.
name|allowLeadingWildcard
condition|)
block|{
return|return
literal|true
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
name|this
operator|.
name|allowLeadingWildcard
condition|?
operator|-
literal|1
else|:
name|Integer
operator|.
name|MAX_VALUE
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
literal|"<allowLeadingWildcard allowLeadingWildcard="
operator|+
name|this
operator|.
name|allowLeadingWildcard
operator|+
literal|"/>"
return|;
block|}
block|}
end_class

end_unit

