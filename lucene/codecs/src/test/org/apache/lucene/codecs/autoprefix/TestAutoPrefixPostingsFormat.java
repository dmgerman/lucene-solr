begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.autoprefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|autoprefix
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
name|IndexOptions
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
name|RandomPostingsTester
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  * Tests AutoPrefix's postings  */
end_comment

begin_comment
comment|// NOTE: we don't extend BasePostingsFormatTestCase becase we can only handle DOCS_ONLY fields:
end_comment

begin_class
DECL|class|TestAutoPrefixPostingsFormat
specifier|public
class|class
name|TestAutoPrefixPostingsFormat
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|RandomPostingsTester
argument_list|(
name|random
argument_list|()
argument_list|)
operator|.
name|testFull
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|AutoPrefixPostingsFormat
argument_list|()
argument_list|)
argument_list|,
name|createTempDir
argument_list|(
literal|"autoprefix"
argument_list|)
argument_list|,
name|IndexOptions
operator|.
name|DOCS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

