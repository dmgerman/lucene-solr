begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link Predicate} which returns true if the BytesRef contains a given substring.  */
end_comment

begin_class
DECL|class|SubstringBytesRefFilter
specifier|public
class|class
name|SubstringBytesRefFilter
implements|implements
name|Predicate
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|contains
specifier|final
specifier|private
name|String
name|contains
decl_stmt|;
DECL|field|ignoreCase
specifier|final
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
DECL|method|SubstringBytesRefFilter
specifier|public
name|SubstringBytesRefFilter
parameter_list|(
name|String
name|contains
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
operator|.
name|contains
operator|=
name|contains
expr_stmt|;
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
block|}
DECL|method|substring
specifier|public
name|String
name|substring
parameter_list|()
block|{
return|return
name|contains
return|;
block|}
DECL|method|includeString
specifier|protected
name|boolean
name|includeString
parameter_list|(
name|String
name|term
parameter_list|)
block|{
if|if
condition|(
name|ignoreCase
condition|)
block|{
return|return
name|StringUtils
operator|.
name|containsIgnoreCase
argument_list|(
name|term
argument_list|,
name|contains
argument_list|)
return|;
block|}
return|return
name|StringUtils
operator|.
name|contains
argument_list|(
name|term
argument_list|,
name|contains
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|test
specifier|public
name|boolean
name|test
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|includeString
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

