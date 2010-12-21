begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ant
operator|.
name|DocumentTestCase
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
name|ant
operator|.
name|HtmlDocument
import|;
end_import

begin_class
DECL|class|HtmlDocumentTest
specifier|public
class|class
name|HtmlDocumentTest
extends|extends
name|DocumentTestCase
block|{
DECL|field|doc
name|HtmlDocument
name|doc
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|doc
operator|=
operator|new
name|HtmlDocument
argument_list|(
name|getFile
argument_list|(
literal|"test.html"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoc
specifier|public
name|void
name|testDoc
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Title"
argument_list|,
literal|"Test Title"
argument_list|,
name|doc
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Body"
argument_list|,
name|doc
operator|.
name|getBody
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"This is some test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|doc
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

