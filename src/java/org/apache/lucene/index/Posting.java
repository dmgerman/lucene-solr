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
comment|/* Used by DocumentsWriter to track postings for a single  * term.  One of these exists per unique term seen since the  * last flush. */
end_comment

begin_class
DECL|class|Posting
specifier|final
class|class
name|Posting
block|{
DECL|field|textStart
name|int
name|textStart
decl_stmt|;
comment|// Address into char[] blocks where our text is stored
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
comment|// # times this term occurs in the current doc
DECL|field|freqStart
name|int
name|freqStart
decl_stmt|;
comment|// Address of first byte[] slice for freq
DECL|field|freqUpto
name|int
name|freqUpto
decl_stmt|;
comment|// Next write address for freq
DECL|field|proxStart
name|int
name|proxStart
decl_stmt|;
comment|// Address of first byte[] slice
DECL|field|proxUpto
name|int
name|proxUpto
decl_stmt|;
comment|// Next write address for prox
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
comment|// Last docID where this term occurred
DECL|field|lastDocCode
name|int
name|lastDocCode
decl_stmt|;
comment|// Code for prior doc
DECL|field|lastPosition
name|int
name|lastPosition
decl_stmt|;
comment|// Last position where this term occurred
DECL|field|vector
name|PostingVector
name|vector
decl_stmt|;
comment|// Corresponding PostingVector instance
block|}
end_class

end_unit

