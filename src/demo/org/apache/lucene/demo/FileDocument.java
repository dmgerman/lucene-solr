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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
comment|/** Makes a document for a File.<p>     The document has three fields:<ul><li><code>path</code>--containing the pathname of the file, as a stored,     tokenized field;<li><code>modified</code>--containing the last modified date of the file as     a keyword field as encoded by<a     href="lucene.document.DateField.html">DateField</a>; and<li><code>contents</code>--containing the full contents of the file, as a     Reader field;     */
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
comment|// Add the path of the file as a field named "path".  Use a Text field, so
comment|// that the index stores the path, and so that the path is searchable
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
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

