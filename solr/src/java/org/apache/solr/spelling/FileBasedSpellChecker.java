begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

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
name|InputStreamReader
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|search
operator|.
name|spell
operator|.
name|PlainTextDictionary
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
name|store
operator|.
name|RAMDirectory
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
name|NamedList
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
name|SolrCore
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
name|schema
operator|.
name|FieldType
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
name|util
operator|.
name|HighFrequencyDictionary
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  *<p>  * A spell checker implementation that loads words from a text file (one word per line).  *</p>  *  * @since solr 1.3  **/
end_comment

begin_class
DECL|class|FileBasedSpellChecker
specifier|public
class|class
name|FileBasedSpellChecker
extends|extends
name|AbstractLuceneSpellChecker
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FileBasedSpellChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOURCE_FILE_CHAR_ENCODING
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_FILE_CHAR_ENCODING
init|=
literal|"characterEncoding"
decl_stmt|;
DECL|field|characterEncoding
specifier|private
name|String
name|characterEncoding
decl_stmt|;
DECL|field|WORD_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|WORD_FIELD_NAME
init|=
literal|"word"
decl_stmt|;
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|characterEncoding
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|SOURCE_FILE_CHAR_ENCODING
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
try|try
block|{
name|loadExternalFileDictionary
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|spellChecker
operator|.
name|indexDictionary
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Override to return null, since there is no reader associated with a file based index    */
annotation|@
name|Override
DECL|method|determineReader
specifier|protected
name|IndexReader
name|determineReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|loadExternalFileDictionary
specifier|private
name|void
name|loadExternalFileDictionary
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
try|try
block|{
comment|// Get the field's analyzer
if|if
condition|(
name|fieldTypeName
operator|!=
literal|null
operator|&&
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fieldTypeName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|FieldType
name|fieldType
init|=
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|fieldTypeName
argument_list|)
decl_stmt|;
comment|// Do index-time analysis using the given fieldType's analyzer
name|RAMDirectory
name|ramDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|LogMergePolicy
name|mp
init|=
operator|new
name|LogByteSizeMergePolicy
argument_list|()
decl_stmt|;
name|mp
operator|.
name|setMergeFactor
argument_list|(
literal|300
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|luceneMatchVersion
argument_list|,
name|fieldType
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|150
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getLines
argument_list|(
name|sourceLocation
argument_list|,
name|characterEncoding
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|lines
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|WORD_FIELD_NAME
argument_list|,
name|s
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictionary
operator|=
operator|new
name|HighFrequencyDictionary
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir
argument_list|)
argument_list|,
name|WORD_FIELD_NAME
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// check if character encoding is defined
if|if
condition|(
name|characterEncoding
operator|==
literal|null
condition|)
block|{
name|dictionary
operator|=
operator|new
name|PlainTextDictionary
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|sourceLocation
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dictionary
operator|=
operator|new
name|PlainTextDictionary
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|sourceLocation
argument_list|)
argument_list|,
name|characterEncoding
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
name|log
operator|.
name|error
argument_list|(
literal|"Unable to load spellings"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCharacterEncoding
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|characterEncoding
return|;
block|}
block|}
end_class

end_unit

