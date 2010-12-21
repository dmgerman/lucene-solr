begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|analysis
operator|.
name|TokenStream
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
name|icu
operator|.
name|ICUTransformFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|BaseTokenFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
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
name|text
operator|.
name|Transliterator
import|;
end_import

begin_comment
comment|/**  * Factory for {@link ICUTransformFilter}.  *<p>  * Supports the following attributes:  *<ul>  *<li>id (mandatory): A Transliterator ID, one from {@link Transliterator#getAvailableIDs()}  *<li>direction (optional): Either 'forward' or 'reverse'. Default is forward.  *</ul>  * @see Transliterator  */
end_comment

begin_class
DECL|class|ICUTransformFilterFactory
specifier|public
class|class
name|ICUTransformFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|transliterator
specifier|private
name|Transliterator
name|transliterator
decl_stmt|;
comment|// TODO: add support for custom rules
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|args
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"id is required."
argument_list|)
throw|;
block|}
name|int
name|dir
decl_stmt|;
name|String
name|direction
init|=
name|args
operator|.
name|get
argument_list|(
literal|"direction"
argument_list|)
decl_stmt|;
if|if
condition|(
name|direction
operator|==
literal|null
operator|||
name|direction
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"forward"
argument_list|)
condition|)
name|dir
operator|=
name|Transliterator
operator|.
name|FORWARD
expr_stmt|;
elseif|else
if|if
condition|(
name|direction
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"reverse"
argument_list|)
condition|)
name|dir
operator|=
name|Transliterator
operator|.
name|REVERSE
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"invalid direction: "
operator|+
name|direction
argument_list|)
throw|;
name|transliterator
operator|=
name|Transliterator
operator|.
name|getInstance
argument_list|(
name|id
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|ICUTransformFilter
argument_list|(
name|input
argument_list|,
name|transliterator
argument_list|)
return|;
block|}
block|}
end_class

end_unit

