begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStreamBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ContentStreamTest
specifier|public
class|class
name|ContentStreamTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testStringStream
specifier|public
name|void
name|testStringStream
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|input
init|=
literal|"aads ghaskdgasgldj asl sadg ajdsg&jag # @ hjsakg hsakdg hjkas s"
decl_stmt|;
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFileStream
specifier|public
name|void
name|testFileStream
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|openResource
argument_list|(
literal|"solrj/README"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"README"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|FileStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|stream
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|,
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testURLStream
specifier|public
name|void
name|testURLStream
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|content
init|=
literal|null
decl_stmt|;
name|String
name|contentType
init|=
literal|null
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://svn.apache.org/repos/asf/lucene/dev/trunk/"
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URLConnection
name|conn
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|in
operator|=
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|contentType
operator|=
name|conn
operator|.
name|getContentType
argument_list|()
expr_stmt|;
name|content
operator|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
literal|"not enough content for test to be useful"
argument_list|,
name|content
operator|.
name|length
operator|>
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|assumeNoException
argument_list|(
literal|"Unable to connect to "
operator|+
name|url
operator|+
literal|" to run the test."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|URLStream
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|in
operator|=
name|stream
operator|.
name|getStream
argument_list|()
expr_stmt|;
comment|// getStream is needed before getSize is valid
name|assertEquals
argument_list|(
name|content
operator|.
name|length
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
argument_list|)
argument_list|,
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|String
name|charset
init|=
name|ContentStreamBase
operator|.
name|getCharsetFromContentType
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|charset
operator|==
literal|null
condition|)
name|charset
operator|=
name|ContentStreamBase
operator|.
name|DEFAULT_CHARSET
expr_stmt|;
comment|// Re-open the stream and this time use a reader
name|stream
operator|=
operator|new
name|ContentStreamBase
operator|.
name|URLStream
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
operator|new
name|StringReader
argument_list|(
operator|new
name|String
argument_list|(
name|content
argument_list|,
name|charset
argument_list|)
argument_list|)
argument_list|,
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

