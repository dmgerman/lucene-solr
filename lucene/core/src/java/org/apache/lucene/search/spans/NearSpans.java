begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Common super class for un/ordered Spans with a maximum slop between them.  */
end_comment

begin_class
DECL|class|NearSpans
specifier|abstract
class|class
name|NearSpans
extends|extends
name|ConjunctionSpans
block|{
DECL|field|query
specifier|final
name|SpanNearQuery
name|query
decl_stmt|;
DECL|field|allowedSlop
specifier|final
name|int
name|allowedSlop
decl_stmt|;
DECL|method|NearSpans
name|NearSpans
parameter_list|(
name|SpanNearQuery
name|query
parameter_list|,
name|List
argument_list|<
name|Spans
argument_list|>
name|subSpans
parameter_list|)
block|{
name|super
argument_list|(
name|subSpans
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|allowedSlop
operator|=
name|query
operator|.
name|getSlop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

