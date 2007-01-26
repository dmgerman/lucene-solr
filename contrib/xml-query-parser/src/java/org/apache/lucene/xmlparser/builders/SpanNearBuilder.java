begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|builders
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|SpanNearQuery
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
name|xmlparser
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
name|xmlparser
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|SpanNearBuilder
specifier|public
class|class
name|SpanNearBuilder
extends|extends
name|SpanBuilderBase
block|{
DECL|field|factory
name|SpanQueryBuilder
name|factory
decl_stmt|;
DECL|method|SpanNearBuilder
specifier|public
name|SpanNearBuilder
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
name|String
name|slopString
init|=
name|DOMUtils
operator|.
name|getAttributeOrFail
argument_list|(
name|e
argument_list|,
literal|"slop"
argument_list|)
decl_stmt|;
name|int
name|slop
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|slopString
argument_list|)
decl_stmt|;
name|boolean
name|inOrder
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"inOrder"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ArrayList
name|spans
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|kid
init|=
name|e
operator|.
name|getFirstChild
argument_list|()
init|;
name|kid
operator|!=
literal|null
condition|;
name|kid
operator|=
name|kid
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
if|if
condition|(
name|kid
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|spans
operator|.
name|add
argument_list|(
name|factory
operator|.
name|getSpanQuery
argument_list|(
operator|(
name|Element
operator|)
name|kid
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|SpanQuery
index|[]
name|spanQueries
init|=
operator|(
name|SpanQuery
index|[]
operator|)
name|spans
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|spans
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|snq
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|spanQueries
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
return|return
name|snq
return|;
block|}
block|}
end_class

end_unit

