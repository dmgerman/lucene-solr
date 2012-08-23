begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
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
name|search
operator|.
name|spans
operator|.
name|SpanNotQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|queryparser
operator|.
name|xml
operator|.
name|DOMUtils
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
name|queryparser
operator|.
name|xml
operator|.
name|ParserException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Builder for {@link SpanNotQuery}  */
end_comment

begin_class
DECL|class|SpanNotBuilder
specifier|public
class|class
name|SpanNotBuilder
extends|extends
name|SpanBuilderBase
block|{
DECL|field|factory
specifier|private
specifier|final
name|SpanQueryBuilder
name|factory
decl_stmt|;
comment|/**    * @param factory    */
DECL|method|SpanNotBuilder
specifier|public
name|SpanNotBuilder
parameter_list|(
name|SpanQueryBuilder
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|Element
name|includeElem
init|=
name|DOMUtils
operator|.
name|getChildByTagOrFail
argument_list|(
name|e
argument_list|,
literal|"Include"
argument_list|)
decl_stmt|;
name|includeElem
operator|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|includeElem
argument_list|)
expr_stmt|;
name|Element
name|excludeElem
init|=
name|DOMUtils
operator|.
name|getChildByTagOrFail
argument_list|(
name|e
argument_list|,
literal|"Exclude"
argument_list|)
decl_stmt|;
name|excludeElem
operator|=
name|DOMUtils
operator|.
name|getFirstChildOrFail
argument_list|(
name|excludeElem
argument_list|)
expr_stmt|;
name|SpanQuery
name|include
init|=
name|factory
operator|.
name|getSpanQuery
argument_list|(
name|includeElem
argument_list|)
decl_stmt|;
name|SpanQuery
name|exclude
init|=
name|factory
operator|.
name|getSpanQuery
argument_list|(
name|excludeElem
argument_list|)
decl_stmt|;
name|SpanNotQuery
name|snq
init|=
operator|new
name|SpanNotQuery
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|)
decl_stmt|;
name|snq
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|snq
return|;
block|}
block|}
end_class

end_unit

