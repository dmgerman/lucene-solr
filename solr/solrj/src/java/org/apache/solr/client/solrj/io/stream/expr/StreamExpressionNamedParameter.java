begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.expr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Provides a named parameter  */
end_comment

begin_class
DECL|class|StreamExpressionNamedParameter
specifier|public
class|class
name|StreamExpressionNamedParameter
implements|implements
name|StreamExpressionParameter
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|parameter
specifier|private
name|StreamExpressionParameter
name|parameter
decl_stmt|;
DECL|method|StreamExpressionNamedParameter
specifier|public
name|StreamExpressionNamedParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|StreamExpressionNamedParameter
specifier|public
name|StreamExpressionNamedParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|parameter
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|setParameter
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
block|}
DECL|method|StreamExpressionNamedParameter
specifier|public
name|StreamExpressionNamedParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|StreamExpressionParameter
name|parameter
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|setParameter
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|name
operator|||
literal|0
operator|==
name|name
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Null or empty name is not allowed is not allowed."
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getParameter
specifier|public
name|StreamExpressionParameter
name|getParameter
parameter_list|()
block|{
return|return
name|this
operator|.
name|parameter
return|;
block|}
DECL|method|setParameter
specifier|public
name|void
name|setParameter
parameter_list|(
name|StreamExpressionParameter
name|parameter
parameter_list|)
block|{
name|this
operator|.
name|parameter
operator|=
name|parameter
expr_stmt|;
block|}
DECL|method|withParameter
specifier|public
name|StreamExpressionNamedParameter
name|withParameter
parameter_list|(
name|StreamExpressionParameter
name|parameter
parameter_list|)
block|{
name|setParameter
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setParameter
specifier|public
name|void
name|setParameter
parameter_list|(
name|String
name|parameter
parameter_list|)
block|{
name|this
operator|.
name|parameter
operator|=
operator|new
name|StreamExpressionValue
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
block|}
DECL|method|withParameter
specifier|public
name|StreamExpressionNamedParameter
name|withParameter
parameter_list|(
name|String
name|parameter
parameter_list|)
block|{
name|setParameter
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
return|return
name|this
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
comment|// check if we require quoting
name|boolean
name|requiresQuote
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|parameter
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
name|String
name|value
init|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|parameter
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|requiresQuote
operator|=
operator|!
name|StreamExpressionParser
operator|.
name|wordToken
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requiresQuote
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|parameter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|requiresQuote
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
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
operator|.
name|getClass
argument_list|()
operator|!=
name|StreamExpressionNamedParameter
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|StreamExpressionNamedParameter
name|check
init|=
operator|(
name|StreamExpressionNamedParameter
operator|)
name|other
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|name
operator|&&
literal|null
operator|!=
name|check
operator|.
name|name
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
literal|null
operator|!=
name|this
operator|.
name|name
operator|&&
literal|null
operator|==
name|check
operator|.
name|name
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
literal|null
operator|!=
name|this
operator|.
name|name
operator|&&
literal|null
operator|!=
name|check
operator|.
name|name
operator|&&
operator|!
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
name|check
operator|.
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|parameter
operator|.
name|equals
argument_list|(
name|check
operator|.
name|parameter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

