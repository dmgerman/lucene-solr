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
comment|/** This is the base class for an in-memory posting list,  *  keyed by a Token.  {@link TermsHash} maintains a hash  *  table holding one instance of this per unique Token.  *  Consumers of TermsHash (@link TermsHashConsumer} must  *  subclass this class with its own concrete class.  *  {@link FreqProxTermsWriter.RawPostingList} is the  *  subclass used for the freq/prox postings, and {@link  *  TermVectorsTermsWriter.PostingList} is the subclass  *  used to hold TermVectors postings. */
end_comment

begin_class
DECL|class|RawPostingList
specifier|abstract
class|class
name|RawPostingList
block|{
DECL|field|BYTES_SIZE
specifier|final
specifier|static
name|int
name|BYTES_SIZE
init|=
name|DocumentsWriter
operator|.
name|OBJECT_HEADER_BYTES
operator|+
literal|3
operator|*
name|DocumentsWriter
operator|.
name|INT_NUM_BYTE
decl_stmt|;
DECL|field|textStart
name|int
name|textStart
decl_stmt|;
DECL|field|intStart
name|int
name|intStart
decl_stmt|;
DECL|field|byteStart
name|int
name|byteStart
decl_stmt|;
block|}
end_class

end_unit

