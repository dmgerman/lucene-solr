begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * Accessor to make some package protected methods in {@link IndexWriter} available for testing.  * @lucene.internal  */
end_comment

begin_class
DECL|class|IndexWriterMaxDocsChanger
specifier|public
specifier|final
class|class
name|IndexWriterMaxDocsChanger
block|{
DECL|method|IndexWriterMaxDocsChanger
specifier|private
name|IndexWriterMaxDocsChanger
parameter_list|()
block|{}
comment|/**    * Tells {@link IndexWriter} to enforce the specified limit as the maximum number of documents in one index; call    * {@link #restoreMaxDocs} once your test is done.    * @see LuceneTestCase#setIndexWriterMaxDocs(int)    */
DECL|method|setMaxDocs
specifier|public
specifier|static
name|void
name|setMaxDocs
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|IndexWriter
operator|.
name|setMaxDocs
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
comment|/**     * Returns to the default {@link IndexWriter#MAX_DOCS} limit.    * @see LuceneTestCase#restoreIndexWriterMaxDocs()    */
DECL|method|restoreMaxDocs
specifier|public
specifier|static
name|void
name|restoreMaxDocs
parameter_list|()
block|{
name|IndexWriter
operator|.
name|setMaxDocs
argument_list|(
name|IndexWriter
operator|.
name|MAX_DOCS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

