begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/* Used by DocumentsWriter to track data for term vectors.  * One of these exists per unique term seen in each field in  * the document. */
end_comment

begin_class
DECL|class|PostingVector
class|class
name|PostingVector
block|{
DECL|field|p
name|Posting
name|p
decl_stmt|;
comment|// Corresponding Posting instance for this term
DECL|field|lastOffset
name|int
name|lastOffset
decl_stmt|;
comment|// Last offset we saw
DECL|field|offsetStart
name|int
name|offsetStart
decl_stmt|;
comment|// Address of first slice for offsets
DECL|field|offsetUpto
name|int
name|offsetUpto
decl_stmt|;
comment|// Next write address for offsets
DECL|field|posStart
name|int
name|posStart
decl_stmt|;
comment|// Address of first slice for positions
DECL|field|posUpto
name|int
name|posUpto
decl_stmt|;
comment|// Next write address for positions
block|}
end_class

end_unit

