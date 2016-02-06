begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|PostingsEnum
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
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * An interface defining the collection of postings information from the leaves  * of a {@link org.apache.lucene.search.spans.Spans}  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|SpanCollector
specifier|public
interface|interface
name|SpanCollector
block|{
comment|/**    * Collect information from postings    * @param postings a {@link PostingsEnum}    * @param position the position of the PostingsEnum    * @param term     the {@link Term} for this postings list    * @throws IOException on error    */
DECL|method|collectLeaf
specifier|public
name|void
name|collectLeaf
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|int
name|position
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Call to indicate that the driving Spans has moved to a new position    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

