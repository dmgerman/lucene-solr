begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Reader
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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|DateField
import|;
end_import

begin_comment
comment|/** A utility for making Lucene Documents from a File. */
end_comment

begin_class
DECL|class|FileDocument
specifier|public
class|class
name|FileDocument
block|{
comment|/** Makes a document for a File.<p>     The document has three fields:<ul><li><code>path</code>--containing the pathname of the file, as a stored,     untokenized field;<li><code>modified</code>--containing the last modified date of the file as     a keyword field as encoded by<a     href="lucene.document.DateField.html">DateField</a>; and<li><code>contents</code>--containing the full contents of the file, as a     Reader field;     */
DECL|method|Document
specifier|public
specifier|static
name|Document
name|Document
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
block|{
comment|// make a new, empty document
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Add the path of the file as a field named "path".  Use a
comment|// Keyword field, so that it's searchable, but so that no attempt is made
comment|// to tokenize the field into words.
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"path"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the last modified date of the file a field named "modified".  Use a
comment|// Keyword field, so that it's searchable, but so that no attempt is made
comment|// to tokenize the field into words.
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"modified"
argument_list|,
name|DateField
operator|.
name|timeToString
argument_list|(
name|f
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the contents of the file a field named "contents".  Use a Text
comment|// field, specifying a Reader, so that the text of the file is tokenized.
comment|// ?? why doesn't FileReader work here ??
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"contents"
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
comment|// return the document
return|return
name|doc
return|;
block|}
DECL|method|FileDocument
specifier|private
name|FileDocument
parameter_list|()
block|{}
block|}
end_class

end_unit

