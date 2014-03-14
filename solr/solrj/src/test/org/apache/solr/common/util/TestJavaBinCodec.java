begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

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
name|ByteArrayOutputStream
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
name|FileOutputStream
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|SolrTestCaseJ4
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
name|EnumFieldValue
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
name|SolrDocument
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
name|SolrDocumentList
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
name|SolrInputDocument
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
name|SolrInputField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestJavaBinCodec
specifier|public
class|class
name|TestJavaBinCodec
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|SOLRJ_JAVABIN_BACKCOMPAT_BIN
specifier|private
specifier|static
specifier|final
name|String
name|SOLRJ_JAVABIN_BACKCOMPAT_BIN
init|=
literal|"/solrj/javabin_backcompat.bin"
decl_stmt|;
DECL|field|BIN_FILE_LOCATION
specifier|private
specifier|final
name|String
name|BIN_FILE_LOCATION
init|=
literal|"./solr/solrj/src/test-files/solrj/javabin_backcompat.bin"
decl_stmt|;
DECL|method|testStrings
specifier|public
name|void
name|testStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|JavaBinCodec
name|javabin
init|=
operator|new
name|JavaBinCodec
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|javabin
operator|.
name|marshal
argument_list|(
name|s
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|javabin
operator|.
name|unmarshal
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|generateAllDataTypes
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|generateAllDataTypes
parameter_list|()
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//NULL
name|types
operator|.
name|add
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|double
operator|)
literal|3
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|-
literal|4
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|42
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
operator|-
literal|5
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
literal|5
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
literal|50
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|float
operator|)
literal|6
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|solrDocs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|solrDocs
operator|.
name|setMaxScore
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|solrDocs
operator|.
name|setNumFound
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|solrDocs
operator|.
name|setStart
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|solrDocs
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|solrDocs
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
comment|// TODO?
comment|// List<String> list = new ArrayList<String>();
comment|// list.add("one");
comment|// types.add(list.iterator());
name|types
operator|.
name|add
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|)
expr_stmt|;
comment|//END
name|SolrInputDocument
name|idoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|idoc
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|idoc
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|parentDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|parentDoc
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|childDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|childDoc
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|parentDoc
operator|.
name|addChildDocument
argument_list|(
name|childDoc
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|parentDoc
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|EnumFieldValue
argument_list|(
literal|1
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|//Map.Entry
name|types
operator|.
name|add
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|1
operator|<<
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|//TAG_AND_LEN
name|types
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
literal|2
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|simpleOrderedMap
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|simpleOrderedMap
operator|.
name|add
argument_list|(
literal|"bar"
argument_list|,
literal|"barbar"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|simpleOrderedMap
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"barbar"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|nl
argument_list|)
expr_stmt|;
return|return
name|types
return|;
block|}
annotation|@
name|Test
DECL|method|testBackCompat
specifier|public
name|void
name|testBackCompat
parameter_list|()
block|{
name|JavaBinCodec
name|javabin
init|=
operator|new
name|JavaBinCodec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|readIterator
parameter_list|(
name|DataInputInputStream
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|readIterator
argument_list|(
name|fis
argument_list|)
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|SOLRJ_JAVABIN_BACKCOMPAT_BIN
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|unmarshaledObj
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|javabin
operator|.
name|unmarshal
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|matchObj
init|=
name|generateAllDataTypes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|unmarshaledObj
operator|.
name|size
argument_list|()
argument_list|,
name|matchObj
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|unmarshaledObj
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|byte
index|[]
operator|&&
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|byte
index|[]
name|b1
init|=
operator|(
name|byte
index|[]
operator|)
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b2
init|=
operator|(
name|byte
index|[]
operator|)
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrDocument
operator|&&
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrDocument
condition|)
block|{
name|assertSolrDocumentEquals
argument_list|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrDocumentList
operator|&&
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrDocumentList
condition|)
block|{
name|assertSolrDocumentEquals
argument_list|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrInputDocument
operator|&&
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrInputDocument
condition|)
block|{
name|assertSolrInputDocumentEquals
argument_list|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrInputField
operator|&&
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|SolrInputField
condition|)
block|{
name|assertSolrInputFieldEquals
argument_list|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|unmarshaledObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|matchObj
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testForwardCompat
specifier|public
name|void
name|testForwardCompat
parameter_list|()
block|{
name|JavaBinCodec
name|javabin
init|=
operator|new
name|JavaBinCodec
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|Object
name|data
init|=
name|generateAllDataTypes
argument_list|()
decl_stmt|;
try|try
block|{
name|javabin
operator|.
name|marshal
argument_list|(
name|data
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|byte
index|[]
name|newFormatBytes
init|=
name|os
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|SOLRJ_JAVABIN_BACKCOMPAT_BIN
argument_list|)
decl_stmt|;
name|byte
index|[]
name|currentFormatBytes
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|currentFormatBytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//ignore the first byte. It is version information
name|assertEquals
argument_list|(
name|currentFormatBytes
index|[
name|i
index|]
argument_list|,
name|newFormatBytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|genBinaryFile
specifier|public
name|void
name|genBinaryFile
parameter_list|()
throws|throws
name|IOException
block|{
name|JavaBinCodec
name|javabin
init|=
operator|new
name|JavaBinCodec
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|Object
name|data
init|=
name|generateAllDataTypes
argument_list|()
decl_stmt|;
name|javabin
operator|.
name|marshal
argument_list|(
name|data
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|byte
index|[]
name|out
init|=
name|os
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|FileOutputStream
name|fs
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|BIN_FILE_LOCATION
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|bos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|TestJavaBinCodec
name|test
init|=
operator|new
name|TestJavaBinCodec
argument_list|()
decl_stmt|;
name|test
operator|.
name|genBinaryFile
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

